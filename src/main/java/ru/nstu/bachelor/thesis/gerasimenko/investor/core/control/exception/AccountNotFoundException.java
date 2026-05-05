package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception;

public class AccountNotFoundException extends InvestorCoreException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}