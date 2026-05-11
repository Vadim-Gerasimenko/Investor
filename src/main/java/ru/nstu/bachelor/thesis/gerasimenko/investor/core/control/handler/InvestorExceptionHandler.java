package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.AccountNotFoundException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.UserNotFoundException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.UserUnauthorizedException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.HttpResponse;

@Slf4j
@ControllerAdvice
public class InvestorExceptionHandler {

    @ExceptionHandler(exception = {UserUnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<?> catchUnauthorizedException(InvestorCoreException e) {
        log.warn("Unauthorized Exception caught: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new HttpResponse<>(HttpStatus.UNAUTHORIZED.name(), e.getMessage()));
    }

    @ExceptionHandler(exception = {UserNotFoundException.class, AccountNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<?> catchNotFoundException(InvestorCoreException e) {
        log.warn("Not Found Exception caught: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new HttpResponse<>(HttpStatus.NOT_FOUND.name(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> catchInvestorCoreException(InvestorCoreException e) {
        log.warn("Investor Core Exception caught: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new HttpResponse<>(HttpStatus.BAD_REQUEST.name(), e.getMessage()));
    }
}
