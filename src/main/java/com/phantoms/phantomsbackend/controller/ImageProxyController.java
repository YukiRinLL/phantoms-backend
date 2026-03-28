package com.phantoms.phantomsbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
@Tag(name = "Image Proxy", description = "图片代理接口")
public class ImageProxyController {

    private final OkHttpClient client;

    @Autowired
    public ImageProxyController() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @GetMapping("/proxy/image")
    @Operation(
            summary = "图片代理",
            description = "代理获取指定URL的图片资源。注意：此接口对内存消耗较大，不建议在生产环境频繁使用",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "图片获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "图片获取失败")
            }
    )
    public ResponseEntity<byte[]> proxyImage(
            @Parameter(description = "图片URL", required = true) @RequestParam String url) throws IOException {
        URL imageUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            byte[] imageBytes = connection.getInputStream().readAllBytes();
            String contentType = connection.getContentType();
            connection.disconnect();
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } else {
            connection.disconnect();
            return ResponseEntity.status(responseCode).body(null);
        }
    }

    @GetMapping("/proxy/qqemoji")
    @Operation(
            summary = "QQ表情代理",
            description = "代理获取QQ表情图片",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "表情获取成功"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "表情获取失败")
            }
    )
    public ResponseEntity<StreamingResponseBody> proxyQQEmoji(
            @Parameter(description = "表情ID", required = true) @RequestParam String faceId) {
        String imageUrl = "https://q1.qlogo.cn/qqemoji/qq/" + faceId + ".gif";
        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        return handleRequest(request);
    }

    private ResponseEntity<StreamingResponseBody> handleRequest(Request request) {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                long contentLength = response.body().contentLength();

                StreamingResponseBody stream = outputStream -> {
                    try (InputStream inputStream = response.body().byteStream()) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            outputStream.flush();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to stream response", e);
                    }
                };

                String contentType = response.header("Content-Type");
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(stream);
            } else {
                return ResponseEntity.status(response.code()).body(null);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
