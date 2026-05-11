package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.sync;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.api.TBankApiService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.InvestorCoreConfig;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.dictionary.DictionaryCache;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class TBankDataSynchronizer {

    private final TaskScheduler taskScheduler;

    private final TBankApiService tBankApiService;
    private final DictionaryCache dictionaryCache;
    private final InvestorCoreConfig investorCoreConfig;

    @PostConstruct
    public void init() {//TODO
       // syncInstruments();
       //syncInstrumentsPrices();

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
        );
    }

    private void syncInstruments() {
        log.info("Starting instruments sync...");
        try {
            tBankApiService.updateInstruments();
        } catch (Exception e) {
            log.error("Instruments sync failed", e);
            throw new InvestorCoreException("Instruments sync failed");
        }
        log.info("Instruments sync completed successfully");
    }

    private void syncInstrumentsPrices() {
        log.info("Starting instruments prices sync...");
        try {
            tBankApiService.updateInstrumentsPrices();
        } catch (Exception e) {
            log.error("Instruments prices sync failed", e);
            throw new InvestorCoreException("Instruments prices sync failed");
        }
        log.info("Instruments prices sync completed successfully");
    }
}