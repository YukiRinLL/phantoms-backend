package com.phantoms.phantomsbackend.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

public interface RisingStonesService {

    JSONObject getUserInfo(String uuid) throws IOException;

    JSONObject getGuildInfo(String guildId) throws IOException;

    JSONObject getGuildMember(String guildId) throws IOException;

    JSONObject getGuildMemberDynamic(String guildId, int page, int limit) throws IOException;
}