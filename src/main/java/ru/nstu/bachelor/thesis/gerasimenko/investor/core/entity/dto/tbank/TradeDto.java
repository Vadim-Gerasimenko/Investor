package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TradeDto(
        String tradeId,
        LocalDateTime dateTime,
        Long quantity,
        Long price,
        String currency) {
}