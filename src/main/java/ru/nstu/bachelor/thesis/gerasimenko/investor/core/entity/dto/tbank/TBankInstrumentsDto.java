package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TBankInstrumentsDto(List<InstrumentDto> instruments) {
}