package com.phantoms.phantomsbackend.repository.secondary;

import com.phantoms.phantomsbackend.pojo.entity.secondary.ExpeditionaryTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("secondaryExpeditionaryTeamRepository")
public interface SecondaryExpeditionaryTeamRepository extends JpaRepository<ExpeditionaryTeam, String> {
}