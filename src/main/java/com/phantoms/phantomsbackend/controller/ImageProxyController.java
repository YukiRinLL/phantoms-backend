package com.phantoms.phantomsbackend.controller;

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
public class ImageProxyController {

    private final OkHttpClient client;

    @Autowired
    public ImageProxyController() {
        // 配置 OkHttpClient 使用连接池
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @GetMapping("/proxy/image")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) throws IOException {
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
    public ResponseEntity<StreamingResponseBody> proxyQQEmoji(@RequestParam String faceId) {
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
//                if (contentLength > 10 * 1024 * 1024) { // 限制图片大小为10MB
//                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(null);
//                }

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