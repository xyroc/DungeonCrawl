package xiroc.dungeoncrawl.dungeon.blueprint.template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;

public class TemplateBlueprintConfiguration {
    private static final ImmutableMap<ResourceLocation, TemplateBlock.PlacementProperties> DEFAULT_BLOCK_TYPES = ImmutableMap.<ResourceLocation, TemplateBlock.PlacementProperties>builder()
            .put(new ResourceLocation("water"), TemplateBlockType.FLUID.defaultProperties())
            .put(new ResourceLocation("chest"), TemplateBlockType.CHEST.defaultProperties())
            .put(new ResourceLocation("cobblestone"), TemplateBlockType.MASONRY.defaultProperties())
            .put(new ResourceLocation("cobblestone_stairs"), TemplateBlockType.STAIRS.defaultProperties())
            .put(new ResourceLocation("cobblestone_slab"), TemplateBlockType.SLAB.defaultProperties())
            .put(new ResourceLocation("stone_bricks"), TemplateBlockType.SOLID_MASONRY.defaultProperties())
            .put(new ResourceLocation("stone_brick_wall"), TemplateBlockType.WALL.defaultProperties())
            .put(new ResourceLocation("stone_brick_slab"), TemplateBlockType.SOLID_SLAB.defaultProperties())
            .put(new ResourceLocation("purpur_pillar"), TemplateBlockType.SOLID_PILLAR.defaultProperties())
            .put(new ResourceLocation("gravel"), TemplateBlockType.FLOOR.defaultProperties())
            .put(new ResourceLocation("cracked_stone_bricks"), TemplateBlockType.SOLID_FLOOR.defaultProperties())
            .put(new ResourceLocation("oak_log"), TemplateBlockType.PILLAR.defaultProperties())
            .put(new ResourceLocation("oak_planks"), TemplateBlockType.MATERIAL.defaultProperties())
            .put(new ResourceLocation("oak_door"), TemplateBlockType.DOOR.defaultProperties())
            .put(new ResourceLocation("oak_stairs"), TemplateBlockType.MATERIAL_STAIRS.defaultProperties())
            .put(new ResourceLocation("oak_slab"), TemplateBlockType.MATERIAL_SLAB.defaultProperties())
            .put(new ResourceLocation("oak_button"), TemplateBlockType.MATERIAL_BUTTON.defaultProperties())
            .put(new ResourceLocation("oak_fence"), TemplateBlockType.FENCE.defaultProperties())
            .put(new ResourceLocation("oak_fence_gate"), TemplateBlockType.FENCE_GATE.defaultProperties())
            .put(new ResourceLocation("oak_pressure_plate"), TemplateBlockType.MATERIAL_PRESSURE_PLATE.defaultProperties())
            .put(new ResourceLocation("oak_trapdoor"), TemplateBlockType.TRAPDOOR.defaultProperties())
            .put(new ResourceLocation("iron_bars"), TemplateBlockType.FENCING.defaultProperties())
            .build();

    protected final ResourceLocation template;
    protected final ImmutableMap<ResourceLocation, TemplateBlock.PlacementProperties> typeMap;
    protected final boolean useDefaultTypes;
    protected final BlueprintSettings settings;
    protected final ImmutableList<FeatureConfiguration> features;
    protected final ImmutableList<BlueprintMultipart> parts;

    protected TemplateBlueprintConfiguration(Builder builder) {
        this.template = builder.template;
        this.typeMap = ImmutableMap.copyOf(builder.typeMap);
        this.useDefaultTypes = builder.useDefaultTypes;
        this.settings = builder.settings;
        this.features = builder.features.build();
        this.parts = builder.parts.build();
    }

    protected TemplateBlock.PlacementProperties blockType(ResourceLocation block) {
        TemplateBlock.PlacementProperties type = typeMap.get(block);
        if (type == null && useDefaultTypes) {
            type = DEFAULT_BLOCK_TYPES.get(block);
        }
        return type != null ? type : TemplateBlockType.BLOCK.defaultProperties();
    }

    public static class Serializer implements JsonSerializer<TemplateBlueprintConfiguration>, JsonDeserializer<TemplateBlueprintConfiguration> {
        private static final String KEY_TEMPLATE = "template";
        private static final String KEY_BLOCK_TYPES = "block_types";
        private static final String KEY_INHERIT_DEFAULT_BLOCK_TYPES = "inherit_default_block_types";
        private static final String KEY_SETTINGS = "settings";
        private static final String KEY_FEATURES = "features";
        private static final String KEY_PARTS = "parts";

        @Override
        public TemplateBlueprintConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Builder builder = new Builder();
            builder.template = new ResourceLocation(object.get(KEY_TEMPLATE).getAsString());
            if (object.has(KEY_INHERIT_DEFAULT_BLOCK_TYPES)) {
                builder.useDefaultTypes = object.get(KEY_INHERIT_DEFAULT_BLOCK_TYPES).getAsBoolean();
            }
            if (object.has(KEY_BLOCK_TYPES)) {
                JsonObject map = object.getAsJsonObject(KEY_BLOCK_TYPES);
                map.entrySet().forEach((entry) ->
                        builder.typeMap.put(new ResourceLocation(entry.getKey()), context.deserialize(entry.getValue(), TemplateBlock.PlacementProperties.class)));
            }
            if (object.has(KEY_SETTINGS)) {
                builder.settings = context.deserialize(object.get(KEY_SETTINGS), BlueprintSettings.class);
            }
            if (object.has(KEY_FEATURES)) {
                for (JsonElement feature : object.getAsJsonArray(KEY_FEATURES)) {
                    builder.feature(context.deserialize(feature, FeatureConfiguration.class));
                }
            }
            if (object.has(KEY_PARTS)) {
                for (JsonElement multipart : object.getAsJsonArray(KEY_PARTS)) {
                    builder.multipart(context.deserialize(multipart, BlueprintMultipart.class));
                }
            }
            return builder.build();
        }

        @Override
        public JsonElement serialize(TemplateBlueprintConfiguration src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_TEMPLATE, src.template.toString());
            JsonObject map = new JsonObject();
            src.typeMap.forEach((key, properties) -> {
                if (!src.useDefaultTypes || !properties.equals(DEFAULT_BLOCK_TYPES.get(key))) {
                    map.add(key.toString(), context.serialize(properties));
                }
            });
            if (!map.entrySet().isEmpty()) {
                object.add(KEY_BLOCK_TYPES, map);
            }
            if (!src.useDefaultTypes) {
                object.addProperty(KEY_INHERIT_DEFAULT_BLOCK_TYPES, false);
            }
            if (!src.features.isEmpty()) {
                JsonArray features = new JsonArray();
                for (FeatureConfiguration feature : src.features) {
                    features.add(context.serialize(feature));
                }
                object.add(KEY_FEATURES, features);
            }
            if (!src.parts.isEmpty()) {
                JsonArray parts = new JsonArray();
                for (BlueprintMultipart part : src.parts) {
                    parts.add(context.serialize(part));
                }
                object.add(KEY_PARTS, parts);
            }
            return object;
        }
    }

    public static class Builder {
        private ResourceLocation template;
        private boolean useDefaultTypes = true;
        private final HashMap<ResourceLocation, TemplateBlock.PlacementProperties> typeMap = new HashMap<>();
        private final ImmutableList.Builder<FeatureConfiguration> features = ImmutableList.builder();
        private final ImmutableList.Builder<BlueprintMultipart> parts = ImmutableList.builder();

        private BlueprintSettings settings;

        public TemplateBlueprintConfiguration build() {
            Objects.requireNonNull(template);
            if (settings == null) {
                settings = BlueprintSettings.builder().build();
            }
            return new TemplateBlueprintConfiguration(this);
        }

        public Builder feature(FeatureConfiguration configuration) {
            this.features.add(configuration);
            return this;
        }

        public Builder multipart(BlueprintMultipart multipart) {
            this.parts.add(multipart);
            return this;
        }

        public Builder settings(BlueprintSettings settings) {
            this.settings = settings;
            return this;
        }

        public Builder template(ResourceLocation template) {
            this.template = template;
            return this;
        }

        public Builder doNotUseDefaultTypes() {
            this.useDefaultTypes = false;
            return this;
        }

        public Builder mapBlock(ResourceLocation block, TemplateBlock.PlacementProperties properties) {
            typeMap.put(block, properties);
            return this;
        }

        public Builder mapBlock(Block block, TemplateBlock.PlacementProperties properties) {
            return mapBlock(ForgeRegistries.BLOCKS.getKey(block), properties);
        }
    }
}