package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TokenDto(String tokenName, LocalDateTime createdAt) {
}
