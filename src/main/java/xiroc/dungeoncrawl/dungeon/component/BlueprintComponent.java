package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

public record BlueprintComponent(Holder<Blueprint> blueprint, BlockPos position, Rotation rotation) implements DungeonComponent {
    public static final MapCodec<BlueprintComponent> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(Blueprint.HOLDER_CODEC.fieldOf("blueprint").forGetter(BlueprintComponent::blueprint),
                            BlockPos.CODEC.fieldOf("pos").forGetter(BlueprintComponent::position),
                            GlobalCodecs.ROTATION.fieldOf("rot").forGetter(BlueprintComponent::rotation))
                    .apply(builder, BlueprintComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        blueprint.value().generate(level, position, rotation, worldGenBounds, random, worldGenContext);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return blueprint.value().boundingBox(rotation).move(position);
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
