package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "dictionary", name = "instrument_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentType {

    @Id
    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "description", nullable = false)
    private String description;
}