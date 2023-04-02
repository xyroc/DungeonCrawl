package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.BuiltinBlueprints;
import xiroc.dungeoncrawl.dungeon.blueprint.template.TemplateBlueprint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class Blueprints {
    private static final String BLUEPRINT_DIRECTORY = DatapackDirectories.BLUEPRINTS.path();

    private static ImmutableMap<ResourceLocation, Blueprint> BLUEPRINTS;
    private static ImmutableSet<ResourceLocation> KEYS;

    public static void load(ResourceManager resourceManager) {
        HashMap<ResourceLocation, StructureTemplate> templateCache = new HashMap<>();
        ImmutableMap.Builder<ResourceLocation, Blueprint> builder = ImmutableMap.builder();

        BuiltinBlueprints.register(builder::put);

        resourceManager.listResources(BLUEPRINT_DIRECTORY, (s) -> s.endsWith(".json"))
                .forEach((file) -> {
                    ResourceLocation key = DatapackDirectories.BLUEPRINTS.key(file, ".json");
                    Blueprint blueprint = TemplateBlueprint.load(resourceManager, file, key,
                            (templateKey) -> {
                                if (templateCache.containsKey(templateKey)) {
                                    return Optional.of(templateCache.get(templateKey));
                                }
                                Optional<StructureTemplate> template = loadTemplate(resourceManager, templateKey);
                                template.ifPresent((structureTemplate) -> templateCache.put(templateKey, structureTemplate));
                                return template;
                            });
                    builder.put(key, blueprint);
                });

        templateCache.clear();
        BLUEPRINTS = builder.build();
        KEYS = BLUEPRINTS.keySet();
    }

    private static Optional<StructureTemplate> loadTemplate(ResourceManager resourceManager, ResourceLocation key) {
        try {
            ResourceLocation path = new ResourceLocation(key.getNamespace(), "structures/" + key.getPath() + ".nbt");
            if (!resourceManager.hasResource(path)) {
                return Optional.empty();
            }
            CompoundTag nbt = NbtIo.readCompressed(resourceManager.getResource(path).getInputStream());
            StructureTemplate template = new StructureTemplate();
            template.load(nbt);
            return Optional.of(template);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load the structure template {} : {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    public static boolean exists(ResourceLocation key) {
        return BLUEPRINTS.containsKey(key);
    }

    public static Blueprint getBlueprint(ResourceLocation key) {
        return BLUEPRINTS.getOrDefault(key, Blueprint.EMPTY);
    }

    public static ImmutableSet<ResourceLocation> getKeys() {
        return KEYS;
    }

    public static ImmutableCollection<Blueprint> blueprints() {
        return BLUEPRINTS.values();
    }
}