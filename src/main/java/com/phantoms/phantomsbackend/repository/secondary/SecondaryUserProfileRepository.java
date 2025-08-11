package com.phantoms.phantomsbackend.repository.secondary;

import com.phantoms.phantomsbackend.pojo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository("secondaryUserProfileRepository")
public interface SecondaryUserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserId(UUID userId);
    Optional<UserProfile> findByLegacyUserId(UUID legacyUserId);
}