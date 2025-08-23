package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.registries.RegisterEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.component.feature.ChestComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.FlowerPotComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.FurnaceComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.SarcophagusComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.SpawnerComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.TNTChestComponent;
import xiroc.dungeoncrawl.init.ModRegistries;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

import java.util.function.Function;

public interface DungeonComponent {
    Codec<DungeonComponent> CODEC = ModRegistries.DUNGEON_COMPONENT_TYPE.byNameCodec().dispatch(
            "component_type",
            DungeonComponent::codec,
            Function.identity()
    );

    static void register(RegisterEvent.RegisterHelper<MapCodec<? extends DungeonComponent>> registry) {
        registry.register(DungeonCrawl.locate("blueprint"), BlueprintComponent.CODEC);
        registry.register(DungeonCrawl.locate("cuboid"), CuboidComponent.CODEC);
        registry.register(DungeonCrawl.locate("entrance"), EntranceComponent.CODEC);
        registry.register(DungeonCrawl.locate("staircase"), StaircaseComponent.CODEC);
        registry.register(DungeonCrawl.locate("tunnel"), TunnelComponent.CODEC);

        registry.register(DungeonCrawl.locate("chest"), ChestComponent.CODEC);
        registry.register(DungeonCrawl.locate("flower_pot"), FlowerPotComponent.CODEC);
        registry.register(DungeonCrawl.locate("furnace"), FurnaceComponent.CODEC);
        registry.register(DungeonCrawl.locate("sarcophagus"), SarcophagusComponent.CODEC);
        registry.register(DungeonCrawl.locate("spawner"), SpawnerComponent.CODEC);
        registry.register(DungeonCrawl.locate("tnt_chest"), TNTChestComponent.CODEC);
    }

    void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext);

    BoundingBoxBuilder boundingBox();

    MapCodec<? extends DungeonComponent> codec();
}