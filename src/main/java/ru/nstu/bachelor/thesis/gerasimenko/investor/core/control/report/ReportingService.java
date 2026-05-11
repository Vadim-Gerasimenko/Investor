package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.TBankInstrumentConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.TBankOperationConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankAccountService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankInstrumentPriceService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankOperationService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankUserTariffService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.OperationDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.InstrumentType;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.InstrumentFee;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankAccount;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankInstrument;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Arrays.stream;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.constant.TBankEtf.ETF_WITHOUT_FEE_TICKERS;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.MoneyValueConverter.*;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.OperationState.OPERATION_STATE_EXECUTED;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.OperationType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {

    private final TBankOperationService tBankOperationService;
    private final TBankAccountService tBankAccountService;
    private final TBankUserTariffService tBankUserTariffService;

    private final TBankInstrumentPriceService tBankInstrumentPriceService;

    private final ExcelReportGenerator excelReportGenerator;

    private static final String FEE = "FEE";
    private static final String TAX = "TAX";
    private static final long TAX_PERCENTAGE_NANO = 13 * ONE_TO_NANO;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");


    @Transactional(readOnly = true)
    public ExcelReport getExcelReport(User user) {
        Report report = getReport(user);
        return excelReportGenerator.generateExcelReport(report);
    }

    @Transactional(readOnly = true)
    public Report getReport(User user) {
        log.info("to get report: userId=[{}]", user.getId());
        TBankAccount account = tBankAccountService.findActiveAccount(user);
        List<TBankInstrument> instruments = tBankOperationService.findAccountInstruments(account);
        List<InstrumentFee> instrumentFees = tBankUserTariffService.getUserTariff(user).getInstrumentFees();

        List<TradeGroup> closedTrades = new ArrayList<>();
        List<TradeGroup> openTrades = new ArrayList<>();

        for (TBankInstrument instrument : instruments) {
            List<TBankOperation> operations = tBankOperationService.findAccountOperationsByInstrument(account, instrument);
            if (operations.isEmpty()) {
                continue;
            }

            InstrumentFee instrumentFee = instrumentFees.stream()
                    .filter(fee -> fee.getInstrumentType().equals(instrument.getInstrumentType()))
                    .findFirst().orElseThrow(() -> new InvestorCoreException(String.format(
                            "Instrument fee not found: instrumentType=[%s]", instrument.getInstrumentType().getType())));

            List<List<TBankOperation>> tradeGroups = groupByFifo(operations);

            for (List<TBankOperation> group : tradeGroups) {
                TradeGroup tradeGroup = buildTradeGroup(group, instrument, instrumentFee);

                if (tradeGroup.getRemainingQuantity() == 0) {
                    closedTrades.add(tradeGroup);
                } else {
                    openTrades.add(tradeGroup);
                }
            }
        }

        List<TBankOperation> operations = tBankOperationService.findAccountOperations(account);
        FinancialSummary financialSummary = buildFinancialSummary(operations, closedTrades, openTrades);

        log.info("from get report: userId=[{}]", user.getId());
        return Report.builder()
                .closedTrades(closedTrades)
                .openTrades(openTrades)
                .financialSummary(financialSummary)
                .build();
    }

    private List<List<TBankOperation>> groupByFifo(List<TBankOperation> operations) {
        List<List<TBankOperation>> groups = new ArrayList<>();
        List<TBankOperation> currentGroup = new LinkedList<>();
        long balance = 0;

        for (TBankOperation operation : operations) {
            String opType = operation.getOperationType().getType();
            String state = operation.getState().getState();

            if (!OPERATION_STATE_EXECUTED.getState().equals(state)) {
                continue;
            }

            boolean isBuy = OPERATION_TYPE_BUY.getType().equals(opType);
            boolean isSell = OPERATION_TYPE_SELL.getType().equals(opType);

            if (isBuy || isSell) {
                long quantity = operation.getQuantity() - operation.getQuantityRest();
                balance = isBuy ? balance + quantity : balance - quantity;

                currentGroup.add(operation);

                if (balance == 0) {
                    groups.add(new ArrayList<>(currentGroup));
                    currentGroup.clear();
                }
            } else {
                if (!currentGroup.isEmpty()) {
                    currentGroup.add(operation);
                } else if (!groups.isEmpty()) {
                    groups.getLast().add(operation);
                } else {
                    List<TBankOperation> nonTradeGroup = new ArrayList<>();
                    nonTradeGroup.add(operation);
                    groups.add(nonTradeGroup);
                }
            }
        }

        if (!currentGroup.isEmpty()) {
            groups.add(currentGroup);
        }

        return groups;
    }

    private TradeGroup buildTradeGroup(List<TBankOperation> operations, TBankInstrument instrument, InstrumentFee instrumentFee) {
        long totalBuyQuantity = 0;
        long totalSellQuantity = 0;

        BigDecimal totalBuyValue = BigDecimal.ZERO;
        BigDecimal totalSellValue = BigDecimal.ZERO;
        BigDecimal accruedFees = BigDecimal.ZERO;
        BigDecimal accruedTaxes = BigDecimal.ZERO;
        BigDecimal passiveIncomeBeforeTax = BigDecimal.ZERO;

        Map<String, Long> otherOperationsSum = new HashMap<>();

        LocalDateTime openedAt = null;
        LocalDateTime closedAt = null;

        for (TBankOperation op : operations) {
            if (openedAt == null || op.getOperationDate().isBefore(openedAt)) {
                openedAt = op.getOperationDate();
            }

            if (closedAt == null || op.getOperationDate().isAfter(closedAt)) {
                closedAt = op.getOperationDate();
            }

            long quantity = op.getQuantity() - op.getQuantityRest();

            if (OPERATION_TYPE_BUY.getType().equals(op.getOperationType().getType())) {
                totalBuyQuantity += quantity;
                totalBuyValue = totalBuyValue.add(convert(-op.getPaymentValue()));
            } else if (OPERATION_TYPE_SELL.getType().equals(op.getOperationType().getType())) {
                totalSellQuantity += quantity;
                totalSellValue = totalSellValue.add(convert(op.getPaymentValue()));
            } else if (OPERATION_TYPE_DIVIDEND.getType().equals(op.getOperationType().getType()) ||
                    OPERATION_TYPE_COUPON.getType().equals(op.getOperationType().getType())) {
                passiveIncomeBeforeTax = passiveIncomeBeforeTax.add(convert(op.getPaymentValue()));
            } else if (op.getOperationType().getType().contains(FEE)) {
                accruedFees = accruedFees.add(convert(-op.getPaymentValue()));
            } else if (op.getOperationType().getType().contains(TAX)) {
                accruedTaxes = accruedTaxes.add(convert(-op.getPaymentValue()));
            } else {
                otherOperationsSum.put(op.getOperationType().getType(), quantity);
            }
        }

        long remainingQuantity = totalBuyQuantity - totalSellQuantity;
        BigDecimal avgBuyPrice = totalBuyQuantity > 0 ? getAverage(totalBuyValue, totalBuyQuantity) : BigDecimal.ZERO;
        BigDecimal avgSellPrice = totalSellQuantity > 0 ? getAverage(totalSellValue, totalSellQuantity) : BigDecimal.ZERO;

        BigDecimal currentPrice = convert(tBankInstrumentPriceService.getCurrentPrice(instrument.getUid()));

        BigDecimal currentAmount = getTotal(currentPrice, remainingQuantity);

        BigDecimal potentialFees = getPercent(currentAmount, instrumentFee.getPercentNano());

        if (instrument.getInstrumentType().getType().equals(InstrumentType.ETF.getType()) &&
                ETF_WITHOUT_FEE_TICKERS.contains(instrument.getTicker())) {
            potentialFees = BigDecimal.ZERO;
        }

        BigDecimal profitFromSpeculationBeforeTax = totalSellValue.add(getTotal(currentPrice, remainingQuantity))
                .subtract(totalBuyValue)
                .subtract(accruedFees)
                .subtract(potentialFees);

        BigDecimal taxAdjustment = getPercent(profitFromSpeculationBeforeTax, TAX_PERCENTAGE_NANO);

        BigDecimal passiveIncome = passiveIncomeBeforeTax.subtract(accruedTaxes);

        BigDecimal profitFromSpeculation = profitFromSpeculationBeforeTax
                .subtract(taxAdjustment.compareTo(BigDecimal.ZERO) > 0 ? taxAdjustment : BigDecimal.ZERO);
        BigDecimal finalProfit = profitFromSpeculation.add(passiveIncomeBeforeTax.subtract(accruedTaxes));

        return TradeGroup.builder()
                .instrument(TBankInstrumentConverter.convert(instrument))
                .operations(operations.stream().map(TBankOperationConverter::convert).toList())
                .currentPrice(currentPrice)
                .currentAmount(getTotal(currentPrice, remainingQuantity))
                .totalBuyValue(totalBuyValue)
                .totalSellValue(totalSellValue)
                .totalBuyQuantity(totalBuyQuantity)
                .totalSellQuantity(totalSellQuantity)
                .remainingQuantity(remainingQuantity)
                .passiveIncomeBeforeTax(passiveIncomeBeforeTax)
                .passiveIncome(passiveIncome)
                .avgBuyPrice(avgBuyPrice)
                .avgSellPrice(avgSellPrice)
                .otherOperationsSumNano(otherOperationsSum)
                .accruedFees(accruedFees)
                .accruedTaxes(accruedTaxes)
                .profitFromSpeculationBeforeTax(profitFromSpeculationBeforeTax)
                .taxAdjustment(taxAdjustment)
                .potentialFees(potentialFees)
                .profitFromSpeculation(profitFromSpeculation)
                .finalProfit(finalProfit)
                .openedAt(openedAt)
                .closedAt(remainingQuantity == 0 ? closedAt : null)
                .build();
    }

    private FinancialSummary buildFinancialSummary(List<TBankOperation> operations,
                                                   List<TradeGroup> closedTrades,
                                                   List<TradeGroup> openTrades) {
        List<TBankOperation> executedOperations = operations.stream()
                .filter(op -> op.getState().getState().equals(OPERATION_STATE_EXECUTED.getState()))
                .toList();

        List<OperationDto> inputOperations = executedOperations.stream()
                .filter(op -> op.getOperationType().getType().equals(OPERATION_TYPE_INPUT.getType()))
                .map(TBankOperationConverter::convert)
                .toList();
        List<OperationDto> outputOperations = executedOperations.stream()
                .filter(op -> op.getOperationType().getType().equals(OPERATION_TYPE_OUTPUT.getType()))
                .map(TBankOperationConverter::convert)
                .toList();

        BigDecimal balance = convert(executedOperations.stream()
                .mapToLong(TBankOperation::getPaymentValue)
                .sum());

        BigDecimal closedTradesProfitBeforeTax = closedTrades.stream()
                .map(TradeGroup::getProfitFromSpeculationBeforeTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal closedTradesTax = closedTradesProfitBeforeTax.compareTo(BigDecimal.ZERO) > 0
                ? getPercent(closedTradesProfitBeforeTax, TAX_PERCENTAGE_NANO)
                : BigDecimal.ZERO;
        BigDecimal closedTradesProfit = closedTradesProfitBeforeTax.subtract(closedTradesTax);

        BigDecimal closedTradesFees = closedTrades.stream()
                .map(TradeGroup::getAccruedFees)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal closedTradesPassiveIncome = closedTrades.stream()
                .map(TradeGroup::getPassiveIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal closedTradesTotalProfit = closedTradesProfit.add(closedTradesPassiveIncome);

        BigDecimal portfolioAmount = openTrades.stream()
                .map(TradeGroup::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal openTradesFees = openTrades.stream()
                .map(TradeGroup::getAccruedFees)
                .reduce(BigDecimal.ZERO, BigDecimal::add).add(openTrades.stream()
                        .map(TradeGroup::getPotentialFees)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        BigDecimal openTradesProfitBeforeTax = openTrades.stream()
                .map(TradeGroup::getProfitFromSpeculationBeforeTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal openTradesPotentialTax = BigDecimal.ZERO;
        BigDecimal openTradesPotentialTaxableBase = BigDecimal.ZERO;

        if (openTradesProfitBeforeTax.compareTo(BigDecimal.ZERO) > 0) {
            if (closedTradesProfitBeforeTax.compareTo(BigDecimal.ZERO) <= 0) {
                openTradesPotentialTaxableBase = openTradesProfitBeforeTax.add(closedTradesProfitBeforeTax);
            } else {
                openTradesPotentialTaxableBase = openTradesProfitBeforeTax;
            }

            if (openTradesPotentialTaxableBase.compareTo(BigDecimal.ZERO) > 0) {
                openTradesPotentialTax = getPercent(openTradesPotentialTaxableBase, TAX_PERCENTAGE_NANO);
            }
        }

        BigDecimal openTradesPotentialProfit = openTradesProfitBeforeTax.subtract(openTradesPotentialTax);
        BigDecimal openTradesPassiveIncome = openTrades.stream()
                .map(TradeGroup::getPassiveIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal openTradesTotalPotentialProfit = openTradesPotentialProfit.add(openTradesPassiveIncome);

        BigDecimal potentialTotalProfit = closedTradesTotalProfit.add(openTradesPotentialProfit);

        return FinancialSummary.builder()
                .currentCashBalance(balance)
                .inputOperations(inputOperations)
                .outputOperations(outputOperations)
                .closedTradesProfitBeforeTax(closedTradesProfitBeforeTax)
                .closedTradesTax(closedTradesTax)
                .closedTradesProfit(closedTradesProfit)
                .closedTradesPassiveIncome(closedTradesPassiveIncome)
                .closedTradesTotalProfit(closedTradesTotalProfit)
                .closedTradesFees(closedTradesFees)
                .portfolioAmount(portfolioAmount)
                .openTradesFees(openTradesFees)
                .openTradesProfitBeforeTax(openTradesProfitBeforeTax)
                .openTradesPotentialTaxableBase(openTradesPotentialTaxableBase)
                .openTradesPotentialTax(openTradesPotentialTax)
                .openTradesPotentialProfit(openTradesPotentialProfit)
                .openTradesPassiveIncome(openTradesPassiveIncome)
                .openTradesTotalPotentialProfit(openTradesTotalPotentialProfit)
                .potentialTotalProfit(potentialTotalProfit)
                .build();
    }

}