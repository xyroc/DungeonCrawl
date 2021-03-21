/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.dungeon.model;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.config.Config;

import java.util.Hashtable;
import java.util.Random;

public enum DungeonModelBlockType {

    AIR,
    SOLID_STAIRS(PlacementBehaviour.SOLID),
    SOLID(PlacementBehaviour.SOLID),
    GENERIC,
    GENERIC_SECONDARY(PlacementBehaviour.SOLID),
    PILLAR,
    FLOOR(PlacementBehaviour.RANDOM_IF_SOLID_NEARBY),
    MATERIAL_STAIRS,
    STAIRS,
    TRAPDOOR,
    SLAB,
    SOLID_SLAB(PlacementBehaviour.SOLID),
    DOOR,
    FENCE,
    FENCE_GATE,
    WOODEN_SLAB,
    WOODEN_BUTTON,
    WOODEN_PRESSURE_PLATE,
    WALL,
    MATERIAL,
    SKULL,
    CARPET,
    OTHER;

    public final PlacementBehaviour placementBehavior;

    /**
     * A hash table that holds each {@link DungeonModelBlockType} with its corresponding name
     * as the key to allow looking up whether there is an existing type for a given name or not.
     */
    public static final Hashtable<String, DungeonModelBlockType> NAME_TO_TYPE = new Hashtable<>();


    DungeonModelBlockType() {
        this(PlacementBehaviour.NON_SOLID);
    }

    DungeonModelBlockType(PlacementBehaviour placementBehavior) {
        this.placementBehavior = placementBehavior;
    }

    public boolean isSolid(IWorld world, BlockPos pos, Random rand, int relativeX, int relativeY, int relativeZ) {
        return Config.SOLID.get() || placementBehavior.function.isSolid(world, pos, rand, relativeX, relativeY, relativeZ);
    }

    public static DungeonModelBlockType get(Block block, ModelBlockDefinition definition) {
        if (definition.definition.containsKey(block)) {
            return definition.definition.get(block);
        } else if (definition.fallback != null && definition.fallback.definition.containsKey(block)) {
            return definition.fallback.definition.get(block);
        }
        if (BlockTags.CARPETS.contains(block))
            return CARPET;
        return OTHER;
    }

    public static void buildNameTable() {
        for (DungeonModelBlockType type : values()) {
            NAME_TO_TYPE.put(type.name(), type);
        }
        // Renamed types
        NAME_TO_TYPE.put("NORMAL", GENERIC);
        NAME_TO_TYPE.put("NORMAL_2", GENERIC_SECONDARY);
        NAME_TO_TYPE.put("VANILLA_WALL", WALL);

        // Removed types
        NAME_TO_TYPE.put("SPAWNER", AIR);
        NAME_TO_TYPE.put("CHEST", AIR);
        NAME_TO_TYPE.put("BARREL", AIR);

    }

}
