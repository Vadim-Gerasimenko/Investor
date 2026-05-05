package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.AuthResponseDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.AuthToken;

@UtilityClass
public class AuthConverter {

    public static AuthResponseDto convert(AuthToken authToken) {
        return AuthResponseDto.builder()
                .accessToken(authToken.getAccessToken())
                .refreshToken(authToken.getRefreshToken())
                .build();
    }
}