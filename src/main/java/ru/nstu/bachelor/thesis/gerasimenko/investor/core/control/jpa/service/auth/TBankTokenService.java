package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank.TBankActiveTokenRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank.TBankTokenRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankActiveToken;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankToken;

@Slf4j
@Service
@RequiredArgsConstructor
public class TBankTokenService {

    private final TBankTokenRepository tBankTokenRepository;
    private final TBankActiveTokenRepository tBankActiveTokenRepository;

    @Transactional
    public void addToken(User user, String tokenName, String token) {
        TBankToken tBankToken = new TBankToken(user, tokenName, token);
        tBankTokenRepository.save(tBankToken);
    }

    public TBankToken getActiveToken(User user) {
        return tBankActiveTokenRepository.findById(user.getId())
                .orElseThrow(() -> new InvestorCoreException("No active token found"))
                .getToken();
    }

    private TBankToken getTokenByName(User user, String tokenName) {
        return tBankTokenRepository.findByUserAndTokenName(user, tokenName).orElseThrow(() -> new InvestorCoreException(
                String.format("T-Bank token with specified name not found: tokenName=[%s]", tokenName)));
    }

    @Transactional
    public void activateToken(User user, String tokenName) {
        TBankToken token = getTokenByName(user, tokenName);
        tBankActiveTokenRepository.upsert(user.getId(), token.getId());
    }
}