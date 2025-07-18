package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.FlowerPotComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;

public record FlowerPotFeature(PlacementSettings placement, Block soil, BlockStateProvider flowers) implements BlueprintFeature.AnchorBased {
    public static final MapCodec<FlowerPotFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlacementSettings.CODEC.fieldOf(SharedSerializationConstants.KEY_PLACEMENT_SETTINGS).forGetter(FlowerPotFeature::placement),
            GlobalCodecs.BLOCK.fieldOf("soil").forGetter(FlowerPotFeature::soil),
            BlockStateProvider.CODEC.fieldOf("flowers").forGetter(FlowerPotFeature::flowers)
    ).apply(instance, FlowerPotFeature::new));

    @Override
    public DungeonComponent createInstance(LevelGenerator levelGenerator, Anchor anchor) {
        return new FlowerPotComponent(anchor.position(), soil, flowers.get(anchor.position(), levelGenerator.random).getBlock());
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
