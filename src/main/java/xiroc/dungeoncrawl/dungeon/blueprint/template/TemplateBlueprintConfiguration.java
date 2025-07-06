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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.FixedTemplateBlockType;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.util.JSONUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class TemplateBlueprintConfiguration {
    private static final ImmutableMap<Block, TemplateBlockType> DEFAULT_BLOCK_TYPES = ImmutableMap.<Block, TemplateBlockType>builder()
            .put(Blocks.COBBLESTONE, TemplateBlockType.MASONRY.create())
            .put(Blocks.COBBLESTONE_STAIRS, TemplateBlockType.MASONRY_STAIRS.create())
            .put(Blocks.COBBLESTONE_SLAB, TemplateBlockType.MATERIAL_SLAB.create())

            .put(Blocks.STONE_BRICKS, TemplateBlockType.MASONRY.create())
            .put(Blocks.STONE_BRICK_STAIRS, TemplateBlockType.MASONRY_STAIRS.create())
            .put(Blocks.STONE_BRICK_SLAB, TemplateBlockType.MASONRY_SLAB.create())
            .put(Blocks.PURPUR_PILLAR, TemplateBlockType.MASONRY_PILLAR.create())

            .put(Blocks.GRAVEL, TemplateBlockType.FLOOR.create())
            .put(Blocks.POLISHED_ANDESITE, TemplateBlockType.FLOOR.create())

            .put(Blocks.IRON_BARS, TemplateBlockType.FENCING.create())
            .put(Blocks.WATER, TemplateBlockType.FLUID.create())

            .put(Blocks.COBBLESTONE_WALL, TemplateBlockType.WALL.create())
            .put(Blocks.STONE_BRICK_WALL, TemplateBlockType.WALL.create())

            .put(Blocks.OAK_PLANKS, TemplateBlockType.MATERIAL.create())
            .put(Blocks.OAK_STAIRS, TemplateBlockType.MATERIAL_STAIRS.create())
            .put(Blocks.OAK_SLAB, TemplateBlockType.MATERIAL_SLAB.create())
            .put(Blocks.OAK_LOG, TemplateBlockType.MATERIAL_PILLAR.create())
            .put(Blocks.OAK_BUTTON, TemplateBlockType.BUTTON.create())
            .put(Blocks.OAK_PRESSURE_PLATE, TemplateBlockType.PRESSURE_PLATE.create())
            .put(Blocks.OAK_TRAPDOOR, TemplateBlockType.TRAPDOOR.create())
            .put(Blocks.OAK_FENCE, TemplateBlockType.FENCE.create())
            .put(Blocks.OAK_FENCE_GATE, TemplateBlockType.FENCE_GATE.create())
            .put(Blocks.OAK_DOOR, TemplateBlockType.DOOR.create(true))

            .put(Blocks.SPRUCE_PLANKS, TemplateBlockType.MATERIAL.create())
            .put(Blocks.SPRUCE_STAIRS, TemplateBlockType.MATERIAL_STAIRS.create())
            .put(Blocks.SPRUCE_SLAB, TemplateBlockType.MATERIAL_SLAB.create())
            .put(Blocks.SPRUCE_LOG, TemplateBlockType.MATERIAL_PILLAR.create())
            .put(Blocks.SPRUCE_BUTTON, TemplateBlockType.BUTTON.create())
            .put(Blocks.SPRUCE_PRESSURE_PLATE, TemplateBlockType.PRESSURE_PLATE.create())
            .put(Blocks.SPRUCE_TRAPDOOR, TemplateBlockType.TRAPDOOR.create())
            .put(Blocks.SPRUCE_FENCE, TemplateBlockType.FENCE.create())
            .put(Blocks.SPRUCE_FENCE_GATE, TemplateBlockType.FENCE_GATE.create())
            .put(Blocks.SPRUCE_DOOR, TemplateBlockType.DOOR.create(true))
            .build();

    private static final ImmutableMap<Block, TemplateBlockPlacementSettings> DEFAULT_BLOCK_PLACEMENTS = ImmutableMap.<Block, TemplateBlockPlacementSettings>builder()
            .put(Blocks.COBBLESTONE, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.COBBLESTONE_STAIRS, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.COBBLESTONE_SLAB, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.GRAVEL, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.COBBLESTONE_WALL, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)

            .put(Blocks.WATER, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.IRON_BARS, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)

            .put(Blocks.STONE_BRICKS, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.STONE_BRICK_STAIRS, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.STONE_BRICK_SLAB, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.POLISHED_ANDESITE, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.STONE_BRICK_WALL, TemplateBlockPlacementSettings.SOLID_PLACEMENT)

            .put(Blocks.OAK_PLANKS, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.OAK_STAIRS, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.OAK_SLAB, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.OAK_LOG, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.OAK_DOOR, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.OAK_BUTTON, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.OAK_PRESSURE_PLATE, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.OAK_TRAPDOOR, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.OAK_FENCE, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)
            .put(Blocks.OAK_FENCE_GATE, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)

            .put(Blocks.SPRUCE_PLANKS, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.SPRUCE_STAIRS, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.SPRUCE_SLAB, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.SPRUCE_LOG, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.SPRUCE_DOOR, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.SPRUCE_BUTTON, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.SPRUCE_PRESSURE_PLATE, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.SPRUCE_TRAPDOOR, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.SPRUCE_FENCE, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.SPRUCE_FENCE_GATE, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .build();

    private static final ImmutableMap<ResourceLocation, EntranceType> DEFAULT_ENTRANCE_TYPES = ImmutableMap.of(
            BuiltinAnchorTypes.ENTRANCE, new EntranceType(Optional.of(Entrance.Decoration.PRIMARY), Optional.empty())
    );

    protected final ResourceLocation template;
    protected final ImmutableMap<Block, TemplateBlockType> blockTypes;
    protected final boolean useDefaultTypes;
    protected final ImmutableList<BlueprintFeature> features;
    protected final ImmutableList<BlueprintMultipart> parts;
    protected final ImmutableMap<ResourceLocation, EntranceType> entranceTypes;
    /**
     * Custom placement settings for template blocks.
     */
    private final ImmutableMap<Block, TemplateBlockPlacementSettings> placementSettings;
    /**
     * Custom default placement settings for template blocks.
     */
    private final TemplateBlockPlacementSettings defaultPlacementSettings;
    /**
     * Whether the global default placement settings should be considered if no custom setting is found for a block.
     */
    private final boolean useGlobalDefaultPlacementSettings;

    protected TemplateBlueprintConfiguration(Builder builder) {
        this.template = builder.template;
        this.blockTypes = ImmutableMap.copyOf(builder.blockTypes);
        this.useDefaultTypes = builder.useDefaultBlockTypes;
        this.features = builder.features.build();
        this.parts = builder.parts.build();
        this.entranceTypes = builder.entranceTypes == null ? DEFAULT_ENTRANCE_TYPES : builder.entranceTypes.build();
        this.placementSettings = builder.placementSettings.build();
        this.defaultPlacementSettings = builder.defaultPlacementSettings != null ? builder.defaultPlacementSettings : TemplateBlockPlacementSettings.DEFAULT;
        this.useGlobalDefaultPlacementSettings = builder.useGlobalDefaultPlacementSettings;
    }

    protected TemplateBlockType blockType(BlockState block) {
        TemplateBlockType type = blockTypes.get(block.getBlock());
        if (type == null && useDefaultTypes) {
            type = DEFAULT_BLOCK_TYPES.get(block.getBlock());
        }
        return type != null ? type : new FixedTemplateBlockType(new SingleBlock(block));
    }

    protected TemplateBlockPlacementSettings blockPlacement(Block block) {
        TemplateBlockPlacementSettings settings = placementSettings.get(block);
        if (settings == null && useGlobalDefaultPlacementSettings) {
            settings = DEFAULT_BLOCK_PLACEMENTS.get(block);
        }
        return settings != null ? settings : defaultPlacementSettings;
    }

    public static class Serializer implements JsonSerializer<TemplateBlueprintConfiguration>, JsonDeserializer<TemplateBlueprintConfiguration> {
        private static final String KEY_TEMPLATE = "template";
        private static final String KEY_BLOCK_TYPES = "block_types";
        private static final String KEY_USE_DEFAULT_BLOCK_TYPES = "use_global_default_block_types";
        private static final String KEY_FEATURES = "features";
        private static final String KEY_PARTS = "parts";
        private static final String KEY_ENTRANCE_TYPES = "entrances";
        private static final String KEY_PLACEMENT_SETTINGS = "placement_settings";
        private static final String KEY_DEFAULT_PLACEMENT_SETTINGS = "default_placement_settings";
        private static final String KEY_USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS = "use_global_default_placement_settings";

        @Override
        public TemplateBlueprintConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Builder builder = new Builder();
            builder.template = ResourceLocation.parse(object.get(KEY_TEMPLATE).getAsString());
            if (object.has(KEY_USE_DEFAULT_BLOCK_TYPES)) {
                builder.useDefaultBlockTypes = object.get(KEY_USE_DEFAULT_BLOCK_TYPES).getAsBoolean();
            }
            if (object.has(KEY_BLOCK_TYPES)) {
                JsonObject map = object.getAsJsonObject(KEY_BLOCK_TYPES);
                map.entrySet().forEach((entry) -> {
                            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(entry.getKey()));
                            builder.blockTypes.put(block, JSONUtils.parse(entry.getValue(), TemplateBlockType.CODEC));
                        }
                );
            }
            if (object.has(KEY_FEATURES)) {
                for (JsonElement feature : object.getAsJsonArray(KEY_FEATURES)) {
                    builder.feature(context.deserialize(feature, BlueprintFeature.class));
                }
            }
            if (object.has(KEY_PARTS)) {
                for (JsonElement multipart : object.getAsJsonArray(KEY_PARTS)) {
                    BlueprintMultipart part = context.deserialize(multipart, BlueprintMultipart.class);
                    builder.parts.add(part);
                }
            }
            if (object.has(KEY_ENTRANCE_TYPES)) {
                object.getAsJsonObject(KEY_ENTRANCE_TYPES).entrySet().forEach((entry) ->
                        builder.entranceType(ResourceLocation.parse(entry.getKey()), context.deserialize(entry.getValue(), EntranceType.class)));
            }
            if (object.has(KEY_PLACEMENT_SETTINGS)) {
                JsonObject map = object.getAsJsonObject(KEY_PLACEMENT_SETTINGS);
                map.entrySet().forEach(entry -> {
                    Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(entry.getKey()));
                    builder.placementSettings.put(block, JSONUtils.parse(entry.getValue(), TemplateBlockPlacementSettings.CODEC));
                });
            }
            if (object.has(KEY_DEFAULT_PLACEMENT_SETTINGS)) {
                builder.defaultPlacementSettings = JSONUtils.parse(object.get(KEY_DEFAULT_PLACEMENT_SETTINGS), TemplateBlockPlacementSettings.CODEC);
            }
            if (object.has(KEY_USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS)) {
                builder.useGlobalDefaultPlacementSettings = object.get(KEY_USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS).getAsBoolean();
            }
            return builder.build();
        }

        @Override
        public JsonElement serialize(TemplateBlueprintConfiguration src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_TEMPLATE, src.template.toString());
            JsonObject map = new JsonObject();
            src.blockTypes.forEach((block, type) -> {
                if (!src.useDefaultTypes || !type.equals(DEFAULT_BLOCK_TYPES.get(block))) {
                    map.add(BuiltInRegistries.BLOCK.getKey(block).toString(), JSONUtils.encode(type, TemplateBlockType.CODEC));
                }
            });
            if (!map.entrySet().isEmpty()) {
                object.add(KEY_BLOCK_TYPES, map);
            }
            if (!src.useDefaultTypes) {
                object.addProperty(KEY_USE_DEFAULT_BLOCK_TYPES, false);
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
            JsonObject unmappedPlacements = new JsonObject();
            src.placementSettings.forEach((block, settings) -> {
                String name = BuiltInRegistries.BLOCK.getKey(block).toString();
                unmappedPlacements.add(name, JSONUtils.encode(settings, TemplateBlockPlacementSettings.CODEC));
            });
            if (!unmappedPlacements.entrySet().isEmpty()) {
                object.add(KEY_PLACEMENT_SETTINGS, unmappedPlacements);
            }
            if (!src.defaultPlacementSettings.equals(TemplateBlockPlacementSettings.DEFAULT)) {
                object.add(KEY_DEFAULT_PLACEMENT_SETTINGS, JSONUtils.encode(src.defaultPlacementSettings, TemplateBlockPlacementSettings.CODEC));
            }
            if (!src.useGlobalDefaultPlacementSettings) {
                object.addProperty(KEY_USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS, false);
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
        private boolean useDefaultBlockTypes = true;
        private final HashMap<Block, TemplateBlockType> blockTypes = new HashMap<>();
        private final ImmutableList.Builder<BlueprintFeature> features = ImmutableList.builder();
        private final ImmutableList.Builder<BlueprintMultipart> parts = ImmutableList.builder();
        @Nullable
        private ImmutableMap.Builder<ResourceLocation, EntranceType> entranceTypes = null;
        private final ImmutableMap.Builder<Block, TemplateBlockPlacementSettings> placementSettings = ImmutableMap.builder();
        @Nullable
        private TemplateBlockPlacementSettings defaultPlacementSettings = null;
        private boolean useGlobalDefaultPlacementSettings = true;

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

        public Builder noGlobalDefaultTypes() {
            this.useDefaultBlockTypes = false;
            return this;
        }

        public Builder mapBlock(Block block, TemplateBlockType type) {
            blockTypes.put(block, type);
            return this;
        }

        public Builder configureBlock(Block block, TemplateBlockPlacementSettings settings) {
            this.placementSettings.put(block, settings);
            return this;
        }

        public Builder defaultPlacementSettings(TemplateBlockPlacementSettings settings) {
            this.defaultPlacementSettings = settings;
            return this;
        }

        public Builder noGlobalDefaultPlacementSettings() {
            this.useGlobalDefaultPlacementSettings = false;
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