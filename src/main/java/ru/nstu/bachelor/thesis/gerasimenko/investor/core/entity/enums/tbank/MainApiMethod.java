package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MainApiMethod {

    GET_INFO("GetInfo"),
    GET_ACCOUNTS("GetAccounts"),
    GET_OPERATIONS("GetOperations"),
    GET_MARKET_VALUES("GetMarketValues");

    private final String method;
}