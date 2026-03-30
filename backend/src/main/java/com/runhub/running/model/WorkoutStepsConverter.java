package com.runhub.running.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class WorkoutStepsConverter implements AttributeConverter<List<WorkoutStep>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<WorkoutStep> steps) {
        if (steps == null) return "[]";
        try {
            return MAPPER.writeValueAsString(steps);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<WorkoutStep> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return MAPPER.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
