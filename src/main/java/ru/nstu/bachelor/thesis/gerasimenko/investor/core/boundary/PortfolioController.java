package ru.nstu.bachelor.thesis.gerasimenko.investor.core.boundary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.AccountService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.report.ReportingService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.NameRequestDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting.ExcelReport;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.reporting.Report;


@Slf4j
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final ReportingService reportingService;
    private final AccountService accountService;

    @GetMapping("/report")
    public ResponseEntity<Report> getReport(@AuthenticationPrincipal User user) {
        log.info("to get report: userId=[{}]", user.getId());
        return ResponseEntity.ok().body(reportingService.getReport(user));
    }

    @GetMapping("/report/excel")
    public ResponseEntity<byte[]> getExcelReport(@AuthenticationPrincipal User user) {
        log.info("to get excel report: userId=[{}]", user.getId());
        ExcelReport excelReport = reportingService.getExcelReport(user);
        return ResponseEntity.ok()
                .header("Content-Disposition", String.format("attachment; filename=%s", excelReport.getName()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelReport.getReport());
    }

    @GetMapping("/accounts")
    @Transactional
    public ResponseEntity<?> getAllAccounts(@AuthenticationPrincipal User user) {
        log.info("to get all accounts: userId=[{}]", user.getId());
        return ResponseEntity.ok().body(accountService.getAllAccounts(user));
    }

    @PostMapping("/accounts/activate")
    public ResponseEntity<?> activateAccount(@AuthenticationPrincipal User user, @RequestBody NameRequestDto accountNameDto) {
        log.info("to activate account: userId=[{}], accountName=[{}]", user.getId(), accountNameDto.name());
        return ResponseEntity.ok().body(accountService.activateAccount(user, accountNameDto.name()));
    }
}