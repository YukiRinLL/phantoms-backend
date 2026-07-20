package com.phantoms.phantomsbackend.repository.primary;

import com.phantoms.phantomsbackend.pojo.entity.primary.HousingNotifyArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousingNotifyAreaRepository extends JpaRepository<HousingNotifyArea, Long> {

    List<HousingNotifyArea> findByTargetId(Long targetId);

    void deleteByTargetId(Long targetId);
}
