package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class TBankApiConfig {

    @NotNull(message = "TBankApiConfig: baseUrl is null")
    private String baseUrl;

    @NotNull(message = "TBankApiConfig: contractVersion is null")
    private String contractVersion;

    @NotNull(message = "TBankApiConfig: instrumentsServiceName is null")
    private String instrumentsServiceName;

    @NotNull(message = "TBankApiConfig: marketDataServiceName is null")
    private String marketDataServiceName;

    @NotNull(message = "TBankApiConfig: operationsServiceName is null")
    private String operationsServiceName;

    @NotNull(message = "TBankApiConfig: usersServiceName is null")
    private String usersServiceName;

    private String sandboxServiceName;
}