package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.OperationDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankOperation;


@UtilityClass
public class TBankOperationConverter {

    public static OperationDto convert(TBankOperation operation) {
        return OperationDto.builder()
                .id(operation.getId())
                .parentOperationId(operation.getId())
                .operationState(operation.getState().getDescription())
                .operationType(operation.getOperationType().getDescription())
                .quantity(operation.getQuantity())
                .quantityRest(operation.getQuantityRest())
                .currency(operation.getCurrency())
                .payment(operation.getPaymentValue())
                .date(operation.getOperationDate())
                .instrumentType(operation.getInstrument().getInstrumentType().getDescription())
                .instrumentUid(operation.getInstrument().getUid())
                .trades(operation.getTrades().stream().map(TBankTradeConverter::convert).toList())
                .build();
    }
}