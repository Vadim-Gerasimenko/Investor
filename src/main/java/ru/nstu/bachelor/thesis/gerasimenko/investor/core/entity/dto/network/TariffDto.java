package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import lombok.Builder;

import java.util.List;

@Builder
public record TariffDto(String name, List<InstrumentFeeDto> fees) {
}