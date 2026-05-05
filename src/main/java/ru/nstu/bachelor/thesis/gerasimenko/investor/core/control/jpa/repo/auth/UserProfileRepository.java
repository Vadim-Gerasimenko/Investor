package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}