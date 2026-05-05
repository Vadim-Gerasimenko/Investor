package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.InvestorCoreException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.auth.RoleRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.enums.RoleType;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.Role;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role findDefaultRole() {
        return roleRepository.findByName(RoleType.ROLE_USER).orElseThrow(() -> {
            log.error("Default user role not found in database: defaultRole=[{}]", RoleType.ROLE_USER.name());
            return new InvestorCoreException(
                    String.format("Default user role not found in database: defaultRole=[%s]", RoleType.ROLE_USER.name()));
        });
    }

    @Override
    public Optional<Role> findRole(RoleType roleType) {
        return roleRepository.findByName(roleType);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
}
