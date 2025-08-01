package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.pojo.entity.AuthUser;
import com.phantoms.phantomsbackend.pojo.entity.Message;
import com.phantoms.phantomsbackend.pojo.entity.UserProfile;
import com.phantoms.phantomsbackend.pojo.entity.onebot.ChatRecord;
import com.phantoms.phantomsbackend.pojo.model.AuthUserModel;
import com.phantoms.phantomsbackend.pojo.model.MessageModel;
import com.phantoms.phantomsbackend.pojo.model.UserProfileModel;
import com.phantoms.phantomsbackend.pojo.model.onebot.ChatRecordModel;
import com.phantoms.phantomsbackend.repository.AuthUserRepository;
import com.phantoms.phantomsbackend.repository.MessageRepository;
import com.phantoms.phantomsbackend.repository.UserProfileRepository;
import com.phantoms.phantomsbackend.mapper.AuthUserMapper;
import com.phantoms.phantomsbackend.mapper.MessageMapper;
import com.phantoms.phantomsbackend.mapper.UserProfileMapper;
import com.phantoms.phantomsbackend.mapper.onebot.ChatRecordMapper;
import com.phantoms.phantomsbackend.repository.onebot.ChatRecordRepository;
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
    private ChatRecordRepository chatRecordRepository;
    @Autowired
    private AuthUserMapper authUserMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private ChatRecordMapper chatRecordMapper;

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
            AuthUserModel authUserModel = new AuthUserModel();
            authUserModel.setId(authUser.getId());
            authUserModel.setInstanceId(authUser.getInstanceId());
            authUserModel.setAud(authUser.getAud());
            authUserModel.setRole(authUser.getRole());
            authUserModel.setEmail(authUser.getEmail());
            authUserModel.setEncryptedPassword(authUser.getEncryptedPassword());
            authUserModel.setEmailConfirmedAt(authUser.getEmailConfirmedAt());
            authUserModel.setInvitedAt(authUser.getInvitedAt());
            authUserModel.setConfirmationToken(authUser.getConfirmationToken());
            authUserModel.setConfirmationSentAt(authUser.getConfirmationSentAt());
            authUserModel.setRecoveryToken(authUser.getRecoveryToken());
            authUserModel.setRecoverySentAt(authUser.getRecoverySentAt());
            authUserModel.setEmailChangeTokenNew(authUser.getEmailChangeTokenNew());
            authUserModel.setEmailChange(authUser.getEmailChange());
            authUserModel.setEmailChangeSentAt(authUser.getEmailChangeSentAt());
            authUserModel.setLastSignInAt(authUser.getLastSignInAt());
            authUserModel.setRawAppMetaData(authUser.getRawAppMetaData());
            authUserModel.setRawUserMetaData(authUser.getRawUserMetaData());
            authUserModel.setIsSuperAdmin(authUser.getIsSuperAdmin());
            authUserModel.setCreatedAt(authUser.getCreatedAt());
            authUserModel.setUpdatedAt(authUser.getUpdatedAt());
            authUserModel.setPhone(authUser.getPhone());
            authUserModel.setPhoneConfirmedAt(authUser.getPhoneConfirmedAt());
            authUserModel.setPhoneChange(authUser.getPhoneChange());
            authUserModel.setPhoneChangeToken(authUser.getPhoneChangeToken());
            authUserModel.setPhoneChangeSentAt(authUser.getPhoneChangeSentAt());
            authUserModel.setConfirmedAt(authUser.getConfirmedAt());
            authUserModel.setEmailChangeTokenCurrent(authUser.getEmailChangeTokenCurrent());
            authUserModel.setEmailChangeConfirmStatus(authUser.getEmailChangeConfirmStatus());
            authUserModel.setBannedUntil(authUser.getBannedUntil());
            authUserModel.setReauthenticationToken(authUser.getReauthenticationToken());
            authUserModel.setReauthenticationSentAt(authUser.getReauthenticationSentAt());
            authUserModel.setSsoUser(authUser.isSsoUser());
            authUserModel.setDeletedAt(authUser.getDeletedAt());
            authUserModel.setAnonymous(authUser.isAnonymous());
            authUserMapper.insert(authUserModel);
        }
    }

    private void syncMessages() {
        List<Message> messages = messageRepository.findAll();
        for (Message message : messages) {
            MessageModel messageModel = new MessageModel();
            messageModel.setId(message.getId());
            messageModel.setLegacyUserId(message.getLegacyUserId());
            messageModel.setUserId(message.getUserId());
            messageModel.setMessage(message.getMessage());
            messageModel.setCreatedAt(message.getCreatedAt());
            messageMapper.insert(messageModel);
        }
    }

    private void syncUserProfiles() {
        List<UserProfile> userProfiles = userProfileRepository.findAll();
        for (UserProfile userProfile : userProfiles) {
            UserProfileModel userProfileModel = new UserProfileModel();
            userProfileModel.setId(userProfile.getId());
            userProfileModel.setLegacyUserId(userProfile.getLegacyUserId());
            userProfileModel.setUserId(userProfile.getUserId());
            userProfileModel.setName(userProfile.getName());
            userProfileModel.setData(userProfile.getData());
            userProfileModel.setCreatedAt(userProfile.getCreatedAt());
            userProfileModel.setUploadedBy(userProfile.getUploadedBy());
            userProfileMapper.insert(userProfileModel);
        }
    }

    private void syncChatRecords() {
        List<ChatRecord> chatRecords = chatRecordRepository.findAll();
        for (ChatRecord chatRecord : chatRecords) {
            ChatRecordModel chatRecordModel = new ChatRecordModel();
            chatRecordModel.setId(chatRecord.getId());
            chatRecordModel.setMessageType(chatRecord.getMessageType());
            chatRecordModel.setQqUserId(chatRecord.getUserId());
            chatRecordModel.setQqGroupId(chatRecord.getGroupId());
            chatRecordModel.setMessage(chatRecord.getMessage());
            chatRecordModel.setTimestamp(chatRecord.getTimestamp());
            chatRecordModel.setCreatedAt(chatRecord.getCreatedAt());
            chatRecordModel.setUpdatedAt(chatRecord.getUpdatedAt());
            chatRecordMapper.insert(chatRecordModel);
        }
    }
}