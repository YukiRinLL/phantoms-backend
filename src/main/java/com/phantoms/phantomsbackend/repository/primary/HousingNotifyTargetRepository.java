package com.phantoms.phantomsbackend.repository.primary;

import com.phantoms.phantomsbackend.pojo.entity.primary.HousingNotifyTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HousingNotifyTargetRepository extends JpaRepository<HousingNotifyTarget, Long> {

    Optional<HousingNotifyTarget> findByName(String name);

    List<HousingNotifyTarget> findByEnabledTrue();

    @Query("SELECT t FROM HousingNotifyTarget t LEFT JOIN FETCH t.servers LEFT JOIN FETCH t.areas LEFT JOIN FETCH t.groups LEFT JOIN FETCH t.sizes WHERE t.enabled = true")
    List<HousingNotifyTarget> findAllEnabledWithDetails();

    @Query("SELECT t FROM HousingNotifyTarget t LEFT JOIN FETCH t.servers LEFT JOIN FETCH t.areas LEFT JOIN FETCH t.groups WHERE t.enabled = true")
    List<HousingNotifyTarget> findAllWithBasicDetails();

    @Query("SELECT t FROM HousingNotifyTarget t LEFT JOIN FETCH t.servers LEFT JOIN FETCH t.areas LEFT JOIN FETCH t.groups LEFT JOIN FETCH t.sizes WHERE t.id = :id")
    Optional<HousingNotifyTarget> findByIdWithDetails(Long id);
}
