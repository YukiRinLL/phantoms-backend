package com.phantoms.phantomsbackend.service;

import com.phantoms.phantomsbackend.pojo.entity.primary.Recruitment;
import com.phantoms.phantomsbackend.repository.primary.RecruitmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecruitmentService {

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    /**
     * 多条件组合查询，支持模糊查询 name 和 description
     *
     * @param name        名称（模糊查询）
     * @param description 描述（模糊查询）
     * @param homeWorld   所在世界
     * @param category    类别
     * @param minItemLevel 最低装备等级
     * @param isCrossWorld 是否跨服
     * @param datacenter  数据中心
     * @param pageable    分页参数
     * @return 分页结果
     */
    public Page<Recruitment> findByConditions(String name,
                                              String description,
                                              String homeWorld,
                                              String category,
                                              Integer minItemLevel,
                                              Boolean isCrossWorld,
                                              String datacenter,
                                              Pageable pageable) {
        Specification<Recruitment> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 名称模糊查询
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            // 描述模糊查询
            if (description != null && !description.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%"
                ));
            }

            // 其他精确查询条件
            if (homeWorld != null && !homeWorld.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("homeWorld"), homeWorld));
            }

            if (category != null && !category.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            if (minItemLevel != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("minItemLevel"), minItemLevel));
            }

            if (isCrossWorld != null) {
                predicates.add(criteriaBuilder.equal(root.get("crossWorld"), isCrossWorld));
            }

            if (datacenter != null && !datacenter.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("datacenter"), datacenter));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return recruitmentRepository.findAll(spec, pageable);
    }
}