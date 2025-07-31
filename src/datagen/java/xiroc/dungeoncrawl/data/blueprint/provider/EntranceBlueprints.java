package xiroc.dungeoncrawl.data.blueprint.provider;

import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.blueprint.BlueprintKeys;
import xiroc.dungeoncrawl.data.blueprint.TemplateKeys;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;

public interface EntranceBlueprints {
    static void generate(BootstrapContext<Blueprint> context) {
        context.register(BlueprintKeys.Entrance.ENIKO_TOWER, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Entrance.ENIKO_TOWER)
                .noGlobalDefaultPlacementSettings()
                .defaultPlacementSettings(TemplateBlockPlacementSettings.SOLID_PLACEMENT)
                .build()));
    }
}
