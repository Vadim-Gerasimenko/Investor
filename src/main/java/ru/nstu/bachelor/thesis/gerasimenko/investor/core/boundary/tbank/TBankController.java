package ru.nstu.bachelor.thesis.gerasimenko.investor.core.boundary.tbank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank.TBankAccountService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.api.TBankApiService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.TBankTokenService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.reporting.ReportingService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting.Report;

import java.time.LocalDateTime;
import static ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.reporting.ExcelReportGenerator.DATE_FORMATTER;

@Slf4j
@RestController
@RequestMapping("api/tbank")
@RequiredArgsConstructor
public class TBankController {

    private final TBankTokenService tBankTokenService;
    private final TBankAccountService tBankAccountService;
    private final TBankApiService tBankApiService;
    private final ReportingService reportingService;

    @PostMapping("/token")
    public void addToken(@AuthenticationPrincipal User user, @RequestBody TBankTokenDto tokenDto) {
        tBankTokenService.addToken(user, tokenDto.token(), tokenDto.name());
    }

    @PostMapping("/token/activate")
    public void activateToken(@AuthenticationPrincipal User user, @RequestBody ActivateRequestDto tokenNameDto) {
        tBankTokenService.activateToken(user, tokenNameDto.name());
        tBankApiService.syncTBankAccountData(user);
    }

    @PostMapping("/accounts")
    public ResponseEntity<TBankAccountsDto> updateAccounts(@AuthenticationPrincipal User user) {
        tBankApiService.syncTBankAccountData(user);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/accounts/activate")
    public void activateAccount(@AuthenticationPrincipal User user, @RequestBody ActivateRequestDto accountNameDto) {
        tBankAccountService.activateAccount(user, accountNameDto.name());
        tBankApiService.updateOperations(user, accountNameDto.name());
    }

    @PostMapping("/instruments")
    public ResponseEntity<TBankInstrumentsDto> updateInstruments(@AuthenticationPrincipal User user) {
        tBankApiService.updateInstruments(user);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/operations")
    public ResponseEntity<TBankOperationsDto> updateOperations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(tBankApiService.updateOperations(user));
    }

    @GetMapping("/report")
    public ResponseEntity<Report> getReport(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(reportingService.getReport(user));
    }

    @GetMapping("/report/excel")
    public ResponseEntity<byte[]> getExcelReport(@AuthenticationPrincipal User user) {
        byte[] excelReport = reportingService.getExcelReport(user);
        String fileName = String.format("report_%s_%s.xlsx",
                user.getId(),
                LocalDateTime.now().format(DATE_FORMATTER));

        return ResponseEntity.ok()
                .header("Content-Disposition", String.format("attachment; filename=%s", fileName))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelReport);
    }
}