package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record TunnelComponent(BlockPos start, Direction direction, int length, int height, int width) implements DungeonComponent {
    public static final MapCodec<TunnelComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPos.CODEC.fieldOf("start").forGetter(TunnelComponent::start),
            Direction.CODEC.fieldOf("direction").forGetter(TunnelComponent::direction),
            Codec.INT.fieldOf("length").forGetter(TunnelComponent::length),
            Codec.INT.fieldOf("height").forGetter(TunnelComponent::height),
            Codec.INT.fieldOf("width").forGetter(TunnelComponent::width)
    ).apply(instance, TunnelComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        PrimaryTheme primaryTheme = worldGenContext.primaryTheme().value();
        BlockPos corner = start.relative(direction.getCounterClockWise(), width);

        WorldEditor.fill(level, primaryTheme.floor(), null, corner.relative(direction.getClockWise()), corner.relative(direction.getClockWise(), 2 * width - 1)
                .relative(direction, length - 1), worldGenBounds, random, false);

        WorldEditor.fill(level, primaryTheme.masonry(), null, corner, corner.relative(direction, length - 1).above(height - 1), worldGenBounds, random, false);

        WorldEditor.fill(level, primaryTheme.masonry(), null, corner.relative(direction.getClockWise(), 2 * width),
                corner.relative(direction.getClockWise(), 2 * width).relative(direction, length - 1).above(height - 1),
                worldGenBounds, random, false);

        WorldEditor.fill(level, primaryTheme.masonry(), null, corner.relative(direction.getClockWise()).above(height - 1),
                corner.relative(direction.getClockWise(), 2 * width - 1).above(height - 1).relative(direction, length - 1), worldGenBounds, random, false);

        WorldEditor.fill(level, new SingleBlock(Blocks.CAVE_AIR.defaultBlockState()), null, corner.above().relative(direction.getClockWise()),
                corner.above(height - 2).relative(direction, length - 1).relative(direction.getClockWise(), 2 * width - 1), worldGenBounds, random, false);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.tunnel(start, direction, length, height, width);
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
