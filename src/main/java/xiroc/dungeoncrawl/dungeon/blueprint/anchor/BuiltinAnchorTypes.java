package xiroc.dungeoncrawl.dungeon.blueprint.anchor;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.DungeonCrawl;

public interface BuiltinAnchorTypes {
    /**
     * Marks attachment points for corridor segments
     */
    ResourceLocation CORRIDOR = DungeonCrawl.locate("corridor");
    /**
     * Marks attachment points for node room entrances.
     */
    ResourceLocation ENTRANCE = DungeonCrawl.locate("entrance");
    /**
     * Marks generic attachment points, such as for blueprint multi-parts.
     */
    ResourceLocation JUNCTURE = DungeonCrawl.locate("juncture");
    /**
     * Marks attachment points for staircase components.
     */
    ResourceLocation STAIRCASE = DungeonCrawl.locate("staircase");
}