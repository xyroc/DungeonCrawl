package xiroc.dungeoncrawl.data.blueprint.provider;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.data.blueprint.BlueprintKeys;
import xiroc.dungeoncrawl.data.blueprint.TemplateKeys;
import xiroc.dungeoncrawl.data.tags.BlockPoolTags;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.block.provider.RandomBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.SpawnerFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.FixedTemplateBlockType;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Constant;

import java.util.Optional;

public interface PartBlueprints {
    static void generate(BootstrapContext<Blueprint> context) {
        final var blockStatePools = context.lookup(DatapackRegistries.BLOCK_STATE_POOLS);

        context.register(BlueprintKeys.Part.CEILING_7X7_DOME, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_7X7_DOME)
                .build()));

        context.register(BlueprintKeys.Part.CEILING_7X7_FLAT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_7X7_FLAT)
                .build()));

        context.register(BlueprintKeys.Part.CEILING_7X7_LAVA_PIT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_7X7_LAVA_PIT)
                .build()));

        context.register(BlueprintKeys.Part.DARK_HALL_OPEN, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.DARK_HALL_OPEN)
                .build()));

        context.register(BlueprintKeys.Part.DARK_HALL_CLOSED, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.DARK_HALL_CLOSED)
                .build()));

        context.register(BlueprintKeys.Part.DINER_OPEN, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.DINER_OPEN)
                .build()));

        context.register(BlueprintKeys.Part.DINER_CLOSED_TABLE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.DINER_CLOSED_TABLE)
                .build()));

        context.register(BlueprintKeys.Part.DINER_CLOSED_STORAGE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.DINER_CLOSED_STORAGE)
                .build()));

        context.register(BlueprintKeys.Part.FLOOR_3x3_MASONRY, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.FLOOR_3X3)
                .build()));

        context.register(BlueprintKeys.Part.FLOOR_3x3_SOLID, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.FLOOR_3X3)
                .mapBlock(Blocks.STONE_BRICKS, TemplateBlockType.FLOOR.create())
                .build()));

        context.register(BlueprintKeys.Part.FLOOR_5x5_SOLID, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.FLOOR_5x5)
                .configureBlock(Blocks.GRAVEL, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));

        context.register(BlueprintKeys.Part.FLOOR_5x5_FRAGILE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.FLOOR_5x5)
                .build()));

        context.register(BlueprintKeys.Part.FORT_SPAWNER_PILLAR, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.FORT_SPAWNER_PILLAR)
                .feature(new SpawnerFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/spawner")), new Constant(1)),
                        new SpawnerSettings(Optional.empty())
                ))
                .build()));

        context.register(BlueprintKeys.Part.LAB_CONTAINER_CROPS, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.LAB_CONTAINER_WHEAT)
                .defaultPlacementSettings(TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .noGlobalDefaultPlacementSettings()
                .mapBlock(Blocks.WHEAT, new FixedTemplateBlockType(new RandomBlock(IRandom.<BlockState>builder()
                        .setPools(blockStatePools.get(BlockPoolTags.FARMLAND_CROPS).orElseThrow())
                        .build())))
                .build()));

        context.register(BlueprintKeys.Part.LAB_CONTAINER_FLOWER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.LAB_CONTAINER_TORCHFLOWER)
                .defaultPlacementSettings(TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .noGlobalDefaultPlacementSettings()
                .build()));

        context.register(BlueprintKeys.Part.LAB_CONTAINER_KELP, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.LAB_CONTAINER_KELP)
                .defaultPlacementSettings(TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .noGlobalDefaultPlacementSettings()
                .build()));

        context.register(BlueprintKeys.Part.LAB_CONTAINER_SPAWNER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.LAB_CONTAINER_SPAWNER)
                .defaultPlacementSettings(TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .noGlobalDefaultPlacementSettings()
                .feature(new SpawnerFeature(
                        new PlacementSettings(Optional.of(SharedKeys.Anchor.Feature.SPAWNER), new Constant(1)),
                        new SpawnerSettings(Optional.empty())
                ))
                .build()));

        context.register(BlueprintKeys.Part.LIBRARY_ENTRANCE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.LIBRARY_ENTRANCE)
                .build()));

        context.register(BlueprintKeys.Part.LIBRARY_DESK, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.LIBRARY_DESK)
                .build()));

        context.register(BlueprintKeys.Part.LIBRARY_FLOWERS, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.LIBRARY_FLOWERS)
                .build()));
    }
}
