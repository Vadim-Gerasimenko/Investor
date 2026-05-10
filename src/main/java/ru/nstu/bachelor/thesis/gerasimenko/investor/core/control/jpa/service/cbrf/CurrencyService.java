package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.cbrf;

import lombok.RequiredArgsConstructor;
import org.apache.xmlbeans.impl.store.Cur;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.cbrf.CurrencyRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.cbrf.Currency;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    private static final String RUB_CODE_A3 = "RUB";

    public Currency findByCodeA3(String codeA3) {
        return currencyRepository.findByCodeA3(codeA3)
                .orElseThrow(() -> new InvestorCoreException(String.format("Currency not found: codeA3=[%s]", codeA3)));
    }

    public Currency findRub() {
        return findByCodeA3(RUB_CODE_A3);
    }
}
