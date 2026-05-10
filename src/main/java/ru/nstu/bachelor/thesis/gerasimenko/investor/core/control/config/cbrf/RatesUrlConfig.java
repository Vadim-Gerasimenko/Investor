package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.cbrf;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatesUrlConfig {

    @NotNull(message = "RatesUrlConfig: url is null")
    private String url;

    @NotNull(message = "RatesUrlConfig: exactDateQueryParam is null")
    private String exactDateQueryParam;

    @NotNull(message = "RatesUrlConfig: timeZone is null")
    private String timeZone;

    @NotNull(message = "RatesUrlConfig: datePattern is null")
    private String datePattern;
}
