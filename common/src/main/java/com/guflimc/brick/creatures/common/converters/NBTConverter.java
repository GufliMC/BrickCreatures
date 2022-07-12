package com.guflimc.brick.creatures.common.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.parser.SNBTParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

@Converter
public class NBTConverter implements AttributeConverter<NBT, String> {

    @Override
    public String convertToDatabaseColumn(NBT attribute) {
        if (attribute == null) return null;
        return attribute.toSNBT();
    }

    @Override
    public NBT convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;

        try (Reader r = new StringReader(dbData)) {
            return (NBTCompound) new SNBTParser(r).parse();
        } catch (IOException | NBTException e) {
            e.printStackTrace();
        }
        return null;
    }
}