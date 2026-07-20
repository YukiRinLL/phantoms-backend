package com.phantoms.phantomsbackend.repository.primary;

import com.phantoms.phantomsbackend.pojo.entity.primary.HousingNotifyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousingNotifyGroupRepository extends JpaRepository<HousingNotifyGroup, Long> {

    List<HousingNotifyGroup> findByTargetId(Long targetId);

    void deleteByTargetId(Long targetId);
}
