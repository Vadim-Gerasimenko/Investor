package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import java.time.LocalDateTime;

public record InstrumentValueDto(String type, MoneyValueDto value, LocalDateTime time) {
}