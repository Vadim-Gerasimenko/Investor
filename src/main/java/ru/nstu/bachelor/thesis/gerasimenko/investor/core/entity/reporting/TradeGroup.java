package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.InstrumentDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.OperationDto;

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
    private Long currentPrice;
    private long totalBuyQuantity;
    private long totalSellQuantity;
    private long totalBuyValue;
    private long totalSellValue;
    private long remainingQuantity;
    private long profitLoss;
    private long avgBuyPrice;
    private long avgSellPrice;
    private Map<String, Long> otherOperationsSum;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}