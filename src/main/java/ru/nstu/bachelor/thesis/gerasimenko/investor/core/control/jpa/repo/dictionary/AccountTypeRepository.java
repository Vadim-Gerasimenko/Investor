package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.dictionary;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.AccountType;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, String> {
}
