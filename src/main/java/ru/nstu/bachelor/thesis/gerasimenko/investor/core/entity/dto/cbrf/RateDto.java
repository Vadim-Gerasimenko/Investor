package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.cbrf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.MoneyValueConverter.ONE_TO_NANO;

@Data
@NoArgsConstructor
public class RateDto {

    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    private String id;

    @JacksonXmlProperty(localName = "NumCode")
    private String numCode;

    @JacksonXmlProperty(localName = "CharCode")
    private String charCode;

    @JacksonXmlProperty(localName = "Nominal")
    private Integer nominal;

    @JacksonXmlProperty(localName = "Name")
    private String name;

    @JacksonXmlProperty(localName = "Value")
    private String value;

    @JacksonXmlProperty(localName = "VunitRate")
    private String vunitRate;

    public BigDecimal getValueAsBigDecimal() {
        return getAsBigDecimal(value);
    }

    public BigDecimal getVunitRateAsBigDecimal() {
        return getAsBigDecimal(vunitRate);
    }

    public long getValueAsNano() {
        return getAsNano(value);
    }

    public long getVunitRateAsNano() {
        return getAsNano(vunitRate);
    }

    private long getAsNano(String value) {
        if (value == null) {
            return 0L;
        }
        BigDecimal rubles = getAsBigDecimal(value);
        return rubles.multiply(BigDecimal.valueOf(ONE_TO_NANO)).longValue();
    }

    private BigDecimal getAsBigDecimal(String value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.replace(",", "."));
    }
}
