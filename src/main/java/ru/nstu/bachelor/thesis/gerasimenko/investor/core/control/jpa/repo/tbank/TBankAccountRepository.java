package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankAccount;

import java.util.List;
import java.util.Optional;

@Repository
public interface TBankAccountRepository extends JpaRepository<TBankAccount, Long> {

    List<TBankAccount> findAllByUser(User user);

    Optional<TBankAccount> findByUserAndAccountName(User user, String accountName);
}