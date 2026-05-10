package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.cbrf;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CurrenciesUrlConfig {

    @NotNull(message = "CurrenciesUrlConfig: url is null")
    private String url;
}
