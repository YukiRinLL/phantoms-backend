package com.phantoms.phantomsbackend.controller;

import com.phantoms.phantomsbackend.service.OneLiveWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/webhook/onelive")
public class OneLiveWebhookController {

    @Autowired
    private OneLiveWebhookService oneLiveWebhookService;

    @GetMapping("/notify")
    public String receiveNotification(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String type,
            @RequestParam(required = false) List<String> qq,
            @RequestParam(required = false) List<String> groupId) {
        oneLiveWebhookService.handleNotification(title, content, type, qq, groupId);
        return "success";
    }
}
