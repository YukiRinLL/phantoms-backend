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
    public void handleNotification(String title, String content, String type, String qq, String groupId) {
        // 构建消息内容
        String message = buildMessage(title, content, type);

        // 标志位，用于判断是否需要使用默认值
        boolean hasQQ = qq != null && !qq.isEmpty();
        boolean hasGroupId = groupId != null && !groupId.isEmpty();

        // 如果只提供了QQ号，只发送到个人QQ
        if (hasQQ && !hasGroupId) {
            try {
                napCatQQUtil.sendPrivateMessage(qq, message);
                logger.info("发送消息到个人QQ {} 成功", qq);
            } catch (IOException e) {
                logger.error("发送消息到个人QQ {} 失败: {}", qq, e.getMessage());
            }
        }
        // 如果只提供了群聊ID，只发送到QQ群聊
        else if (!hasQQ && hasGroupId) {
            try {
                napCatQQUtil.sendGroupMessage(groupId, message);
                logger.info("发送消息到QQ群聊 {} 成功", groupId);
            } catch (IOException e) {
                logger.error("发送消息到QQ群聊 {} 失败: {}", groupId, e.getMessage());
            }
        }
        // 如果同时提供了QQ号和群聊ID，都发送
        else if (hasQQ && hasGroupId) {
            // 发送到个人QQ
            try {
                napCatQQUtil.sendPrivateMessage(qq, message);
                logger.info("发送消息到个人QQ {} 成功", qq);
            } catch (IOException e) {
                logger.error("发送消息到个人QQ {} 失败: {}", qq, e.getMessage());
            }

            // 发送到QQ群聊
            try {
                napCatQQUtil.sendGroupMessage(groupId, message);
                logger.info("发送消息到QQ群聊 {} 成功", groupId);
            } catch (IOException e) {
                logger.error("发送消息到QQ群聊 {} 失败: {}", groupId, e.getMessage());
            }
        }
        // 如果都没有提供，使用默认值
        else {
            // 发送到默认个人QQ
            try {
                napCatQQUtil.sendPrivateMessage(DEFAULT_QQ, message);
                logger.info("发送消息到默认个人QQ {} 成功", DEFAULT_QQ);
            } catch (IOException e) {
                logger.error("发送消息到默认个人QQ {} 失败: {}", DEFAULT_QQ, e.getMessage());
            }

            // 发送到默认QQ群聊
            try {
                napCatQQUtil.sendGroupMessage(defaultGroupId, message);
                logger.info("发送消息到默认QQ群聊 {} 成功", defaultGroupId);
            } catch (IOException e) {
                logger.error("发送消息到默认QQ群聊 {} 失败: {}", defaultGroupId, e.getMessage());
            }
        }
    }

    /**
     * 构建消息内容
     */
    private String buildMessage(String title, String content, String type) {
        StringBuilder message = new StringBuilder();
//        message.append("[直播通知](bestlive)\n");
        message.append("title: " + title + "\n");
        message.append("content: " + content + "\n");
        message.append("type: " + getTypeDescription(type) + "\n");
        message.append("timeStamp: " + new java.util.Date());
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
