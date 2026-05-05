package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank.TBankTradeRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankTrade;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TBankTradeService {

    private final TBankTradeRepository tBankTradeRepository;

    public List<TBankTrade> saveAll(List<TBankTrade> trades) {
        return tBankTradeRepository.saveAll(trades);
    }
}