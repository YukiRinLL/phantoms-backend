package com.phantoms.phantomsbackend.repository.primary.onebot;

import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("primaryChatRecordRepository")
public interface PrimaryChatRecordRepository extends JpaRepository<ChatRecord, UUID> {
}