package com.phantoms.phantomsbackend.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.phantoms.phantomsbackend.common.serializer.JsonArraySerializer;
import co.casterlabs.rakurai.json.element.JsonArray;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(JsonArray.class, new JsonArraySerializer());
        mapper.registerModule(module);
        return mapper;
    }
}