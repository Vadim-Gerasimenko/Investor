package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.cbrf;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatesUrlConfig {

    @NotNull(message = "RatesUrlConfig: url is null")
    private String url;

    @NotNull(message = "RatesUrlConfig: exactDateQueryParam is null")
    private String exactDateQueryParam;

    @NotNull(message = "RatesUrlConfig: datePattern is null")
    private String datePattern;

    @NotNull(message = "RatesUrlConfig: loaderCron is null")
    private String loaderCron;
}
