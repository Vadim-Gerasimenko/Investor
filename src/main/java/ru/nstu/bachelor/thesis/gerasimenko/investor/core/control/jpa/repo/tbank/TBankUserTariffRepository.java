package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankUserTariff;

@Repository
public interface TBankUserTariffRepository extends JpaRepository<TBankUserTariff, Long> {
    @Modifying
    @Query(value = """
            INSERT INTO tbank.users_tariffs (user_id, tariff) 
            VALUES (:userId, :tariff)
            ON CONFLICT (user_id) DO UPDATE 
                SET tariff = EXCLUDED.tariff
        """, nativeQuery = true)
    void upsert(@Param("userId") Long userId, @Param("tariff") String tariff);
}