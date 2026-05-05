package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.tbank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank.TBankActiveToken;

@Repository
public interface TBankActiveTokenRepository extends JpaRepository<TBankActiveToken, Long> {

        @Modifying
        @Query(value = """
            INSERT INTO tbank.active_tokens (user_id, token_id) 
            VALUES (:userId, :tokenId)
            ON CONFLICT (user_id) DO UPDATE 
                SET token_id = EXCLUDED.token_id
        """, nativeQuery = true)
        void upsert(@Param("userId") Long userId, @Param("tokenId") Long tokenId);
}