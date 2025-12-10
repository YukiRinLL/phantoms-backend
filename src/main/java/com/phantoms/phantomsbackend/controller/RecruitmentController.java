package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.entity.primary.Recruitment;
import com.phantoms.phantomsbackend.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruitments")
public class RecruitmentController {

    @Autowired
    private RecruitmentService recruitmentService;

    /**
     * 多条件组合查询招募信息
     *
     * @param name        名称（模糊）
     * @param description 描述（模糊）
     * @param homeWorld   所在世界
     * @param category    类别
     * @param minItemLevel 最低装备等级
     * @param isCrossWorld 是否跨服
     * @param datacenter  数据中心
     * @param page        页码，默认 0
     * @param size        每页大小，默认 10
     * @param sortBy      排序字段，默认 updatedAt
     * @param direction   排序方向，默认 DESC
     * @return 分页结果
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Recruitment>> searchRecruitments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String homeWorld,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minItemLevel,
            @RequestParam(required = false) Boolean isCrossWorld,
            @RequestParam(required = false) String datacenter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("DESC") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Recruitment> result = recruitmentService.findByConditions(
                name, description, homeWorld, category, minItemLevel, isCrossWorld, datacenter, pageable);

        return ResponseEntity.ok(result);
    }
}