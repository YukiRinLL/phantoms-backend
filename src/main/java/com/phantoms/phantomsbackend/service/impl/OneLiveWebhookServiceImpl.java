package com.phantoms.phantomsbackend.service.impl;

import com.phantoms.phantomsbackend.common.utils.NapCatQQUtil;
import com.phantoms.phantomsbackend.service.OneLiveWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OneLiveWebhookServiceImpl implements OneLiveWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(OneLiveWebhookServiceImpl.class);

    @Autowired
    private NapCatQQUtil napCatQQUtil;

    @Value("${napcat.phantom-group-id}")
    private String phantomGroupId;

    @Value("${napcat.default-group-id}")
    private String defaultGroupId;

    @Value("${napcat.crystal-group-id}")
    private String crystalGroupId;

    private static final String DEFAULT_QQ = "944989026";

    @Override
    public void handleNotification(String title, String content, String type, List<String> qq, List<String> groupId) {
        // 如果没有传入QQ号，使用默认值
        if (qq == null || qq.isEmpty()) {
            qq = new ArrayList<>();
            qq.add(DEFAULT_QQ);
        }

        // 如果没有传入群聊ID，使用默认值
        if (groupId == null || groupId.isEmpty()) {
            groupId = new ArrayList<>();
            groupId.add(defaultGroupId);
        }

        // 构建消息内容
        String message = buildMessage(title, content, type);

        // 发送消息到个人QQ
        for (String qqNumber : qq) {
            try {
                napCatQQUtil.sendPrivateMessage(qqNumber, message);
                logger.info("发送消息到个人QQ {} 成功", qqNumber);
            } catch (IOException e) {
                logger.error("发送消息到个人QQ {} 失败: {}", qqNumber, e.getMessage());
            }
        }

        // 发送消息到QQ群聊
        for (String group : groupId) {
            try {
                napCatQQUtil.sendGroupMessage(group, message);
                logger.info("发送消息到QQ群聊 {} 成功", group);
            } catch (IOException e) {
                logger.error("发送消息到QQ群聊 {} 失败: {}", group, e.getMessage());
            }
        }
    }

    /**
     * 构建消息内容
     */
    private String buildMessage(String title, String content, String type) {
        StringBuilder message = new StringBuilder();
        message.append("【一号直播通知】\n");
        message.append("标题: " + title + "\n");
        message.append("内容: " + content + "\n");
        message.append("类型: " + getTypeDescription(type) + "\n");
        message.append("时间: " + new java.util.Date());
        return message.toString();
    }

    /**
     * 获取通知类型的描述
     */
    private String getTypeDescription(String type) {
        switch (type) {
            case "record_start":
                return "录制开始";
            case "record_stop":
                return "录制结束";
            case "disk_low":
                return "磁盘低告警";
            case "inner_fail":
                return "内部错误";
            default:
                return type;
        }
    }
}
