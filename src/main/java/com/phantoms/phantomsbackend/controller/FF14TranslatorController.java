package com.phantoms.phantomsbackend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phantoms.phantomsbackend.common.LLM.FF14Translator;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ff14")
@Tag(name = "FF14 Translator", description = "FF14龙语翻译接口")
public class FF14TranslatorController {

    @PostMapping("/translate")
    @Operation(
            summary = "翻译文本为FF14龙语",
            description = "将用户输入的中文翻译为龙语、妖灵语和高位妖精语"
    )
    public ResponseEntity<Map<String, String>> translate(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "text不能为空"));
        }
        log.info("收到翻译请求: {}", text);
        String result = FF14Translator.translate(text);
        log.info("翻译结果: {}", result);
        return ResponseEntity.ok(Map.of("result", result));
    }
}