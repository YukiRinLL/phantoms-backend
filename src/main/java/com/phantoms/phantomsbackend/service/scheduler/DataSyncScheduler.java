package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.pojo.entity.AuthUser;
import com.phantoms.phantomsbackend.pojo.entity.Message;
import com.phantoms.phantomsbackend.pojo.entity.UserProfile;
import com.phantoms.phantomsbackend.pojo.entity.onebot.ChatRecord;
import com.phantoms.phantomsbackend.repository.AuthUserRepository;
import com.phantoms.phantomsbackend.repository.MessageRepository;
import com.phantoms.phantomsbackend.repository.UserProfileRepository;
import com.phantoms.phantomsbackend.mapper.AuthUserMapper;
import com.phantoms.phantomsbackend.mapper.ImageMapper;
import com.phantoms.phantomsbackend.mapper.MessageMapper;
import com.phantoms.phantomsbackend.mapper.UserProfileMapper;
import com.phantoms.phantomsbackend.mapper.onebot.ChatRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSyncScheduler {

    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private AuthUserMapper authUserMapper;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private ChatRecordMapper chatRecordMapper;

    // 每天凌晨2点执行同步任务
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncData() {
        syncAuthUsers();
        syncMessages();
        syncUserProfiles();
        syncChatRecords();
    }

    private void syncAuthUsers() {
        List<AuthUser> authUsers = authUserRepository.findAll();
        for (AuthUser authUser : authUsers) {
            authUserMapper.insert(authUser);
        }
    }

    private void syncMessages() {
        List<Message> messages = messageRepository.findAll();
        for (Message message : messages) {
            messageMapper.insert(message);
        }
    }

    private void syncUserProfiles() {
        List<UserProfile> userProfiles = userProfileRepository.findAll();
        for (UserProfile userProfile : userProfiles) {
            userProfileMapper.insert(userProfile);
        }
    }

    private void syncChatRecords() {
        List<ChatRecord> chatRecords = chatRecordMapper.selectList(null);
        for (ChatRecord chatRecord : chatRecords) {
            chatRecordMapper.insert(chatRecord);
        }
    }
}