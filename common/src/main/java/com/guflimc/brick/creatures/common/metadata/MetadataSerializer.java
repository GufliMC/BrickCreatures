package com.guflimc.brick.creatures.common.metadata;

public abstract class MetadataSerializer {

    public static MetadataSerializer SERIALIZER = null;

    public abstract String serialize(Object metadata);

    public abstract Object deserialize(String serialized);

}
