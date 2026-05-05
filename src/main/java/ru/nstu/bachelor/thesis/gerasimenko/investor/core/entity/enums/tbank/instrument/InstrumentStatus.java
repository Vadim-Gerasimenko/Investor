package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.instrument;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InstrumentStatus {

    INSTRUMENT_STATUS_UNSPECIFIED("INSTRUMENT_STATUS_UNSPECIFIED"),
    INSTRUMENT_STATUS_BASE("INSTRUMENT_STATUS_BASE"),
    INSTRUMENT_STATUS_ALL("INSTRUMENT_STATUS_ALL");

    private final String status;
}