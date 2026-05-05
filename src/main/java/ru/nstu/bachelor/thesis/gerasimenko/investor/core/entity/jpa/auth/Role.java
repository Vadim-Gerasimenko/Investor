package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.RoleType;

@Entity
@Getter
@Setter
@Table(schema = "auth", name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", unique = true, nullable = false)
    private RoleType name;

    @Override
    public String getAuthority() {
        return name.name();
    }
}