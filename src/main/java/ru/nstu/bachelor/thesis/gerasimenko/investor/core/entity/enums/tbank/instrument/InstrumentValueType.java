package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.instrument;

import lombok.Getter;

@Getter
public enum InstrumentValueType {

    INSTRUMENT_VALUE_LAST_PRICE("INSTRUMENT_VALUE_LAST_PRICE");

    private final String type;

    InstrumentValueType(String type) {
        this.type = type;
    }
}