package xiroc.dungeoncrawl.data.blueprint.provider;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.ChestFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.SarcophagusFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.SpawnerFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Constant;
import xiroc.dungeoncrawl.util.random.value.Range;

import java.util.Optional;

public interface RoomBlueprints {
    static void generate(BootstrapContext<Blueprint> context) {
        final var blueprints = context.lookup(DatapackRegistries.BLUEPRINT);

        context.register(SharedKeys.Blueprints.Room.DARK_HALL, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Room.DARK_HALL)
                .entranceType(BuiltinAnchorTypes.ENTRANCE, null, new Entrance.CustomParts.Builder()
                        .open(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.DARK_HALL_OPEN))
                                .build())
                        .closed(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.DARK_HALL_CLOSED))
                                .build())
                        .build())
                .build()));

        context.register(SharedKeys.Blueprints.Room.ENIKO, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Room.ENIKO)
                .entranceType(BuiltinAnchorTypes.ENTRANCE, Entrance.Decoration.PRIMARY,
                        new Entrance.CustomParts.Builder()
                                .open(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.FLOOR_3x3_SOLID))
                                        .build())
                                .closed(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.FLOOR_3x3_MASONRY))
                                        .build())
                                .build())
                .feature(new SpawnerFeature(
                        new PlacementSettings(Optional.of(SharedKeys.Anchor.Feature.SPAWNER), new Constant(1)),
                        new SpawnerSettings(Optional.empty())))
                .feature(new ChestFeature(
                        new PlacementSettings(Optional.of(SharedKeys.Anchor.Feature.CHEST), new Range(1, 2)),
                        new ChestSettings(Optional.empty())))
                .multipart(new BlueprintMultipart(SharedKeys.Anchor.FLOOR, new IRandom.Builder<Holder<Blueprint>>()
                        .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.FLOOR_5x5_SOLID))
                        .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.FLOOR_5x5_FRAGILE))
                        .build()))
                .build()));

        context.register(SharedKeys.Blueprints.Room.LIBRARY, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Room.LIBRARY)
                .entranceType(BuiltinAnchorTypes.ENTRANCE, null, new Entrance.CustomParts.Builder()
                        .open(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.LIBRARY_ENTRANCE))
                                .build())
                        .closed(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.LIBRARY_DESK))
                                .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.LIBRARY_FLOWERS))
                                .build())
                        .build())
                .configureBlock(Blocks.REDSTONE_BLOCK, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .configureBlock(Blocks.REDSTONE_LAMP, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));

        // TODO: map cobblestone and entrance types
        context.register(SharedKeys.Blueprints.Room.LOWER_STAIRCASE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Room.LOWER_STAIRCASE)
                .configureBlock(Blocks.COBBLESTONE, new TemplateBlockPlacementSettings(false, true, TemplateBlockType.FENCING.create()))
                .build()));

        context.register(SharedKeys.Blueprints.Room.SARCOPHAGUS, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Room.SARCOPHAGUS)
                .feature(new SarcophagusFeature(
                        new PlacementSettings(Optional.of(SharedKeys.Anchor.Feature.SARCOPHAGUS),
                                new Constant(1)),
                        new ChestSettings(),
                        new SpawnerSettings(Optional.empty())))
                .build()));

        context.register(SharedKeys.Blueprints.Room.SMITHY, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Room.SMITHY)
                .feature(new ChestFeature(
                        new PlacementSettings(Optional.of(DungeonCrawl.locate("chest")), new Constant(1)),
                        new ChestSettings(Optional.of(new TieredResource.Builder<>(SharedKeys.Loot.Specialities.TEMPERED_BLADE).build()))))
                .multipart(new BlueprintMultipart(SharedKeys.Anchor.FLOOR, new IRandom.Builder<Holder<Blueprint>>()
                        .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.FLOOR_5x5_SOLID))
                        .add(blueprints.getOrThrow(SharedKeys.Blueprints.Part.FLOOR_5x5_FRAGILE))
                        .build()))
                .noGlobalDefaultPlacementSettings()
                .configureBlock(Blocks.COBBLESTONE, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
                .configureBlock(Blocks.COBBLESTONE_STAIRS, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
                .build()));

        context.register(SharedKeys.Blueprints.Room.UPPER_STAIRCASE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Room.UPPER_STAIRCASE)
                .build()));
    }
}
