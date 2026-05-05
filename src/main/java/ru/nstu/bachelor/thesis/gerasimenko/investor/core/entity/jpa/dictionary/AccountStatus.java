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
@Table(schema = "dictionary", name = "account_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatus {
    @Id
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "description", nullable = false)
    private String description;
}