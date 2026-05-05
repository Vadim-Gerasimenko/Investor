package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OperationDto(String id,
                           String parentOperationId,
                           String currency,
                           Long payment,
                           String operationState,
                           Long quantity,
                           Long quantityRest,
                           String instrumentType,
                           LocalDateTime date,
                           String operationType,
                           List<TradeDto> trades,
                           String instrumentUid
) {
}
