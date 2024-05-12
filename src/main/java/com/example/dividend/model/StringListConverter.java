package com.example.dividend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;

import java.util.List;

@Convert
public class StringListConverter implements AttributeConverter<List<String>,String> {
   private final ObjectMapper mapper= new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(List<String> roleList) {
        try {
            return this.mapper.writeValueAsString(roleList);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String date) {
        try {
            return this.mapper.readValue(date, List.class);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}
