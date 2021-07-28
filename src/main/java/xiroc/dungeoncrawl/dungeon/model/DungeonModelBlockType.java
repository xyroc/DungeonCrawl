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

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

import java.util.Random;
import java.util.function.Function;

public enum DungeonModelBlockType {

    AIR((block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> DungeonBlocks.CAVE_AIR),

    // Types with Theme Factories
    SOLID           (tFactory(Theme::getSolid), PlacementBehaviour.SOLID),
    SOLID_STAIRS    (tFactory(Theme::getSolidStairs), PlacementBehaviour.SOLID),
    SOLID_SLAB      (tFactory(Theme::getSolidSlab), PlacementBehaviour.SOLID),
    GENERIC         (tFactory(Theme::getGeneric)),
    SLAB            (tFactory(Theme::getSlab)),
    SOLID_PILLAR    (tFactory(Theme::getPillar), PlacementBehaviour.SOLID, true),
    SOLID_FLOOR     (tFactory(Theme::getFloor), PlacementBehaviour.SOLID),
    FENCING         (tFactory(Theme::getFencing)),
    FLOOR           (tFactory(Theme::getFloor), PlacementBehaviour.RANDOM_IF_SOLID_NEARBY),
    FLUID           (tFactory(Theme::getFluid)),
    LOOSE_GROUND    (tFactory(Theme::getFloor)),
    STAIRS          (tFactory(Theme::getStairs)),
    WALL            (tFactory(Theme::getWall)),

    // Types with Sub-Theme Factories
    PILLAR                      (sFactory(Theme.SecondaryTheme::getPillar), true),
    MATERIAL_STAIRS             (sFactory(Theme.SecondaryTheme::getStairs)),
    TRAPDOOR                    (sFactory(Theme.SecondaryTheme::getTrapDoor)),
    DOOR                        (sFactory(Theme.SecondaryTheme::getDoor)),
    FENCE                       (sFactory(Theme.SecondaryTheme::getFence)),
    FENCE_GATE                  (sFactory(Theme.SecondaryTheme::getFenceGate)),
    MATERIAL_SLAB               (sFactory(Theme.SecondaryTheme::getSlab)),
    MATERIAL_BUTTON             (sFactory(Theme.SecondaryTheme::getButton)),
    MATERIAL_PRESSURE_PLATE     (sFactory(Theme.SecondaryTheme::getPressurePlate)),
    MATERIAL                    (sFactory(Theme.SecondaryTheme::getMaterial)),

    // Other
    CHEST((block, rotation, world, pos, theme, subTheme, rand, variation,
           stage) -> block.create(Blocks.CHEST.getDefaultState(), rotation)),
    SKULL((block, rotation, world, pos, theme, subTheme, rand, variation,
           stage) -> {
        BlockState state = block.create(Blocks.SKELETON_SKULL.getDefaultState(), rotation);
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
            return state;
        }
        return state;
    }),
    CARPET((block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
        Block b = block.variation != null && variation != null ?
                DungeonBlocks.CARPET[(block.variation + variation[block.variation % variation.length]) % DungeonBlocks.CARPET.length]
                : ForgeRegistries.BLOCKS.getValue(block.blockName);
        if (b == null) {
            b = DungeonBlocks.CARPET[rand.nextInt(DungeonBlocks.CARPET.length)];
        }
        return block.create(b.getDefaultState());
    }),
    OTHER((block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
        return block.create(block.getBlock().getDefaultState(), rotation);
    });

    /**
     * A hash table that holds each {@link DungeonModelBlockType} with its corresponding name as the key.
     * Also contains aliases of renamed types.
     */
    public static final ImmutableMap<String, DungeonModelBlockType> NAME_TO_TYPE;

    static {
        ImmutableMap.Builder<String, DungeonModelBlockType> builder = new ImmutableMap.Builder<>();
        for (DungeonModelBlockType type : values()) {
            builder.put(type.name(), type);
        }

        // Renamed types
        builder.put("NORMAL", GENERIC);
        builder.put("VANILLA_WALL", WALL);
        builder.put("WOODEN_BUTTON", MATERIAL_BUTTON);
        builder.put("WOODEN_SLAB", MATERIAL_SLAB);
        builder.put("WOODEN_PRESSURE_PLATE", MATERIAL_PRESSURE_PLATE);

        // Removed types
        builder.put("NORMAL_2", AIR);
        builder.put("GENERIC_SECONDARY", AIR);
        builder.put("SPAWNER", AIR);
        builder.put("BARREL", AIR);
        NAME_TO_TYPE = builder.build();
    }

    public final BlockFactory blockFactory;
    public final PlacementBehaviour placementBehavior;

    private final boolean isPillar;

    DungeonModelBlockType(BlockFactory blockFactory) {
        this(blockFactory, PlacementBehaviour.NON_SOLID);
    }

    DungeonModelBlockType(BlockFactory blockFactory, boolean isPillar) {
        this(blockFactory, PlacementBehaviour.NON_SOLID, isPillar);
    }

    DungeonModelBlockType(BlockFactory blockFactory, PlacementBehaviour placementBehavior) {
        this(blockFactory, placementBehavior, false);
    }

    DungeonModelBlockType(BlockFactory blockFactory, PlacementBehaviour placementBehavior, boolean isPillar) {
        this.blockFactory = blockFactory;
        this.placementBehavior = placementBehavior;
        this.isPillar = isPillar;
    }

    public boolean isPillar() {
        return isPillar;
    }

    public boolean isSolid(IWorld world, BlockPos pos, Random rand, int relativeX, int relativeY, int relativeZ) {
        return Config.SOLID.get() || placementBehavior.function.isSolid(world, pos, rand, relativeX, relativeY, relativeZ);
    }

    public static DungeonModelBlockType get(Block block, ModelBlockDefinition definition) {
        if (definition.containsBlock(block)) {
            return definition.getType(block);
        } else if (definition.fallback != null && definition.fallback.containsBlock(block)) {
            return definition.fallback.getType(block);
        }
        if (BlockTags.CARPETS.contains(block)) {
            return CARPET;
        }
        return OTHER;
    }

    /**
     * A functional interface used to generate a BlockState from a
     * DungeonModelBlock.
     */
    @FunctionalInterface
    public interface BlockFactory {

        BlockState get(DungeonModelBlock block, Rotation rotation, IWorld world, BlockPos pos, Theme theme,
                       Theme.SecondaryTheme secondaryTheme, Random rand, byte[] variation, int stage);

    }

    /**
     * Creates a block factory that pulls from a theme.
     *
     * @param blockSelector a function that selects the desired field from the theme
     * @return the block factory
     */
    private static BlockFactory tFactory(Function<Theme, IBlockStateProvider> blockSelector) {
        return (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(blockSelector.apply(theme).get(pos, rotation), rotation);
    }

    /**
     * Creates a block factory that pulls from a secondary theme.
     *
     * @param blockSelector a function that selects the desired field from the sub-theme
     * @return the block factory
     */
    private static BlockFactory sFactory(Function<Theme.SecondaryTheme, IBlockStateProvider> blockSelector) {
        return (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(blockSelector.apply(subTheme).get(pos, rotation), rotation);
    }

}
