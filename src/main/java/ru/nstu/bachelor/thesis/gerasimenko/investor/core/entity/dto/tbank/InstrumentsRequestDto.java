package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.instrument.InstrumentExchange;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.instrument.InstrumentStatus;

public record InstrumentsRequestDto(String instrumentStatus, String instrumentExchange) {

    public static InstrumentsRequestDto byDefault() {
        return new InstrumentsRequestDto(
                InstrumentStatus.INSTRUMENT_STATUS_ALL.getStatus(),
                InstrumentExchange.INSTRUMENT_EXCHANGE_UNSPECIFIED.getExchange()
        );
    }
}