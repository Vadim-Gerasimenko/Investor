package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.api.TBankApiService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.TBankTokenConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.TariffConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.TBankTokenService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankUserTariffService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.AllTokensDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.TariffDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.TokenDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.TBankTokenDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.Tariff;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankToken;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TBankUserDetailsService {

    private final TBankUserTariffService tBankUserTariffService;
    private final TBankTokenService tBankTokenService;
    private final TBankApiService tBankApiService;

    @Transactional(readOnly = true)
    public TariffDto getTariffInfo(User user) {
        Tariff tariff = tBankUserTariffService.getUserTariff(user);
        return TariffConverter.convert(tariff);
    }

    @Transactional(readOnly = true)
    public AllTokensDto getAllTokensInfo(User user) {
        List<TokenDto> tokensDto = tBankTokenService.getAllTokens(user).stream()
                .map(TBankTokenConverter::convert)
                .toList();
        TokenDto activeTokenDto = TBankTokenConverter.convert(tBankTokenService.getActiveToken(user));
        return new AllTokensDto(activeTokenDto, tokensDto);
    }

    @Transactional
    public TokenDto addToken(User user, TBankTokenDto tokenDto) {
        TBankToken token = tBankTokenService.addToken(user, tokenDto.token(), tokenDto.name());
        activateToken(user, tokenDto.name());
        return TBankTokenConverter.convert(token);
    }

    @Transactional
    public void removeToken(User user, String tokenName) {
        tBankTokenService.removeToken(user, tokenName);
    }

    @Transactional
    public TokenDto activateToken(User user, String tokenName) {
        TBankToken token = tBankTokenService.activateToken(user, tokenName);
        tBankApiService.syncTBankAccountData(user);
        tBankApiService.updateAccounts(user);
        return TBankTokenConverter.convert(token);
    }
}
