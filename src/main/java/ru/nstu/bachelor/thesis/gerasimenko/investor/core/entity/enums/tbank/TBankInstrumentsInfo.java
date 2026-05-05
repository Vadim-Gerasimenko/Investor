package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.InstrumentType;

@Getter
@RequiredArgsConstructor
public enum TBankInstrumentsInfo {
    BONDS("Bonds", InstrumentType.BOND),
    CURRENCIES("Currencies", InstrumentType.CURRENCY),
    ETFS("Etfs", InstrumentType.ETF),
    SHARES("Shares", InstrumentType.SHARE),
    FUTURES("Futures", InstrumentType.FUTURE);

    private final String officialName;
    private final InstrumentType type;
}