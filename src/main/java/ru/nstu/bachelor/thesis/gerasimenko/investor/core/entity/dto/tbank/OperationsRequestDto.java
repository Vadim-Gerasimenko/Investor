package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank;

import lombok.Builder;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.tbank.operation.OperationState;

import java.time.LocalDateTime;

@Builder
public record OperationsRequestDto(
        String accountId,
        LocalDateTime from,
        LocalDateTime to,
        OperationState state,
        String figi
) {
}