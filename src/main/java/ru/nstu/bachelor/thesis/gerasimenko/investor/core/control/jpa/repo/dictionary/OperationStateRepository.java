package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.OperationState;

@Repository
public interface OperationStateRepository extends JpaRepository<OperationState, String> {
}
