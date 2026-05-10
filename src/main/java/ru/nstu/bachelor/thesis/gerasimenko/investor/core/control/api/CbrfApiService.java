package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.cbrf.CbrfApiConfig;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.InvestorCoreConfig;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.cbrf.CurrenciesUrlConfig;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.cbrf.RatesUrlConfig;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.HttpInteractionService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.utils.HttpUtils;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.cbrf.CurrenciesDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.cbrf.RatesDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class CbrfApiService {

    private final CbrfApiConfig cbrfApiConfig;

    private final HttpInteractionService httpInteractionService;
    private final XmlMapper xmlMapper;

    private final DateTimeFormatter DATE_FORMATTER;
    private final ZoneId ZONE_ID;

    public CbrfApiService(HttpInteractionService httpInteractionService,
                          InvestorCoreConfig investorCoreConfig,
                          XmlMapper xmlMapper) {
        this.cbrfApiConfig = investorCoreConfig.getCbrfApi();

        this.httpInteractionService = httpInteractionService;
        this.xmlMapper = xmlMapper;

        DATE_FORMATTER = DateTimeFormatter.ofPattern(cbrfApiConfig.getRates().getDatePattern());
        ZONE_ID = ZoneId.of(cbrfApiConfig.getRates().getTimeZone());
    }

    public void loadRates(LocalDateTime date) {
        String formattedDate = date.atZone(ZONE_ID).format(DATE_FORMATTER);
        RatesUrlConfig urlConfig = cbrfApiConfig.getRates();
        String url = HttpUtils.getUrlWithParam(urlConfig.getUrl(), urlConfig.getExactDateQueryParam(), formattedDate);

        log.info("to load rates: date=[{}], url=[{}]", formattedDate, url);

        ResponseEntity<String> response = httpInteractionService.get(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new InvestorCoreException(String.format("Invalid rates response from CBRF API: statusCode=[%d]", response.getStatusCode().value()));
        }


        RatesDto ratesDto = null;
        try {
            ratesDto = xmlMapper.readValue(response.getBody(), RatesDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse rates response", e);
        }

        log.info("from load rates: ratesDto=[{}]", ratesDto);
    }

    public void loadCurrencies() {
        CurrenciesUrlConfig urlConfig = cbrfApiConfig.getCurrencies();

        log.info("to load currencies: url=[{}]", urlConfig.getUrl());
        ResponseEntity<String> response = httpInteractionService.get(urlConfig.getUrl(), String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new InvestorCoreException(String.format("Invalid currencies response from CBRF API: statusCode=[%d]", response.getStatusCode().value()));
        }

        CurrenciesDto currenciesDto = null;
        try {
            currenciesDto = xmlMapper.readValue(response.getBody(), CurrenciesDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse currencies response", e);
        }

        log.info("from load currencies: currenciesDto=[{}]", currenciesDto);
    }
}