package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.service.RisingStonesService;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/risingstones")
@Tag(name = "Rising Stones", description = "石之家相关接口")
public class RisingStonesController {

    @Autowired
    private RisingStonesService risingStonesService;

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/user-info")
    @Operation(
            summary = "获取用户信息",
            description = "根据UUID获取石之家用户信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取用户信息成功")
            }
    )
    public JSONObject userInfo(
            @Parameter(description = "用户UUID", required = true) @RequestParam String uuid) throws IOException {
        return risingStonesService.getUserInfo(uuid);
    }

    @GetMapping("/guild-info")
    @Operation(
            summary = "获取部队信息",
            description = "根据部队ID获取石之家部队信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取部队信息成功")
            }
    )
    public JSONObject guildInfo(
            @Parameter(description = "部队ID，默认为拂晓之间") @RequestParam(value = "guildId", defaultValue = "9381138761301105564") String guildId) throws IOException {
        return risingStonesService.getGuildInfo(guildId);
    }

    @GetMapping("/guild-member")
    @Operation(
            summary = "获取部队成员",
            description = "根据部队ID获取石之家部队成员列表",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取部队成员成功")
            }
    )
    public JSONObject guildMember(
            @Parameter(description = "部队ID，默认为拂晓之间") @RequestParam(value = "guildId", defaultValue = "9381138761301105564") String guildId) throws IOException {
        return risingStonesService.getGuildMember(guildId);
    }

    @GetMapping("/guild-member-dynamic")
    @Operation(
            summary = "获取部队成员动态",
            description = "获取部队成员的动态列表",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取部队成员动态成功")
            }
    )
    public JSONObject guildMemberDynamic(
            @Parameter(description = "部队ID，默认为拂晓之间") @RequestParam(value = "guildId", defaultValue = "9381138761301105564") String guildId,
            @Parameter(description = "页码，默认1") @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "每页数量，默认10") @RequestParam(value = "limit", defaultValue = "10") int limit) throws IOException {
        return risingStonesService.getGuildMemberDynamic(guildId, page, limit);
    }

    @PostMapping("/update-daoyu-key")
    @Operation(
            summary = "更新叨鱼密钥",
            description = "更新系统中的叨鱼Key密钥",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功或失败")
            }
    )
    public JSONObject updateDaoYuKey(
            @Parameter(description = "新的叨鱼密钥", required = true) @RequestParam String newKey) {
        JSONObject result = new JSONObject();
        try {
            systemConfigService.updateDaoYuKey(newKey);
            result.put("success", true);
            result.put("message", "daoyu_key更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "daoyu_key更新失败: " + e.getMessage());
        }
        return result;
    }
}
