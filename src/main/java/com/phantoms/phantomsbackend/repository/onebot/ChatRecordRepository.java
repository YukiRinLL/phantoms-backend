package com.phantoms.phantomsbackend.repository.onebot;

import com.phantoms.phantomsbackend.pojo.entity.onebot.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRecordRepository extends JpaRepository<ChatRecord, Long> {
}