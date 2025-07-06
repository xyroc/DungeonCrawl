package xiroc.dungeoncrawl.dungeon.blueprint.anchor;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.DungeonCrawl;

public interface BuiltinAnchorTypes {
    ResourceLocation JUNCTURE = DungeonCrawl.locate("juncture");
    ResourceLocation ENTRANCE = DungeonCrawl.locate("entrance");
    ResourceLocation STAIRCASE = DungeonCrawl.locate("staircase");
    ResourceLocation CORRIDOR = DungeonCrawl.locate("corridor");
}