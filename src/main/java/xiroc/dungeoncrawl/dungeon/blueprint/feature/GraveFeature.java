package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.ChestComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.GraveComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.SpawnerComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;

import java.util.Optional;
import java.util.function.Consumer;

public record GraveFeature(PlacementSettings placement, Optional<SpawnerSettings> spawner, Optional<ChestSettings> chest) implements BlueprintFeature.AnchorBased {
    public static final MapCodec<GraveFeature> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            PlacementSettings.CODEC.fieldOf("placement").forGetter(GraveFeature::placement),
            SpawnerSettings.CODEC.optionalFieldOf("spawner").forGetter(GraveFeature::spawner),
            ChestSettings.CODEC.optionalFieldOf("chest").forGetter(GraveFeature::chest)
    ).apply(builder, GraveFeature::new));

    @Override
    public void createAt(Consumer<DungeonComponent> components, LevelGenerator levelGenerator, Anchor anchor) {
        if (spawner.isPresent()) {
            BlockPos spawnerPos = anchor.position().relative(anchor.direction(), 1);
            components.accept(new SpawnerComponent(spawnerPos, spawner.get().getSpawnerTypes(levelGenerator).roll(levelGenerator.random)));
        }

        if (chest.isPresent()) {
            BlockPos chestPos = anchor.position();
            components.accept(new ChestComponent(new Anchor(chestPos, anchor.direction()), chest.get().getLootTable(levelGenerator)));
        }

        BlockPos sealPos = anchor.position().relative(anchor.direction(), 2);
        components.accept(new GraveComponent(sealPos));
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
