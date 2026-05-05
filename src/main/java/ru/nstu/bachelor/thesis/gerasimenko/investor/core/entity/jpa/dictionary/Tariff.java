package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "dictionary", name = "tariffs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    @Column(name = "tariff", length = 20, nullable = false)
    private String tariff;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstrumentFee> instrumentFees = new ArrayList<>();
}