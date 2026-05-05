package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankInstrumentPrice;

import java.util.Optional;

@Repository
public interface TBankInstrumentPriceRepository extends JpaRepository<TBankInstrumentPrice, String> {

    @Query(value = "SELECT tbip.price FROM TBankInstrumentPrice tbip WHERE tbip.instrumentUid = :instrumentUid")
    Optional<Long> getPriceByInstrumentUid(String instrumentUid);
}