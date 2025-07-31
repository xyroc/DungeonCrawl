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
        ResourceKey<Blueprint> DARK_HALL = key(TemplateKeys.Room.DARK_HALL);
        ResourceKey<Blueprint> DINER = key(TemplateKeys.Room.DINER);
        ResourceKey<Blueprint> ENIKO = key(TemplateKeys.Room.ENIKO);
        ResourceKey<Blueprint> LIBRARY = key(TemplateKeys.Room.LIBRARY);
        ResourceKey<Blueprint> LOWER_STAIRCASE = key(TemplateKeys.Room.LOWER_STAIRCASE);
        ResourceKey<Blueprint> SARCOPHAGUS = key(TemplateKeys.Room.SARCOPHAGUS);
        ResourceKey<Blueprint> SMITHY = key(TemplateKeys.Room.SMITHY);
        ResourceKey<Blueprint> UPPER_STAIRCASE = key(TemplateKeys.Room.UPPER_STAIRCASE);
    }

    interface Part {
        ResourceLocation FLOOR_3X3 = TemplateKeys.Part.FLOOR_3X3;
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
