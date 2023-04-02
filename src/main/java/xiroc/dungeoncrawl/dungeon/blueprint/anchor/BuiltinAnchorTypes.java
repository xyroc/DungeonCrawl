package xiroc.dungeoncrawl.dungeon.blueprint.anchor;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.DungeonCrawl;

public interface BuiltinAnchorTypes {
    ResourceLocation JUNCTURE = DungeonCrawl.locate("juncture");
    ResourceLocation ENTRANCE = DungeonCrawl.locate("entrance");
    ResourceLocation STAIRCASE = DungeonCrawl.locate("staircase");
    ResourceLocation MEGA_NODE_ENTRANCE = DungeonCrawl.locate("mega_node_entrance");
    ResourceLocation MEGA_NODE_CONNECTOR = DungeonCrawl.locate("mega_node_connector");
}