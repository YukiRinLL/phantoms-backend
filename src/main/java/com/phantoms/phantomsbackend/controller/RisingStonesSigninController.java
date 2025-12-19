package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.RisingStonesSigninHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ffxiv/signin")
public class RisingStonesSigninController {

    @Autowired
    private RisingStonesSigninHelper ffxivSigninHelper;

    /**
     * 1. 获取登录二维码
     * 用于获取叨鱼登录二维码内容
     */
    @GetMapping("/login/qrcode")
    public ResponseEntity<?> getLoginQRCode() {
        try {
            String qrCodeContent = ffxivSigninHelper.getLoginQRCode();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", qrCodeContent,
                    "message", "获取二维码成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "获取二维码失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 2. 轮询检查登录状态
     * 用户扫描二维码后，前端需要轮询此接口检查登录状态
     */
    @GetMapping("/login/status")
    public ResponseEntity<?> checkLoginStatus() {
        try {
            JSONObject loginInfo = ffxivSigninHelper.getLoginInfo(RisingStonesSigninHelper.SSO_REDIRECT_URL);
            JSONObject data = loginInfo.getJSONObject("data");
            
            // 检查是否有ticket（登录成功）
            if (data.containsKey("ticket")) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", Map.of(
                                "ticket", data.getString("ticket"),
                                "status", "LOGIN_SUCCESS"
                        ),
                        "message", "登录成功"
                ));
            }
            
            // 检查二维码是否失效
            if (data.containsKey("mappedErrorCode") && data.getInteger("mappedErrorCode") == -10515801) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "data", Map.of(
                                "status", "QRCODE_EXPIRED"
                        ),
                        "message", "二维码已失效"
                ));
            }
            
            // 二维码未扫描或正在处理
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "status", "WAITING_FOR_SCAN"
                    ),
                    "message", "等待用户扫描二维码"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "检查登录状态失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 3. 完成登录并获取Cookies
     * 当用户扫描二维码成功后，调用此接口完成登录并获取Cookies
     */
    @PostMapping("/login/finish")
    public ResponseEntity<?> finishLogin(@RequestBody Map<String, String> request) {
        String ticket = request.get("ticket");
        if (ticket == null || ticket.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "ticket不能为空"
            ));
        }
        
        try {
            ffxivSigninHelper.finishLogin(ticket);
            String cookies = ffxivSigninHelper.getCookies();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "cookies", cookies
                    ),
                    "message", "登录完成，获取Cookies成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "完成登录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 4. 完整登录流程（测试用）
     * 一次性完成整个登录流程（不推荐生产环境使用，因为需要等待用户扫描二维码）
     */
    @GetMapping("/login/full")
    public ResponseEntity<?> fullLogin() {
        try {
            String cookies = ffxivSigninHelper.login();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "cookies", cookies
                    ),
                    "message", "完整登录流程完成"
            ));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(Map.of(
                    "success", false,
                    "message", "登录超时: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "登录失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 5. 检查登录状态（使用Cookies）
     */
    @PostMapping("/check/login")
    public ResponseEntity<?> checkLoginStatus(@RequestBody Map<String, String> request) {
        String cookies = request.get("cookies");
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "cookies不能为空"
            ));
        }
        
        try {
            JSONObject result = ffxivSigninHelper.checkLoginStatus(cookies);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "检查登录状态成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "检查登录状态失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 6. 获取角色绑定信息
     */
    @PostMapping("/character/bind")
    public ResponseEntity<?> getCharacterBindInfo(@RequestBody Map<String, String> request) {
        String cookies = request.get("cookies");
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "cookies不能为空"
            ));
        }
        
        try {
            JSONObject result = ffxivSigninHelper.getCharacterBindInfo(cookies);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "获取角色绑定信息成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "获取角色绑定信息失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 7. 执行签到
     */
    @PostMapping("/sign/in")
    public ResponseEntity<?> doSignIn(@RequestBody Map<String, String> request) {
        String cookies = request.get("cookies");
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "cookies不能为空"
            ));
        }
        
        try {
            JSONObject result = ffxivSigninHelper.doSignIn(cookies);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "执行签到成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "执行签到失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 8. 获取签到日志
     */
    @PostMapping("/sign/log")
    public ResponseEntity<?> getSignLog(@RequestBody Map<String, String> request) {
        String cookies = request.get("cookies");
        String month = request.get("month");
        
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "cookies不能为空"
            ));
        }
        
        if (month == null || month.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "month不能为空"
            ));
        }
        
        try {
            JSONObject result = ffxivSigninHelper.getSignLog(cookies, month);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "获取签到日志成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "获取签到日志失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 9. 获取签到奖励列表
     */
    @PostMapping("/sign/reward/list")
    public ResponseEntity<?> getSignInRewardList(@RequestBody Map<String, String> request) {
        String cookies = request.get("cookies");
        String month = request.get("month");
        
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "cookies不能为空"
            ));
        }
        
        if (month == null || month.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "month不能为空"
            ));
        }
        
        try {
            JSONObject result = ffxivSigninHelper.getSignInRewardList(cookies, month);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "获取签到奖励列表成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "获取签到奖励列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 10. 领取签到奖励
     */
    @PostMapping("/sign/reward/get")
    public ResponseEntity<?> getSignInReward(@RequestBody Map<String, Object> request) {
        String cookies = (String) request.get("cookies");
        Integer id = (Integer) request.get("id");
        String month = (String) request.get("month");
        
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "cookies不能为空"
            ));
        }
        
        if (id == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "id不能为空"
            ));
        }
        
        if (month == null || month.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "month不能为空"
            ));
        }
        
        try {
            JSONObject result = ffxivSigninHelper.getSignInReward(cookies, id, month);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "领取签到奖励成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "领取签到奖励失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 11. 创建动态
     */
    @PostMapping("/dynamic/create")
    public ResponseEntity<?> createDynamic(@RequestBody Map<String, Object> request) {
        String cookies = (String) request.get("cookies");
        String content = (String) request.get("content");
        Integer scope = (Integer) request.get("scope");
        String pic_url = (String) request.get("pic_url");
        
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "cookies不能为空"
            ));
        }
        
        if (content == null || content.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "content不能为空"
            ));
        }
        
        if (scope == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "scope不能为空"
            ));
        }
        
        try {
            JSONObject result = ffxivSigninHelper.createDynamic(cookies, content, scope, pic_url != null ? pic_url : "");
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "创建动态成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "创建动态失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 12. 创建动态评论
     */
    @PostMapping("/post/comment")
    public ResponseEntity<?> createPostComment(@RequestBody Map<String, String> request) {
        String cookies = request.get("cookies");
        String content = request.get("content");
        String posts_id = request.get("posts_id");
        String parent_id = request.get("parent_id");
        String root_parent = request.get("root_parent");
        String comment_pic = request.get("comment_pic");
        
        if (cookies == null || cookies.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "cookies不能为空"
            ));
        }
        
        if (content == null || content.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "content不能为空"
            ));
        }
        
        if (posts_id == null || posts_id.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "posts_id不能为空"
            ));
        }
        
        try {
            JSONObject result = ffxivSigninHelper.createPostComment(
                    cookies,
                    content,
                    posts_id,
                    parent_id != null ? parent_id : "0",
                    root_parent != null ? root_parent : "0",
                    comment_pic != null ? comment_pic : ""
            );
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "创建动态评论成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "创建动态评论失败: " + e.getMessage()
            ));
        }
    }
}