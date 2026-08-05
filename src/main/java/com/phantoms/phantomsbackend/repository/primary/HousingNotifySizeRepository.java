package com.phantoms.phantomsbackend.repository.primary;

import com.phantoms.phantomsbackend.pojo.entity.primary.HousingNotifySize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousingNotifySizeRepository extends JpaRepository<HousingNotifySize, Long> {

    List<HousingNotifySize> findByTargetId(Long targetId);

    void deleteByTargetId(Long targetId);
}
