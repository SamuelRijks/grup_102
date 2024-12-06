package com.tecnocampus.LS2.protube_back.persistence;

import com.tecnocampus.LS2.protube_back.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
