package xiroc.dungeoncrawl.data.blueprint.provider;

import net.minecraft.core.Holder;
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
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.ChainedFeatures;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.ChestFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.NothingFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.GraveFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.RandomFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.SpawnerFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.FixedTemplateBlockType;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Constant;
import xiroc.dungeoncrawl.util.random.value.Range;

import java.util.List;
import java.util.Optional;

public interface PartBlueprints {
    static void generate(BootstrapContext<Blueprint> context) {
        final var blockStatePools = context.lookup(DatapackRegistries.BLOCK_STATE_POOLS);
        final var blueprints = context.lookup(DatapackRegistries.BLUEPRINT);

        context.register(BlueprintKeys.Part.CEILING_7X7_DOME, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_7X7_DOME)
                .build()));

        context.register(BlueprintKeys.Part.CEILING_7X7_FLAT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_7X7_FLAT)
                .build()));

        context.register(BlueprintKeys.Part.CEILING_7X7_LAVA_PIT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_7X7_LAVA_PIT)
                .build()));

        context.register(BlueprintKeys.Part.CEILING_9X3_FLAT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_9X3_FLAT)
                .build()));

        context.register(BlueprintKeys.Part.CEILING_9X3_ORNATE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_9X3_ORNATE)
                .build()));

        context.register(BlueprintKeys.Part.CEILING_CENTERED_5X5_FLAT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_CENTERED_5X5_FLAT)
                .build()));

        context.register(BlueprintKeys.Part.CEILING_CENTERED_5X5_ORNATE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_CENTERED_5X5_ORNATE)
                .build()));

        context.register(BlueprintKeys.Part.CEILING_CENTERED_5X5_RAISED, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CEILING_CENTERED_5X5_RAISED)
                .build()));

        context.register(BlueprintKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN)
                .build()));

        context.register(BlueprintKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN_LEFT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN_LEFT)
                .build()));

        context.register(BlueprintKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN_RIGHT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN_RIGHT)
                .build()));

        context.register(BlueprintKeys.Part.CRYPT_ENTRANCE_OPEN, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CRYPT_ENTRANCE_OPEN)
                .build()));

        context.register(BlueprintKeys.Part.CRYPT_WALL_MAUSOLEUM, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CRYPT_WALL_MAUSOLEUM)
                .configureBlock(Blocks.RED_NETHER_BRICK_STAIRS, TemplateBlockPlacementSettings.ANTI_SOLID.withPostProcessing())
                .feature(new ChainedFeatures(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/grave")), new Constant(0)),
                        List.of(new GraveFeature(
                                        new PlacementSettings(Optional.empty(), new Range(1, 3)),
                                        Optional.of(new SpawnerSettings(Optional.empty())),
                                        Optional.of(new ChestSettings())),
                                new GraveFeature(
                                        new PlacementSettings(Optional.empty(), new Range(1, 3)),
                                        Optional.empty(),
                                        Optional.empty()
                                )
                        )))
                .build()));

        context.register(BlueprintKeys.Part.CRYPT_WALL_PLAIN, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.CRYPT_WALL_PLAIN)
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

        context.register(BlueprintKeys.Part.ENTRANCE_CLOSED_5X5, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.ENTRANCE_CLOSED_5X5)
                .build()));

        context.register(BlueprintKeys.Part.ENTRANCE_FENCING_CLOSED_3X3, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.ENTRANCE_FENCING_CLOSED_3X3)
                .configureBlock(Blocks.IRON_BARS, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));

        context.register(BlueprintKeys.Part.ENTRANCE_FENCING_OPEN_3X3, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.ENTRANCE_FENCING_OPEN_3X3)
                .configureBlock(Blocks.IRON_BARS, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));

        context.register(BlueprintKeys.Part.ENTRANCE_CLOSED_5X9, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.ENTRANCE_CLOSED_5X9)
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

        context.register(BlueprintKeys.Part.PRISON_CELL, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.PRISON_CELL)
                .multipart(new BlueprintMultipart(DataGen.resource("fencing"), IRandom.<Holder<Blueprint>>builder()
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.ENTRANCE_FENCING_CLOSED_3X3))
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.ENTRANCE_FENCING_OPEN_3X3))
                        .build()))
                .feature(new RandomFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("generic")), new Constant(1)),
                        IRandom.<BlueprintFeature>builder()
                                .add(new SpawnerFeature(
                                        new PlacementSettings(Optional.empty(), new Constant(1)),
                                        new SpawnerSettings(Optional.empty())))
                                .add(new ChestFeature(
                                        new PlacementSettings(Optional.empty(), new Constant(1)),
                                        new ChestSettings(Optional.empty())))
                                .add(new NothingFeature())
                                .build()
                ))
                .build()));

        context.register(BlueprintKeys.Part.PRISON_CELL_CORNER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.PRISON_CELL_CORNER)
                .multipart(new BlueprintMultipart(DataGen.resource("fencing"), IRandom.<Holder<Blueprint>>builder()
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.ENTRANCE_FENCING_CLOSED_3X3))
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.ENTRANCE_FENCING_OPEN_3X3))
                        .build()))
                .feature(new RandomFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("generic")), new Constant(1)),
                        IRandom.<BlueprintFeature>builder()
                                .add(new SpawnerFeature(
                                        new PlacementSettings(Optional.empty(), new Constant(1)),
                                        new SpawnerSettings(Optional.empty())))
                                .add(new ChestFeature(
                                        new PlacementSettings(Optional.empty(), new Constant(1)),
                                        new ChestSettings(Optional.empty())))
                                .add(new NothingFeature())
                                .build()
                ))
                .build()));

        context.register(BlueprintKeys.Part.PRISON_ENTRANCE_INSET, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.PRISON_ENTRANCE_INSET)
                .build()));

        context.register(BlueprintKeys.Part.PRISON_ENTRANCE_OPEN, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.PRISON_ENTRANCE_OPEN)
                .build()));

        context.register(BlueprintKeys.Part.SARCOPHAGUS_SMALL_EMPTY, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.SARCOPHAGUS_SMALL)
                .build()));

        context.register(BlueprintKeys.Part.SARCOPHAGUS_SMALL_GRAVE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Part.SARCOPHAGUS_SMALL)
                .feature(new GraveFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/grave")), new Constant(1)),
                        Optional.of(new SpawnerSettings(Optional.empty())),
                        Optional.of(new ChestSettings())
                ))
                .build()));
    }
}
