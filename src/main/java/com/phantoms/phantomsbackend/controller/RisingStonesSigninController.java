package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.RisingStonesSigninHelper;
import com.phantoms.phantomsbackend.common.utils.RisingStonesUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ffxiv/signin")
@Tag(name = "Rising Stones Signin", description = "FFXI石之家签到相关接口")
public class RisingStonesSigninController {

    @Autowired
    private RisingStonesSigninHelper ffxivSigninHelper;

    @Autowired
    private RisingStonesUtils risingStonesUtils;

    @GetMapping("/login/qrcode")
    @Operation(
            summary = "获取登录二维码",
            description = "获取叨鱼登录二维码内容",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取二维码成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "获取二维码失败")
            }
    )
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

    @GetMapping("/login/status")
    @Operation(
            summary = "检查登录状态",
            description = "用户扫描二维码后，轮询此接口检查登录状态",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "检查登录状态失败")
            }
    )
    public ResponseEntity<?> checkLoginStatus() {
        try {
            JSONObject loginInfo = ffxivSigninHelper.getLoginInfo(RisingStonesSigninHelper.SSO_REDIRECT_URL);
            JSONObject data = loginInfo.getJSONObject("data");
            
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
            
            if (data.containsKey("mappedErrorCode") && data.getInteger("mappedErrorCode") == -10515801) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "data", Map.of(
                                "status", "QRCODE_EXPIRED"
                        ),
                        "message", "二维码已失效"
                ));
            }
            
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

    @PostMapping("/login/finish")
    @Operation(
            summary = "完成登录",
            description = "用户扫描二维码成功后，调用此接口完成登录并获取Cookies",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录完成"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "ticket不能为空"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "完成登录失败")
            }
    )
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
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "登录完成"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "完成登录失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/check/login")
    @Operation(
            summary = "检查登录状态（使用Cookies）",
            description = "使用已保存的Cookies检查登录状态",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "检查登录状态成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "检查登录状态失败")
            }
    )
    public ResponseEntity<?> checkLoginStatusWithCookies() {
        try {
            JSONObject result = ffxivSigninHelper.checkLoginStatus();
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

    @PostMapping("/character/bind")
    @Operation(
            summary = "获取角色绑定信息",
            description = "获取当前登录用户的角色绑定信息",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取角色绑定信息成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "获取角色绑定信息失败")
            }
    )
    public ResponseEntity<?> getCharacterBindInfo() {
        try {
            JSONObject result = risingStonesUtils.getCharacterBindInfo();
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

    @PostMapping("/sign/in")
    @Operation(
            summary = "执行签到",
            description = "执行石之家每日签到",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "签到成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "签到失败")
            }
    )
    public ResponseEntity<?> doSignIn() {
        try {
            JSONObject result = risingStonesUtils.doSignIn();
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

    @PostMapping("/sign/log")
    @Operation(
            summary = "获取签到日志",
            description = "获取指定月份的签到日志",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取签到日志成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "month不能为空"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "获取签到日志失败")
            }
    )
    public ResponseEntity<?> getSignLog(@RequestBody Map<String, String> request) {
        String month = request.get("month");
        
        if (month == null || month.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "month不能为空"
            ));
        }
        
        try {
            JSONObject result = risingStonesUtils.getSignLog(month);
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

    @PostMapping("/sign/reward/list")
    @Operation(
            summary = "获取签到奖励列表",
            description = "获取指定月份的签到奖励列表",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取签到奖励列表成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "month不能为空"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "获取签到奖励列表失败")
            }
    )
    public ResponseEntity<?> getSignInRewardList(@RequestBody Map<String, String> request) {
        String month = request.get("month");
        
        if (month == null || month.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "month不能为空"
            ));
        }
        
        try {
            JSONObject result = risingStonesUtils.getSignInRewardList(month);
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

    @PostMapping("/sign/reward/get")
    @Operation(
            summary = "领取签到奖励",
            description = "领取指定的签到奖励",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "领取签到奖励成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "领取签到奖励失败")
            }
    )
    public ResponseEntity<?> getSignInReward(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        String month = (String) request.get("month");
        
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
            JSONObject result = risingStonesUtils.getSignInReward(id, month);
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

    @PostMapping("/dynamic/create")
    @Operation(
            summary = "创建动态",
            description = "在石之家创建一条新动态",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建动态成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "创建动态失败")
            }
    )
    public ResponseEntity<?> createDynamic(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        Integer scope = (Integer) request.get("scope");
        String pic_url = (String) request.get("pic_url");
        
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
            JSONObject result = risingStonesUtils.createDynamic(content, scope, pic_url != null ? pic_url : "");
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

    @PostMapping("/post/comment")
    @Operation(
            summary = "创建动态评论",
            description = "在石之家动态下创建评论",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "创建动态评论成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "创建动态评论失败")
            }
    )
    public ResponseEntity<?> createPostComment(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String posts_id = request.get("posts_id");
        String parent_id = request.get("parent_id");
        String root_parent = request.get("root_parent");
        String comment_pic = request.get("comment_pic");
        
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
            JSONObject result = risingStonesUtils.createPostComment(
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
