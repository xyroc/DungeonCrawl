package xiroc.dungeoncrawl.data.blueprint;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.JsonDataProvider;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.blueprint.template.TemplateBlueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.template.TemplateBlueprintConfiguration;

import java.util.function.BiConsumer;

public class TemplateConfigurations extends JsonDataProvider<TemplateBlueprintConfiguration> {
    public TemplateConfigurations(DataGenerator generator) {
        super(generator, "Template Configurations", DatapackDirectories.BLUEPRINTS.path(), TemplateBlueprint.GSON::toJsonTree);
    }

    @Override
    public void collect(BiConsumer<ResourceLocation, TemplateBlueprintConfiguration> collector) {
        // TODO: add template configurations
    }
}
