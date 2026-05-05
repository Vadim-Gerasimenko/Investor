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
@Table(schema = "dictionary", name = "operation_states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationState {
    @Id
    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "description", nullable = false)
    private String description;
}
