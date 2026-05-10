package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.cbrf;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.cbrf.RateRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.cbrf.RatesDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.cbrf.Currency;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.cbrf.Rate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RateService {

    private final RateRepository rateRepository;
    private final CurrencyService currencyService;

    public List<Rate> saveAll(List<Rate> rates) {
        List<Rate> ratesToSave = rates.stream()
                .filter(rate -> !rateRepository.existsById(rate.getId()))
                .collect(Collectors.toList());
        return rateRepository.saveAll(ratesToSave);
    }
/*
    public List<Rate> calculateRates(RatesDto ratesDto) {
        List<Rate> rates = new ArrayList<>();

        LocalDate startDate = ratesDto.getDate();
        Currency rubCurrency = currencyService.findRub();

        ratesDto.getRates().forEach(rateDto -> {
            Rate.builder()
                    .id(Rate.RateId.builder()
                            .startDate(startDate)
                            .currencyFrom()
                            .currencyTo()
                            .build())
        });
    }*/
}