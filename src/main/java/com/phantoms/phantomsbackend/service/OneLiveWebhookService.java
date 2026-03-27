package com.phantoms.phantomsbackend.service;

import java.util.List;

public interface OneLiveWebhookService {
    void handleNotification(String title, String content, String type, List<String> qq, List<String> groupId);
}
