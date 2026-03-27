package com.phantoms.phantomsbackend.service;

public interface OneLiveWebhookService {
    void handleNotification(String title, String content, String type, String qq, String groupId);
}
