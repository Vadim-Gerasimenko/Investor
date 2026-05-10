package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.cbrf;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.cbrf.Rate;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.cbrf.Rate.RateId;


@Repository
public interface RateRepository extends JpaRepository<Rate, RateId> {

}