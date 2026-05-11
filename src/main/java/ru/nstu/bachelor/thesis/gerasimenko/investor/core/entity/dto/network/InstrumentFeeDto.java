package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import lombok.Builder;

@Builder
public record InstrumentFeeDto(String instrument, Long percentNano) {
}