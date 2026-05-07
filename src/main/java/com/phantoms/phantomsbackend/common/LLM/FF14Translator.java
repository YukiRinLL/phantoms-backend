package com.phantoms.phantomsbackend.common.LLM;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class FF14Translator {
    private static LLMClient llmClientStatic;

    private final LLMClient llmClient;

    public FF14Translator(LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    @PostConstruct
    private void init() {
        llmClientStatic = this.llmClient;
    }

    public static String translate(String text) {
        try {
            return llmClientStatic.chat(FF14Prompt.getSystemPrompt(), text);
        } catch (Exception e) {
            e.printStackTrace();
            return "翻译失败：" + e.getMessage();
        }
    }
}