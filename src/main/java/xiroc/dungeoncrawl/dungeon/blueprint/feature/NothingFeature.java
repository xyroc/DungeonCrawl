package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;

import java.util.ArrayList;
import java.util.function.Consumer;

public class NothingFeature implements BlueprintFeature {
    public static final MapCodec<NothingFeature> CODEC = MapCodec.unit(NothingFeature::new);

    @Override
    public void create(LevelGenerator levelGenerator, Consumer<DungeonComponent> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation) {
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
