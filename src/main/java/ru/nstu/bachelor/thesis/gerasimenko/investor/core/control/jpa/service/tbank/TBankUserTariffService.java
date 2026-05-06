package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.tbank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank.TBankUserTariffRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.Tariff;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankUserTariff;

@Slf4j
@Service
@RequiredArgsConstructor
public class TBankUserTariffService {

    private final TBankUserTariffRepository tBankUserTariffRepository;

    public void updateUserTariff(User user, Tariff tariff) {
        tBankUserTariffRepository.upsert(user.getId(), tariff.getTariff());
    }

    public Tariff getUserTariff(User user) {
        return tBankUserTariffRepository.findById(user.getId()).orElseThrow(
                () -> new InvestorCoreException(String.format("User tariff not found: userId=[%s]", user.getId()))
        ).getTariff();
    }
}