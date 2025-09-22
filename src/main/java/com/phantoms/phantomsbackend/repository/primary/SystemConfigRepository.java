package com.phantoms.phantomsbackend.repository.primary;

import com.phantoms.phantomsbackend.pojo.entity.primary.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, String> {

    // 根据 key 查询 SystemConfig
    Optional<SystemConfig> findByKey(String key);

    // 根据 key 更新 value 和 description
    @Modifying
    @Transactional
    @Query("UPDATE SystemConfig sc SET sc.value = :value, sc.description = :description WHERE sc.key = :key")
    int updateByKey(String key, String value, String description);
}