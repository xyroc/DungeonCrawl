package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;
import java.util.stream.IntStream;

public record StaircaseComponent(BlockPos center, int height, int wallBottom, int wallTop, int rotation) implements DungeonComponent {
    public static final Codec<StaircaseComponent> CODEC = Codec.INT_STREAM.comapFlatMap(
            encoded -> Util.fixedSize(encoded, 7).map(values -> new StaircaseComponent(new BlockPos(values[0], values[1], values[2]), values[3], values[4], values[5],values[6])),
            decoded -> IntStream.of(decoded.center.getX(), decoded.center.getY(), decoded.center.getZ(), decoded.height, decoded.wallBottom, decoded.wallTop, decoded.rotation)
    ).stable();

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

    private void placeSpiralStairStep(LevelAccessor world, BlockStateProvider pillar, BlockStateProvider stairs, BlockPos center, BoundingBox boundingBox, Random random) {
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
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
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
    public int componentType() {
        return DECODERS.getId(CODEC);
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this);
    }
}
