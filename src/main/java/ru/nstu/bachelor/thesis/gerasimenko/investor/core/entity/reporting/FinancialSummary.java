package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.OperationDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummary {
    private BigDecimal currentCashBalance;
    private BigDecimal portfolioAmount;
    private BigDecimal potentialTotalProfit;

    private List<OperationDto> inputOperations;
    private List<OperationDto> outputOperations;

    private BigDecimal closedTradesProfitBeforeTax;
    private BigDecimal closedTradesFees;
    private BigDecimal closedTradesTax;
    private BigDecimal closedTradesProfit;
    private BigDecimal closedTradesPassiveIncome;
    private BigDecimal closedTradesTotalProfit;

    private BigDecimal openTradesFees;
    private BigDecimal openTradesProfitBeforeTax;
    private BigDecimal openTradesPotentialTaxableBase;
    private BigDecimal openTradesPotentialTax;
    private BigDecimal openTradesPotentialProfit;
    private BigDecimal openTradesPassiveIncome;
    private BigDecimal openTradesTotalPotentialProfit;
}
