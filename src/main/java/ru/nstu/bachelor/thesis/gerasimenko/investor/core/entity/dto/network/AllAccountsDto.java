package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import java.util.List;

public record AllAccountsDto(AccountDto activeAccount, List<AccountDto> accounts) {
}
