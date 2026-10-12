package xiroc.dungeoncrawl.data.blueprint;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;

public interface BlueprintKeys {
    interface Entrance {
        ResourceKey<Blueprint> ENIKO_TOWER = key(TemplateKeys.Entrance.ENIKO_TOWER);
    }

    interface Room {
        ResourceLocation __ROOM__ = DataGen.resource("room");

        ResourceKey<Blueprint> BEDROOM = key(TemplateKeys.Room.BEDROOM);
        ResourceKey<Blueprint> BRICK = key(TemplateKeys.Room.BRICK);
        ResourceKey<Blueprint> CORNER = key(TemplateKeys.Room.CORNER);
        ResourceKey<Blueprint> DARK_HALL = key(TemplateKeys.Room.DARK_HALL);
        ResourceKey<Blueprint> DINER = key(TemplateKeys.Room.DINER);
        ResourceKey<Blueprint> ENIKO = key(TemplateKeys.Room.ENIKO);
        ResourceKey<Blueprint> FORT = key(TemplateKeys.Room.FORT);
        ResourceKey<Blueprint> LIBRARY = key(TemplateKeys.Room.LIBRARY);
        ResourceKey<Blueprint> POOL = key(TemplateKeys.Room.POOL);
        ResourceKey<Blueprint> POTION_LAB = key(TemplateKeys.Room.POTION_LAB);
        ResourceKey<Blueprint> PRISON = key(TemplateKeys.Room.PRISON);
        ResourceKey<Blueprint> SARCOPHAGUS = key(TemplateKeys.Room.SARCOPHAGUS);
        ResourceKey<Blueprint> SEWER = key(DataGen.resource(__ROOM__, "sewer"));
        ResourceKey<Blueprint> SMITHY = key(TemplateKeys.Room.SMITHY);
        ResourceKey<Blueprint> SPIDER = key(TemplateKeys.Room.SPIDER);
        ResourceKey<Blueprint> STAIRCASE_LOWER = key(TemplateKeys.Room.STAIRCASE_LOWER);
        ResourceKey<Blueprint> STAIRCASE_UPPER = key(TemplateKeys.Room.STAIRCASE_UPPER);
        ResourceKey<Blueprint> STORAGE = key(TemplateKeys.Room.STORAGE);

        ResourceKey<Blueprint> CRYPT_CROSSING = key(TemplateKeys.Room.CRYPT_CROSSING);
        ResourceKey<Blueprint> CRYPT_TURN = key(TemplateKeys.Room.CRYPT_TURN);
    }

    interface Part {
        ResourceKey<Blueprint> CEILING_7X7_DOME = key(TemplateKeys.Part.CEILING_7X7_DOME);
        ResourceKey<Blueprint> CEILING_7X7_FLAT = key(TemplateKeys.Part.CEILING_7X7_FLAT);
        ResourceKey<Blueprint> CEILING_7X7_LAVA_PIT = key(TemplateKeys.Part.CEILING_7X7_LAVA_PIT);
        ResourceKey<Blueprint> CEILING_9X3_FLAT = key(TemplateKeys.Part.CEILING_9X3_FLAT);
        ResourceKey<Blueprint> CEILING_9X3_ORNATE = key(TemplateKeys.Part.CEILING_9X3_ORNATE);
        ResourceKey<Blueprint> CEILING_CENTERED_5X5_FLAT = key(TemplateKeys.Part.CEILING_CENTERED_5X5_FLAT);
        ResourceKey<Blueprint> CEILING_CENTERED_5X5_ORNATE = key(TemplateKeys.Part.CEILING_CENTERED_5X5_ORNATE);
        ResourceKey<Blueprint> CEILING_CENTERED_5X5_RAISED = key(TemplateKeys.Part.CEILING_CENTERED_5X5_RAISED);

        ResourceKey<Blueprint> CRYPT_ENTRANCE_OPEN = key(TemplateKeys.Part.CRYPT_ENTRANCE_OPEN);
        ResourceKey<Blueprint> CRYPT_ENTRANCE_CLUSTER_OPEN = key(TemplateKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN);
        ResourceKey<Blueprint> CRYPT_ENTRANCE_CLUSTER_OPEN_LEFT = key(TemplateKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN_LEFT);
        ResourceKey<Blueprint> CRYPT_ENTRANCE_CLUSTER_OPEN_RIGHT = key(TemplateKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN_RIGHT);

        ResourceKey<Blueprint> CRYPT_WALL_MAUSOLEUM = key(TemplateKeys.Part.CRYPT_WALL_MAUSOLEUM);
        ResourceKey<Blueprint> CRYPT_WALL_PLAIN = key(TemplateKeys.Part.CRYPT_WALL_PLAIN);

        ResourceKey<Blueprint> ENTRANCE_CLOSED_5X5 = key(TemplateKeys.Part.ENTRANCE_CLOSED_5X5);
        ResourceKey<Blueprint> ENTRANCE_CLOSED_5X9 = key(TemplateKeys.Part.ENTRANCE_CLOSED_5X9);
        ResourceKey<Blueprint> ENTRANCE_FENCING_CLOSED_3X3 = key(TemplateKeys.Part.ENTRANCE_FENCING_CLOSED_3X3);
        ResourceKey<Blueprint> ENTRANCE_FENCING_OPEN_3X3 = key(TemplateKeys.Part.ENTRANCE_FENCING_OPEN_3X3);

        ResourceKey<Blueprint> FLOOR_3x3_MASONRY = key(DataGen.resource(TemplateKeys.Part.FLOOR_3X3, "masonry"));
        ResourceKey<Blueprint> FLOOR_3x3_SOLID = key(DataGen.resource(TemplateKeys.Part.FLOOR_3X3, "solid"));

        ResourceKey<Blueprint> FLOOR_5x5_SOLID = key(DataGen.resource(TemplateKeys.Part.FLOOR_5x5, "solid"));
        ResourceKey<Blueprint> FLOOR_5x5_FRAGILE = key(DataGen.resource(TemplateKeys.Part.FLOOR_5x5, "fragile"));

        ResourceKey<Blueprint> LIBRARY_ENTRANCE = key(TemplateKeys.Part.LIBRARY_ENTRANCE);
        ResourceKey<Blueprint> LIBRARY_DESK = key(TemplateKeys.Part.LIBRARY_DESK);
        ResourceKey<Blueprint> LIBRARY_FLOWERS = key(TemplateKeys.Part.LIBRARY_FLOWERS);

        ResourceKey<Blueprint> DARK_HALL_OPEN = key(TemplateKeys.Part.DARK_HALL_OPEN);
        ResourceKey<Blueprint> DARK_HALL_CLOSED = key(TemplateKeys.Part.DARK_HALL_CLOSED);

        ResourceKey<Blueprint> DINER_OPEN = key(TemplateKeys.Part.DINER_OPEN);
        ResourceKey<Blueprint> DINER_CLOSED_TABLE = key(TemplateKeys.Part.DINER_CLOSED_TABLE);
        ResourceKey<Blueprint> DINER_CLOSED_STORAGE = key(TemplateKeys.Part.DINER_CLOSED_STORAGE);

        ResourceKey<Blueprint> FORT_SPAWNER_PILLAR = key(TemplateKeys.Part.FORT_SPAWNER_PILLAR);

        ResourceKey<Blueprint> SARCOPHAGUS_SMALL_EMPTY = key(DataGen.resource(TemplateKeys.Part.SARCOPHAGUS_SMALL, "empty"));
        ResourceKey<Blueprint> SARCOPHAGUS_SMALL_GRAVE = key(DataGen.resource(TemplateKeys.Part.SARCOPHAGUS_SMALL, "grave"));

        ResourceLocation __LAB_CONTAINER__ = TemplateKeys.Part.__LAB_CONTAINER__;
        ResourceKey<Blueprint> LAB_CONTAINER_CROPS = key(DataGen.resource(__LAB_CONTAINER__, "crops"));
        ResourceKey<Blueprint> LAB_CONTAINER_FLOWER = key(DataGen.resource(__LAB_CONTAINER__, "flower"));
        ResourceKey<Blueprint> LAB_CONTAINER_KELP = key(DataGen.resource(__LAB_CONTAINER__, "kelp"));
        ResourceKey<Blueprint> LAB_CONTAINER_SPAWNER = key(DataGen.resource(__LAB_CONTAINER__, "spawner"));

        ResourceKey<Blueprint> PRISON_CELL = key(TemplateKeys.Part.PRISON_CELL);
        ResourceKey<Blueprint> PRISON_CELL_CORNER = key(TemplateKeys.Part.PRISON_CELL_CORNER);

        ResourceKey<Blueprint> PRISON_ENTRANCE_INSET = key(TemplateKeys.Part.PRISON_ENTRANCE_INSET);
        ResourceKey<Blueprint> PRISON_ENTRANCE_OPEN = key(TemplateKeys.Part.PRISON_ENTRANCE_OPEN);

        ResourceKey<Blueprint> SEWER_ENTRANCE_CLOSED = key(TemplateKeys.Part.SEWER_ENTRANCE_CLOSED);
        ResourceKey<Blueprint> SEWER_ENTRANCE_OPEN = key(TemplateKeys.Part.SEWER_ENTRANCE_OPEN);
    }

    interface Corridor {
        interface Segment {
            ResourceKey<Blueprint> ARCH = key(TemplateKeys.Corridor.Segment.ARCH);
            ResourceKey<Blueprint> BASE = key(TemplateKeys.Corridor.Segment.BASE);
        }

        interface Side {
            ResourceKey<Blueprint> BASE = key(TemplateKeys.Corridor.Side.BASE);
            ResourceKey<Blueprint> CROPS = key(TemplateKeys.Corridor.Side.CROPS);
            ResourceKey<Blueprint> DOOR = key(TemplateKeys.Corridor.Side.DOOR);
            ResourceKey<Blueprint> FIRE = key(TemplateKeys.Corridor.Side.FIRE);
            ResourceKey<Blueprint> FLOWER_POT = key(TemplateKeys.Corridor.Side.FLOWER_POT);
            ResourceKey<Blueprint> MASONRY = key(TemplateKeys.Corridor.Side.MASONRY);
        }
    }

    private static ResourceKey<Blueprint> key(ResourceLocation location) {
        return ResourceKey.create(DatapackRegistries.BLUEPRINT, location);
    }
}
