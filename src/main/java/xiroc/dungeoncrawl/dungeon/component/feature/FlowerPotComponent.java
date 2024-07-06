package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public record FlowerPotComponent(BlockPos position, Block soil, Block flower) implements DungeonComponent {
    public static final Codec<FlowerPotComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockPos.CODEC.fieldOf("position").forGetter(FlowerPotComponent::position),
            StorageHelper.BLOCK_CODEC.fieldOf("soil").forGetter(FlowerPotComponent::soil),
            StorageHelper.BLOCK_CODEC.fieldOf("flower").forGetter(FlowerPotComponent::flower)
    ).apply(builder, FlowerPotComponent::new));

    private boolean isDoublePlant() {
        return flower instanceof DoublePlantBlock;
    }

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        WorldEditor.placeBlock(level, soil.defaultBlockState(), position, worldGenBounds, true, true, false);
        BlockState flowerState = flower.defaultBlockState();
        if (flower.canSurvive(flowerState, level, position.above())) {
            if (isDoublePlant()) {
                WorldEditor.placeBlock(level, DungeonBlocks.applyProperty(flowerState, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), position.above(), worldGenBounds, true, true, false);
                WorldEditor.placeBlock(level, DungeonBlocks.applyProperty(flowerState, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), position.above(2), worldGenBounds, true, true, false);
            } else {
                WorldEditor.placeBlock(level, flowerState, position.above(), worldGenBounds, true, true, false);
            }
        }
        WorldEditor.placeBlock(level, DungeonBlocks.TRAPDOOR_OPEN_NORTH.attach(secondaryTheme.trapDoor()), position.north(), worldGenBounds, random, true, false, false);
        WorldEditor.placeBlock(level, DungeonBlocks.TRAPDOOR_OPEN_EAST.attach(secondaryTheme.trapDoor()), position.east(), worldGenBounds, random, true, false, false);
        WorldEditor.placeBlock(level, DungeonBlocks.TRAPDOOR_OPEN_SOUTH.attach(secondaryTheme.trapDoor()), position.south(), worldGenBounds, random, true, false, false);
        WorldEditor.placeBlock(level, DungeonBlocks.TRAPDOOR_OPEN_WEST.attach(secondaryTheme.trapDoor()), position.west(), worldGenBounds, random, true, false, false);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        int size = isDoublePlant() ? 2 : 1;
        return BoundingBoxBuilder.fromCorners(position, position.above(size));
    }

    @Override
    public int componentType() {
        return DECODERS.getId(CODEC);
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this);
    }
}
