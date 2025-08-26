package com.phantoms.phantomsbackend.service.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.service.OneBotService;
import com.phantoms.phantomsbackend.service.RisingStonesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GuildMemberReminderScheduler {

    @Autowired
    private RisingStonesService risingStonesService;

    @Autowired
    private OneBotService oneBotService;

//    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨0点执行
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void remindInactiveMembers() {
        try {
            // 获取公会成员信息
            JSONObject guildMemberResponse = risingStonesService.getGuildMember("9381138761301105564");
            if (guildMemberResponse != null && guildMemberResponse.getInteger("code") == 10000) {
                List<JSONObject> registeredMembers = guildMemberResponse.getJSONObject("data").getJSONArray("registered").toJavaList(JSONObject.class);
                List<String> inactiveMembers = registeredMembers.stream()
                        .map(member -> {
                            String uuid = member.getString("uuid");
                            if (uuid != null) {
                                // 获取用户详细信息
                                JSONObject userInfoResponse = null;
                                try {
                                    userInfoResponse = risingStonesService.getUserInfo(uuid);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                if (userInfoResponse != null && userInfoResponse.getInteger("code") == 10000) {
                                    JSONObject userInfo = userInfoResponse.getJSONObject("data");
                                    List<JSONObject> characterDetails = userInfo.getJSONArray("characterDetail").toJavaList(JSONObject.class);
                                    if (!characterDetails.isEmpty()) {
                                        JSONObject characterDetail = characterDetails.get(0);
                                        String lastLoginTimeStr = characterDetail.getString("last_login_time");
                                        Integer houseInfoPublish = userInfo.getInteger("house_info_publish");
                                        String houseInfo = characterDetail.getString("house_info");

                                        // 判断是否超过25天未登录
                                        if (lastLoginTimeStr != null
                                                && houseInfoPublish != null
                                                && houseInfoPublish == 1
                                                && houseInfo != null
                                        ) {
                                            LocalDate lastLoginTime = LocalDate.parse(lastLoginTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                            LocalDate now = LocalDate.now();
                                            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastLoginTime, now);

                                            if (daysBetween >= 25) {
                                                // 构造提醒信息
                                                String message = String.format("[系统提示]%s 已经超过25天未登录，房屋信息：%s", member.getString("character_name"), houseInfo);
                                                // 发送提醒信息到群聊
                                                try {
                                                    oneBotService.sendGroupMessageWithDefaultGroup(message,"787909466");
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                                return member.getString("character_name");
                                            }
                                        }
                                    }
                                }
                            }
                            return null;
                        })
                        .filter(name -> name != null)
                        .collect(Collectors.toList());

                // 打印未活跃成员名单（可选）
                System.out.println("未活跃成员名单：" + inactiveMembers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}