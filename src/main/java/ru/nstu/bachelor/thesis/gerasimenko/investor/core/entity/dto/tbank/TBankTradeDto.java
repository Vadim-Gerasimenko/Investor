package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import java.time.LocalDateTime;

public record TBankTradeDto(
        String tradeId,
        LocalDateTime dateTime,
        Long quantity,
        MoneyValueDto price
) {
}