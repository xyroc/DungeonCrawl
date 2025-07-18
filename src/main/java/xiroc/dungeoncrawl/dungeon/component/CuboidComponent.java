package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record CuboidComponent(BoundingBox size) implements DungeonComponent {
    public static final Codec<CuboidComponent> CODEC = BoundingBox.CODEC.xmap(CuboidComponent::new, CuboidComponent::size);

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        final BlockPos start = new BlockPos(size.minX(), size.minY(), size.minZ());
        final BlockPos end = new BlockPos(size.maxX(), size.maxY(), size.maxZ());
        WorldEditor.fill(level, worldGenContext.primaryTheme().value().masonry(), null, start, end, worldGenBounds, random, false);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return new BoundingBoxBuilder(size);
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
