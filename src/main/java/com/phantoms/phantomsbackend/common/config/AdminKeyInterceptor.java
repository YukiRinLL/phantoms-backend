package com.phantoms.phantomsbackend.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理接口鉴权拦截器：校验请求头 X-Admin-Key（密码来源于 system_config，环境变量兜底）。
 * 具体保护路径及是否放行 GET 在 {@link WebConfig} 中注册。
 */
public class AdminKeyInterceptor implements HandlerInterceptor {

    public static final String ADMIN_KEY_HEADER = "X-Admin-Key";

    private final AdminApiAccess adminApiAccess;

    /**
     * 是否放行 GET 请求（只读页面不需要密码，写操作仍需校验）
     */
    private final boolean allowGet;

    public AdminKeyInterceptor(AdminApiAccess adminApiAccess, boolean allowGet) {
        this.adminApiAccess = adminApiAccess;
        this.allowGet = allowGet;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // CORS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 只读请求按配置放行
        if (allowGet && "GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (!adminApiAccess.isAllowed(request.getHeader(ADMIN_KEY_HEADER))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
