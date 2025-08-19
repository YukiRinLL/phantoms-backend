package com.phantoms.phantomsbackend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class ImageProxyController {

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
    public ResponseEntity<byte[]> proxyQQEmoji(@RequestParam String faceId) throws IOException {
        String imageUrl = "https://q1.qlogo.cn/qqemoji/qq/" + faceId + ".gif";
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
}