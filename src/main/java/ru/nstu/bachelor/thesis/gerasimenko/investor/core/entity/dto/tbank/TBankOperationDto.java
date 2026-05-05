package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import lombok.Builder;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.InstrumentType;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.OperationState;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.OperationType;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TBankOperationDto(
        String id,
        String parentOperationId,
        String currency,
        MoneyValueDto payment,
        MoneyValueDto price,
        OperationState state,
        Long quantity,
        Long quantityRest,
        String figi,
        InstrumentType instrumentType,
        LocalDateTime date,
        String type,
        OperationType operationType,
        List<TBankTradeDto> trades,
        String assetUid,
        String positionUid,
        String instrumentUid,
        List<ChildOperationDto> childOperations
) {
}
