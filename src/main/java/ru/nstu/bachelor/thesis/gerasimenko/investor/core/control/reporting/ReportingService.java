package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.reporting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.TBankInstrumentConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.TBankOperationConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankAccountService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankInstrumentPriceService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankOperationService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting.Report;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting.Summary;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting.TradeGroup;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankAccount;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankInstrument;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.OperationState.OPERATION_STATE_EXECUTED;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.OperationType.OPERATION_TYPE_BUY;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.OperationType.OPERATION_TYPE_SELL;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {

    private final TBankOperationService tBankOperationService;
    private final TBankAccountService tBankAccountService;

    private final TBankInstrumentPriceService tBankInstrumentPriceService;

    private final ExcelReportGenerator excelReportGenerator;

    @Transactional(readOnly = true)
    public byte[] getExcelReport(User user) {
        Report report = getReport(user);
        return excelReportGenerator.generateExcelReport(report);
    }

    @Transactional(readOnly = true)
    public Report getReport(User user) {
        log.info("to get report: userId=[{}]", user.getId());
        TBankAccount account = tBankAccountService.findActiveAccount(user);
        List<TBankInstrument> instruments = tBankOperationService.findAccountInstruments(account);

        List<TradeGroup> closedTrades = new ArrayList<>();
        List<TradeGroup> openTrades = new ArrayList<>();

        for (TBankInstrument instrument : instruments) {
            List<TBankOperation> operations = tBankOperationService.findAccountOperationsByInstrument(account, instrument);

            if (operations.isEmpty()) {
                continue;
            }

            List<List<TBankOperation>> tradeGroups = groupByFifo(operations);

            for (List<TBankOperation> group : tradeGroups) {
                TradeGroup tradeGroup = buildTradeGroup(group, instrument);

                if (tradeGroup.getRemainingQuantity() == 0) {
                    closedTrades.add(tradeGroup);
                } else {
                    openTrades.add(tradeGroup);
                }
            }
        }

        log.info("from get report: userId=[{}]", user.getId());
        return Report.builder()
                .closedTrades(closedTrades)
                .openTrades(openTrades)
                .summary(calculateSummary(closedTrades, openTrades))
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

    private TradeGroup buildTradeGroup(List<TBankOperation> operations, TBankInstrument instrument) {
        long totalBuyQuantity = 0;
        long totalSellQuantity = 0;
        long totalBuyValue = 0;
        long totalSellValue = 0;
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
                totalBuyValue -= op.getPaymentValue();
            } else if (OPERATION_TYPE_SELL.getType().equals(op.getOperationType().getType())) {
                totalSellQuantity += quantity;
                totalSellValue += op.getPaymentValue();
            } else {
                otherOperationsSum.merge(op.getOperationType().getType(), op.getPaymentValue(), Long::sum);
            }
        }

        long remainingQuantity = totalBuyQuantity - totalSellQuantity;
        long profitLoss = totalSellValue - totalBuyValue;
        long avgBuyPrice = totalBuyQuantity > 0 ? totalBuyValue / totalBuyQuantity : 0;
        long avgSellPrice = totalSellQuantity > 0 ? totalSellValue / totalSellQuantity : 0;

        return TradeGroup.builder()
                .instrument(TBankInstrumentConverter.convert(instrument))
                .operations(operations.stream().map(TBankOperationConverter::convert).toList())
                .currentPrice(tBankInstrumentPriceService.getCurrentPrice(instrument.getUid()))
                .totalBuyQuantity(totalBuyQuantity)
                .totalSellQuantity(totalSellQuantity)
                .remainingQuantity(Math.max(remainingQuantity, 0))
                .profitLoss(profitLoss)
                .avgBuyPrice(avgBuyPrice)
                .avgSellPrice(avgSellPrice)
                .otherOperationsSum(otherOperationsSum)
                .openedAt(openedAt)
                .closedAt(remainingQuantity == 0 ? closedAt : null)
                .build();
    }

    private Summary calculateSummary(List<TradeGroup> closedTrades, List<TradeGroup> openTrades) {
        long totalProfit = closedTrades.stream()
                .mapToLong(TradeGroup::getProfitLoss)
                .sum();

        long totalInvested = closedTrades.stream()
                .mapToLong(TradeGroup::getTotalBuyValue)
                .sum();

        long currentValue = openTrades.stream()
                .mapToLong(tg -> {
                    long currentPrice = tBankInstrumentPriceService.getCurrentPrice(tg.getInstrument().uid());
                    return tg.getRemainingQuantity() * currentPrice;
                })
                .sum();

        long totalValue = totalInvested + totalProfit + currentValue;
        BigDecimal returnPercent = totalInvested > 0
                ? BigDecimal.valueOf((double) (totalProfit + currentValue) / totalInvested * 100)
                : BigDecimal.ZERO;

        return Summary.builder()
                .totalProfit(totalProfit)
                .totalInvested(totalInvested)
                .currentValue(currentValue)
                .totalReturnPercent(returnPercent)
                .build();
    }
}