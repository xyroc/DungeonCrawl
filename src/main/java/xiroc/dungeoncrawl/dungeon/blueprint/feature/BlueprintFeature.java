package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.init.ModRegistries;
import xiroc.dungeoncrawl.util.CoordinateSpace;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public interface BlueprintFeature {
    Codec<BlueprintFeature> CODEC = ModRegistries.BLUEPRINT_FEATURE.byNameCodec().dispatch(
            BlueprintFeature::type,
            Function.identity()
    );

    static void register(RegisterEvent.RegisterHelper<MapCodec<? extends BlueprintFeature>> registry) {
        registry.register(DungeonCrawl.locate("feature_chain"), ChainedFeatures.CODEC);
        registry.register(DungeonCrawl.locate("chest"), ChestFeature.CODEC);
        registry.register(DungeonCrawl.locate("spawner"), SpawnerFeature.CODEC);
        registry.register(DungeonCrawl.locate("flower_pot"), FlowerPotFeature.CODEC);
        registry.register(DungeonCrawl.locate("sarcophagus"),  SarcophagusFeature.CODEC);
    }

    MapCodec<? extends BlueprintFeature> type();

    void create(LevelGenerator levelGenerator, Consumer<DungeonComponent> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation);

    static @Nullable ArrayList<Anchor> gatherPositions(@Nullable ArrayList<Anchor> positions, Blueprint blueprint, PlacementSettings placement) {
        if (positions != null) {
            return positions;
        }
        return placement.anchors(blueprint).orElse(null);
    }

    interface AnchorBased extends BlueprintFeature {
        @Override
        default void create(LevelGenerator levelGenerator, Consumer<DungeonComponent> features, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation) {
            positions = gatherPositions(positions, blueprint, placement());
            if (positions == null) {
                return;
            }
            CoordinateSpace coordinateSpace = blueprint.coordinateSpace(offset);
            placement().drawPositions(positions, levelGenerator.random, (anchor) ->
                    features.accept(createInstance(levelGenerator, coordinateSpace.rotateAndTranslateToOrigin(anchor, rotation)))
            );
        }

        DungeonComponent createInstance(LevelGenerator levelGenerator, Anchor anchor);

        PlacementSettings placement();
    }
}
