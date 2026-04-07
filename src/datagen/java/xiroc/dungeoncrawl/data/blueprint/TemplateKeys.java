package xiroc.dungeoncrawl.data.blueprint;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;

public interface TemplateKeys {
    interface Entrance {
        ResourceLocation __ENTRANCE = DataGen.resource("entrance");

        ResourceLocation ENIKO_TOWER = DataGen.resource(__ENTRANCE, "eniko_tower");
    }

    interface Room {
        ResourceLocation __ROOM = DataGen.resource("room");

        ResourceLocation BEDROOM = DataGen.resource(__ROOM, "bedroom");
        ResourceLocation BRICK = DataGen.resource(__ROOM, "brick");
        ResourceLocation CORNER = DataGen.resource(__ROOM, "corner");
        ResourceLocation DARK_HALL = DataGen.resource(__ROOM, "dark_hall");
        ResourceLocation DINER = DataGen.resource(__ROOM, "diner");
        ResourceLocation ENIKO = DataGen.resource(__ROOM, "eniko");
        ResourceLocation LIBRARY = DataGen.resource(__ROOM, "library");
        ResourceLocation POTION_LAB = DataGen.resource(__ROOM, "potion_lab");
        ResourceLocation SARCOPHAGUS = DataGen.resource(__ROOM, "sarcophagus");
        ResourceLocation SMITHY = DataGen.resource(__ROOM, "smithy");
        ResourceLocation STORAGE = DataGen.resource(__ROOM, "storage");

        ResourceLocation __STAIRCASE =  DataGen.resource(__ROOM, "staircase");
        ResourceLocation STAIRCASE_LOWER = DataGen.resource(__STAIRCASE, "lower");
        ResourceLocation STAIRCASE_UPPER = DataGen.resource(__STAIRCASE, "upper");
    }

    interface Part {
        ResourceLocation __PART = DataGen.resource("part");

        ResourceLocation __FLOOR = DataGen.resource(__PART, "floor");
        ResourceLocation FLOOR_3X3 = DataGen.resource(__FLOOR, "3x3");
        ResourceLocation FLOOR_5x5 = DataGen.resource(__FLOOR, "5x5");

        ResourceLocation LIBRARY_ENTRANCE = DataGen.resource(Room.LIBRARY, "entrance");
        ResourceLocation LIBRARY_DESK = DataGen.resource(Room.LIBRARY, "desk");
        ResourceLocation LIBRARY_FLOWERS = DataGen.resource(Room.LIBRARY, "flowers");

        ResourceLocation DARK_HALL_OPEN = DataGen.resource(Room.DARK_HALL, "open");
        ResourceLocation DARK_HALL_CLOSED = DataGen.resource(Room.DARK_HALL, "closed");

        ResourceLocation DINER_OPEN = DataGen.resource(Room.DINER, "open");
        ResourceLocation DINER_CLOSED_TABLE = DataGen.resource(Room.DINER, "closed/table");
        ResourceLocation DINER_CLOSED_STORAGE = DataGen.resource(Room.DINER, "closed/storage");

        ResourceLocation __LAB = DataGen.resource(__PART, "lab");
        ResourceLocation __LAB_CONTAINER = DataGen.resource(__LAB, "container");

        ResourceLocation LAB_CONTAINER_KELP = DataGen.resource(__LAB_CONTAINER, "kelp");
        ResourceLocation LAB_CONTAINER_SPAWNER = DataGen.resource(__LAB_CONTAINER, "spawner");
        ResourceLocation LAB_CONTAINER_TORCHFLOWER = DataGen.resource(__LAB_CONTAINER, "torchflower");
        ResourceLocation LAB_CONTAINER_WHEAT = DataGen.resource(__LAB_CONTAINER, "wheat");
    }

    interface Corridor {
        ResourceLocation __CORRIDOR = DataGen.resource("corridor");

        interface Side {
            ResourceLocation __SIDE = DataGen.resource(__CORRIDOR, "side");

            ResourceLocation BASE = DataGen.resource(__SIDE, "base");
            ResourceLocation CROPS = DataGen.resource(__SIDE, "crops");
            ResourceLocation DOOR = DataGen.resource(__SIDE, "door");
            ResourceLocation FIRE = DataGen.resource(__SIDE, "fire");
            ResourceLocation FLOWER_POT = DataGen.resource(__SIDE, "flower_pot");
            ResourceLocation MASONRY = DataGen.resource(__SIDE, "masonry");
        }

        interface Segment {
            ResourceLocation __SEGMENT = DataGen.resource(__CORRIDOR, "segment");

            ResourceLocation ARCH = DataGen.resource(__SEGMENT, "arch");
            ResourceLocation BASE = DataGen.resource(__SEGMENT, "base");
        }
    }
}
