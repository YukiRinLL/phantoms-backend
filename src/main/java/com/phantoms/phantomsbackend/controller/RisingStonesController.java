package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.service.RisingStonesService;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/risingstones")
public class RisingStonesController {

    @Autowired
    private RisingStonesService risingStonesService;

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/user-info")
    public JSONObject userInfo(@RequestParam String uuid) throws IOException {
        return risingStonesService.getUserInfo(uuid);
    }

    @GetMapping("/guild-info")
    public JSONObject guildInfo(@RequestParam(value = "guildId", defaultValue = "9381138761301105564") String guildId) throws IOException {
        return risingStonesService.getGuildInfo(guildId);
    }

    @GetMapping("/guild-member")
    public JSONObject guildMember(@RequestParam(value = "guildId", defaultValue = "9381138761301105564") String guildId) throws IOException {
        return risingStonesService.getGuildMember(guildId);
    }

    // 更新daoyu_key密钥
    @PostMapping("/update-daoyu-key")
    public JSONObject updateDaoYuKey(@RequestParam String newKey) {
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