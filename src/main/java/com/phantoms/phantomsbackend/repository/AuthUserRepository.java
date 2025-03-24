package com.phantoms.phantomsbackend.repository;

import com.phantoms.phantomsbackend.pojo.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, String> {
    Optional<AuthUser> findByEmail(String email);
}