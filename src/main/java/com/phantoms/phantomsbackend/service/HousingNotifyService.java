package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.entity.primary.HousingNotifyTarget;
import com.phantoms.phantomsbackend.repository.primary.HousingNotifyTargetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class HousingNotifyService {

    private static final Logger logger = LoggerFactory.getLogger(HousingNotifyService.class);

    @Autowired
    private HousingNotifyTargetRepository targetRepository;

    private List<HousingNotifyTarget> cachedTargets = new ArrayList<>();

    @PostConstruct
    public void loadConfig() {
        refreshCache();
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void refreshCache() {
        logger.debug("刷新房屋监控配置缓存");
        try {
            cachedTargets = targetRepository.findAllEnabledWithDetails();
        } catch (Exception e) {
            logger.warn("加载带 sizes 的配置失败，尝试不带 sizes: {}", e.getMessage());
            try {
                cachedTargets = targetRepository.findAllWithBasicDetails();
            } catch (Exception e2) {
                logger.error("加载房屋监控配置失败: {}", e2.getMessage());
                cachedTargets = new ArrayList<>();
                return;
            }
        }
        logger.debug("房屋监控配置缓存刷新完成，共 {} 条", cachedTargets.size());
    }

    public List<HousingNotifyTarget> getAllEnabledTargets() {
        return new ArrayList<>(cachedTargets);
    }

    public Set<String> getAllServerIds() {
        return cachedTargets.stream()
                .flatMap(t -> t.getServers().stream())
                .map(s -> s.getServerId())
                .collect(Collectors.toSet());
    }

    public List<TargetSummary> getTargetSummaries() {
        return cachedTargets.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HousingNotifyTarget getTargetById(Long id) {
        return targetRepository.findByIdWithDetails(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<HousingNotifyTarget> getAllTargets() {
        return targetRepository.findAllEnabledWithDetails();
    }

    @Transactional
    public HousingNotifyTarget createTarget(String name, String description,
                                            List<String> serverIds,
                                            List<Integer> areaIds,
                                            List<String> groupIds,
                                            List<Integer> sizeIds) {
        HousingNotifyTarget target = new HousingNotifyTarget();
        target.setName(name);
        target.setDescription(description);
        target.setEnabled(true);

        for (String serverId : serverIds) {
            target.addServer(serverId);
        }
        for (Integer areaId : areaIds) {
            target.addArea(areaId);
        }
        for (String groupId : groupIds) {
            target.addGroup(groupId);
        }
        // 默认添加 M 和 L 尺寸
        List<Integer> sizes = (sizeIds != null && !sizeIds.isEmpty()) ? sizeIds : List.of(1, 2);
        for (Integer sizeId : sizes) {
            target.addSize(sizeId);
        }

        HousingNotifyTarget saved = targetRepository.save(target);
        refreshCache();
        logger.info("创建房屋监控配置: {}", name);
        return saved;
    }

    @Transactional
    public HousingNotifyTarget updateTarget(Long id, String name, String description,
                                            List<String> serverIds,
                                            List<Integer> areaIds,
                                            List<String> groupIds,
                                            List<Integer> sizeIds) {
        HousingNotifyTarget target = targetRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + id));

        if (name != null && !name.isEmpty()) {
            target.setName(name);
        }
        if (description != null) {
            target.setDescription(description);
        }

        target.clearServers();
        for (String serverId : serverIds) {
            target.addServer(serverId);
        }

        target.clearAreas();
        for (Integer areaId : areaIds) {
            target.addArea(areaId);
        }

        target.clearGroups();
        for (String groupId : groupIds) {
            target.addGroup(groupId);
        }

        target.clearSizes();
        for (Integer sizeId : sizeIds) {
            target.addSize(sizeId);
        }

        HousingNotifyTarget saved = targetRepository.save(target);
        refreshCache();
        logger.info("更新房屋监控配置: {}", id);
        return saved;
    }

    @Transactional
    public void deleteTarget(Long id) {
        targetRepository.deleteById(id);
        refreshCache();
        logger.info("删除房屋监控配置: {}", id);
    }

    @Transactional
    public HousingNotifyTarget toggleTarget(Long id, boolean enabled) {
        HousingNotifyTarget target = targetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + id));
        target.setEnabled(enabled);
        HousingNotifyTarget saved = targetRepository.save(target);
        refreshCache();
        logger.info("{}房屋监控配置: {}", enabled ? "启用" : "禁用", id);
        return saved;
    }

    private TargetSummary toSummary(HousingNotifyTarget target) {
        TargetSummary summary = new TargetSummary();
        summary.setId(target.getId());
        summary.setName(target.getName());
        summary.setDescription(target.getDescription());
        summary.setEnabled(target.getEnabled());
        summary.setServers(target.getServers().stream()
                .map(s -> s.getServerId())
                .collect(Collectors.toList()));
        summary.setAreas(target.getAreas().stream()
                .map(a -> a.getAreaId())
                .collect(Collectors.toList()));
        summary.setGroups(target.getGroups().stream()
                .map(g -> g.getGroupId())
                .collect(Collectors.toList()));
        summary.setSizes(target.getSizes().stream()
                .map(s -> s.getSizeId())
                .collect(Collectors.toList()));
        summary.setCreatedAt(target.getCreatedAt());
        summary.setUpdatedAt(target.getUpdatedAt());
        return summary;
    }

    public static class TargetSummary {
        private Long id;
        private String name;
        private String description;
        private Boolean enabled;
        private List<String> servers;
        private List<Integer> areas;
        private List<String> groups;
        private List<Integer> sizes;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
        public List<String> getServers() { return servers; }
        public void setServers(List<String> servers) { this.servers = servers; }
        public List<Integer> getAreas() { return areas; }
        public void setAreas(List<Integer> areas) { this.areas = areas; }
        public List<String> getGroups() { return groups; }
        public void setGroups(List<String> groups) { this.groups = groups; }
        public List<Integer> getSizes() { return sizes; }
        public void setSizes(List<Integer> sizes) { this.sizes = sizes; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
