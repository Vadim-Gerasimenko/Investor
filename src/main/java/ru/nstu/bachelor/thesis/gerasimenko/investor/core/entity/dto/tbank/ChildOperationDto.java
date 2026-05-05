package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

public record ChildOperationDto(
        String instrumentUid,
        MoneyValueDto payment
) {
}