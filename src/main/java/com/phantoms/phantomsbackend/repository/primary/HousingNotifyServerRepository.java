package com.phantoms.phantomsbackend.repository.primary;

import com.phantoms.phantomsbackend.pojo.entity.primary.HousingNotifyServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousingNotifyServerRepository extends JpaRepository<HousingNotifyServer, Long> {

    List<HousingNotifyServer> findByTargetId(Long targetId);

    void deleteByTargetId(Long targetId);
}
