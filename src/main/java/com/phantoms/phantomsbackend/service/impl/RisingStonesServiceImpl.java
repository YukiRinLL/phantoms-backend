package com.phantoms.phantomsbackend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.RisingStonesLoginTool;
import com.phantoms.phantomsbackend.common.utils.RisingStonesUtils;
import com.phantoms.phantomsbackend.service.RisingStonesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class RisingStonesServiceImpl implements RisingStonesService {

    @Autowired
    private RisingStonesLoginTool risingStonesLoginTool;

    private String daoyuToken;
    private String cookie;
    private long tokenObtainTime;

    private synchronized void ensureTokenAndCookie() throws IOException {
        long currentTime = System.currentTimeMillis();
        if (daoyuToken == null
            || cookie == null
            || currentTime - tokenObtainTime > TimeUnit.MINUTES.toMillis(720) // 判断DaoyuToken在12小时后过期
        ) {
            String[] tokenAndCookie = risingStonesLoginTool.getDaoYuTokenAndCookie();
            daoyuToken = tokenAndCookie[0];
            cookie = tokenAndCookie[1];
            tokenObtainTime = currentTime;
        }
    }

    @Override
    public JSONObject getUserInfo(String uuid) throws IOException {
        ensureTokenAndCookie();
        return RisingStonesUtils.getUserInfo(uuid, daoyuToken, cookie);
    }

    @Override
    public JSONObject getGuildInfo(String guildId) throws IOException {
        ensureTokenAndCookie();
        return RisingStonesUtils.getGuildInfo(guildId, daoyuToken, cookie);
    }

    @Override
    public JSONObject getGuildMember(String guildId) throws IOException {
        ensureTokenAndCookie();
        return RisingStonesUtils.getGuildMember(guildId, daoyuToken, cookie);
    }

    @Override
    public JSONObject getGuildMemberDynamic(String guildId, int page, int limit) throws IOException {
        ensureTokenAndCookie();
        return RisingStonesUtils.getGuildMemberDynamic(guildId, page, limit, daoyuToken, cookie);
    }
}