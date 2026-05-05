package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank.TBankInstrumentRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankInstrument;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TBankInstrumentService {

    private final TBankInstrumentRepository tBankInstrumentRepository;

    public List<String> findAllUids() {
        return tBankInstrumentRepository.findAllUids();
    }

    public Optional<TBankInstrument> findById(String id) {
        return tBankInstrumentRepository.findById(id);
    }

    public List<TBankInstrument> findByFigi(String figi) {
        return tBankInstrumentRepository.findByFigi(figi);

    }
    public TBankInstrument save(TBankInstrument bankInstrument) {
        return tBankInstrumentRepository.save(bankInstrument);
    }

    public void saveAll(List<TBankInstrument> bankInstruments) {
        tBankInstrumentRepository.saveAllAndFlush(bankInstruments);
    }
}