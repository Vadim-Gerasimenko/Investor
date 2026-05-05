package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum InstrumentType {
    BOND("bond"),
    SHARE("share"),
    CURRENCY("currency"),
    ETF("etf"),
    PRECIOUS_METAL("precious metal"),
    FUTURE("future"),
    OPTION("option"),
    UNSPECIFIED("");

    @JsonValue
    private final String type;
}