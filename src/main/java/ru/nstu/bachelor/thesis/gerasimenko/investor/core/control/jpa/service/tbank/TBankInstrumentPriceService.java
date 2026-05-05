package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank.TBankInstrumentPriceRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankInstrumentPrice;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TBankInstrumentPriceService {

    private final TBankInstrumentPriceRepository tBankInstrumentPriceRepository;

    public void saveAll(List<TBankInstrumentPrice> instrumentsPrices) {
        tBankInstrumentPriceRepository.saveAllAndFlush(instrumentsPrices);
    }

    public Long getCurrentPrice(String instrumentUid) {
        return tBankInstrumentPriceRepository.getPriceByInstrumentUid(instrumentUid)
                .orElseThrow(() -> new InvestorCoreException(
                        String.format("Instrument price not found: instrumentUid=[%s]", instrumentUid)));
    }
}