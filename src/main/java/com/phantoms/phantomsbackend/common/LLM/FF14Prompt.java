package com.phantoms.phantomsbackend.common.LLM;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class FF14Prompt {
    private static String SYSTEM_PROMPT;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/ff14-translator-prompt.md");
            Path path = Paths.get(resource.getURI());
            SYSTEM_PROMPT = Files.readString(path, StandardCharsets.UTF_8);
            log.info("FF14翻译提示词加载成功，长度: {} 字符", SYSTEM_PROMPT.length());
        } catch (IOException e) {
            log.error("加载FF14翻译提示词失败", e);
            SYSTEM_PROMPT = "你是最终幻想14专用翻译器。";
        }
    }

    public static String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }
}