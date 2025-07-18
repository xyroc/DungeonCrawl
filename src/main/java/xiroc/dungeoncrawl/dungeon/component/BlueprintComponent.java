package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
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
    public static final Codec<BlueprintComponent> CODEC = RecordCodecBuilder.create(builder ->
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
    public int componentType() {
        return DECODERS.getId(CODEC);
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this);
    }
}
