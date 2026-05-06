package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.MoneyValueDto;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@UtilityClass
public class MoneyValueConverter {

    public static final long ONE_TO_NANO = 1_000_000_000L;

    public static final MathContext MATH_CONTEXT = new MathContext(15, RoundingMode.HALF_UP);

    public static Long convert(MoneyValueDto moneyValueDto) {
        return moneyValueDto.units() * ONE_TO_NANO + moneyValueDto.nano();
    }

    public static BigDecimal convert(long nanoValue) {
        return BigDecimal.valueOf(nanoValue).divide(BigDecimal.valueOf(ONE_TO_NANO), MATH_CONTEXT);
    }

    public static BigDecimal getAverage(BigDecimal totalValue, long quantity) {
        return totalValue.divide(BigDecimal.valueOf(quantity), MATH_CONTEXT);
    }

    public static BigDecimal getTotal(BigDecimal price, long quantity) {
        return price.multiply(BigDecimal.valueOf(quantity), MATH_CONTEXT);
    }

    public static BigDecimal getPercent(BigDecimal totalValue, long percentNano) {
        return getPercent(totalValue, convert(percentNano));
    }

    public static BigDecimal getPercent(BigDecimal totalValue, BigDecimal percent) {
        return totalValue.divide(BigDecimal.valueOf(100), MATH_CONTEXT)
                .multiply(percent, MATH_CONTEXT);
    }
}