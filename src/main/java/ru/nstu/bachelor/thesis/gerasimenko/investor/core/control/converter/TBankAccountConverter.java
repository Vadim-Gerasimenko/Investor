package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.AccountDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankAccount;

@UtilityClass
public class TBankAccountConverter {

    public static AccountDto convert(TBankAccount tBankAccount) {
        return AccountDto.builder()
                .name(tBankAccount.getAccountName())
                .type(tBankAccount.getType().getDescription())
                .status(tBankAccount.getStatus().getDescription())
                .accessLevel(tBankAccount.getAccessLevel().getDescription())
                .build();
    }
}