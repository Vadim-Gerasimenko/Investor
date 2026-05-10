package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.sync;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.api.CbrfApiService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CbrfDataSynchronizer {

    private final TaskScheduler taskScheduler;
    private final CbrfApiService cbrfApiService;

    @PostConstruct
    public void init() {//TODO
        cbrfApiService.loadCurrencies();
        cbrfApiService.loadRates(LocalDateTime.now());
        // syncInstruments();
        //  syncInstrumentsPrices();
/*
        taskScheduler.scheduleWithFixedDelay(
                this::syncInstruments,
                Instant.now().plus(investorCoreConfig.getSynchronization().getInstrumentsSyncFixedDelay()),
                investorCoreConfig.getSynchronization().getInstrumentsSyncFixedDelay()
        );

        taskScheduler.scheduleWithFixedDelay(
                this::syncInstrumentsPrices,
                Instant.now().plus(investorCoreConfig.getSynchronization().getInstrumentsPricesFixedDelay()),
                investorCoreConfig.getSynchronization().getInstrumentsPricesFixedDelay()
        );

        taskScheduler.scheduleWithFixedDelay(
                dictionaryCache::refresh,
                Instant.now().plus(investorCoreConfig.getSynchronization().getDictionaryRefreshFixedDelay()),
                investorCoreConfig.getSynchronization().getDictionaryRefreshFixedDelay()
        );*/
    }
}
