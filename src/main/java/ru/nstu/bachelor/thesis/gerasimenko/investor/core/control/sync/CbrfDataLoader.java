package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.sync;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.api.CbrfApiService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.InvestorCoreConfig;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.cbrf.CbrfApiConfig;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
public class CbrfDataLoader {

    private final TaskScheduler taskScheduler;
    private final CbrfApiService cbrfApiService;
    private final CbrfApiConfig cbrfApiConfig;

    public CbrfDataLoader(TaskScheduler taskScheduler,
                          CbrfApiService cbrfApiService,
                          InvestorCoreConfig investorCoreConfig) {
        this.taskScheduler = taskScheduler;
        this.cbrfApiService = cbrfApiService;
        this.cbrfApiConfig = investorCoreConfig.getCbrfApi();
    }

    @PostConstruct
    public void init() {
        loadCurrencies();
        loadRates();

        taskScheduler.schedule(
                this::loadCurrencies,
                new CronTrigger(cbrfApiConfig.getCurrencies().getLoaderCron(), ZoneId.of(cbrfApiConfig.getTimeZone()))
        );
        taskScheduler.schedule(
                this::loadRates,
                new CronTrigger(cbrfApiConfig.getRates().getLoaderCron(), ZoneId.of(cbrfApiConfig.getTimeZone()))
        );
    }

    private void loadCurrencies() {
        cbrfApiService.loadCurrencies();
    }

    private void loadRates() {
        cbrfApiService.loadRates(LocalDateTime.now());
    }
}
