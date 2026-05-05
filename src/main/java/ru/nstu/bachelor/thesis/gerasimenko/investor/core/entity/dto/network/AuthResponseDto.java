package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import lombok.Builder;
import lombok.ToString;

@Builder
public record AuthResponseDto(

        @ToString.Exclude
        String accessToken,

        @ToString.Exclude
        String refreshToken) {
}