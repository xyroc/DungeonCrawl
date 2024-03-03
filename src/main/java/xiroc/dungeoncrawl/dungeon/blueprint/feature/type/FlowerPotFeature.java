package xiroc.dungeoncrawl.dungeon.blueprint.feature.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.PlacedFeature;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public record FlowerPotFeature(BlockPos position, Block soil, Block flower) implements PlacedFeature {
    public static final Codec<PlacedFeature> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockPos.CODEC.fieldOf("position").forGetter(feature -> ((FlowerPotFeature) feature).position),
            StorageHelper.BLOCK_CODEC.fieldOf("soil").forGetter(feature -> ((FlowerPotFeature) feature).soil),
            StorageHelper.BLOCK_CODEC.fieldOf("flower").forGetter(feature -> ((FlowerPotFeature) feature).flower)
    ).apply(builder, FlowerPotFeature::new));

    @Override
    public void place(WorldGenLevel level, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        WorldEditor.placeBlock(level, soil.defaultBlockState(), position, worldGenBounds, true, true, false);
        BlockState flowerState = flower.defaultBlockState();
        if (flower.canSurvive(flowerState, level, position.above())) {
            if (flower instanceof DoublePlantBlock) {
                WorldEditor.placeBlock(level, DungeonBlocks.applyProperty(flowerState, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), position.above(), worldGenBounds, true, true, false);
                WorldEditor.placeBlock(level, DungeonBlocks.applyProperty(flowerState, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), position.above(2), worldGenBounds, true, true, false);
            } else {
                WorldEditor.placeBlock(level, flowerState, position.above(), worldGenBounds, true, true, false);
            }
        }
        BlockPos east = position.east();
        // TODO: safe trapdoor blockstate property application
        WorldEditor.placeBlock(level, secondaryTheme.trapDoor().get(east, random)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                .setValue(BlockStateProperties.OPEN, true), east, worldGenBounds, true, false, false);

        BlockPos south = position.south();
        WorldEditor.placeBlock(level, secondaryTheme.trapDoor().get(south, random)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .setValue(BlockStateProperties.OPEN, true), south, worldGenBounds, true, false, false);

        BlockPos west = position.west();
        WorldEditor.placeBlock(level, secondaryTheme.trapDoor().get(west, random)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
                .setValue(BlockStateProperties.OPEN, true), west, worldGenBounds, true, false, false);

        BlockPos north = position.north();
        WorldEditor.placeBlock(level, secondaryTheme.trapDoor().get(north, random)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.OPEN, true), north, worldGenBounds, true, false, false);
    }
}
