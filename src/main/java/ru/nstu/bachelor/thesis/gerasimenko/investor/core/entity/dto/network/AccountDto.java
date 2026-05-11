package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import lombok.Builder;

@Builder
public record AccountDto(String name, String type, String status, String accessLevel) {
}
