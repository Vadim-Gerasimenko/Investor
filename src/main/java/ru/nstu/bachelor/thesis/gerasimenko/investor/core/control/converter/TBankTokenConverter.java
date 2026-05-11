package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.TokenDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankToken;

@UtilityClass
public class TBankTokenConverter {

    public static TokenDto convert(TBankToken token) {
        return TokenDto.builder()
                .tokenName(token.getTokenName())
                .createdAt(token.getCreatedAt())
                .build();
    }
}