package xiroc.dungeoncrawl.data.blueprint.provider;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;

public interface PartBlueprints {
    static void generate(BootstrapContext<Blueprint> context) {
        context.register(SharedKeys.Blueprints.Part.DARK_HALL_OPEN, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Part.DARK_HALL_OPEN)
                .build()));

        context.register(SharedKeys.Blueprints.Part.DARK_HALL_CLOSED, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Part.DARK_HALL_CLOSED)
                .build()));

        context.register(SharedKeys.Blueprints.Part.FLOOR_3x3_MASONRY, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Part.FLOOR_3X3)
                .build()));

        context.register(SharedKeys.Blueprints.Part.FLOOR_3x3_SOLID, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Part.FLOOR_3X3)
                .mapBlock(Blocks.STONE_BRICKS, TemplateBlockType.FLOOR.create())
                .build()));

        context.register(SharedKeys.Blueprints.Part.FLOOR_5x5_SOLID, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Part.FLOOR_5x5)
                .configureBlock(Blocks.GRAVEL, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));

        context.register(SharedKeys.Blueprints.Part.FLOOR_5x5_FRAGILE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Part.FLOOR_5x5)
                .build()));

        context.register(SharedKeys.Blueprints.Part.LIBRARY_ENTRANCE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Part.LIBRARY_ENTRANCE)
                .build()));

        context.register(SharedKeys.Blueprints.Part.LIBRARY_DESK, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Part.LIBRARY_DESK)
                .build()));

        context.register(SharedKeys.Blueprints.Part.LIBRARY_FLOWERS, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Part.LIBRARY_FLOWERS)
                .build()));
    }
}
