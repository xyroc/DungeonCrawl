package xiroc.dungeoncrawl.data.blueprint.provider;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import xiroc.dungeoncrawl.data.blueprint.BlueprintKeys;
import xiroc.dungeoncrawl.data.blueprint.TemplateKeys;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;

public interface PartBlueprints {
    static void generate(BootstrapContext<Blueprint> context) {
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
