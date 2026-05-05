package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account.AccountAccessLevel;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account.AccountStatus;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.account.AccountType;

import java.time.LocalDateTime;

public record TBankAccountDto(
        String id,
        String name,
        AccountType type,
        AccountStatus status,
        AccountAccessLevel accessLevel,
        LocalDateTime openedDate,
        LocalDateTime closedDate) {
}