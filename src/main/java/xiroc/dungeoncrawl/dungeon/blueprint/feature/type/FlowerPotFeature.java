package xiroc.dungeoncrawl.dungeon.blueprint.feature.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FlowerPotConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.instance.FlowerPotInstance;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public class FlowerPotFeature extends BlueprintFeature<FlowerPotConfiguration, FlowerPotInstance> {
    private static final String NBT_KEY_SOIL = "Soil";

    private final Block soil;

    public FlowerPotFeature(FlowerPotConfiguration configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random, int stage) {
        super(configuration, coordinateSpace, rotation, random);
        this.soil = configuration.soil;
    }

    public FlowerPotFeature(CompoundTag nbt) {
        super(nbt);
        this.soil = Registry.BLOCK.get(new ResourceLocation(nbt.getString(NBT_KEY_SOIL)));
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
    }

    @Override
    protected FlowerPotInstance[] makeInstanceArray(int size) {
        return new FlowerPotInstance[size];
    }

    @Override
    protected FlowerPotInstance createInstance(BlockPos position, Direction facing, FlowerPotConfiguration configuration, CoordinateSpace coordinateSpace, Rotation rotation, Random random) {
        return new FlowerPotInstance(position, configuration.flowers.get(position, random).getBlock());
    }

    @Override
    protected FlowerPotInstance readInstance(CompoundTag nbt) {
        return new FlowerPotInstance(nbt);
    }

    @Override
    protected CompoundTag writeInstance(FlowerPotInstance instance) {
        CompoundTag nbt = new CompoundTag();
        instance.write(nbt);
        return nbt;
    }

    @Override
    protected void place(WorldGenLevel level, FlowerPotInstance instance, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        WorldEditor.placeBlock(level, soil.defaultBlockState(), instance.position, worldGenBounds, true, true, false);
        BlockState flower = instance.flower.defaultBlockState();
        if (instance.flower.canSurvive(flower, level, instance.position.above())) {
            if (instance.flower instanceof DoublePlantBlock) {
                WorldEditor.placeBlock(level, DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), instance.position.above(), worldGenBounds, true, true, false);
                WorldEditor.placeBlock(level, DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), instance.position.above(2), worldGenBounds, true, true, false);
            } else {
                WorldEditor.placeBlock(level, flower, instance.position.above(), worldGenBounds, true, true, false);
            }
        }
        BlockPos east = instance.position.east();
        // TODO: safe trapdoor blockstate property application
        WorldEditor.placeBlock(level, secondaryTheme.trapDoor().get(east, random)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
                .setValue(BlockStateProperties.OPEN, true), east, worldGenBounds, true, false, false);

        BlockPos south = instance.position.south();
        WorldEditor.placeBlock(level, secondaryTheme.trapDoor().get(south, random)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.OPEN, true), south, worldGenBounds, true, false, false);

        BlockPos west = instance.position.west();
        WorldEditor.placeBlock(level, secondaryTheme.trapDoor().get(west, random)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                .setValue(BlockStateProperties.OPEN, true), west, worldGenBounds, true, false, false);

        BlockPos north = instance.position.north();
        WorldEditor.placeBlock(level, secondaryTheme.trapDoor().get(north, random)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .setValue(BlockStateProperties.OPEN, true), north, worldGenBounds, true, false, false);
    }
}
