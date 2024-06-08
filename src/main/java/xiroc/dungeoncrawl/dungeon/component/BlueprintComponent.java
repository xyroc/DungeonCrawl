package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.Random;

public record BlueprintComponent(Delegate<Blueprint> blueprint, BlockPos position, Rotation rotation) implements DungeonComponent {
    public static final Codec<BlueprintComponent> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(Blueprint.CODEC.fieldOf("blueprint").forGetter(BlueprintComponent::blueprint),
                            BlockPos.CODEC.fieldOf("pos").forGetter(BlueprintComponent::position),
                            StorageHelper.ROTATION_CODEC.fieldOf("rot").forGetter(BlueprintComponent::rotation))
                    .apply(builder, BlueprintComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        blueprint.get().build(level, position, rotation, worldGenBounds, random, primaryTheme, secondaryTheme, stage);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return blueprint.get().boundingBox(rotation).move(position);
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
