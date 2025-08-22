package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.service.RisingStonesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/risingstones")
public class RisingStonesController {

    @Autowired
    private RisingStonesService risingStonesService;

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
}