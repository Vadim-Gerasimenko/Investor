package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.api.TBankApiService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.AccountNotFoundException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank.TBankAccountRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank.TBankActiveAccountRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankAccount;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankActiveAccount;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TBankAccountService {

    private final TBankAccountRepository tBankAccountRepository;
    private final TBankActiveAccountRepository tBankActiveAccountRepository;

    @Transactional
    public LocalDateTime updateLastSyncTime(TBankAccount account) {
        account.setLastSyncAt(LocalDateTime.now());
        return account.getLastSyncAt();
    }

    @Transactional
    public List<TBankAccount> saveAll(List<TBankAccount> tBankAccounts) {
        return tBankAccountRepository.saveAll(tBankAccounts);
    }

    @Transactional(readOnly = true)
    public String findActiveAccountIdOrElseNull(@AuthenticationPrincipal User user) {
        return tBankActiveAccountRepository.findById(user.getId())
                .map(TBankActiveAccount::getAccount).map(TBankAccount::getId).orElse(null);
    }

    @Transactional(readOnly = true)
    public TBankAccount findActiveAccount(@AuthenticationPrincipal User user) throws AccountNotFoundException {
        return tBankActiveAccountRepository.findById(user.getId())
                .orElseThrow(() -> new AccountNotFoundException(String.format("Active account not found: userId=[%s]", user.getId())))
                .getAccount();
    }

    @Transactional(readOnly = true)
    public TBankAccount findAccountByName(@AuthenticationPrincipal User user, @Nonnull String accountName)
            throws AccountNotFoundException {
        return tBankAccountRepository.findByUserAndAccountName(user, accountName)
                .orElseThrow(() -> new AccountNotFoundException(String.format("Account not found: userId=[%s], accountName=[%s]", user.getId(), accountName)));
    }

    @Transactional
    public TBankAccount activateAccount(User user, String accountName) {
        log.info("to activate account: userId=[{}], accountName=[{}]", user.getId(), accountName);
        TBankAccount account = getAccountByName(user, accountName);
        tBankActiveAccountRepository.upsert(user.getId(), account.getId());
        log.info("from activate account: userId=[{}], activeAccountName=[{}]", user.getId(), accountName);
        return account;
    }

    @Transactional(readOnly = true)
    public List<TBankAccount> findAllAccounts(User user) {
        return tBankAccountRepository.findAllByUser(user);
    }

    @Transactional(readOnly = true)
    public TBankAccount getAccountByName(User user, String accountName) throws AccountNotFoundException {
        return tBankAccountRepository.findByUserAndAccountName(user, accountName).orElseThrow(() -> new AccountNotFoundException(
                String.format("T-Bank account with specified name not found: userId=[%s], accountName=[%s]", user.getId(), accountName)));
    }
}