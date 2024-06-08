package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxUtils;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;
import java.util.stream.IntStream;

public record TunnelComponent(BlockPos start, Direction direction, int length, int height, int width) implements DungeonComponent {
    public static final Codec<TunnelComponent> CODEC = Codec.INT_STREAM.comapFlatMap(
            encoded -> Util.fixedSize(encoded, 7).map(
                    values -> new TunnelComponent(new BlockPos(values[0], values[1], values[2]), Direction.from3DDataValue(values[3]), values[4], values[5], values[6])),
            decoded -> IntStream.of(
                    decoded.start.getX(), decoded.start.getY(), decoded.start.getZ(), decoded.direction.get3DDataValue(), decoded.length, decoded.height, decoded.width)
    ).stable();

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        BlockPos corner = start.relative(direction.getCounterClockWise(), width);

        WorldEditor.fill(level, primaryTheme.floor(), corner.relative(direction.getClockWise()), corner.relative(direction.getClockWise(), 2 * width - 1)
                .relative(direction, length - 1), worldGenBounds, random, true, true);

        WorldEditor.fill(level, primaryTheme.masonry(), corner, corner.relative(direction, length - 1).above(height - 1), worldGenBounds, random, true, true);

        WorldEditor.fill(level, primaryTheme.masonry(), corner.relative(direction.getClockWise(), 2 * width),
                corner.relative(direction.getClockWise(), 2 * width).relative(direction, length - 1).above(height - 1),
                worldGenBounds, random, true, true);

        WorldEditor.fill(level, primaryTheme.masonry(), corner.relative(direction.getClockWise()).above(height - 1),
                corner.relative(direction.getClockWise(), 2 * width - 1).above(height - 1).relative(direction, length - 1), worldGenBounds, random, true, true);

        WorldEditor.fill(level, new SingleBlock(Blocks.CAVE_AIR.defaultBlockState()), corner.above().relative(direction.getClockWise()),
                corner.above(height - 2).relative(direction, length - 1).relative(direction.getClockWise(), 2 * width - 1), worldGenBounds, random, false, true);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxUtils.tunnelBuilder(start, direction, length, height, width);
    }

    @Override
    public int type() {
        return DECODERS.getId(CODEC);
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this);
    }
}
