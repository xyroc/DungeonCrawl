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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;

public class TemplateBlueprintConfiguration {
    private static final ImmutableMap<Block, TemplateBlock.PlacementProperties> DEFAULT_BLOCK_TYPES = ImmutableMap.<Block, TemplateBlock.PlacementProperties>builder()
            .put(Blocks.COBBLESTONE, TemplateBlockType.MASONRY.placementProperties(false))
            .put(Blocks.COBBLESTONE_STAIRS, TemplateBlockType.MASONRY_STAIRS.placementProperties(false))
            .put(Blocks.COBBLESTONE_SLAB, TemplateBlockType.MASONRY_SLAB.placementProperties(false))

            .put(Blocks.STONE_BRICKS, TemplateBlockType.MASONRY.placementProperties(true))
            .put(Blocks.STONE_BRICK_STAIRS, TemplateBlockType.MASONRY_STAIRS.placementProperties(true))
            .put(Blocks.STONE_BRICK_SLAB, TemplateBlockType.MASONRY_SLAB.placementProperties(true))

            .put(Blocks.PURPUR_PILLAR, TemplateBlockType.MASONRY_PILLAR.placementProperties(true))
            .put(Blocks.GRAVEL, TemplateBlockType.FLOOR.placementProperties(false))
            .put(Blocks.POLISHED_ANDESITE, TemplateBlockType.FLOOR.placementProperties(true))
            .put(Blocks.IRON_BARS, TemplateBlockType.FENCING.placementProperties(true))
            .put(Blocks.WATER, TemplateBlockType.FLUID.placementProperties(false))
            .put(Blocks.STONE_BRICK_WALL, TemplateBlockType.WALL.placementProperties(true))

            .put(Blocks.OAK_LOG, TemplateBlockType.MATERIAL_PILLAR.placementProperties(false))
            .put(Blocks.OAK_PLANKS, TemplateBlockType.MATERIAL.placementProperties(false))
            .put(Blocks.OAK_STAIRS, TemplateBlockType.MATERIAL_STAIRS.placementProperties(false))
            .put(Blocks.OAK_SLAB, TemplateBlockType.MATERIAL_SLAB.placementProperties(false))

            .put(Blocks.SPRUCE_LOG, TemplateBlockType.MATERIAL_PILLAR.placementProperties(true))
            .put(Blocks.SPRUCE_PLANKS, TemplateBlockType.MATERIAL.placementProperties(true))
            .put(Blocks.SPRUCE_STAIRS, TemplateBlockType.MATERIAL_STAIRS.placementProperties(true))
            .put(Blocks.SPRUCE_SLAB, TemplateBlockType.MATERIAL_SLAB.placementProperties(true))

            .put(Blocks.OAK_BUTTON, TemplateBlockType.BUTTON.placementProperties(false))
            .put(Blocks.OAK_PRESSURE_PLATE, TemplateBlockType.PRESSURE_PLATE.placementProperties(false))
            .put(Blocks.OAK_DOOR, TemplateBlockType.DOOR.placementProperties(false))
            .put(Blocks.OAK_TRAPDOOR, TemplateBlockType.TRAPDOOR.placementProperties(false))
            .put(Blocks.OAK_FENCE, TemplateBlockType.FENCE.placementProperties(false))
            .put(Blocks.OAK_FENCE_GATE, TemplateBlockType.FENCE_GATE.placementProperties(false))
            .build();

    protected final ResourceLocation template;
    protected final ImmutableMap<Block, TemplateBlock.PlacementProperties> typeMap;
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

    protected TemplateBlock.PlacementProperties blockType(Block block) {
        TemplateBlock.PlacementProperties type = typeMap.get(block);
        if (type == null && useDefaultTypes) {
            type = DEFAULT_BLOCK_TYPES.get(block);
        }
        return type != null ? type : TemplateBlockType.BLOCK.placementProperties(false);
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
                        builder.typeMap.put(Registry.BLOCK.get(new ResourceLocation(entry.getKey())), context.deserialize(entry.getValue(), TemplateBlock.PlacementProperties.class)));
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
        private final HashMap<Block, TemplateBlock.PlacementProperties> typeMap = new HashMap<>();
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

        public Builder mapBlock(Block block, TemplateBlock.PlacementProperties properties) {
            typeMap.put(block, properties);
            return this;
        }
    }
}