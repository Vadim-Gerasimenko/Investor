package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankTrade;

@Repository
public interface TBankTradeRepository extends JpaRepository<TBankTrade, String> {
}
