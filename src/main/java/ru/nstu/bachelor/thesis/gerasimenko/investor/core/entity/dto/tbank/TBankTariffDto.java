package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import java.util.List;

public record TBankTariffDto(String premStatus,
                             String qualStatus,
                             List<String> qualifiedForWorkWith,
                             String tariff,
                             String userId,
                             String riskLevelCode) {
}