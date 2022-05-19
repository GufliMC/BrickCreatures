package com.guflimc.brick.creatures.api.meta;

public class PlayerSkin {

    private final String texture;
    private final String signature;

    public PlayerSkin(String texture, String signature) {
        this.texture = texture;
        this.signature = signature;
    }

    public String texture() {
        return texture;
    }

    public String signature() {
        return signature;
    }
}
