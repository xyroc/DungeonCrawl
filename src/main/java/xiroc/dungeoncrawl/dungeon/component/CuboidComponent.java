package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record CuboidComponent(BoundingBox extent) implements DungeonComponent {
    public static final MapCodec<CuboidComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BoundingBox.CODEC.fieldOf("extent").forGetter(CuboidComponent::extent)
    ).apply(instance, CuboidComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        final BlockPos start = new BlockPos(extent.minX(), extent.minY(), extent.minZ());
        final BlockPos end = new BlockPos(extent.maxX(), extent.maxY(), extent.maxZ());
        WorldEditor.fill(level, worldGenContext.primaryTheme().value().masonry(), null, start, end, worldGenBounds, random, false);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return new BoundingBoxBuilder(extent);
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
