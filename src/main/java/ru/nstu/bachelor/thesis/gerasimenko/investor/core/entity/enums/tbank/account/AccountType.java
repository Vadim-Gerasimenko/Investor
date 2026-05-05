package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    ACCOUNT_TYPE_UNSPECIFIED("ACCOUNT_TYPE_UNSPECIFIED"),
    ACCOUNT_TYPE_TINKOFF("ACCOUNT_TYPE_TINKOFF"),
    ACCOUNT_TYPE_TINKOFF_IIS("ACCOUNT_TYPE_TINKOFF_IIS"),
    ACCOUNT_TYPE_INVEST_BOX("ACCOUNT_TYPE_INVEST_BOX"),
    ACCOUNT_TYPE_INVEST_FUND("ACCOUNT_TYPE_INVEST_FUND"),
    ACCOUNT_TYPE_DEBIT("ACCOUNT_TYPE_DEBIT"),
    ACCOUNT_TYPE_SAVING("ACCOUNT_TYPE_SAVING"),
    ACCOUNT_TYPE_DFA("ACCOUNT_TYPE_DFA"),;

    private final String type;
}