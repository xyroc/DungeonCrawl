package xiroc.dungeoncrawl.data.blueprint.provider;

import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;

public interface EntranceBlueprints {
    static void generate(BootstrapContext<Blueprint> context) {
        context.register(SharedKeys.Blueprints.Entrance.ENIKO_TOWER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(SharedKeys.Template.Entrance.ENIKO_TOWER)
                .noGlobalDefaultPlacementSettings()
                .defaultPlacementSettings(TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));
    }
}
