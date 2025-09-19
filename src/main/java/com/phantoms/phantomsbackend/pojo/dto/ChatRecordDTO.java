package com.phantoms.phantomsbackend.pojo.dto;

import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRecordDTO extends ChatRecord {
    private String nickname;
}