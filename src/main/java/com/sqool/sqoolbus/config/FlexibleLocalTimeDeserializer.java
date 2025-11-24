package com.sqool.sqoolbus.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom deserializer that handles LocalTime in multiple formats:
 * - String format: "HH:mm:ss" or "HH:mm"
 * - Object format: {"hour": 8, "minute": 30, "second": 0}
 */
public class FlexibleLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER_SHORT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        // Handle string format
        if (node.isTextual()) {
            String timeStr = node.asText();
            try {
                // Try HH:mm:ss format first
                return LocalTime.parse(timeStr, TIME_FORMATTER);
            } catch (Exception e1) {
                try {
                    // Try HH:mm format
                    return LocalTime.parse(timeStr, TIME_FORMATTER_SHORT);
                } catch (Exception e2) {
                    // Try default ISO format
                    return LocalTime.parse(timeStr);
                }
            }
        }
        
        // Handle object format {"hour": 8, "minute": 30, "second": 0}
        if (node.isObject()) {
            int hour = node.has("hour") ? node.get("hour").asInt() : 0;
            int minute = node.has("minute") ? node.get("minute").asInt() : 0;
            int second = node.has("second") ? node.get("second").asInt() : 0;
            return LocalTime.of(hour, minute, second);
        }
        
        return null;
    }
}
