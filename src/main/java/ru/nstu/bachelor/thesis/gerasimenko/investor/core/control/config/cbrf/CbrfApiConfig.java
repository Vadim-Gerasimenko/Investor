package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.cbrf;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CbrfApiConfig {

    @NotNull(message = "CbrfApiConfig: timeZone is null")
    private String timeZone;

    @NotNull(message = "CbrfApiConfig: rates is null")
    private RatesUrlConfig rates;

    @NotNull(message = "CbrfApiConfig: currencies is null")
    private CurrenciesUrlConfig currencies;
}
