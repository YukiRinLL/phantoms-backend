package com.phantoms.phantomsbackend.repository.primary;

import com.phantoms.phantomsbackend.pojo.entity.primary.ExpeditionaryTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("primaryExpeditionaryTeamRepository")
public interface PrimaryExpeditionaryTeamRepository extends JpaRepository<ExpeditionaryTeam, UUID> {
}



