package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountAccessLevel {
    ACCOUNT_ACCESS_LEVEL_UNSPECIFIED("ACCOUNT_ACCESS_LEVEL_UNSPECIFIED"),
    ACCOUNT_ACCESS_LEVEL_FULL_ACCESS("ACCOUNT_ACCESS_LEVEL_FULL_ACCESS"),
    ACCOUNT_ACCESS_LEVEL_READ_ONLY("ACCOUNT_ACCESS_LEVEL_READ_ONLY"),
    ACCOUNT_ACCESS_LEVEL_NO_ACCESS("ACCOUNT_ACCESS_LEVEL_NO_ACCESS");

    private final String level;
}