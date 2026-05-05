package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.instrument;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InstrumentExchange {

    INSTRUMENT_EXCHANGE_UNSPECIFIED("INSTRUMENT_EXCHANGE_UNSPECIFIED"),
    INSTRUMENT_EXCHANGE_DEALER("INSTRUMENT_EXCHANGE_DEALER");

    private final String exchange;
}
