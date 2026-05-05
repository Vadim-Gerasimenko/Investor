package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "dictionary", name = "operation_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationType {
    @Id
    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "category", length = 50)
    private String category;
}