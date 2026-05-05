package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankActiveAccount;

@Repository
public interface TBankActiveAccountRepository extends JpaRepository<TBankActiveAccount, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO tbank.active_accounts (user_id, account_id) 
            VALUES (:userId, :accountId)
            ON CONFLICT (user_id) DO UPDATE 
                SET account_id = EXCLUDED.account_id
        """, nativeQuery = true)
    void upsert(@Param("userId") Long userId, @Param("accountId") String accountId);
}