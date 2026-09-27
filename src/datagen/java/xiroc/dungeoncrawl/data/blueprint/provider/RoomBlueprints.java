package xiroc.dungeoncrawl.data.blueprint.provider;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.data.blueprint.BlueprintKeys;
import xiroc.dungeoncrawl.data.blueprint.TemplateKeys;
import xiroc.dungeoncrawl.data.loot.chest.ChestLootTableKeys;
import xiroc.dungeoncrawl.data.spawner.SpawnerTypeKeys;
import xiroc.dungeoncrawl.data.tags.BlueprintPoolTags;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.block.provider.RandomBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.config.SimpleAssemblyConfig;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.ChainedFeatures;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.ChestFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.GraveFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.SarcophagusFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.SpawnerFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.FixedTemplateBlockType;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Constant;
import xiroc.dungeoncrawl.util.random.value.Range;

import java.util.List;
import java.util.Optional;

public interface RoomBlueprints {
    static void generate(BootstrapContext<Blueprint> context) {
        final var blueprints = context.lookup(DatapackRegistries.BLUEPRINT);
        final var blueprintPools = context.lookup(DatapackRegistries.BLUEPRINT_POOLS);
        final var spawnerTypes = context.lookup(DatapackRegistries.SPAWNER_TYPE);

        context.register(BlueprintKeys.Room.BEDROOM, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.BEDROOM)
                .defaultPlacementSettings(TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .configureBlock(Blocks.TORCH, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
                .configureBlock(Blocks.CHEST, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT.withLoot(TieredResource.single(ChestLootTableKeys.SUPPLY)))
                .build()));

        context.register(BlueprintKeys.Room.BRICK, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.BRICK)
                .feature(new SpawnerFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/spawner")), new Constant(1)),
                        new SpawnerSettings(Optional.empty())
                ))
                .feature(new ChestFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/chest")), new Range(1, 2)),
                        new ChestSettings()
                ))
                .build()));

        context.register(BlueprintKeys.Room.CORNER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.CORNER)
                .build()));

        context.register(BlueprintKeys.Room.CRYPT_CROSSING, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.CRYPT_CROSSING)
                .assembly(new SimpleAssemblyConfig.Builder()
                        .entranceType(DataGen.resource("entrance"), BlueprintConfiguration.EntranceType.HYBRID, null,
                                new Entrance.CustomParts.Builder()
                                        .open(IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.CRYPT_ENTRANCE_OPEN))
                                                .build())
                                        .closed(IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.ENTRANCE_CLOSED_5X5))
                                                .build())
                                        .clusterOpen(IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN))
                                                .build())
                                        .build())
                        .build())
                .multipart(new BlueprintMultipart(DataGen.resource("part/ceiling/centered/5x5"), IRandom.<Holder<Blueprint>>builder()
                        .setPools(blueprintPools.getOrThrow(BlueprintPoolTags.CRYPT_CEILING_CENTERED_5X5))
                        .build()))
                .feature(new ChainedFeatures(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/grave")), new Constant(0)),
                        List.of(
                                new GraveFeature(
                                        new PlacementSettings(Optional.empty(), new Range(2, 4)),
                                        Optional.of(new SpawnerSettings(Optional.empty())),
                                        Optional.of(new ChestSettings())
                                ),
                                new GraveFeature(
                                        new PlacementSettings(Optional.empty(), new Range(1, 2)),
                                        Optional.empty(),
                                        Optional.of(new ChestSettings())
                                ),
                                new GraveFeature(
                                        new PlacementSettings(Optional.empty(), new Range(2, 4)),
                                        Optional.empty(),
                                        Optional.empty()
                                )
                        )))
                .build()));

        context.register(BlueprintKeys.Room.CRYPT_TURN, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.CRYPT_TURN)
                .assembly(new SimpleAssemblyConfig.Builder()
                        .entranceType(DataGen.resource("entrance/left"), BlueprintConfiguration.EntranceType.HYBRID, null,
                                new Entrance.CustomParts.Builder()
                                        .open(IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.CRYPT_ENTRANCE_OPEN))
                                                .build())
                                        .closed(IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.ENTRANCE_CLOSED_5X9))
                                                .build())
                                        .clusterOpen(IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN_LEFT))
                                                .build())
                                        .build()
                        )
                        .entranceType(DataGen.resource("entrance/right"), BlueprintConfiguration.EntranceType.HYBRID, null,
                                new Entrance.CustomParts.Builder()
                                        .open(IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.CRYPT_ENTRANCE_OPEN))
                                                .build())
                                        .closed(IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.ENTRANCE_CLOSED_5X9))
                                                .build())
                                        .clusterOpen(IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.CRYPT_ENTRANCE_CLUSTER_OPEN_RIGHT))
                                                .build())
                                        .build())
                        .build())
                .multipart(new BlueprintMultipart(DataGen.resource("part/crypt/sarcophagus"), IRandom.<Holder<Blueprint>>builder()
                        .setPools(blueprintPools.getOrThrow(BlueprintPoolTags.CRYPT_SARCOPHAGUS_SMALL))
                        .build()))
                .multipart(new BlueprintMultipart(DataGen.resource("part/crypt/graves"), IRandom.<Holder<Blueprint>>builder()
                        .setPools(blueprintPools.getOrThrow(BlueprintPoolTags.CRYPT_WALL))
                        .build()))
                .multipart(new BlueprintMultipart(DataGen.resource("part/ceiling/centered/5x5"), IRandom.<Holder<Blueprint>>builder()
                        .setPools(blueprintPools.getOrThrow(BlueprintPoolTags.CRYPT_CEILING_CENTERED_5X5))
                        .build()))
                .multipart(new BlueprintMultipart(DataGen.resource("part/ceiling/9x3"), IRandom.<Holder<Blueprint>>builder()
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.CEILING_9X3_FLAT))
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.CEILING_9X3_ORNATE))
                        .build()))
                .feature(new GraveFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/grave")), new Constant(2)),
                        Optional.of(new SpawnerSettings(Optional.empty())),
                        Optional.of(new ChestSettings())
                ))
                .build()));

        context.register(BlueprintKeys.Room.DARK_HALL, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.DARK_HALL)
                .assembly(new SimpleAssemblyConfig.Builder()
                        .entranceType(DataGen.resource("entrance"), null, new Entrance.CustomParts.Builder()
                                .open(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Part.DARK_HALL_OPEN))
                                        .build())
                                .closed(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Part.DARK_HALL_CLOSED))
                                        .build())
                                .build())
                        .build())
                .build()));

        context.register(BlueprintKeys.Room.DINER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.DINER)
                .assembly(new SimpleAssemblyConfig.Builder()
                        .entranceType(DataGen.resource("entrance"), Entrance.Decoration.PRIMARY, new Entrance.CustomParts.Builder()
                                .open(IRandom.<Holder<Blueprint>>builder()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Part.DINER_OPEN))
                                        .build())
                                .closed(IRandom.<Holder<Blueprint>>builder()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Part.DINER_CLOSED_TABLE))
                                        .add(blueprints.getOrThrow(BlueprintKeys.Part.DINER_CLOSED_STORAGE))
                                        .build())
                                .build())
                        .build())
                // The iron bars in front of the fireplace should be solid
                .configureBlock(Blocks.IRON_BARS, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .configureBlock(Blocks.NETHERRACK, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .configureBlock(Blocks.FIRE, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));

        context.register(BlueprintKeys.Room.ENIKO, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.ENIKO)
                .assembly(new SimpleAssemblyConfig.Builder()
                        .entranceType(DataGen.resource("entrance"), Entrance.Decoration.PRIMARY,
                                new Entrance.CustomParts.Builder()
                                        .open(new IRandom.Builder<Holder<Blueprint>>()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.FLOOR_3x3_SOLID))
                                                .build())
                                        .closed(new IRandom.Builder<Holder<Blueprint>>()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Part.FLOOR_3x3_MASONRY))
                                                .build())
                                        .build())
                        .build())
                .feature(new SpawnerFeature(
                        new PlacementSettings(Optional.of(SharedKeys.Anchor.Feature.SPAWNER), new Constant(1)),
                        new SpawnerSettings(Optional.empty())))
                .feature(new ChestFeature(
                        new PlacementSettings(Optional.of(SharedKeys.Anchor.Feature.CHEST), new Range(1, 2)),
                        new ChestSettings(Optional.empty())))
                .multipart(new BlueprintMultipart(SharedKeys.Anchor.FLOOR, new IRandom.Builder<Holder<Blueprint>>()
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.FLOOR_5x5_SOLID))
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.FLOOR_5x5_FRAGILE))
                        .build()))
                .build()));

        context.register(BlueprintKeys.Room.FORT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.FORT)
                .multipart(new BlueprintMultipart(DataGen.resource("part/spawner_pillar"), IRandom.<Holder<Blueprint>>builder()
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.FORT_SPAWNER_PILLAR))
                        .build()))
                .multipart(new BlueprintMultipart(DataGen.resource("part/ceiling/7x7"), IRandom.<Holder<Blueprint>>builder()
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.CEILING_7X7_FLAT))
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.CEILING_7X7_LAVA_PIT), 2)
                        .build()))
                .feature(new ChestFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/chest")), new Range(1, 2)),
                        new ChestSettings()
                ))
                .build()));

        context.register(BlueprintKeys.Room.LIBRARY, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.LIBRARY)
                .assembly(new SimpleAssemblyConfig.Builder()
                        .entranceType(DataGen.resource("entrance"), null, new Entrance.CustomParts.Builder()
                                .open(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Part.LIBRARY_ENTRANCE))
                                        .build())
                                .closed(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Part.LIBRARY_DESK))
                                        .add(blueprints.getOrThrow(BlueprintKeys.Part.LIBRARY_FLOWERS))
                                        .build())
                                .build())
                        .build())
                .configureBlock(Blocks.REDSTONE_BLOCK, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .configureBlock(Blocks.REDSTONE_LAMP, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));

        context.register(BlueprintKeys.Room.SARCOPHAGUS, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.SARCOPHAGUS)
                .feature(new SarcophagusFeature(
                        new PlacementSettings(Optional.of(SharedKeys.Anchor.Feature.SARCOPHAGUS),
                                new Constant(1)),
                        new ChestSettings(),
                        new SpawnerSettings(Optional.empty())))
                .build()));

        context.register(BlueprintKeys.Room.POOL, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.POOL)
                .feature(new ChestFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/chest")), new Range(1, 3)),
                        new ChestSettings(Optional.empty()))
                )
                .feature(new SpawnerFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/spawner")), new Range(1, 2)),
                        new SpawnerSettings(Optional.of(new TieredResource.Builder<>(
                                IRandom.<Holder<SpawnerType>>builder()
                                        .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.DROWNED_LEVEL_0))
                                        .build())
                                .tier(IRandom.<Holder<SpawnerType>>builder()
                                        .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.DROWNED_LEVEL_1))
                                        .build(), 1)
                                .tier(IRandom.<Holder<SpawnerType>>builder()
                                        .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.DROWNED_LEVEL_2))
                                        .build(), 2)
                                .tier(IRandom.<Holder<SpawnerType>>builder()
                                        .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.DROWNED_LEVEL_3))
                                        .build(), 3)
                                .tier(IRandom.<Holder<SpawnerType>>builder()
                                        .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.DROWNED_LEVEL_4))
                                        .build(), 4)
                                .build())))
                )
                .configureBlock(Blocks.WATER, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));

        context.register(BlueprintKeys.Room.POTION_LAB, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.POTION_LAB)
                .multipart(new BlueprintMultipart(
                        SharedKeys.Anchor.LAB_CONTAINER,
                        IRandom.<Holder<Blueprint>>builder()
                                .setPools(blueprintPools.getOrThrow(BlueprintPoolTags.LAB_CONTAINER))
                                .build()
                ))
                .mapBlock(Blocks.BREWING_STAND, new FixedTemplateBlockType(new RandomBlock(IRandom.<BlockState>builder()
                        .add(Blocks.BREWING_STAND.defaultBlockState())
                        .add(Blocks.AIR.defaultBlockState(), 3)
                        .build())))
                .configureBlock(Blocks.CHEST, TemplateBlockPlacementSettings.SOLID_PLACEMENT.withLoot(
                        new TieredResource.SingleTier<>(ChestLootTableKeys.LEVEL_0)
                ))
                .configureBlock(Blocks.CHAIN, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .feature(new SpawnerFeature(
                        new PlacementSettings(Optional.of(SharedKeys.Anchor.Feature.SPAWNER), new Constant(1)),
                        new SpawnerSettings(Optional.empty())
                ))
                .build()));

        context.register(BlueprintKeys.Room.SMITHY, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.SMITHY)
                .feature(new ChestFeature(
                        new PlacementSettings(Optional.of(DungeonCrawl.locate("chest")), new Constant(1)),
                        new ChestSettings(Optional.of(new TieredResource.Builder<>(ChestLootTableKeys.Speciality.TEMPERED_BLADE).build()))))
                .multipart(new BlueprintMultipart(SharedKeys.Anchor.FLOOR, new IRandom.Builder<Holder<Blueprint>>()
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.FLOOR_5x5_SOLID))
                        .add(blueprints.getOrThrow(BlueprintKeys.Part.FLOOR_5x5_FRAGILE))
                        .build()))
                .noGlobalDefaultPlacementSettings()
                .configureBlock(Blocks.COBBLESTONE, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
                .configureBlock(Blocks.COBBLESTONE_STAIRS, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
                .build()));

        context.register(BlueprintKeys.Room.SPIDER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.SPIDER)
                .feature(new ChestFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/chest")), new Range(2, 3)),
                        new ChestSettings()
                ))
                .feature(new SpawnerFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/spawner")), new Constant(1)),
                        new SpawnerSettings(Optional.of(new TieredResource.Builder<>(IRandom.<Holder<SpawnerType>>builder()
                                .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_0))
                                .build())
                                .tier(IRandom.<Holder<SpawnerType>>builder()
                                        .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_1))
                                        .build(), 1)
                                .tier(IRandom.<Holder<SpawnerType>>builder()
                                        .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_2))
                                        .build(), 2)
                                .tier(IRandom.<Holder<SpawnerType>>builder()
                                        .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_3))
                                        .build(), 3)
                                .tier(IRandom.<Holder<SpawnerType>>builder()
                                        .add(spawnerTypes.getOrThrow(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_4))
                                        .build(), 4)
                                .build()))
                ))
                .mapBlock(Blocks.AIR, new FixedTemplateBlockType(new RandomBlock(IRandom.<BlockState>builder()
                        .add(Blocks.CAVE_AIR.defaultBlockState(), 5)
                        .add(Blocks.COBWEB.defaultBlockState(), 10)
                        .add(Blocks.GRAVEL.defaultBlockState(), 3)
                        .build())))
                .configureBlock(Blocks.AIR, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));

        // TODO: map cobblestone and entrance types
        context.register(BlueprintKeys.Room.STAIRCASE_LOWER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.STAIRCASE_LOWER)
                .configureBlock(Blocks.COBBLESTONE, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT
                        .withAlternative(TemplateBlockType.FENCING.create())
                        .withPostProcessing())
                .build()));

        context.register(BlueprintKeys.Room.STAIRCASE_UPPER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.STAIRCASE_UPPER)
                .build()));

        context.register(BlueprintKeys.Room.STORAGE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Room.STORAGE)
                .feature(new ChestFeature(
                        new PlacementSettings(Optional.of(DataGen.resource("feature/chest")), new Range(1, 2)),
                        new ChestSettings()
                ))
                .build()));
    }
}
