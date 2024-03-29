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
import xiroc.dungeoncrawl.dungeon.PlacementConfiguration;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;

public enum DungeonModelBlockType {

    AIR((block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> DungeonBlocks.CAVE_AIR),

    // Types with Theme Factories
    SOLID               (tFactory(Theme::getSolid),         new TypeBuilder(PlacementConfiguration::getSolid).expandable().placement(PlacementBehaviour.SOLID)),
    SOLID_STAIRS        (tFactory(Theme::getSolidStairs),   new TypeBuilder(PlacementConfiguration::getSolidStairs).placement(PlacementBehaviour.SOLID)),
    SOLID_SLAB          (tFactory(Theme::getSolidSlab),     new TypeBuilder(PlacementConfiguration::getSolidSlab).placement(PlacementBehaviour.SOLID)),
    GENERIC             (tFactory(Theme::getGeneric),       new TypeBuilder(PlacementConfiguration::getGeneric).expandable()),
    GENERIC_OR_FENCING  (tFactory(Theme::getGeneric),       new TypeBuilder(PlacementConfiguration::getGenericOrFencing).placement(PlacementBehaviour.NON_SOLID.withAirBlock((theme, secondaryTheme) -> theme.getFencing()))),
    SLAB                (tFactory(Theme::getSlab),          PlacementConfiguration::getSlab),
    SOLID_PILLAR        (tFactory(Theme::getPillar),        new TypeBuilder(PlacementConfiguration::getSolidPillar).expandable().pillar().placement(PlacementBehaviour.SOLID)),
    SOLID_FLOOR         (tFactory(Theme::getFloor),         new TypeBuilder(PlacementConfiguration::getSolidFloor).expandable().placement(PlacementBehaviour.SOLID)),
    FENCING             (tFactory(Theme::getFencing),       PlacementConfiguration::getFencing),
    FLOOR               (tFactory(Theme::getFloor),         new TypeBuilder(PlacementConfiguration::getFloor).placement(PlacementBehaviour.SMALL_GRID)),
    FLUID               (tFactory(Theme::getFluid),         PlacementConfiguration::getFluid),
    LOOSE_GROUND        (tFactory(Theme::getFloor),         PlacementConfiguration::getLooseGround),
    STAIRS              (tFactory(Theme::getStairs),        PlacementConfiguration::getStairs),
    WALL                (tFactory(Theme::getWall),          PlacementConfiguration::getWall),

    // Types with Secondary-Theme Factories
    PILLAR                      (sFactory(SecondaryTheme::getPillar),           new TypeBuilder(PlacementConfiguration::getPillar).expandable().pillar()),
    MATERIAL_STAIRS             (sFactory(SecondaryTheme::getStairs),           PlacementConfiguration::getStairs),
    TRAPDOOR                    (sFactory(SecondaryTheme::getTrapDoor),         PlacementConfiguration::getTrapdoor),
    DOOR                        (sFactory(SecondaryTheme::getDoor),             PlacementConfiguration::getDoor),
    FENCE                       (sFactory(SecondaryTheme::getFence),            PlacementConfiguration::getFence),
    FENCE_GATE                  (sFactory(SecondaryTheme::getFenceGate),        PlacementConfiguration::getFenceGate),
    MATERIAL_SLAB               (sFactory(SecondaryTheme::getSlab),             PlacementConfiguration::getSlab),
    MATERIAL_BUTTON             (sFactory(SecondaryTheme::getButton),           PlacementConfiguration::getMaterialButton),
    MATERIAL_PRESSURE_PLATE     (sFactory(SecondaryTheme::getPressurePlate),    PlacementConfiguration::getMaterialPressurePlate),
    MATERIAL                    (sFactory(SecondaryTheme::getMaterial),         new TypeBuilder(PlacementConfiguration::getMaterial).expandable()),

    // Other

    CHEST((block, rotation, world, pos, theme, subTheme, rand, variation,
           stage) -> block.create(Blocks.CHEST.defaultBlockState(), world, pos, rotation), PlacementConfiguration::getChest),
    SKULL((block, rotation, world, pos, theme, subTheme, rand, variation,
           stage) -> {
        BlockState state = block.create(Blocks.SKELETON_SKULL.defaultBlockState(), world, pos, rotation);
        if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
            int r = state.getValue(BlockStateProperties.ROTATION_16);
            int add = rand.nextInt(3);
            if (rand.nextBoolean()) {
                r -= add;
                if (r < 0)
                    r += 16;
            } else {
                r = (r + add) % 16;
            }
            state = state.setValue(BlockStateProperties.ROTATION_16, r);
            return state;
        }
        return state;
    }, PlacementConfiguration::getSkull),
    CARPET((block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
        Block b = block.variation != null && variation != null ?
                DungeonBlocks.CARPET[(block.variation + variation[block.variation % variation.length]) % DungeonBlocks.CARPET.length]
                : ForgeRegistries.BLOCKS.getValue(block.blockName);
        if (b == null) {
            b = DungeonBlocks.CARPET[rand.nextInt(DungeonBlocks.CARPET.length)];
        }
        return block.create(b.defaultBlockState());
    }, PlacementConfiguration::getCarpet),
    OTHER((block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
        return block.create(block.getBlock().defaultBlockState(), world, pos, rotation);
    }, PlacementConfiguration::getOther);

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
    public final PlacementBehaviour defaultPlacementBehavior;
    public final Function<PlacementConfiguration, PlacementBehaviour> placementBehaviourFromConfig;

    private final boolean isPillar; // Whether this block type represents a pillar.
    private final boolean expandable; // Whether this block type supports generation of pillars below it or not.

    DungeonModelBlockType(BlockFactory blockFactory) {
        this(blockFactory, (config) -> PlacementBehaviour.NON_SOLID);
    }

    DungeonModelBlockType(BlockFactory blockFactory, Function<PlacementConfiguration, PlacementBehaviour> placementBehaviourFromConfig) {
        this(blockFactory, new TypeBuilder(placementBehaviourFromConfig));
    }

    DungeonModelBlockType(BlockFactory blockFactory, TypeBuilder typeBuilder) {
        this.blockFactory = blockFactory;
        this.defaultPlacementBehavior = typeBuilder.defaultPlacementBehaviour;
        this.placementBehaviourFromConfig = typeBuilder.placementBehaviourFromConfig;
        this.isPillar = typeBuilder.isPillar;
        this.expandable = typeBuilder.expandable;
    }

    public boolean isPillar() {
        return isPillar;
    }

    public boolean isExpandable() {
        return expandable;
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
                       SecondaryTheme secondaryTheme, Random rand, byte[] variation, int stage);

    }

    /**
     * Creates a block factory that pulls from a theme
     *
     * @param blockSelector a function that selects the desired field from the theme
     * @return the block factory
     */
    private static BlockFactory tFactory(Function<Theme, BlockStateProvider> blockSelector) {
        return (block, rotation, world, pos, theme, secondaryTheme, rand, variation, stage) -> block.create(blockSelector.apply(theme).get(world, pos, rand, rotation), world, pos, rotation);
    }

    /**
     * Creates a block factory that pulls from a secondary theme.
     *
     * @param blockSelector a function that selects the desired field from the secondary theme
     * @return the block factory
     */
    private static BlockFactory sFactory(Function<SecondaryTheme, BlockStateProvider> blockSelector) {
        return (block, rotation, world, pos, theme, secondaryTheme, rand, variation, stage) -> block.create(blockSelector.apply(secondaryTheme).get(world, pos, rand, rotation), world, pos, rotation);
    }

    /**
     * A builder used to define properties of model block types in a clean and readable way.
     */
    private static class TypeBuilder {

        private PlacementBehaviour defaultPlacementBehaviour;
        private Function<PlacementConfiguration, PlacementBehaviour> placementBehaviourFromConfig;

        private boolean expandable;
        private boolean isPillar;

        private TypeBuilder(Function<PlacementConfiguration, PlacementBehaviour> placementBehaviourFromConfig) {
            this.placementBehaviourFromConfig = placementBehaviourFromConfig;
            this.defaultPlacementBehaviour = PlacementBehaviour.NON_SOLID;
            this.expandable = false;
            this.isPillar = false;
        }

        public TypeBuilder placement(@Nonnull PlacementBehaviour placementBehaviour) {
            this.defaultPlacementBehaviour = placementBehaviour;
            return this;
        }

        public TypeBuilder expandable() {
            this.expandable = true;
            return this;
        }


        public TypeBuilder pillar() {
            this.isPillar = true;
            return this;
        }

    }

}
