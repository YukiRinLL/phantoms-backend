package com.phantoms.phantomsbackend.repository.primary.onebot;

import com.phantoms.phantomsbackend.pojo.entity.onebot.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("primaryChatRecordRepository")
public interface PrimaryChatRecordRepository extends JpaRepository<ChatRecord, Long> {
}