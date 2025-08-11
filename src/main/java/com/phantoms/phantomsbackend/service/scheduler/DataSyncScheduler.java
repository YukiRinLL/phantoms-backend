package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.pojo.entity.AuthUser;
import com.phantoms.phantomsbackend.pojo.entity.Message;
import com.phantoms.phantomsbackend.pojo.entity.UserProfile;
import com.phantoms.phantomsbackend.pojo.entity.onebot.ChatRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSyncScheduler {

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

    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void syncData() {
        syncAuthUsers();
        syncMessages();
        syncUserProfiles();
        syncChatRecords();
    }

    private void syncAuthUsers() {
        List<AuthUser> authUsers = primaryAuthUserRepository.findAll();
        secondaryAuthUserRepository.saveAll(authUsers);
    }

    private void syncMessages() {
        List<Message> messages = primaryMessageRepository.findAll();
        secondaryMessageRepository.saveAll(messages);
    }

    private void syncUserProfiles() {
        List<UserProfile> userProfiles = primaryUserProfileRepository.findAll();
        secondaryUserProfileRepository.saveAll(userProfiles);
    }

    private void syncChatRecords() {
        List<ChatRecord> chatRecords = primaryChatRecordRepository.findAll();
        secondaryChatRecordRepository.saveAll(chatRecords);
    }
}