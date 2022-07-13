# BrickCreatures

An extension for [Minestom](https://github.com/Minestom/Minestom) to create persistent NPCs.

## Install

Get the [release](https://github.com/GufliMC/BrickCreatures/releases)
and place it in the extension folder of your minestom server.

### Dependencies

* [BrickI18n](https://github.com/GufliMC/BrickI18n)

## Usage

### Creatures

A creature contains all the necessary information that makes the entity unique (name, skin, traits, ...). A single
creature can be used for multiple spawns, changing a creature will also update all spawns.

| Command                              | Permission                  |
|--------------------------------------|-----------------------------|
| /bc list                             | brickcreatures.list         |
| /bc create (name) (entitytype)       | brickcreatures.create       |
| /bc delete (creature)                | brickcreatures.delete       |
| /bc setskin (creature) (playername)  | brickcreatures.setskin      |
| /bc setmeta (creature) (key) (value) | brickcreatures.setmeta      |
| /bc tphere (creature)                | brickcreatures.tphere       |
| /bc lookhere (creature)              | brickcreatures.lookhere     |
| /bc trait add (creature) (trait)     | brickcreatures.trait.add    |
| /bc trait remove (creature) (trait)  | brickcreatures.trait.remove |
| /bc trait list (creature)            | brickcreatures.trait.list   |

## Database

You can change the database settings in the `config.json`.

```json
{
  "database": {
    "dsn": "jdbc:h2:file:./extensions/BrickCreatures/data/database.h2",
    "username": "dbuser",
    "password": "dbuser"
  }
}
```

MySQL is supported, use the following format:

````
"dsn": "jdbc:mysql://<hostname>:<ip>/<database>"
````

## API

### Gradle

```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}

dependencies {
    implementation 'com.guflimc.brick.creatures:minestom-api:1.0-SNAPSHOT'
}
```

### Usage

Check the [javadocs](https://guflimc.github.io/BrickCreatures/)

```java

MinestomCreature creature = MinestomCreatureAPI.get().create(EntityType.PLAYER);
MinestomCreatureAPI.get().persist(creature); // insert into database
MinestomCreatureAPI.get().merge(creature); // update in database
MinestomCreatureAPI.get().remove(creature); // delete from database

MinestomCreatureAPI.get().registerTrait(new TraitKey(...));

```

