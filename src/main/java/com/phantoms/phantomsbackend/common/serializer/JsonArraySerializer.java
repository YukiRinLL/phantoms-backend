package com.phantoms.phantomsbackend.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import co.casterlabs.rakurai.json.element.JsonArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonArraySerializer extends JsonSerializer<JsonArray> {
    @Override
    public void serialize(JsonArray value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            list.add(value.get(i));
        }
        gen.writeObject(list);
    }
}