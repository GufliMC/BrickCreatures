package com.guflimc.brick.creatures.minestom.metadata;

import com.google.gson.Gson;
import com.guflimc.brick.creatures.common.metadata.MetadataSerializer;
import net.minestom.server.entity.Metadata;

public class MinestomMetadataSerializer extends MetadataSerializer {

    private static final Gson gson = new Gson();

    @Override
    public String serialize(Object metadata) {
        return gson.toJson(metadata);
    }

    @Override
    public Object deserialize(String serialized) {
        return gson.fromJson(serialized, Metadata.class);
    }
}
