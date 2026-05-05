package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.InstrumentDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.InstrumentType;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankInstrument;

@UtilityClass
public class TBankInstrumentConverter {

    public static TBankInstrument convert(InstrumentDto instrumentDto, InstrumentType instrumentType) {
        return TBankInstrument.builder()
                .uid(instrumentDto.uid())
                .figi(instrumentDto.figi())
                .isin(instrumentDto.isin())
                .lot(instrumentDto.lot())
                .ticker(instrumentDto.ticker())
                .name(instrumentDto.name())
                .currency(instrumentDto.currency())
                .instrumentType(instrumentType)
                .build();
    }

    public static InstrumentDto convert(TBankInstrument instrument) {
        return InstrumentDto.builder()
                .uid(instrument.getUid())
                .figi(instrument.getFigi())
                .isin(instrument.getIsin())
                .lot(instrument.getLot())
                .ticker(instrument.getTicker())
                .type(instrument.getInstrumentType().getDescription())
                .name(instrument.getName())
                .currency(instrument.getCurrency())
                .build();
    }
}