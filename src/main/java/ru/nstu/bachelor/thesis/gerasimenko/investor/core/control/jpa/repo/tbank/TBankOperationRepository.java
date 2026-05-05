package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankAccount;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankInstrument;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankOperation;

import java.util.List;

@Repository
public interface TBankOperationRepository extends JpaRepository<TBankOperation, String> {

    @Query("""
            SELECT DISTINCT tbo.instrument FROM TBankOperation tbo
            WHERE tbo.account = :account
              AND tbo.instrument IS NOT NULL
            """)
    List<TBankInstrument> findAccountInstruments(@Param("account") TBankAccount account);

    @Query("""
            SELECT tbo FROM TBankOperation tbo
            WHERE tbo.account = :account
              AND tbo.instrument = :instrument
            ORDER BY tbo.operationDate
            """)
    List<TBankOperation> findAccountOperationsByInstrument(@Param("account") TBankAccount account,
                                                           @Param("instrument") TBankInstrument instrument);
}