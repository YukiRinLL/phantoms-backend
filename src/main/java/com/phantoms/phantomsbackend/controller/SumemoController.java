//package com.phantoms.phantomsbackend.controller;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//
//import com.phantoms.phantomsbackend.common.utils.SumemoUtils;
//import com.phantoms.phantomsbackend.service.RisingStonesService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//@RestController
//@RequestMapping("/api/sumemo")
//public class SumemoController {
//    @Autowired
//    private RisingStonesService risingStonesService;
//
//    private static final Logger logger = Logger.getLogger(SumemoController.class.getName());
//    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
//
//    /**
//     * 查询所有公会成员的副本攻略情况
//     * @param guildIds 公会ID列表，多个ID用逗号分隔
//     * @param zoneId 副本ID，支持的副本ID：1321, 1323, 1325, 1327，不指定则默认查询所有4个副本
//     * @return 所有成员的副本攻略情况
//     */
//    @GetMapping("/guild-member-duties")
//    public JSONObject getGuildMemberDuties(
//            @RequestParam(value = "guildIds", defaultValue = "9381138761301105564") String guildIds,
//            @RequestParam(required = false) Integer zoneId) throws IOException, InterruptedException {
//
//        // 定义副本ID列表
//        int[] zoneIds;
//        if (zoneId != null) {
//            // 验证单个副本ID是否合法
//            if (zoneId != 1321 && zoneId != 1323 && zoneId != 1325 && zoneId != 1327) {
//                JSONObject errorResult = new JSONObject();
//                errorResult.put("success", false);
//                errorResult.put("message", "不支持的副本ID，支持的副本ID：1321, 1323, 1325, 1327");
//                return errorResult;
//            }
//            zoneIds = new int[]{zoneId};
//        } else {
//            // 默认查询所有4个副本
//            zoneIds = new int[]{1321, 1323, 1325, 1327};
//        }
//
//        // 将公会ID字符串分割成多个ID
//        String[] guildIdArray = guildIds.split(",");
//
//        // 并行获取每个成员的副本攻略信息
//        CompletionService<JSONObject> completionService = new ExecutorCompletionService<>(executorService);
//
//        // 计数器，用于跟踪已提交的任务数量
//        int taskCount = 0;
//
//        // 遍历每个公会ID获取成员列表
//        for (String guildId : guildIdArray) {
//            // 去除空格
//            guildId = guildId.trim();
//            if (guildId.isEmpty()) continue;
//
//            // 获取公会成员
//            JSONObject guildMemberResult = risingStonesService.getGuildMember(guildId);
//            JSONObject data = guildMemberResult.getJSONObject("data");
//            JSONArray members = data.getJSONArray("registered");
//
//            // 提交任务获取每个成员的副本攻略信息
//            for (int i = 0; i < members.size(); i++) {
//                JSONObject member = members.getJSONObject(i);
//
//                // 遍历每个副本ID
//                for (int currentZoneId : zoneIds) {
//                    int finalCurrentZoneId = currentZoneId; // 用于lambda表达式
//                    completionService.submit(() -> {
//                        try {
//                            String playerName = member.getString("character_name");
//                            String serverName = member.getString("group_name");
//
//                            // 获取玩家的最佳攻略信息
//                            JSONObject dutyInfo = SumemoUtils.getPlayerBestDuty(playerName, serverName, finalCurrentZoneId);
//
//                            // 构建结果对象
//                            JSONObject memberDuty = new JSONObject();
//                            memberDuty.put("memberInfo", member);
//                            memberDuty.put("zoneId", finalCurrentZoneId);
//
//                            // 获取副本名称
//                            String dutyName;
//                            try {
//                                dutyName = SumemoUtils.getDutyName(finalCurrentZoneId);
//                            } catch (IOException e) {
//                                logger.log(Level.WARNING, "获取副本名称失败: " + e.getMessage());
//                                dutyName = "未知副本";
//                            }
//                            memberDuty.put("dutyName", dutyName);
//
//                            memberDuty.put("dutyInfo", dutyInfo);
//
//                            return memberDuty;
//                        } catch (IOException e) {
//                            logger.log(Level.SEVERE, "获取玩家副本攻略信息失败: " + e.getMessage(), e);
//                            JSONObject errorMember = new JSONObject();
//                            errorMember.put("memberInfo", member);
//                            errorMember.put("zoneId", finalCurrentZoneId);
//                            errorMember.put("error", e.getMessage());
//                            return errorMember;
//                        }
//                    });
//                    taskCount++;
//                }
//            }
//        }
//
//        // 收集结果到Map中，使用玩家UUID作为key
//        Map<String, JSONObject> memberMap = new HashMap<>();
//        for (int i = 0; i < taskCount; i++) {
//            try {
//                Future<JSONObject> future = completionService.take();
//                JSONObject result = future.get();
//
//                // 获取玩家信息
//                JSONObject memberInfo = result.getJSONObject("memberInfo");
//                String uuid = memberInfo.getString("uuid");
//
//                // 获取玩家的记录，如果不存在则创建
//                JSONObject memberRecord;
//                if (memberMap.containsKey(uuid)) {
//                    memberRecord = memberMap.get(uuid);
//                } else {
//                    memberRecord = new JSONObject();
//                    memberRecord.put("memberInfo", memberInfo);
//                    memberRecord.put("duties", new JSONArray());
//                    memberMap.put(uuid, memberRecord);
//                }
//
//                // 获取副本信息
//                JSONArray duties = memberRecord.getJSONArray("duties");
//                JSONObject dutyInfo = new JSONObject();
//                dutyInfo.put("zoneId", result.getInteger("zoneId"));
//                dutyInfo.put("dutyName", result.getString("dutyName"));
//                dutyInfo.put("dutyData", result.getJSONObject("dutyInfo"));
//
//                // 添加副本信息到玩家记录中
//                duties.add(dutyInfo);
//
//            } catch (ExecutionException e) {
//                logger.log(Level.SEVERE, "处理玩家副本攻略信息失败: " + e.getMessage(), e);
//            }
//        }
//
//        // 将Map转换为JSONArray
//        JSONArray memberDuties = new JSONArray();
//        for (JSONObject memberRecord : memberMap.values()) {
//            memberDuties.add(memberRecord);
//        }
//
//        // 构建最终结果
//        JSONObject result = new JSONObject();
//        result.put("success", true);
//        result.put("total", memberDuties.size());
//        result.put("data", memberDuties);
//
//        // 如果指定了单个副本ID，则添加副本信息
//        if (zoneIds.length == 1) {
//            try {
//                String dutyName = SumemoUtils.getDutyName(zoneIds[0]);
//                result.put("dutyName", dutyName);
//            } catch (IOException e) {
//                logger.log(Level.WARNING, "获取副本名称失败: " + e.getMessage());
//                result.put("dutyName", "未知副本");
//            }
//            result.put("zoneId", zoneIds[0]);
//        } else {
//            // 如果查询了多个副本，则添加副本ID列表
//            JSONArray zoneIdArray = new JSONArray();
//            for (int id : zoneIds) {
//                zoneIdArray.add(id);
//            }
//            result.put("zoneIds", zoneIdArray);
//            result.put("message", "查询了所有支持的副本");
//        }
//
//        return result;
//    }
//
//    /**
//     * 查询单个玩家的副本攻略情况
//     * @param playerName 玩家名称
//     * @param serverName 服务器名称
//     * @param zoneId 副本ID
//     * @return 玩家的副本攻略情况
//     */
//    @GetMapping("/player-duty")
//    public JSONObject getPlayerDuty(
//            @RequestParam String playerName,
//            @RequestParam String serverName,
//            @RequestParam int zoneId) throws IOException {
//
//        // 验证副本ID是否合法
//        if (zoneId != 1321 && zoneId != 1323 && zoneId != 1325 && zoneId != 1327) {
//            JSONObject errorResult = new JSONObject();
//            errorResult.put("success", false);
//            errorResult.put("message", "不支持的副本ID，支持的副本ID：1321, 1323, 1325, 1327");
//            return errorResult;
//        }
//
//        JSONObject dutyInfo = SumemoUtils.getPlayerBestDuty(playerName, serverName, zoneId);
//
//        JSONObject result = new JSONObject();
//        result.put("success", true);
//        result.put("data", dutyInfo);
//
//        try {
//            String dutyName = SumemoUtils.getDutyName(zoneId);
//            result.put("dutyName", dutyName);
//        } catch (IOException e) {
//            logger.log(Level.WARNING, "获取副本名称失败: " + e.getMessage());
//            result.put("dutyName", "未知副本");
//        }
//
//        result.put("zoneId", zoneId);
//
//        return result;
//    }
//}