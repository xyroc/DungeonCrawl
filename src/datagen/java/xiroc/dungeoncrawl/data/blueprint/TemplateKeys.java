package xiroc.dungeoncrawl.data.blueprint;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;

public interface TemplateKeys {
    interface Entrance {
        ResourceLocation __ENTRANCE__ = DataGen.resource("entrance");

        ResourceLocation ENIKO_TOWER = DataGen.resource(__ENTRANCE__, "eniko_tower");
    }

    interface Room {
        ResourceLocation __ROOM__ = DataGen.resource("room");

        ResourceLocation BEDROOM = DataGen.resource(__ROOM__, "bedroom");
        ResourceLocation BRICK = DataGen.resource(__ROOM__, "brick");
        ResourceLocation CORNER = DataGen.resource(__ROOM__, "corner");
        ResourceLocation DARK_HALL = DataGen.resource(__ROOM__, "dark_hall");
        ResourceLocation DINER = DataGen.resource(__ROOM__, "diner");
        ResourceLocation ENIKO = DataGen.resource(__ROOM__, "eniko");
        ResourceLocation FORT = DataGen.resource(__ROOM__, "fort");
        ResourceLocation LIBRARY = DataGen.resource(__ROOM__, "library");
        ResourceLocation POTION_LAB = DataGen.resource(__ROOM__, "potion_lab");
        ResourceLocation SARCOPHAGUS = DataGen.resource(__ROOM__, "sarcophagus");
        ResourceLocation SMITHY = DataGen.resource(__ROOM__, "smithy");
        ResourceLocation SPIDER = DataGen.resource(__ROOM__, "spider");
        ResourceLocation STORAGE = DataGen.resource(__ROOM__, "storage");

        ResourceLocation __STAIRCASE__ = DataGen.resource(__ROOM__, "staircase");
        ResourceLocation STAIRCASE_LOWER = DataGen.resource(__STAIRCASE__, "lower");
        ResourceLocation STAIRCASE_UPPER = DataGen.resource(__STAIRCASE__, "upper");

        ResourceLocation __CRYPT__ = DataGen.resource(__ROOM__, "crypt");
        ResourceLocation CRYPT_TURN = DataGen.resource(__CRYPT__, "turn");
    }

    interface Part {
        ResourceLocation __PART__ = DataGen.resource("part");

        ResourceLocation __CEILING__ = DataGen.resource(__PART__, "ceiling");

        ResourceLocation __CEILING_7X7__ = DataGen.resource(__CEILING__, "7x7");
        ResourceLocation CEILING_7X7_DOME = DataGen.resource(__CEILING_7X7__, "dome");
        ResourceLocation CEILING_7X7_FLAT = DataGen.resource(__CEILING_7X7__, "flat");
        ResourceLocation CEILING_7X7_LAVA_PIT = DataGen.resource(__CEILING_7X7__, "lava_pit");

        ResourceLocation __CEILING_9X3__ = DataGen.resource(__CEILING__, "9x3");
        ResourceLocation CEILING_9X3_FLAT = DataGen.resource(__CEILING_9X3__, "flat");
        ResourceLocation CEILING_9X3_ORNATE = DataGen.resource(__CEILING_9X3__, "ornate");

        ResourceLocation __CEILING_CENTERED__ = DataGen.resource(__CEILING__, "centered");
        ResourceLocation __CEILING_CENTERED_5X5__ = DataGen.resource(__CEILING_CENTERED__, "5x5");
        ResourceLocation CEILING_CENTERED_5X5_FLAT = DataGen.resource(__CEILING_CENTERED_5X5__, "flat");
        ResourceLocation CEILING_CENTERED_5X5_ORNATE = DataGen.resource(__CEILING_CENTERED_5X5__, "ornate");
        ResourceLocation CEILING_CENTERED_5X5_RAISED = DataGen.resource(__CEILING_CENTERED_5X5__, "raised");

        ResourceLocation __FLOOR__ = DataGen.resource(__PART__, "floor");
        ResourceLocation FLOOR_3X3 = DataGen.resource(__FLOOR__, "3x3");
        ResourceLocation FLOOR_5x5 = DataGen.resource(__FLOOR__, "5x5");

        ResourceLocation LIBRARY_ENTRANCE = DataGen.resource(Room.LIBRARY, "entrance");
        ResourceLocation LIBRARY_DESK = DataGen.resource(Room.LIBRARY, "desk");
        ResourceLocation LIBRARY_FLOWERS = DataGen.resource(Room.LIBRARY, "flowers");

        ResourceLocation DARK_HALL_OPEN = DataGen.resource(Room.DARK_HALL, "open");
        ResourceLocation DARK_HALL_CLOSED = DataGen.resource(Room.DARK_HALL, "closed");

        ResourceLocation DINER_OPEN = DataGen.resource(Room.DINER, "open");
        ResourceLocation DINER_CLOSED_TABLE = DataGen.resource(Room.DINER, "closed/table");
        ResourceLocation DINER_CLOSED_STORAGE = DataGen.resource(Room.DINER, "closed/storage");

        ResourceLocation FORT_SPAWNER_PILLAR = DataGen.resource(Room.FORT, "spawner_pillar");

        ResourceLocation __CRYPT__ = DataGen.resource(__PART__, "crypt");

        ResourceLocation CRYPT_GRAVES = DataGen.resource(__CRYPT__, "graves");
        ResourceLocation CRYPT_GRAVES_WALL = DataGen.resource(CRYPT_GRAVES, "wall");

        ResourceLocation __CRYPT_SARCOPHAGUS__ = DataGen.resource(__CRYPT__, "sarcophagus");
        ResourceLocation SARCOPHAGUS_SMALL = DataGen.resource(__CRYPT_SARCOPHAGUS__, "small");

        ResourceLocation __LAB__ = DataGen.resource(__PART__, "lab");
        ResourceLocation __LAB_CONTAINER__ = DataGen.resource(__LAB__, "container");

        ResourceLocation LAB_CONTAINER_KELP = DataGen.resource(__LAB_CONTAINER__, "kelp");
        ResourceLocation LAB_CONTAINER_SPAWNER = DataGen.resource(__LAB_CONTAINER__, "spawner");
        ResourceLocation LAB_CONTAINER_TORCHFLOWER = DataGen.resource(__LAB_CONTAINER__, "torchflower");
        ResourceLocation LAB_CONTAINER_WHEAT = DataGen.resource(__LAB_CONTAINER__, "wheat");
    }

    interface Corridor {
        ResourceLocation __CORRIDOR__ = DataGen.resource("corridor");

        interface Side {
            ResourceLocation __SIDE__ = DataGen.resource(__CORRIDOR__, "side");

            ResourceLocation BASE = DataGen.resource(__SIDE__, "base");
            ResourceLocation CROPS = DataGen.resource(__SIDE__, "crops");
            ResourceLocation DOOR = DataGen.resource(__SIDE__, "door");
            ResourceLocation FIRE = DataGen.resource(__SIDE__, "fire");
            ResourceLocation FLOWER_POT = DataGen.resource(__SIDE__, "flower_pot");
            ResourceLocation MASONRY = DataGen.resource(__SIDE__, "masonry");
        }

        interface Segment {
            ResourceLocation __SEGMENT__ = DataGen.resource(__CORRIDOR__, "segment");

            ResourceLocation ARCH = DataGen.resource(__SEGMENT__, "arch");
            ResourceLocation BASE = DataGen.resource(__SEGMENT__, "base");
        }
    }
}
