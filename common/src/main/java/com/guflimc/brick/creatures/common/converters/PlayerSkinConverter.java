package com.guflimc.brick.creatures.common.converters;

import com.google.gson.Gson;
import com.guflimc.brick.creatures.api.meta.PlayerSkin;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PlayerSkinConverter implements AttributeConverter<PlayerSkin, String> {

    private static final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(PlayerSkin attribute) {
        if ( attribute == null ) return null;
        return gson.toJson(attribute);
    }

    @Override
    public PlayerSkin convertToEntityAttribute(String dbData) {
        if ( dbData == null ) return null;
        return gson.fromJson(dbData, PlayerSkin.class);
    }
}