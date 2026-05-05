package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank.TBankOperationRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankAccount;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankInstrument;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankOperation;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TBankOperationService {

    private final TBankOperationRepository tBankOperationRepository;

    public TBankOperation findById(String id) {
        return tBankOperationRepository.findById(id).orElse(null);
    }

    public TBankOperation save(TBankOperation tBankOperation) {
        return tBankOperationRepository.save(tBankOperation);
    }

    public List<TBankInstrument> findAccountInstruments(TBankAccount account) {
        return tBankOperationRepository.findAccountInstruments(account);
    }

    public List<TBankOperation> findAccountOperationsByInstrument(TBankAccount account, TBankInstrument instrument) {
        return tBankOperationRepository.findAccountOperationsByInstrument(account, instrument);
    }
}
