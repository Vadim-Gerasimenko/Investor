package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.OperationDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummary {
    private BigDecimal currentCashBalance;
    private List<OperationDto> inputOperations;
    private List<OperationDto> outputOperations;
}
