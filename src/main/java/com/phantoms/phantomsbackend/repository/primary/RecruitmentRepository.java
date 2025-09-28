package com.phantoms.phantomsbackend.repository.primary;

import com.phantoms.phantomsbackend.pojo.entity.primary.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// RecruitmentRepository.java
// RecruitmentRepository.java
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    // 使用 PostgreSQL 的 ON CONFLICT
    @Modifying
    @Query(value = "INSERT INTO recruitments " +
        "(id, name, description, created_world, created_world_id, home_world, home_world_id, " +
        "category, category_id, duty, min_item_level, slots_filled, slots_available, " +
        "time_left, updated_at, is_cross_world, datacenter) " +
        "VALUES (:#{#recruitment.id}, :#{#recruitment.name}, :#{#recruitment.description}, " +
        ":#{#recruitment.createdWorld}, :#{#recruitment.createdWorldId}, :#{#recruitment.homeWorld}, " +
        ":#{#recruitment.homeWorldId}, :#{#recruitment.category}, :#{#recruitment.categoryId}, " +
        ":#{#recruitment.duty}, :#{#recruitment.minItemLevel}, :#{#recruitment.slotsFilled}, " +
        ":#{#recruitment.slotsAvailable}, :#{#recruitment.timeLeft}, :#{#recruitment.updatedAt}, " +
        ":#{#recruitment.crossWorld}, :#{#recruitment.datacenter}) " +
        "ON CONFLICT (id) DO NOTHING",
        nativeQuery = true)
    void insertIgnore(@Param("recruitment") Recruitment recruitment);
}