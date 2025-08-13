package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.common.utils.PIS.DateUtils;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncScheduler.class);

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

//    @Scheduled(fixedRate = 600000) // 每10分钟执行一次
    @Scheduled(cron = "0 0 */2 * * ?") // 每两小时执行一次，整点执行
    public void syncData() {
        syncAuthUsers();
        syncUsers();
        syncPasswords();
        syncUserProfiles();
        syncMessages();
        syncChatRecords();
        logger.info("All sync jobs completed successfully.");
    }

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

    private void syncChatRecords() {
        List<com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord> primaryChatRecords = primaryChatRecordRepository.findAll();

        // 使用并行流转换数据
        List<com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord> secondaryChatRecords = primaryChatRecords.parallelStream()
                .map(this::convertToSecondaryChatRecord)
                .filter(record -> !secondaryChatRecordRepository.existsById(record.getId())) // 检查是否已存在
                .collect(Collectors.toList());

        // 使用 EntityManager 批量插入
        batchInsert(secondaryChatRecords);
        logger.info("ChatRecords synced successfully.");
    }

    private void batchInsert(List<com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord> secondaryChatRecords) {
        int batchSize = 500; // 每批插入 500 条记录
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            for (int i = 0; i < secondaryChatRecords.size(); i++) {
                com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord record = secondaryChatRecords.get(i);
                entityManager.persist(record);
                if ((i + 1) % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }
    private com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord convertToSecondaryChatRecord(com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord primaryChatRecord) {
        com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord secondaryChatRecord = new com.phantoms.phantomsbackend.pojo.entity.secondary.onebot.ChatRecord();
        BeanUtils.copyProperties(primaryChatRecord, secondaryChatRecord);
        secondaryChatRecord.setId(primaryChatRecord.getId().toString());
        return secondaryChatRecord;
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