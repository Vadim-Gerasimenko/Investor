package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

public record MoneyValueDto(
        String currency,
        Long units,
        Long nano
) {
}