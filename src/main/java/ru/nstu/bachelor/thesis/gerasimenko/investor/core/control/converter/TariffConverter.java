package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.InstrumentFeeDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.TariffDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.InstrumentFee;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.Tariff;

import java.util.Comparator;

@UtilityClass
public class TariffConverter {

    public static TariffDto convert(Tariff tariff) {
        return TariffDto.builder()
                .name(tariff.getDescription())
                .fees(tariff.getInstrumentFees().stream()
                        .map(TariffConverter::convert)
                        .sorted(Comparator.comparing(InstrumentFeeDto::instrument))
                        .toList())
                .build();
    }

    private static InstrumentFeeDto convert(InstrumentFee instrumentFee) {
        return InstrumentFeeDto.builder()
                .instrument(instrumentFee.getInstrumentType().getDescription())
                .percentNano(instrumentFee.getPercentNano())
                .build();
    }
}