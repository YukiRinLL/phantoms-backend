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

    // 查询最新的3条消息，按群组分组
    @Query(value = "SELECT * FROM onebot.chat_records cr WHERE cr.qq_group_id = ?1 ORDER BY cr.created_at DESC LIMIT 3", nativeQuery = true)
    List<ChatRecord> findTop3ByGroupIdOrderByCreatedAtDesc(Long groupId);

    // 查询最新的几条消息，按用户和群组分组
    @Query(value = "SELECT * FROM onebot.chat_records cr WHERE cr.qq_user_id = ?1 AND cr.qq_group_id = ?2 ORDER BY cr.created_at DESC LIMIT ?3", nativeQuery = true)
    List<ChatRecord> findTopByUserIdAndGroupIdOrderByCreatedAtDesc(Long userId, Long groupId, int limit);

    // 查询用户在指定群组中发送的图片消息数量（过去24小时内）
    @Query(value = "SELECT COUNT(*) FROM onebot.chat_records cr WHERE cr.qq_user_id = ?1 AND cr.qq_group_id = ?2 AND cr.message LIKE '%type=image%' AND cr.created_at > ?3", nativeQuery = true)
    long countByUserIdAndGroupIdAndMessageTypeAndTimestampAfter(Long userId, Long groupId, LocalDateTime timestamp);

    // 查询用户在指定群组中发送的所有消息数量（过去24小时内）
    @Query(value = "SELECT COUNT(*) FROM onebot.chat_records cr WHERE cr.qq_user_id = ?1 AND cr.qq_group_id = ?2 AND cr.created_at > ?3", nativeQuery = true)
    long countByUserIdAndGroupIdAndTimestampAfter(Long userId, Long groupId, LocalDateTime timestamp);
}