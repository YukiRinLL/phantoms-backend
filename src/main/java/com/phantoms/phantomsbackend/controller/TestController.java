package com.phantoms.phantomsbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Test", description = "测试接口，用于调试和验证请求")
public class TestController {

    @RequestMapping("/**")
    @Operation(
            summary = "通用测试接口",
            description = "接收所有/test路径下的请求，返回详细的请求信息，用于调试和测试",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "请求已成功接收并记录")
            }
    )
    public ResponseEntity<Map<String, Object>> handleAllWebServiceRequests(
        HttpServletRequest request,
        @RequestBody(required = false) Object requestBody) {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getClientIp(request);

        log.info("🚀 收到WebService请求");
        log.info("📝 完整路径: {}", requestURI);
        log.info("⚡ 请求方法: {}", method);
        log.info("🌐 客户端IP: {}", clientIp);

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

        Object requestBodyData = null;
        if (requestBody != null) {
            requestBodyData = requestBody;
            if (requestBody instanceof Map) {
                String formattedBody = formatMapWithNull((Map<?, ?>) requestBody);
                log.info("📦 请求体数据: {}", formattedBody);
            } else {
                log.info("📦 请求体数据: {}", requestBody.toString());
            }
        } else {
            log.info("📦 请求体数据: 空");
        }

        log.info("✅ 请求处理完成");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("message", "请求已成功接收并记录");
        response.put("timestamp", System.currentTimeMillis());

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

        Map<String, Object> requestData = new LinkedHashMap<>();
        requestData.put("headers", headers);
        requestData.put("parameters", parameters);
        requestData.put("body", requestBodyData);

        response.put("requestData", requestData);

        return ResponseEntity.ok(response);
    }

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
                sb.append(formatMapWithNull((Map<?, ?>) value));
            } else if (value instanceof Iterable) {
                sb.append(formatIterableWithNull((Iterable<?>) value));
            } else {
                sb.append(value);
            }
        }

        sb.append("}");
        return sb.toString();
    }

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

        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        return ip;
    }
}
