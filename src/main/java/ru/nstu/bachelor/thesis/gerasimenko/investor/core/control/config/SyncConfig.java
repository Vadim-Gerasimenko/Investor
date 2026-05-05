package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config;

import lombok.Data;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
public class SyncConfig {

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration instrumentsSyncFixedDelay = Duration.ofHours(4);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration instrumentsPricesFixedDelay = Duration.ofMinutes(1);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration accountSyncMaxDelay = Duration.ofMinutes(2);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration dictionaryRefreshFixedDelay = Duration.ofHours(4);
}