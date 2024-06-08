package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;
import java.util.stream.IntStream;

public record StaircaseComponent(BlockPos position, int height, int wallBottom, int wallTop) implements DungeonComponent {
    public static final Codec<StaircaseComponent> CODEC = Codec.INT_STREAM.comapFlatMap(
            encoded -> Util.fixedSize(encoded, 6).map(values -> new StaircaseComponent(new BlockPos(values[0], values[1], values[2]), values[3], values[4], values[5])),
            decoded -> IntStream.of(decoded.position.getX(), decoded.position.getY(), decoded.position.getZ(), decoded.height, decoded.wallBottom, decoded.wallTop)
    ).stable();

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        for (int i = 0; i < height; ++i) {
            BlockPos center = position.above(i);
            WorldEditor.placeSpiralStairStep(level, primaryTheme.pillar(), primaryTheme.stairs(), center, worldGenBounds, random, true);
        }
        if (wallTop >= wallBottom) {
            WorldEditor.fillRing(level, primaryTheme.masonry(), position.atY(wallBottom), 2, 1, wallTop - wallBottom + 1, worldGenBounds, random, true, true);
        }
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return new BoundingBoxBuilder(
                position.getX() - 2, position.getY(), position.getZ() - 2,
                position.getX() + 2, position.getY() + height - 1, position.getZ() + 2);
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
