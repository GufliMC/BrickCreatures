# BrickNPCs

An extension for [Minestom](https://github.com/Minestom/Minestom) to create persistent NPCs.

## Install

Get the [release](https://github.com/MinestomBrick/BrickNPCs/releases)
and place it in the extension folder of your minestom server.

### Dependencies
* [BrickI18n](https://github.com/MinestomBrick/BrickI18n)


## Usage
### Templates

An NPC templatate contains all the necessary information that makes the persistentCreature unique (name, skin, traits, ...). 
A single template can be used for multiple npcs, changing a template will also update all npcs.

| Command                                        | Permission                         |
|------------------------------------------------|------------------------------------|
| /bn template list                              | bricknpcs.template.list            |
| /bn template create (name) (entitytype)        | bricknpcs.template.create          |
| /bn template delete (template)                 | bricknpcs.template.delete          |
| /bn template edit customname (template) (name) | bricknpcs.template.edit.customname |
| /bn template edit skin (template) (player)     | bricknpcs.template.edit.skin       |

### Spawns

A spawn is a position in the world where an persistentCreature is spawned with a specific template.
You can create multiple spawns with the same template.

| Command                            | Permission                    |
|------------------------------------|-------------------------------|
| /bn spawn list                     | bricknpcs.spawn.list          |
| /bn spawn create (name) (template) | bricknpcs.spawn.create        |
| /bn spawn delete (spawn)           | bricknpcs.spawn.delete        |
| /bn spawn edit lookhere (spawn)    | bricknpcs.spawn.edit.lookhere |

### NPCS

You can instantly create an template and a spawn of the same name with the following command.


| Command                            | Permission           |
|------------------------------------|----------------------|
| /bn persistentCreature create (name) (entitytype) | bricknpcs.persistentCreature.create |

## Database

You can change the database settings in the `config.json`.

```json
{
  "database": {
    "dsn": "jdbc:h2:file:./extensions/BrickNPCs/data/database.h2",
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

### Maven
```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}

dependencies {
    implementation 'org.minestombrick.npcs:api:1.0-SNAPSHOT'
}
```

### Usage

Check the [javadocs](https://minestombrick.github.io/BrickNPCs/)

