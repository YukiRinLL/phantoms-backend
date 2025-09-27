package com.phantoms.phantomsbackend.service.scheduler;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class DataSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncScheduler.class);

    // 性能优化配置
    private static final int BATCH_PAGE_SIZE = 5000; // 每页5000条
    private static final int INSERT_BATCH_SIZE = 500; // 每次插入500条
    private static final int SYNC_HOURS = 12; // 同步最近12小时的数据
    private static final int PARALLEL_THREADS = 3; // 并发线程数

    // 创建固定大小的线程池
    private final ExecutorService executorService = Executors.newFixedThreadPool(PARALLEL_THREADS);

    @Autowired
    @Qualifier("secondaryEntityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    @Qualifier("primaryAuthUserRepository")
    private com.phantoms.phantomsbackend.repository.primary.PrimaryAuthUserRepository primaryAuthUserRepository;

    @Autowired
    @Qualifier("secondaryAuthUserRepository")
    private com.phantoms.phantomsbackend.repository.secondary.SecondaryAuthUserRepository secondaryAuthUserRepository;

    @Autowired
    @Qualifier("primaryMessageRepository")
    private com.phantoms.phantomsbackend.repository.primary.PrimaryMessageRepository primaryMessageRepository;

    @Autowired
    @Qualifier("secondaryMessageRepository")
    private com.phantoms.phantomsbackend.repository.secondary.SecondaryMessageRepository secondaryMessageRepository;

    @Autowired
    @Qualifier("primaryUserProfileRepository")
    private com.phantoms.phantomsbackend.repository.primary.PrimaryUserProfileRepository primaryUserProfileRepository;

    @Autowired
    @Qualifier("secondaryUserProfileRepository")
    private com.phantoms.phantomsbackend.repository.secondary.SecondaryUserProfileRepository secondaryUserProfileRepository;

    @Autowired
    @Qualifier("primaryChatRecordRepository")
    private com.phantoms.phantomsbackend.repository.primary.onebot.PrimaryChatRecordRepository primaryChatRecordRepository;

    @Autowired
    @Qualifier("secondaryChatRecordRepository")
    private com.phantoms.phantomsbackend.repository.secondary.onebot.SecondaryChatRecordRepository secondaryChatRecordRepository;

    @Autowired
    @Qualifier("primaryUserRepository")
    private com.phantoms.phantomsbackend.repository.primary.PrimaryUserRepository primaryUserRepository;

    @Autowired
    @Qualifier("secondaryUserRepository")
    private com.phantoms.phantomsbackend.repository.secondary.SecondaryUserRepository secondaryUserRepository;

    @Autowired
    @Qualifier("primaryPasswordRepository")
    private com.phantoms.phantomsbackend.repository.primary.PrimaryPasswordRepository primaryPasswordRepository;

    @Autowired
    @Qualifier("secondaryPasswordRepository")
    private com.phantoms.phantomsbackend.repository.secondary.SecondaryPasswordRepository secondaryPasswordRepository;

    @Autowired
    @Qualifier("primaryExpeditionaryTeamRepository")
    private com.phantoms.phantomsbackend.repository.primary.PrimaryExpeditionaryTeamRepository primaryExpeditionaryTeamRepository;

    @Autowired
    @Qualifier("secondaryExpeditionaryTeamRepository")
    private com.phantoms.phantomsbackend.repository.secondary.SecondaryExpeditionaryTeamRepository secondaryExpeditionaryTeamRepository;

    @Scheduled(fixedRate = 600000) // 每10分钟执行一次
    public void syncData() {
        StopWatch totalStopWatch = new StopWatch("TotalSync");
        totalStopWatch.start("total-sync");

        try {
            syncChatRecordsOptimized();
            syncAuthUsers();
            syncUsers();
            syncPasswords();
            syncUserProfiles();
            syncMessages();
            syncExpeditionaryTeams();
            logger.info("All sync jobs completed successfully.");
        } finally {
            totalStopWatch.stop();
            logger.info("Total sync time: {} seconds", totalStopWatch.getTotalTimeSeconds());
        }
    }

    /**
     * 聊天记录同步方法
     */
    private void syncChatRecordsOptimized() {
        StopWatch stopWatch = new StopWatch("ChatRecordsSync");
        AtomicInteger totalProcessed = new AtomicInteger(0);
        AtomicInteger totalBatches = new AtomicInteger(0);

        try {
            LocalDateTime syncTimeThreshold = LocalDateTime.now().minusHours(SYNC_HOURS);

            logger.info("Starting ChatRecords sync for records after: {}", syncTimeThreshold);

            stopWatch.start("total-processing");

            // 第一步：使用分页+并发处理
            int pageNumber = 0;
            boolean hasMore = true;

            while (hasMore) {
                long pageStartTime = System.currentTimeMillis();

                Pageable pageable = PageRequest.of(pageNumber, BATCH_PAGE_SIZE);
                Page<com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord> page =
                    primaryChatRecordRepository.findByUpdatedAtAfter(syncTimeThreshold, pageable);

                List<com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord> primaryChatRecords = page.getContent();

                if (primaryChatRecords.isEmpty()) {
                    hasMore = false;
                    continue;
                }

                // 使用并发处理当前页的数据
                processChatRecordBatch(primaryChatRecords, totalProcessed, totalBatches);

                long pageEndTime = System.currentTimeMillis();
                logger.info("Processed page {} with {} records in {} ms. Total processed: {}",
                    pageNumber + 1, primaryChatRecords.size(), (pageEndTime - pageStartTime), totalProcessed.get());

                hasMore = page.hasNext();
                pageNumber++;
            }

            stopWatch.stop();

            logger.info("ChatRecords sync completed. Total batches: {}, Total records: {}, Total time: {} ms",
                totalBatches.get(), totalProcessed.get(), stopWatch.getTotalTimeMillis());

        } catch (Exception e) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            logger.error("ChatRecords sync failed after processing {} records", totalProcessed.get(), e);
            throw e;
        }
    }

    /**
     * 并发处理单页数据
     */
    private void processChatRecordBatch(
        List<com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord> primaryRecords,
        AtomicInteger totalProcessed, AtomicInteger totalBatches) {

        if (primaryRecords.isEmpty()) {
            return;
        }

        // 将数据分成多个子批次进行并发处理
        int subBatchSize = Math.max(primaryRecords.size() / PARALLEL_THREADS, 100);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < primaryRecords.size(); i += subBatchSize) {
            int endIndex = Math.min(i + subBatchSize, primaryRecords.size());
            List<com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord> subBatch =
                primaryRecords.subList(i, endIndex);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    List<com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord> secondaryRecords =
                        convertAndFilterChatRecords(subBatch);

                    if (!secondaryRecords.isEmpty()) {
                        batchInsertOptimized(secondaryRecords);
                        int processedCount = secondaryRecords.size();
                        totalProcessed.addAndGet(processedCount);
                        totalBatches.incrementAndGet();

                        if (processedCount > 1000) {
                            logger.debug("Processed sub-batch with {} records", processedCount);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error processing sub-batch", e);
                    throw new RuntimeException("Sub-batch processing failed", e);
                }
            }, executorService);

            futures.add(future);
        }

        // 等待所有子批次完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * 转换并过滤聊天记录（优化版本）
     */
    private List<com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord> convertAndFilterChatRecords(
        List<com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord> primaryRecords) {

        if (primaryRecords.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量检查已存在的ID，减少数据库查询次数
        List<String> primaryIds = primaryRecords.stream()
            .map(record -> record.getId().toString())
            .collect(Collectors.toList());

        List<String> existingIds = secondaryChatRecordRepository.findAllById(primaryIds)
            .stream()
            .map(com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord::getId)
            .collect(Collectors.toList());

        // 使用并行流转换，过滤已存在的记录
        return primaryRecords.parallelStream()
            .filter(record -> !existingIds.contains(record.getId().toString()))
            .map(this::convertToSecondaryChatRecord)
            .collect(Collectors.toList());
    }

    /**
     * 优化后的批量插入方法
     */
    private void batchInsertOptimized(List<com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord> records) {
        if (records.isEmpty()) {
            return;
        }

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            for (int i = 0; i < records.size(); i++) {
                entityManager.persist(records.get(i));

                // 分批刷新，避免内存溢出
                if ((i + 1) % INSERT_BATCH_SIZE == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }

            entityManager.flush();
            entityManager.clear();
            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Batch insert failed for {} records", records.size(), e);
            throw e;
        } finally {
            entityManager.close();
        }
    }

    // 原有的同步方法（保持不动）
    private void syncAuthUsers() {
        List<com.phantoms.phantomsbackend.pojo.entity.primary.AuthUser> primaryAuthUsers = primaryAuthUserRepository.findAll();
        List<com.phantoms.phantomsbackend.pojo.entity.secondary.AuthUser> secondaryAuthUsers = primaryAuthUsers.stream()
            .map(this::convertToSecondaryAuthUser)
            .collect(Collectors.toList());
        secondaryAuthUserRepository.saveAll(secondaryAuthUsers);
    }

    private void syncUsers() {
        List<com.phantoms.phantomsbackend.pojo.entity.primary.User> primaryUsers = primaryUserRepository.findAll();
        List<com.phantoms.phantomsbackend.pojo.entity.secondary.User> secondaryUsers = primaryUsers.stream()
            .map(this::convertToSecondaryUser)
            .collect(Collectors.toList());
        secondaryUserRepository.saveAll(secondaryUsers);
    }

    private void syncPasswords() {
        List<com.phantoms.phantomsbackend.pojo.entity.primary.Password> primaryPasswords = primaryPasswordRepository.findAll();
        List<com.phantoms.phantomsbackend.pojo.entity.secondary.Password> secondaryPasswords = primaryPasswords.stream()
            .map(this::convertToSecondaryPassword)
            .collect(Collectors.toList());
        secondaryPasswordRepository.saveAll(secondaryPasswords);
    }

    private void syncMessages() {
        List<com.phantoms.phantomsbackend.pojo.entity.primary.Message> primaryMessages = primaryMessageRepository.findAll();
        List<com.phantoms.phantomsbackend.pojo.entity.secondary.Message> secondaryMessages = primaryMessages.stream()
            .map(this::convertToSecondaryMessage)
            .collect(Collectors.toList());
        secondaryMessageRepository.saveAll(secondaryMessages);
    }

    private void syncUserProfiles() {
        List<com.phantoms.phantomsbackend.pojo.entity.primary.UserProfile> primaryUserProfiles = primaryUserProfileRepository.findAll();
        List<com.phantoms.phantomsbackend.pojo.entity.secondary.UserProfile> secondaryUserProfiles = primaryUserProfiles.stream()
            .map(this::convertToSecondaryUserProfile)
            .collect(Collectors.toList());
        secondaryUserProfileRepository.saveAll(secondaryUserProfiles);
    }

    private void syncExpeditionaryTeams() {
        try {
            List<com.phantoms.phantomsbackend.pojo.entity.primary.ExpeditionaryTeam> primaryTeams = primaryExpeditionaryTeamRepository.findAll();
            List<com.phantoms.phantomsbackend.pojo.entity.secondary.ExpeditionaryTeam> secondaryTeams = primaryTeams.stream()
                .map(this::convertToSecondaryExpeditionaryTeam)
                .collect(Collectors.toList());

            secondaryExpeditionaryTeamRepository.saveAll(secondaryTeams);
            logger.info("ExpeditionaryTeams synced successfully. Count: {}", secondaryTeams.size());
        } catch (Exception e) {
            logger.error("Failed to sync ExpeditionaryTeams", e);
        }
    }

    // 转换方法保持不变
    private com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord convertToSecondaryChatRecord(com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord primaryChatRecord) {
        com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord secondaryChatRecord = new com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord();
        BeanUtils.copyProperties(primaryChatRecord, secondaryChatRecord);
        secondaryChatRecord.setId(primaryChatRecord.getId().toString());
        return secondaryChatRecord;
    }

    private com.phantoms.phantomsbackend.pojo.entity.secondary.ExpeditionaryTeam convertToSecondaryExpeditionaryTeam(
        com.phantoms.phantomsbackend.pojo.entity.primary.ExpeditionaryTeam primaryTeam) {

        com.phantoms.phantomsbackend.pojo.entity.secondary.ExpeditionaryTeam secondaryTeam = new com.phantoms.phantomsbackend.pojo.entity.secondary.ExpeditionaryTeam();

        secondaryTeam.setUuid(primaryTeam.getUuid().toString());
        secondaryTeam.setName(primaryTeam.getName());
        secondaryTeam.setFreeStartTime(primaryTeam.getFreeStartTime());
        secondaryTeam.setFreeEndTime(primaryTeam.getFreeEndTime());
        secondaryTeam.setOccupation(primaryTeam.getOccupation());
        secondaryTeam.setNotes(primaryTeam.getNotes());
        secondaryTeam.setVolunteerDungeon(primaryTeam.getVolunteerDungeon());
        secondaryTeam.setLevel(primaryTeam.getLevel());
        secondaryTeam.setGuildName(primaryTeam.getGuildName());
        secondaryTeam.setOnlineStatus(primaryTeam.getOnlineStatus());
        secondaryTeam.setCreatedAt(primaryTeam.getCreatedAt());
        secondaryTeam.setUpdatedAt(primaryTeam.getUpdatedAt());

        return secondaryTeam;
    }

    private com.phantoms.phantomsbackend.pojo.entity.secondary.User convertToSecondaryUser(com.phantoms.phantomsbackend.pojo.entity.primary.User primaryUser) {
        com.phantoms.phantomsbackend.pojo.entity.secondary.User secondaryUser = new com.phantoms.phantomsbackend.pojo.entity.secondary.User();
        BeanUtils.copyProperties(primaryUser, secondaryUser);
        secondaryUser.setId(primaryUser.getId().toString());
        secondaryUser.setUserId(primaryUser.getUserId().toString());
        return secondaryUser;
    }

    private com.phantoms.phantomsbackend.pojo.entity.secondary.Password convertToSecondaryPassword(com.phantoms.phantomsbackend.pojo.entity.primary.Password primaryPassword) {
        com.phantoms.phantomsbackend.pojo.entity.secondary.Password secondaryPassword = new com.phantoms.phantomsbackend.pojo.entity.secondary.Password();
        BeanUtils.copyProperties(primaryPassword, secondaryPassword);
        secondaryPassword.setUserId(primaryPassword.getUserId().toString());
        secondaryPassword.setLegacyUserId(primaryPassword.getLegacyUserId().toString());
        return secondaryPassword;
    }

    private com.phantoms.phantomsbackend.pojo.entity.secondary.AuthUser convertToSecondaryAuthUser(com.phantoms.phantomsbackend.pojo.entity.primary.AuthUser primaryAuthUser) {
        com.phantoms.phantomsbackend.pojo.entity.secondary.AuthUser secondaryAuthUser = new com.phantoms.phantomsbackend.pojo.entity.secondary.AuthUser();
        BeanUtils.copyProperties(primaryAuthUser,secondaryAuthUser);
        secondaryAuthUser.setId(primaryAuthUser.getId().toString());
        secondaryAuthUser.setInstanceId(primaryAuthUser.getInstanceId().toString());
        return secondaryAuthUser;
    }

    private com.phantoms.phantomsbackend.pojo.entity.secondary.Message convertToSecondaryMessage(com.phantoms.phantomsbackend.pojo.entity.primary.Message primaryMessage) {
        com.phantoms.phantomsbackend.pojo.entity.secondary.Message secondaryMessage = new com.phantoms.phantomsbackend.pojo.entity.secondary.Message();
        BeanUtils.copyProperties(primaryMessage,secondaryMessage);
        secondaryMessage.setId(primaryMessage.getId().toString());
        secondaryMessage.setLegacyUserId(primaryMessage.getLegacyUserId().toString());
        secondaryMessage.setUserId(primaryMessage.getUserId().toString());
        return secondaryMessage;
    }

    private com.phantoms.phantomsbackend.pojo.entity.secondary.UserProfile convertToSecondaryUserProfile(com.phantoms.phantomsbackend.pojo.entity.primary.UserProfile primaryUserProfile) {
        com.phantoms.phantomsbackend.pojo.entity.secondary.UserProfile secondaryUserProfile = new com.phantoms.phantomsbackend.pojo.entity.secondary.UserProfile();
        BeanUtils.copyProperties(primaryUserProfile,secondaryUserProfile);
        secondaryUserProfile.setId(primaryUserProfile.getId().toString());
        secondaryUserProfile.setLegacyUserId(primaryUserProfile.getLegacyUserId().toString());
        secondaryUserProfile.setUserId(primaryUserProfile.getUserId().toString());
        return secondaryUserProfile;
    }
}