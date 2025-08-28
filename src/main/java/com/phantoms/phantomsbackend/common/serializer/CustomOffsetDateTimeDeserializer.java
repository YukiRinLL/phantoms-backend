package com.phantoms.phantomsbackend.common.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class CustomOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String str = jsonParser.getText();
        try {
            return OffsetDateTime.parse(str, formatter);
        } catch (Exception e) {
            throw new JsonProcessingException("Invalid date format. Please use this format:'" + formatter.toString() + "'") {};
        }
    }

    public static SimpleModule createModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(OffsetDateTime.class, new CustomOffsetDateTimeDeserializer());
        return module;
    }
}