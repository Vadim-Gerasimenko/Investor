package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import java.util.List;

public record InstrumentValuesDto(String instrumentUid,
                                  List<InstrumentValueDto> values,
                                  String ticker,
                                  String classCode) {
}