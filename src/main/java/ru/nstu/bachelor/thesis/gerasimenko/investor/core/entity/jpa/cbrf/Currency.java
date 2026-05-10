package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.cbrf;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "cbrf", name = "currencies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    @Id
    @Column(name = "code_a3", length = 3, nullable = false)
    private String codeA3;

    @Column(name = "code_n3", length = 3, nullable = false, unique = true)
    private String codeN3;

    @Column(name = "nominal", nullable = false)
    private Integer nominal;

    @Column(name = "name_rus", length = 50, nullable = false)
    private String nameRus;

    @Column(name = "name_eng", length = 50, nullable = false)
    private String nameEng;
}