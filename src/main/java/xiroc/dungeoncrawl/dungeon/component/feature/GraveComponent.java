package xiroc.dungeoncrawl.dungeon.component.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record GraveComponent(BlockPos sealPos) implements DungeonComponent {
    public static final MapCodec<GraveComponent> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            BlockPos.CODEC.fieldOf("seal").forGetter(GraveComponent::sealPos)
    ).apply(builder, GraveComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        if (worldGenBounds.isInside(sealPos)) {
            BlockState seal = worldGenContext.primaryTheme().value().graveSeal().get(sealPos, random);
            WorldEditor.Unsafe.placeBlock(level, sealPos, seal, false);
        }
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.fromPosition(sealPos);
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
