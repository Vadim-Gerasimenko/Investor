package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankInstrument;

import java.util.List;
import java.util.Optional;

@Repository
public interface TBankInstrumentRepository extends JpaRepository<TBankInstrument, String> {

    @Query("SELECT tbi.uid FROM TBankInstrument tbi")
    List<String> findAllUids();

    List<TBankInstrument> findByFigi(String figi);
}