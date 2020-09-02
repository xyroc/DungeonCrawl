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
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;

import java.util.Random;

public enum DungeonModelBlockType {

    NONE(Blocks.BARRIER),
    SOLID_STAIRS(Blocks.STONE_BRICK_STAIRS, PlacementBehaviour.SOLID),
    SOLID(Blocks.STONE_BRICKS, PlacementBehaviour.SOLID),
    WALL(Blocks.COBBLESTONE),
    WALL_2(Blocks.OBSIDIAN),
    PILLAR(Blocks.OAK_LOG),
    FLOOR(Blocks.GRAVEL, PlacementBehaviour.RANDOM_IF_SOLID_NEARBY),
    MATERIAL_STAIRS(Blocks.OAK_STAIRS),
    STAIRS(Blocks.COBBLESTONE_STAIRS),
    SPAWNER(Blocks.SPAWNER),
    RARE_SPAWNER(Blocks.SPAWNER),
    RAND_WALL_SPAWNER(Blocks.SPAWNER),
    CHEST(Blocks.CHEST), RARE_CHEST(Blocks.CHEST),
    CHEST_50(Blocks.CHEST),
    RAND_WALL_AIR(Blocks.CRACKED_STONE_BRICKS),
    RAND_FLOOR_CHEST_SPAWNER(Blocks.CHEST),
    TRAPDOOR(Blocks.OAK_TRAPDOOR),
    SLAB(Blocks.COBBLESTONE_SLAB),
    SOLID_SLAB(Blocks.STONE_BRICK_SLAB, PlacementBehaviour.SOLID),
    WOODEN_SLAB(Blocks.OAK_SLAB),
    BARREL(Blocks.BARREL),
    DOOR(Blocks.OAK_DOOR),
    FENCE(Blocks.OAK_FENCE),
    FENCE_GATE(Blocks.OAK_FENCE_GATE),
    WOODEN_BUTTON(Blocks.OAK_BUTTON),
    WOODEN_PRESSURE_PLATE(Blocks.OAK_PRESSURE_PLATE),
    RAND_FLOOR_WATER(Blocks.CLAY),
    RAND_FLOOR_LAVA(Blocks.SOUL_SAND),
    RAND_BOOKSHELF_COBWEB(Blocks.BOOKSHELF),
    RAND_COBWEB_AIR(Blocks.COBWEB),
    VANILLA_WALL(Blocks.STONE_BRICK_WALL),
    MATERIAL(Blocks.OAK_PLANKS),
    SKULL(Blocks.SKELETON_SKULL),
    CARPET,
    OTHER;

    public final PlacementBehaviour placementBehavior;
    private final Block baseBlock;

    DungeonModelBlockType() {
        this(null, PlacementBehaviour.NON_SOLID);
    }

    DungeonModelBlockType(Block baseBlock) {
        this(baseBlock, PlacementBehaviour.NON_SOLID);
    }

    DungeonModelBlockType(Block baseBlock, PlacementBehaviour placementBehavior) {
        this.baseBlock = baseBlock;
        this.placementBehavior = placementBehavior;
    }

    public Block getBaseBlock(DungeonModelBlock block) {
        if (baseBlock != null) {
            return baseBlock;
        } else if (this == OTHER) {
            return ForgeRegistries.BLOCKS.getValue(block.resource);
        } else if (this == CARPET) {
            return DungeonBlocks.CARPET[block.variation];
        } else {
            return null;
        }
    }

    public boolean isSolid(IWorld world, BlockPos pos, Random rand, int relativeX, int relativeY, int relativeZ) {
        return Config.SOLID.get() || placementBehavior.function.isSolid(world, pos, rand, relativeX, relativeY, relativeZ);
    }

    public static DungeonModelBlockType get(Block block, int spawnerType, int chestType) {
        if (block == Blocks.AIR)
            return null;
        if (block == Blocks.OAK_PLANKS)
            return MATERIAL;
        if (block == Blocks.BEDROCK || block == Blocks.BARRIER)
            return NONE;
        if (block == Blocks.OAK_DOOR)
            return DOOR;
        if (block == Blocks.COBWEB)
            return RAND_COBWEB_AIR;
        if (block == Blocks.SOUL_SAND)
            return RAND_FLOOR_LAVA;
        if (block == Blocks.CRACKED_STONE_BRICKS)
            return RAND_WALL_AIR;
        if (block == Blocks.CLAY)
            return RAND_FLOOR_WATER;
        if (block == Blocks.BOOKSHELF)
            return RAND_BOOKSHELF_COBWEB;
        if (block == Blocks.OAK_STAIRS)
            return MATERIAL_STAIRS;
        if (block == Blocks.STONE_BRICK_STAIRS)
            return SOLID_STAIRS;
        if (block == Blocks.COBBLESTONE)
            return WALL;
        if (block == Blocks.STONE_BRICKS)
            return SOLID;
        if (block == Blocks.OAK_LOG)
            return PILLAR;
        if (block == Blocks.GRAVEL)
            return FLOOR;
        if (block == Blocks.COBBLESTONE_STAIRS)
            return STAIRS;
        if (block == Blocks.COBBLESTONE_SLAB)
            return SLAB;
        if (block == Blocks.STONE_BRICK_SLAB)
            return SOLID_SLAB;
        if (block == Blocks.OAK_SLAB)
            return WOODEN_SLAB;
        if (block == Blocks.OAK_BUTTON)
            return WOODEN_BUTTON;
        if (block == Blocks.OAK_FENCE)
            return FENCE;
        if (block == Blocks.OAK_FENCE_GATE)
            return FENCE_GATE;
        if (block == Blocks.OAK_PRESSURE_PLATE)
            return WOODEN_PRESSURE_PLATE;
        if (block == Blocks.OBSIDIAN)
            return WALL_2;
        if (block == Blocks.SPAWNER) {
            switch (spawnerType) {
                case 1:
                    return RARE_SPAWNER;
                case 2:
                    return RAND_WALL_SPAWNER;
                default:
                    return SPAWNER;
            }
        }
        if (block == Blocks.CHEST)
            switch (chestType) {
                case 1:
                    return RARE_CHEST;
                case 2:
                    return RAND_FLOOR_CHEST_SPAWNER;
                case 3:
                    return CHEST_50;
                default:
                    return CHEST;
            }
        if (block == Blocks.OAK_TRAPDOOR)
            return TRAPDOOR;
        if (block == Blocks.BARREL)
            return BARREL;
        if (block == Blocks.STONE_BRICK_WALL)
            return VANILLA_WALL;
        if (block == Blocks.SKELETON_SKULL) {
            return SKULL;
        }
        if (BlockTags.CARPETS.contains(block)) {
            return CARPET;
        }
        return OTHER;
    }

}
