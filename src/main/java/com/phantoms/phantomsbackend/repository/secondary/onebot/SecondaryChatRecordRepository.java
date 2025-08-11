package com.phantoms.phantomsbackend.repository.secondary.onebot;

import com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("secondaryChatRecordRepository")
public interface SecondaryChatRecordRepository extends JpaRepository<ChatRecord, String> {
}