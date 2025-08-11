//package com.phantoms.phantomsbackend;
//
//import com.phantoms.phantomsbackend.mapper.UserProfileMapper;
//import com.phantoms.phantomsbackend.pojo.model.UserProfileModel;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class MySQLConnectionTest {
//
//    @Autowired
//    private UserProfileMapper userProfileMapper;
//
//    @Test
//    public void testMySQLConnection() {
//        // 创建一个测试用户资料
//        UserProfileModel userProfileModel = new UserProfileModel();
//        userProfileModel.setName("Test User");
//        userProfileModel.setData("Test Data");
//        userProfileModel.setLegacyUserId(UUID.randomUUID());
//        userProfileModel.setUserId(UUID.randomUUID());
//        userProfileModel.setUploadedBy("Test User");
//
//        // 插入用户资料
//        int insertResult = userProfileMapper.insert(userProfileModel);
//        System.out.println("Insert Result: " + insertResult);
//        assertNotNull(userProfileModel.getId(), "User Profile ID should not be null after insert");
//
//        // 查询用户资料
//        UserProfileModel retrievedUserProfile = userProfileMapper.selectById(userProfileModel.getId());
//        System.out.println("Retrieved User Profile: " + retrievedUserProfile);
//        assertNotNull(retrievedUserProfile, "Retrieved User Profile should not be null");
//        assertEquals(userProfileModel.getName(), retrievedUserProfile.getName(), "User Profile names should match");
//        assertEquals(userProfileModel.getData(), retrievedUserProfile.getData(), "User Profile data should match");
//    }
//}