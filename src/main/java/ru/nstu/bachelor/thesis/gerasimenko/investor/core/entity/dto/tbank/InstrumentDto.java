package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import lombok.Builder;

@Builder
public record InstrumentDto(String uid,
                            String figi,
                            String ticker,
                            String isin,
                            Integer lot,
                            String currency,
                            String name,
                            String type) {
}