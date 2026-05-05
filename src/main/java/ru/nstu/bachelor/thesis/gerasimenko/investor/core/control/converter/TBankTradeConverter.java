package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.TBankTradeDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.TradeDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankOperation;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankTrade;

@UtilityClass
public class TBankTradeConverter {

    public static TBankTrade convert(TBankTradeDto tradeDto, TBankOperation operation) {
        return TBankTrade.builder()
                .tradeId(tradeDto.tradeId())
                .operation(operation)
                .tradeDate(tradeDto.dateTime())
                .quantity(tradeDto.quantity())
                .priceCurrency(tradeDto.price().currency())
                .priceValue(MoneyValueConverter.convert(tradeDto.price()))
                .build();
    }

    public static TradeDto convert(TBankTrade trade) {
        return TradeDto.builder()
                .tradeId(trade.getTradeId())
                .dateTime(trade.getTradeDate())
                .quantity(trade.getQuantity())
                .price(trade.getPriceValue())
                .currency(trade.getPriceCurrency())
                .build();
    }
}
