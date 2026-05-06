package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private List<TradeGroup> closedTrades;
    private List<TradeGroup> openTrades;
    private Summary summary;
    private FinancialSummary financialSummary;
}