package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankToken;

import java.util.List;
import java.util.Optional;

@Repository
public interface TBankTokenRepository extends JpaRepository<TBankToken, Long> {

    Optional<TBankToken> findByUserAndTokenName(User user, String tokenName);
}
