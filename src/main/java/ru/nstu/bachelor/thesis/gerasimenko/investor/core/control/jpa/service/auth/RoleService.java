package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.RoleType;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    Role findDefaultRole();

    Optional<Role> findRole(RoleType roleType);

    List<Role> findAllRoles();
}