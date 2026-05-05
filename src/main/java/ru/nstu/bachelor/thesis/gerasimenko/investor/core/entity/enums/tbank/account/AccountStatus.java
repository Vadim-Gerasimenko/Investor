package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    ACCOUNT_STATUS_UNSPECIFIED("ACCOUNT_STATUS_UNSPECIFIED"),
    ACCOUNT_STATUS_NEW("ACCOUNT_STATUS_NEW"),
    ACCOUNT_STATUS_OPEN("ACCOUNT_STATUS_OPEN"),
    ACCOUNT_STATUS_CLOSED("ACCOUNT_STATUS_CLOSED"),
    ACCOUNT_STATUS_ALL("ACCOUNT_STATUS_ALL");

    private final String status;
}