package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.MoneyValueDto;

@UtilityClass
public class MoneyValueConverter {

    public static final long ONE_TO_NANO = 1_000_000_000L;

    public static Long convert(MoneyValueDto moneyValueDto) {
        return moneyValueDto.units() * ONE_TO_NANO + moneyValueDto.nano();
    }
}