package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.pojo.entity.primary.Recruitment;
import com.phantoms.phantomsbackend.service.RecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruitments")
@Tag(name = "Recruitment", description = "招募信息相关接口")
public class RecruitmentController {

    @Autowired
    private RecruitmentService recruitmentService;

    @GetMapping("/search")
    @Operation(
            summary = "多条件组合查询招募信息",
            description = "支持按名称、描述、世界、类别、装备等级、跨服、数据中心等条件查询招募信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功")
            }
    )
    public ResponseEntity<Page<Recruitment>> searchRecruitments(
            @Parameter(description = "名称（模糊匹配）") @RequestParam(required = false) String name,
            @Parameter(description = "描述（模糊匹配）") @RequestParam(required = false) String description,
            @Parameter(description = "所在世界") @RequestParam(required = false) String homeWorld,
            @Parameter(description = "类别") @RequestParam(required = false) String category,
            @Parameter(description = "最低装备等级") @RequestParam(required = false) Integer minItemLevel,
            @Parameter(description = "是否跨服") @RequestParam(required = false) Boolean isCrossWorld,
            @Parameter(description = "数据中心") @RequestParam(required = false) String datacenter,
            @Parameter(description = "页码，默认0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小，默认10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段，默认updatedAt") @RequestParam(defaultValue = "updatedAt") String sortBy,
            @Parameter(description = "排序方向，默认DESC") @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("DESC") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Recruitment> result = recruitmentService.findByConditions(
                name, description, homeWorld, category, minItemLevel, isCrossWorld, datacenter, pageable);

        return ResponseEntity.ok(result);
    }
}
