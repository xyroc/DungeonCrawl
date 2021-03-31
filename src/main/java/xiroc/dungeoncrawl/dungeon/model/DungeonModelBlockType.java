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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

import java.util.Hashtable;
import java.util.Random;
import java.util.function.Function;

public enum DungeonModelBlockType {

    AIR((block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> DungeonBlocks.CAVE_AIR),

    // Types with Theme Factories
    SOLID                   (tFactory(Theme::getSolid), PlacementBehaviour.SOLID),
    SOLID_STAIRS            (tFactory(Theme::getSolidStairs), PlacementBehaviour.SOLID),
    SOLID_SLAB              (tFactory(Theme::getSolidSlab), PlacementBehaviour.SOLID),
    GENERIC                 (tFactory(Theme::getGeneric)),
    SOLID_PILLAR            (tFactory(Theme::getPillar), PlacementBehaviour.SOLID),
    SOLID_FLOOR             (tFactory(Theme::getFloor)),
    FLOOR                   (tFactory(Theme::getFloor), PlacementBehaviour.RANDOM_IF_SOLID_NEARBY),
    STAIRS                  (tFactory(Theme::getStairs)),
    WALL                    (tFactory(Theme::getWall)),

    // Types with Sub-Theme Factories
    PILLAR                  (sFactory(Theme.SubTheme::getPillar)),
    MATERIAL_STAIRS         (sFactory(Theme.SubTheme::getStairs)),
    TRAPDOOR                (sFactory(Theme.SubTheme::getTrapDoor)),
    SLAB                    (sFactory(Theme.SubTheme::getSlab)),
    DOOR                    (sFactory(Theme.SubTheme::getDoor)),
    FENCE                   (sFactory(Theme.SubTheme::getFence)),
    FENCE_GATE              (sFactory(Theme.SubTheme::getFenceGate)),
    WOODEN_SLAB             (sFactory(Theme.SubTheme::getSlab)),
    WOODEN_BUTTON           (sFactory(Theme.SubTheme::getButton)),
    WOODEN_PRESSURE_PLATE   (sFactory(Theme.SubTheme::getPressurePlate)),
    MATERIAL                (sFactory(Theme.SubTheme::getMaterial)),

    // Other
    CHEST((block, rotation, world, pos, theme, subTheme, rand, variation,
            stage) -> block.create(Blocks.CHEST.getDefaultState(), rotation)),
    SKULL((block, rotation, world, pos, theme, subTheme, rand, variation,
           stage) -> {
        Tuple<BlockState, Boolean> skull = block.create(Blocks.SKELETON_SKULL.getDefaultState(), rotation);
        BlockState state = skull.getA();
        if (state.has(BlockStateProperties.ROTATION_0_15)) {
            int r = state.get(BlockStateProperties.ROTATION_0_15);
            int add = rand.nextInt(3);
            if (rand.nextBoolean()) {
                r -= add;
                if (r < 0)
                    r += 16;
            } else {
                r = (r + add) % 16;
            }
            state = state.with(BlockStateProperties.ROTATION_0_15, r);
            return new Tuple<>(state, skull.getB());
        }
        return skull;
    }),
    CARPET((block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
        Block b = block.variation != null && variation != null ?
                DungeonBlocks.CARPET[(block.variation + variation[block.variation]) % DungeonBlocks.CARPET.length]
                : ForgeRegistries.BLOCKS.getValue(block.resource);
        if (b == null) {
            b = DungeonBlocks.CARPET[rand.nextInt(DungeonBlocks.CARPET.length)];
        }
        return block.create(b.getDefaultState());
    }),
    OTHER((block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
        Block b = ForgeRegistries.BLOCKS.getValue(block.resource);
        if (b != null) {
            return block.create(b.getDefaultState(), rotation);
        } else {
            DungeonCrawl.LOGGER.warn("Unknown block {}", block.resource.toString());
            return DungeonBlocks.CAVE_AIR;
        }
    });

    public final BlockFactory blockFactory;
    public final PlacementBehaviour placementBehavior;

    /**
     * A hash table that holds each {@link DungeonModelBlockType} with its corresponding name
     * as the key to allow looking up whether there is an existing type for a given name or not.
     */
    public static final Hashtable<String, DungeonModelBlockType> NAME_TO_TYPE = new Hashtable<>();


    DungeonModelBlockType(BlockFactory blockFactory) {
        this(blockFactory, PlacementBehaviour.NON_SOLID);
    }

    DungeonModelBlockType(BlockFactory blockFactory, PlacementBehaviour placementBehavior) {
        this.blockFactory = blockFactory;
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
        NAME_TO_TYPE.put("VANILLA_WALL", WALL);

        // Removed types
        NAME_TO_TYPE.put("NORMAL_2", AIR);
        NAME_TO_TYPE.put("SPAWNER", AIR);
        NAME_TO_TYPE.put("BARREL", AIR);

    }

    /**
     * A functional interface used to generate a BlockState from a
     * DungeonModelBlock.
     */
    @FunctionalInterface
    public interface BlockFactory {

        Tuple<BlockState, Boolean> get(DungeonModelBlock block, Rotation rotation, IWorld world, BlockPos pos, Theme theme,
                                       Theme.SubTheme subTheme, Random rand, byte[] variation, int stage);

    }

    /**
     * Creates a block factory that pulls from a theme
     * @param blockSelector a function that selects the desired field from the theme
     * @return the block factory
     */
    private static BlockFactory tFactory(Function<Theme, IBlockStateProvider> blockSelector) {
        return (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(blockSelector.apply(theme).get(pos), rotation);
    }

    /**
     * Creates a block factory that pulls from a sub-theme
     * @param blockSelector a function that selects the desired field from the sub-theme
     * @return the block factory
     */
    private static BlockFactory sFactory(Function<Theme.SubTheme, IBlockStateProvider> blockSelector) {
        return (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(blockSelector.apply(subTheme).get(pos), rotation);
    }

}
