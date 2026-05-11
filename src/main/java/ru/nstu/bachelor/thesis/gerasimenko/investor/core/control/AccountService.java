package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.api.TBankApiService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.TBankAccountConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankAccountService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.AccountDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.AllAccountsDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankAccount;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final TBankApiService tBankApiService;
    private final TBankAccountService tBankAccountService;

    public AllAccountsDto getAllAccounts(User user) {
        log.info("to get all accounts: userId=[{}]", user.getId());
        tBankApiService.syncTBankAccountData(user);
        List<AccountDto> accountsDto = tBankAccountService.findAllAccounts(user).stream()
                .map(TBankAccountConverter::convert)
                .toList();
        AccountDto activeAccountDto = TBankAccountConverter.convert(tBankAccountService.findActiveAccount(user));

        return new AllAccountsDto(activeAccountDto, accountsDto);
    }

    public AccountDto activateAccount(User user, String accountName) {
        log.info("to activate account: userId=[{}], accountName=[{}]", user.getId(), accountName);
        TBankAccount account = tBankAccountService.activateAccount(user, accountName);
        tBankApiService.syncTBankAccountData(user);
        return TBankAccountConverter.convert(account);
    }
}
