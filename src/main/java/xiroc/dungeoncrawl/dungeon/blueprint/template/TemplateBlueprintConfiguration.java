package xiroc.dungeoncrawl.dungeon.blueprint.template;

import com.google.common.collect.ImmutableBiMap;
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
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

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

    private static final ImmutableMap<ResourceLocation, EntranceType> DEFAULT_ENTRANCE_TYPES = ImmutableMap.of(
            BuiltinAnchorTypes.ENTRANCE, new EntranceType(Optional.of(Entrance.Decoration.PRIMARY), Optional.empty())
    );

    protected final ResourceLocation template;
    protected final ImmutableMap<Block, TemplateBlock.PlacementProperties> blockTypes;
    protected final boolean useDefaultTypes;
    protected final ImmutableList<BlueprintFeature> features;
    protected final ImmutableList<BlueprintMultipart> parts;
    protected final ImmutableMap<ResourceLocation, EntranceType> entranceTypes;

    protected TemplateBlueprintConfiguration(Builder builder) {
        this.template = builder.template;
        this.blockTypes = ImmutableMap.copyOf(builder.blockTypes);
        this.useDefaultTypes = builder.useDefaultTypes;
        this.features = builder.features.build();
        this.parts = builder.parts.build();
        this.entranceTypes = builder.entranceTypes == null ? DEFAULT_ENTRANCE_TYPES : builder.entranceTypes.build();
    }

    protected TemplateBlock.PlacementProperties blockType(Block block) {
        TemplateBlock.PlacementProperties type = blockTypes.get(block);
        if (type == null && useDefaultTypes) {
            type = DEFAULT_BLOCK_TYPES.get(block);
        }
        return type != null ? type : TemplateBlockType.BLOCK.placementProperties(false);
    }

    public static class Serializer implements JsonSerializer<TemplateBlueprintConfiguration>, JsonDeserializer<TemplateBlueprintConfiguration> {
        private static final String KEY_TEMPLATE = "template";
        private static final String KEY_BLOCK_TYPES = "block_types";
        private static final String KEY_INHERIT_DEFAULT_BLOCK_TYPES = "inherit_default_block_types";
        private static final String KEY_FEATURES = "features";
        private static final String KEY_PARTS = "parts";
        private static final String KEY_ENTRANCE_TYPES = "entrances";

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
                        builder.blockTypes.put(Registry.BLOCK.get(new ResourceLocation(entry.getKey())), context.deserialize(entry.getValue(), TemplateBlock.PlacementProperties.class)));
            }
            if (object.has(KEY_FEATURES)) {
                for (JsonElement feature : object.getAsJsonArray(KEY_FEATURES)) {
                    builder.feature(context.deserialize(feature, BlueprintFeature.class));
                }
            }
            if (object.has(KEY_PARTS)) {
                for (JsonElement multipart : object.getAsJsonArray(KEY_PARTS)) {
                    builder.multipart(context.deserialize(multipart, BlueprintMultipart.class));
                }
            }
            if (object.has(KEY_ENTRANCE_TYPES)) {
                object.getAsJsonObject(KEY_ENTRANCE_TYPES).entrySet().forEach((entry) ->
                        builder.entranceType(new ResourceLocation(entry.getKey()), context.deserialize(entry.getValue(), EntranceType.class)));
            }
            return builder.build();
        }

        @Override
        public JsonElement serialize(TemplateBlueprintConfiguration src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_TEMPLATE, src.template.toString());
            JsonObject map = new JsonObject();
            src.blockTypes.forEach((block, properties) -> {
                if (!src.useDefaultTypes || !properties.equals(DEFAULT_BLOCK_TYPES.get(block))) {
                    map.add(Registry.BLOCK.getKey(block).toString(), context.serialize(properties));
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
                for (BlueprintFeature feature : src.features) {
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
            if (!src.entranceTypes.equals(DEFAULT_ENTRANCE_TYPES)) {
                JsonObject entrances = new JsonObject();
                src.entranceTypes.forEach((key, value) -> entrances.add(key.toString(), context.serialize(value)));
                object.add(KEY_ENTRANCE_TYPES, entrances);
            }
            return object;
        }
    }

    protected record EntranceType(Optional<Entrance.Decoration> decoration, Optional<Entrance.CustomParts> customParts) {
        public Entrance make(Anchor placement) {
            return new Entrance(placement, decoration, customParts);
        }

        protected static class Serializer implements JsonSerializer<EntranceType>, JsonDeserializer<EntranceType> {
            private static final String KEY_DECORATION = "decoration";
            private static final String KEY_CUSTOM_PARTS = "custom_parts";

            private static final ImmutableBiMap<String, Entrance.Decoration> DECORATIONS = ImmutableBiMap.<String, Entrance.Decoration>builder()
                    .put("primary", Entrance.Decoration.PRIMARY)
                    .put("secondary", Entrance.Decoration.SECONDARY)
                    .build();

            @Override
            public EntranceType deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
                JsonObject object = json.getAsJsonObject();
                Optional<Entrance.Decoration> entranceDecoration = Optional.empty();
                if (object.has(KEY_DECORATION)) {
                    entranceDecoration = Optional.ofNullable(DECORATIONS.get(object.get(KEY_DECORATION).getAsString()));
                }
                Optional<Entrance.CustomParts> customParts = Optional.empty();
                if (object.has(KEY_CUSTOM_PARTS)) {
                    customParts = Optional.of(context.deserialize(object.get(KEY_CUSTOM_PARTS), Entrance.CustomParts.class));
                }
                return new EntranceType(entranceDecoration, customParts);
            }

            @Override
            public JsonElement serialize(EntranceType entranceType, Type type, JsonSerializationContext context) {
                JsonObject object = new JsonObject();
                entranceType.decoration.ifPresent(decoration -> object.addProperty(KEY_DECORATION, DECORATIONS.inverse().get(decoration)));
                entranceType.customParts.ifPresent(customParts -> object.add(KEY_CUSTOM_PARTS, context.serialize(customParts)));
                return object;
            }
        }
    }

    public static class Builder {
        private ResourceLocation template;
        private boolean useDefaultTypes = true;
        private final HashMap<Block, TemplateBlock.PlacementProperties> blockTypes = new HashMap<>();
        private final ImmutableList.Builder<BlueprintFeature> features = ImmutableList.builder();
        private final ImmutableList.Builder<BlueprintMultipart> parts = ImmutableList.builder();
        private ImmutableMap.Builder<ResourceLocation, EntranceType> entranceTypes = null;

        public TemplateBlueprintConfiguration build() {
            Objects.requireNonNull(template);
            return new TemplateBlueprintConfiguration(this);
        }

        public Builder feature(BlueprintFeature configuration) {
            this.features.add(configuration);
            return this;
        }

        public Builder multipart(BlueprintMultipart multipart) {
            this.parts.add(multipart);
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
            blockTypes.put(block, properties);
            return this;
        }

        public Builder entranceType(ResourceLocation anchorType, @Nullable Entrance.Decoration decoration, @Nullable Entrance.CustomParts customParts) {
            return entranceType(anchorType, new EntranceType(Optional.ofNullable(decoration), Optional.ofNullable(customParts)));
        }

        private Builder entranceType(ResourceLocation anchorType, EntranceType type) {
            if (entranceTypes == null) {
                entranceTypes = ImmutableMap.builder();
            }
            entranceTypes.put(anchorType, type);
            return this;
        }
    }
}