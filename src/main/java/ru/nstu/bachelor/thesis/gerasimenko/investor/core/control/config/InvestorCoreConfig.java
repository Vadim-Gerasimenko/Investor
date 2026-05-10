package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.cbrf.CbrfApiConfig;

@Slf4j
@Data
@Configuration
@ConfigurationProperties("investor-core")
public class InvestorCoreConfig {

    @NotNull
    private String systemUserEmail;

    private boolean devMode = false;

    private TBankApiConfig tBankApi = new TBankApiConfig();

    private CbrfApiConfig cbrfApi = new CbrfApiConfig();

    private SyncConfig synchronization = new SyncConfig();

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @PostConstruct
    public void init() {
        log.info("init configuration: InvestorCoreCfg=[{}]", this);
    }
}