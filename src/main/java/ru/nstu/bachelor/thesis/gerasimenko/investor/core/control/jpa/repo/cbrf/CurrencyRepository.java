package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.cbrf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.cbrf.Currency;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

    Optional<Currency> findByCodeA3(String codeA3);

    @Modifying
    @Query(value = """
        INSERT INTO cbrf.currencies (code_a3, code_n3, nominal, name_rus, name_eng)
        VALUES (:codeA3, :codeN3, :nominal, :nameRus, :nameEng)
        ON CONFLICT (code_a3) DO NOTHING;
    """, nativeQuery = true)
    void insert(
            @Param("codeA3") String codeA3,
            @Param("codeN3") String codeN3,
            @Param("nominal") Integer nominal,
            @Param("nameRus") String nameRus,
            @Param("nameEng") String nameEng
    );
}