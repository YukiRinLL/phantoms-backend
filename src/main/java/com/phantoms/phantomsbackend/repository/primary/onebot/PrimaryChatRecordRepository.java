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

    // 本月度消息数量排名 - 修改为 PostgreSQL 语法
    @Query(value = "SELECT " +
        "cr.qq_user_id, " +
        "COUNT(*) AS total " +
        "FROM onebot.chat_records cr " +
        "WHERE cr.created_at >= DATE_TRUNC('month', CURRENT_DATE) " +
        "GROUP BY cr.qq_user_id " +
        "ORDER BY total DESC",
        nativeQuery = true)
    List<Object[]> findMonthlyMessageRanking();

    // 本月度图片数量排名 - 修改为 PostgreSQL 语法
    @Query(value = "SELECT " +
        "cr.qq_user_id, " +
        "COUNT(*) AS total_images " +
        "FROM onebot.chat_records cr " +
        "WHERE cr.message LIKE '%type=image%' " +
        "AND cr.created_at >= DATE_TRUNC('month', CURRENT_DATE) " +
        "GROUP BY cr.qq_user_id " +
        "ORDER BY total_images DESC",
        nativeQuery = true)
    List<Object[]> findMonthlyImageRanking();

    // 本月度图片比例排名（消息总数大于50的用户）- 修改为 PostgreSQL 语法
    @Query(value = "SELECT " +
        "cr.qq_user_id, " +
        "COUNT(*) AS total_messages, " +
        "SUM(CASE WHEN cr.message LIKE '%type=image%' THEN 1 ELSE 0 END) AS total_images, " +
        "(SUM(CASE WHEN cr.message LIKE '%type=image%' THEN 1 ELSE 0 END) * 1.0 / COUNT(*)) AS image_ratio " +
        "FROM onebot.chat_records cr " +
        "WHERE cr.created_at >= DATE_TRUNC('month', CURRENT_DATE) " +
        "GROUP BY cr.qq_user_id " +
        "HAVING COUNT(*) > 50 " +
        "ORDER BY image_ratio DESC",
        nativeQuery = true)
    List<Object[]> findMonthlyImageRatioRanking();

    // 查询本月消息总数
    @Query(value = "SELECT COUNT(*) FROM onebot.chat_records cr WHERE cr.created_at >= DATE_TRUNC('month', CURRENT_DATE)", nativeQuery = true)
    long countMonthlyMessages();

    // 查询本月图片总数
    @Query(value = "SELECT COUNT(*) FROM onebot.chat_records cr WHERE cr.message LIKE '%type=image%' AND cr.created_at >= DATE_TRUNC('month', CURRENT_DATE)", nativeQuery = true)
    long countMonthlyImages();

    // 查询所有图片消息总数
    @Query(value = "SELECT COUNT(*) FROM onebot.chat_records cr WHERE cr.message LIKE '%type=image%'", nativeQuery = true)
    long countByMessageContaining(String pattern);

    // 查询指定类型的通知事件
    List<ChatRecord> findByMessageType(String messageType);

    // 查询戳一戳事件
    @Query(value = "SELECT * FROM onebot.chat_records cr WHERE cr.message_type = 'notice_poke' ORDER BY cr.created_at DESC LIMIT ?1", nativeQuery = true)
    List<ChatRecord> findTopPokeEvents(int limit);

    // 查询用户指定天数内的每日消息统计
    @Query(value = "SELECT " +
            "DATE(cr.created_at) AS date, " +
            "COUNT(*) AS message_count " +
            "FROM onebot.chat_records cr " +
            "WHERE cr.qq_user_id = ?1 " +
            "AND cr.created_at >= CURRENT_DATE - MAKE_INTERVAL(days => ?2) " +
            "GROUP BY DATE(cr.created_at) " +
            "ORDER BY date DESC",
            nativeQuery = true)
    List<Object[]> findDailyMessageStatsByUserId(Long userId, int days);

    // 根据昵称模式查找用户
    @Query(value = "SELECT DISTINCT cr.qq_user_id, cr.message " +
            "FROM onebot.chat_records cr " +
            "WHERE cr.message LIKE ?1 " +
            "LIMIT 10",
            nativeQuery = true)
    List<Object[]> findUsersByNicknamePattern(String pattern);
}