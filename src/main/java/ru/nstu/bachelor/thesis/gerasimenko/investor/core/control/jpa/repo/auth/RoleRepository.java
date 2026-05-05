package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.RoleType;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(RoleType roleType);
}