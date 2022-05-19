package com.guflimc.brick.creatures.api.domain;

public interface Trait {

    String id();

    TraitHandler<?> newHandler(Object entity);

}
