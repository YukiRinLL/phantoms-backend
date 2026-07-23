package com.phantoms.phantomsbackend.controller;

import com.alibaba.fastjson.JSONObject;
import com.phantoms.phantomsbackend.common.utils.RisingStonesSigninHelper;
import com.phantoms.phantomsbackend.common.utils.RisingStonesUtils;
import com.phantoms.phantomsbackend.service.SystemConfigService;
import com.phantoms.phantomsbackend.service.SystemConfigService.LoginAccount;
import com.phantoms.phantomsbackend.service.scheduler.DailySignInScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ffxiv/signin")
@Tag(name = "Rising Stones Signin", description = "FFXI石之家签到相关接口")
public class RisingStonesSigninController {

    @Autowired
    private RisingStonesSigninHelper ffxivSigninHelper;

    @Autowired
    private RisingStonesUtils risingStonesUtils;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private DailySignInScheduler dailySignInScheduler;

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
            String accountId = ffxivSigninHelper.finishLogin(ticket);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of("accountId", accountId),
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

    @GetMapping("/accounts")
    @Operation(
            summary = "获取所有登录账号",
            description = "获取所有已保存的登录账号列表"
    )
    public ResponseEntity<?> getLoginAccounts() {
        try {
            List<LoginAccount> accounts = systemConfigService.getLoginAccounts();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", accounts,
                    "message", "获取账号列表成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "获取账号列表失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/accounts/enabled")
    @Operation(
            summary = "获取启用的登录账号",
            description = "获取所有已启用的登录账号列表"
    )
    public ResponseEntity<?> getEnabledLoginAccounts() {
        try {
            List<LoginAccount> accounts = systemConfigService.getEnabledLoginAccounts();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", accounts,
                    "message", "获取启用账号列表成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "获取启用账号列表失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/accounts/{accountId}")
    @Operation(
            summary = "获取单个账号信息",
            description = "根据accountId获取单个账号的详细信息"
    )
    public ResponseEntity<?> getLoginAccount(@PathVariable String accountId) {
        try {
            LoginAccount account = systemConfigService.getLoginAccount(accountId);
            if (account == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "账号不存在"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", account,
                    "message", "获取账号信息成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "获取账号信息失败: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/accounts/{accountId}")
    @Operation(
            summary = "更新账号信息",
            description = "更新账号的昵称或启用状态"
    )
    public ResponseEntity<?> updateLoginAccount(
            @PathVariable String accountId,
            @RequestBody Map<String, Object> request) {
        try {
            String nickname = (String) request.get("nickname");
            Boolean enabled = (Boolean) request.get("enabled");

            boolean updated = systemConfigService.updateLoginAccount(accountId, nickname, enabled);
            if (!updated) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "账号不存在"
                ));
            }

            LoginAccount account = systemConfigService.getLoginAccount(accountId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", account,
                    "message", "更新账号信息成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "更新账号信息失败: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/accounts/{accountId}")
    @Operation(
            summary = "删除账号",
            description = "根据accountId删除指定的登录账号"
    )
    public ResponseEntity<?> deleteLoginAccount(@PathVariable String accountId) {
        try {
            boolean removed = systemConfigService.removeLoginAccount(accountId);
            if (!removed) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "账号不存在"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "删除账号成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "删除账号失败: " + e.getMessage()
            ));
        }
    }

    

    @PostMapping("/manual-signin")
    @Operation(
            summary = "手动触发所有账号签到",
            description = "手动触发所有启用账号的签到任务"
    )
    public ResponseEntity<?> manualSignIn() {
        try {
            dailySignInScheduler.manualSignIn();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "已触发所有账号签到任务"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "触发签到任务失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/manual-signin/{accountId}")
    @Operation(
            summary = "手动触发单个账号签到",
            description = "手动触发指定账号的签到任务"
    )
    public ResponseEntity<?> manualSignIn(@PathVariable String accountId) {
        try {
            dailySignInScheduler.manualSignIn(accountId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "已触发账号签到任务"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "触发签到任务失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/manual-claim-rewards")
    @Operation(
            summary = "手动触发所有账号领取奖励",
            description = "手动触发所有启用账号的签到奖励领取任务"
    )
    public ResponseEntity<?> manualClaimRewards() {
        try {
            dailySignInScheduler.manualClaimRewards();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "已触发所有账号奖励领取任务"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "触发奖励领取任务失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/manual-claim-rewards/{accountId}")
    @Operation(
            summary = "手动触发单个账号领取奖励",
            description = "手动触发指定账号的签到奖励领取任务"
    )
    public ResponseEntity<?> manualClaimRewards(@PathVariable String accountId) {
        try {
            LoginAccount account = systemConfigService.getLoginAccount(accountId);
            if (account == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "账号不存在"
                ));
            }
            
            dailySignInScheduler.claimAvailableRewards(account.getCookies(), accountId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "已触发账号奖励领取任务"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "触发奖励领取任务失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/accounts/{accountId}/user-info")
    @Operation(
            summary = "获取指定账号的用户信息",
            description = "获取指定账号的用户信息（包含角色信息、服务器、部队等）"
    )
    public ResponseEntity<?> getUserInfoForAccount(@PathVariable String accountId) {
        try {
            LoginAccount account = systemConfigService.getLoginAccount(accountId);
            if (account == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "账号不存在"
                ));
            }

            JSONObject result = risingStonesUtils.getCharacterBindInfo(account.getCookies());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "获取用户信息成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "获取用户信息失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/accounts/{accountId}/set-default-api")
    @Operation(
            summary = "设置默认API账号",
            description = "将指定账号设置为API调用的默认账号，用于查询用户信息、部队信息等操作"
    )
    public ResponseEntity<?> setDefaultApiAccount(@PathVariable String accountId) {
        try {
            boolean success = systemConfigService.setDefaultApiAccount(accountId);
            if (!success) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "账号不存在"
                ));
            }

            LoginAccount account = systemConfigService.getLoginAccount(accountId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", account,
                    "message", "已将账号设置为默认API账号"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "设置默认API账号失败: " + e.getMessage()
            ));
        }
    }

    @PatchMapping("/accounts/{accountId}/toggle")
    @Operation(
            summary = "切换账号启用状态",
            description = "启用或禁用指定账号"
    )
    public ResponseEntity<?> toggleAccountStatus(@PathVariable String accountId, @RequestBody Map<String, Boolean> request) {
        try {
            Boolean enabled = request.get("enabled");
            if (enabled == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "enabled不能为空"
                ));
            }

            boolean updated = systemConfigService.updateLoginAccount(accountId, null, enabled);
            if (!updated) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "账号不存在"
                ));
            }

            LoginAccount account = systemConfigService.getLoginAccount(accountId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", account,
                    "message", enabled ? "账号已启用" : "账号已禁用"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "操作失败: " + e.getMessage()
            ));
        }
    }
}
