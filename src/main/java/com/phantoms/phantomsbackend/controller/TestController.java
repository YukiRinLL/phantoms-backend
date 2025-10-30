package com.phantoms.phantomsbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/**")
    public ResponseEntity<Map<String, Object>> handleAllWebServiceRequests(
        HttpServletRequest request,
        @RequestBody(required = false) Object requestBody) {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getClientIp(request);

        // 详细日志记录
        log.info("🚀 收到WebService请求");
        log.info("📝 完整路径: {}", requestURI);
        log.info("⚡ 请求方法: {}", method);
        log.info("🌐 客户端IP: {}", clientIp);

        // 收集请求头信息
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            log.info("📋 请求头:");
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headers.put(headerName, headerValue);
                log.info("   {}: {}", headerName, headerValue);
            }
        }

        // 收集请求参数
        Map<String, String[]> requestParams = request.getParameterMap();
        Map<String, Object> parameters = new LinkedHashMap<>();
        if (!requestParams.isEmpty()) {
            log.info("📦 请求参数:");
            requestParams.forEach((key, values) -> {
                String valueStr = String.join(", ", values);
                parameters.put(key, values.length == 1 ? values[0] : values);
                log.info("   {}: {}", key, valueStr);
            });
        }

        // 记录请求体 - 关键修改：使用自定义方法格式化显示null值
        Object requestBodyData = null;
        if (requestBody != null) {
            requestBodyData = requestBody;
            if (requestBody instanceof Map) {
                // 如果是Map类型，使用自定义格式化方法
                String formattedBody = formatMapWithNull((Map<?, ?>) requestBody);
                log.info("📦 请求体数据: {}", formattedBody);
            } else {
                log.info("📦 请求体数据: {}", requestBody.toString());
            }
        } else {
            log.info("📦 请求体数据: 空");
        }

        log.info("✅ 请求处理完成");

        // 返回统一响应，包含所有请求数据
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("message", "请求已成功接收并记录");
        response.put("timestamp", System.currentTimeMillis());

        // 请求信息
        Map<String, Object> requestInfo = new LinkedHashMap<>();
        requestInfo.put("path", requestURI);
        requestInfo.put("method", method);
        requestInfo.put("clientIp", clientIp);
        requestInfo.put("contentType", request.getContentType());
        requestInfo.put("characterEncoding", request.getCharacterEncoding());
        requestInfo.put("serverName", request.getServerName());
        requestInfo.put("serverPort", request.getServerPort());
        requestInfo.put("queryString", request.getQueryString());
        requestInfo.put("protocol", request.getProtocol());
        requestInfo.put("scheme", request.getScheme());

        response.put("requestInfo", requestInfo);

        // 请求数据
        Map<String, Object> requestData = new LinkedHashMap<>();
        requestData.put("headers", headers);
        requestData.put("parameters", parameters);
        requestData.put("body", requestBodyData);

        response.put("requestData", requestData);

        return ResponseEntity.ok(response);
    }

    /**
     * 自定义Map格式化方法，明确显示null值
     */
    private String formatMapWithNull(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();

            sb.append(key).append("=");

            if (value == null) {
                sb.append("null");
            } else if (value instanceof Map) {
                // 递归处理嵌套Map
                sb.append(formatMapWithNull((Map<?, ?>) value));
            } else if (value instanceof Iterable) {
                // 处理List等集合类型
                sb.append(formatIterableWithNull((Iterable<?>) value));
            } else {
                sb.append(value);
            }
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * 格式化集合类型，明确显示null值
     */
    private String formatIterableWithNull(Iterable<?> iterable) {
        if (iterable == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        boolean first = true;
        for (Object item : iterable) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            if (item == null) {
                sb.append("null");
            } else if (item instanceof Map) {
                sb.append(formatMapWithNull((Map<?, ?>) item));
            } else if (item instanceof Iterable) {
                sb.append(formatIterableWithNull((Iterable<?>) item));
            } else {
                sb.append(item);
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        return ip;
    }
}