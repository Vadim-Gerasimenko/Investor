package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.InstrumentTradesDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.PortfolioDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.TBankOperationDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankOperation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/*
@UtilityClass
public class PortfolioConverter {

    public static PortfolioDto convert(Map<String, List<List<TBankOperation>>> instrumentsTrades) {
        PortfolioDto portfolioDto = new PortfolioDto(new LinkedList<>());


        for (String instrumentUid : instrumentsTrades.keySet()) {
            List<List<TBankOperation>> instrumentTrades = instrumentsTrades.get(instrumentUid);

            for (List<TBankOperation> trade : instrumentTrades) {

            }

            portfolioDto.instrumentsTrades().add(new InstrumentTradesDto(instrumentUid, ))
        }
    }
}*/
