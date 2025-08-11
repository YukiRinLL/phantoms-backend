package com.phantoms.phantomsbackend.repository.secondary;

import com.phantoms.phantomsbackend.pojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository("secondaryUserRepository")
public interface SecondaryUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(UUID userId);
}