package com.phantoms.phantomsbackend.repository.primary.onebot;

import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrimaryChatRecordRepository extends JpaRepository<ChatRecord, Long> {
    Page<ChatRecord> findByUpdatedAtAfter(LocalDateTime updatedAt, Pageable pageable);

    // 查询最新的几条消息
    @Query(value = "SELECT * FROM onebot.chat_records cr ORDER BY cr.created_at DESC LIMIT ?1", nativeQuery = true)
    List<ChatRecord> findTopByOrderByCreatedAtDesc(int limit);

    // 查询最新的几条消息，只返回 type=text 的消息
    @Query(value = "SELECT * FROM onebot.chat_records cr WHERE cr.message LIKE '%type=text%' ORDER BY cr.created_at DESC LIMIT ?1", nativeQuery = true)
    List<ChatRecord> findTopByOrderByCreatedAtDescWithText(int limit);
}