package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.service.OneLiveWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook/onelive")
@Tag(name = "OneLive Webhook", description = "OneLive通知webhook接口")
public class OneLiveWebhookController {

    @Autowired
    private OneLiveWebhookService oneLiveWebhookService;

    @GetMapping("/notify")
    @Operation(
            summary = "接收OneLive通知",
            description = "接收来自OneLive的通知消息并处理",
            responses = {
                    @ApiResponse(responseCode = "200", description = "通知处理成功")
            }
    )
    public String receiveNotification(
            @Parameter(description = "通知标题", required = true) @RequestParam String title,
            @Parameter(description = "通知内容", required = true) @RequestParam String content,
            @Parameter(description = "通知类型", required = true) @RequestParam String type,
            @Parameter(description = "QQ号") @RequestParam(required = false) String qq,
            @Parameter(description = "群组ID") @RequestParam(required = false) String groupId) {
        oneLiveWebhookService.handleNotification(title, content, type, qq, groupId);
        return "success";
    }
}
