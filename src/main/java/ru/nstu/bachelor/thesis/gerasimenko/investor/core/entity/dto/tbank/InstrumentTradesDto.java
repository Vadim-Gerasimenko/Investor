package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import java.util.List;

public record InstrumentTradesDto(String instrumentUid, List<InstrumentTrade> trades) {
}
