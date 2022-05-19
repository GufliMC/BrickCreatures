package com.guflimc.brick.creatures.common.converters;

import com.guflimc.brick.creatures.common.metadata.MetadataSerializer;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MetadataConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if ( attribute == null ) return null;
        return MetadataSerializer.SERIALIZER.serialize(attribute);
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if ( dbData == null ) return null;
        return MetadataSerializer.SERIALIZER.deserialize(dbData);
    }
}