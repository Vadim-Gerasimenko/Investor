package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.InstrumentDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.OperationDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeGroup {
    private InstrumentDto instrument;
    private List<OperationDto> operations;
    private BigDecimal currentPrice;
    private BigDecimal currentAmount;
    private BigDecimal profitFromSpeculationBeforeTax;
    private BigDecimal potentialAmountWithoutTax;
    private BigDecimal potentialAmount;
    private BigDecimal passiveIncomeBeforeTax;
    private BigDecimal passiveIncome;
    private long totalBuyQuantity;
    private long totalSellQuantity;
    private BigDecimal totalBuyValue;
    private BigDecimal totalSellValue;
    private long remainingQuantity;
    private BigDecimal profitLoss;
    private BigDecimal avgBuyPrice;
    private BigDecimal avgSellPrice;
    private BigDecimal accruedFees;
    private BigDecimal accruedTaxes;
    private BigDecimal potentialFees;
    private BigDecimal taxAdjustment;
    private BigDecimal profitFromSpeculation;
    private BigDecimal finalProfit;
    private Map<String, Long> otherOperationsSumNano;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}