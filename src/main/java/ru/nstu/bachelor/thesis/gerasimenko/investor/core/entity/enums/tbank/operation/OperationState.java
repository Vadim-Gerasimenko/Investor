package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationState {
    OPERATION_STATE_UNSPECIFIED("OPERATION_STATE_UNSPECIFIED"),
    OPERATION_STATE_EXECUTED("OPERATION_STATE_EXECUTED"),
    OPERATION_STATE_CANCELED("OPERATION_STATE_CANCELED"),
    OPERATION_STATE_PROGRESS("OPERATION_STATE_PROGRESS");

    private final String state;
}