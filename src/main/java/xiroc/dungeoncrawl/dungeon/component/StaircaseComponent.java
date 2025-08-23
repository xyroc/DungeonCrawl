package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record StaircaseComponent(BlockPos center, int height, int wallBottom, int wallTop, int rotation) implements DungeonComponent {
    public static final MapCodec<StaircaseComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPos.CODEC.fieldOf("center").forGetter(StaircaseComponent::center),
            Codec.INT.fieldOf("height").forGetter(StaircaseComponent::height),
            Codec.INT.fieldOf("wall_bottom").forGetter(StaircaseComponent::wallBottom),
            Codec.INT.fieldOf("wall_top").forGetter(StaircaseComponent::wallTop),
            Codec.INT.fieldOf("rotation").forGetter(StaircaseComponent::rotation)
    ).apply(instance, StaircaseComponent::new));

    /**
     * Staircase facings, in the order of clockwise 90 degree rotations, starting from north.
     */
    public static final Direction[] FACINGS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public static Direction getFacingAt(int y, int rotation) {
        int facing = (y + rotation) % FACINGS.length;
        if (facing < 0) {
            facing += FACINGS.length;
        }
        return FACINGS[facing];
    }

    private void placeSpiralStairStep(LevelAccessor world, BlockStateProvider pillar, BlockStateProvider stairs, BlockPos center, BoundingBox boundingBox, RandomSource random) {
        WorldEditor.fillRing(world, SingleBlock.AIR, center, 1, 1, 1, boundingBox, random, true, true);
        WorldEditor.placeBlock(world, pillar, center, boundingBox, random, true, true, true);

        final Direction facing = getFacingAt(center.getY(), this.rotation);
        final BlockPos.MutableBlockPos cursor = center.mutable().move(facing);
        WorldEditor.placeStairs(world, stairs, cursor, boundingBox, Half.BOTTOM, facing.getClockWise(), random, true, true, true);

        cursor.move(facing.getClockWise());
        WorldEditor.placeStairs(world, stairs, cursor, boundingBox, Half.TOP, facing.getOpposite(), random, true, true, true);

        cursor.move(facing.getOpposite());
        WorldEditor.placeStairs(world, stairs, cursor, boundingBox, Half.TOP, facing.getCounterClockWise(), random, true, true, true);
    }

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        final PrimaryTheme primaryTheme = worldGenContext.primaryTheme().value();
        for (int i = 0; i < height; ++i) {
            placeSpiralStairStep(level, primaryTheme.pillar(), primaryTheme.stairs(), this.center.above(i), worldGenBounds, random);
        }
        if (wallTop >= wallBottom) {
            WorldEditor.fillRing(level, primaryTheme.masonry(), center.atY(wallBottom), 2, 1, wallTop - wallBottom + 1, worldGenBounds, random, true, true);
        }
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return new BoundingBoxBuilder(
                center.getX() - 2, center.getY(), center.getZ() - 2,
                center.getX() + 2, center.getY() + height - 1, center.getZ() + 2);
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
