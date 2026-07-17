package com.phantoms.phantomsbackend.service.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.service.OneBotService;
import com.phantoms.phantomsbackend.service.RisingStonesService;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    private String getPhantomGroupId() {
        return systemConfigService.getString("napcat.phantom-group-id", "");
    }

    // UTC+8每天8:00执行
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(fixedRate = 60000) // 每分钟执行一次
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

                                    // 判断是否超过30天未登录
                                    if (lastLoginTimeStr != null
                                        && houseInfoPublish != null
                                        && houseInfoPublish == 1
                                        && houseInfo != null
                                    ) {
                                        LocalDate lastLoginTime = LocalDate.parse(lastLoginTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                        LocalDate now = LocalDate.now();
                                        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastLoginTime, now);

                                        if (daysBetween >= 30) {
                                            if (daysBetween <= 45) {
                                                // 构造提醒信息
                                                String message = String.format("[系统提示]%s 已经%d天未登录，房屋信息：%s", member.getString("character_name"), daysBetween, houseInfo);
                                                // 发送提醒信息到群聊
                                                try {
                                                    oneBotService.sendGroupMessage(message, getPhantomGroupId());
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
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
                // System.out.println("未活跃成员名单：" + inactiveMembers);
                try {
                    oneBotService.sendGroupMessage("未活跃成员名单：" + inactiveMembers, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 12 ? * SAT") // 每周六的 12:00（UTC时间）- 20:00（UTC+8时间）
//    @Scheduled(cron = "0 0 16,12 ? * TUE,SAT") // 每周二和周六的 12:00 和 16:00（UTC时间）- 20:00 和 00:00次日（UTC+8时间）
//    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void sendWeeklyActivities() {
        try {
            String url = "https://cqnews.web.sdo.com/api/news/newsList?gameCode=ff&pageIndex=0&pageSize=99&CategoryCode=7141";

            // 发送HTTP请求获取活动数据
            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonResponse = JSONObject.parseObject(response);

            if (jsonResponse != null && "0".equals(jsonResponse.getString("Code"))) {
                List<JSONObject> activities = jsonResponse.getJSONArray("Data").toJavaList(JSONObject.class);

                // 过滤掉部分活动
                List<JSONObject> filteredActivities = activities.stream()
                    .filter(activity -> {
                        String title = activity.getString("Title");
                        return !"申请超域传送".equals(title)
//                            && !"其他不想发送的标题".equals(title)
                            ;
                    })
                    .collect(Collectors.toList());

                // 发送活动信息到群聊
                if (!filteredActivities.isEmpty()) {
                    // 先发送一个标题消息
                    oneBotService.sendGroupMessage("====== 当前FF14活动一览 ======", getPhantomGroupId());

                    Thread.sleep(1000);

                    for (JSONObject activity : filteredActivities) {
                        String title = activity.getString("Title");
                        String summary = activity.getString("Summary");
                        String imageUrl = activity.getString("HomeImagePath");
                        String link = activity.getString("OutLink");
                        String publishDate = activity.getString("PublishDate");

                        StringBuilder message = new StringBuilder();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            message.append("[CQ:image,file=").append(imageUrl).append("]");
                        }
                        message.append(title).append("\n");
                        message.append("📅").append(publishDate).append("\n");
                        message.append("📝").append(summary).append("\n");
                        message.append("🔗").append(link);

                        oneBotService.sendGroupMessage(message.toString(), getPhantomGroupId());

                        Thread.sleep(1000);
                    }

//                    oneBotService.sendGroupMessageWithDefaultGroup("====== 活动信息发送完毕 ======", getPhantomGroupId());
                } else {
                    oneBotService.sendGroupMessage("本周暂无FF14活动信息", getPhantomGroupId());
                }
            } else {
                oneBotService.sendGroupMessage("获取FF14活动信息失败，请稍后重试", getPhantomGroupId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                oneBotService.sendGroupMessage("获取FF14活动信息时发生错误：" + e.getMessage(), getPhantomGroupId());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}