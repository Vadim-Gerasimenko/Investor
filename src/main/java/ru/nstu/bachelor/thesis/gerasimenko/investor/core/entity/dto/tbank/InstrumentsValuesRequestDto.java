package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.instrument.InstrumentValueType;

import java.util.List;

public record InstrumentsValuesRequestDto(List<String> instrumentId, List<String> values) {

    public InstrumentsValuesRequestDto(List<String> instrumentsIds) {
        this(instrumentsIds, List.of(InstrumentValueType.INSTRUMENT_VALUE_LAST_PRICE.getType()));
    }
}