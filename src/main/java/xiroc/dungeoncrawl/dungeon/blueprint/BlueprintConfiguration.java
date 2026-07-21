package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.FixedTemplateBlockType;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BlueprintConfiguration {
    private static final ImmutableMap<Block, TemplateBlockType> DEFAULT_BLOCK_TYPES = ImmutableMap.<Block, TemplateBlockType>builder()
            .put(Blocks.COBBLESTONE, TemplateBlockType.MASONRY.create())
            .put(Blocks.COBBLESTONE_STAIRS, TemplateBlockType.MASONRY_STAIRS.create())
            .put(Blocks.COBBLESTONE_SLAB, TemplateBlockType.MATERIAL_SLAB.create())

            .put(Blocks.STONE_BRICKS, TemplateBlockType.MASONRY.create())
            .put(Blocks.STONE_BRICK_STAIRS, TemplateBlockType.MASONRY_STAIRS.create())
            .put(Blocks.STONE_BRICK_SLAB, TemplateBlockType.MASONRY_SLAB.create())
            .put(Blocks.PURPUR_PILLAR, TemplateBlockType.MASONRY_PILLAR.create())
            .put(Blocks.POLISHED_BASALT, TemplateBlockType.MASONRY_PILLAR.create())

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

            .put(Blocks.RED_NETHER_BRICKS, TemplateBlockType.MASONRY.create())
            .put(Blocks.RED_NETHER_BRICK_STAIRS, TemplateBlockType.MASONRY_STAIRS.create())
            .put(Blocks.RED_NETHER_BRICK_SLAB, TemplateBlockType.MASONRY_SLAB.create())

            .put(Blocks.CRIMSON_PLANKS, TemplateBlockType.MATERIAL.create())
            .put(Blocks.CRIMSON_STAIRS, TemplateBlockType.MATERIAL_STAIRS.create())
            .put(Blocks.CRIMSON_STEM, TemplateBlockType.MATERIAL_PILLAR.create())
            .put(Blocks.CRIMSON_SLAB, TemplateBlockType.MATERIAL_SLAB.create())
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
            .put(Blocks.PURPUR_PILLAR, TemplateBlockPlacementSettings.SOLID_PLACEMENT)
            .put(Blocks.POLISHED_BASALT, TemplateBlockPlacementSettings.NON_SOLID_PLACEMENT)

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

            .put(Blocks.RED_NETHER_BRICKS, TemplateBlockPlacementSettings.ANTI_SOLID)
            .put(Blocks.RED_NETHER_BRICK_STAIRS, TemplateBlockPlacementSettings.ANTI_SOLID)
            .put(Blocks.RED_NETHER_BRICK_SLAB, TemplateBlockPlacementSettings.ANTI_SOLID)

            .put(Blocks.CRIMSON_PLANKS, TemplateBlockPlacementSettings.ANTI_SOLID)
            .put(Blocks.CRIMSON_STAIRS, TemplateBlockPlacementSettings.ANTI_SOLID)
            .put(Blocks.CRIMSON_STEM, TemplateBlockPlacementSettings.ANTI_SOLID)
            .put(Blocks.CRIMSON_SLAB, TemplateBlockPlacementSettings.ANTI_SOLID)
            .build();

    private static final ImmutableMap<ResourceLocation, ConfiguredEntrance> DEFAULT_ENTRANCE_TYPES = ImmutableMap.of(
            BuiltinAnchorTypes.ENTRANCE, new ConfiguredEntrance(Entrance.Decoration.PRIMARY, null, EntranceType.NORMAL)
    );

    // Default values for optional fields
    private interface Defaults {
        boolean USE_GLOBAL_DEFAULT_BLOCK_TYPES = true;
        boolean USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS = true;
    }

    private static final Codec<Map<Block, TemplateBlockType>> BLOCK_TYPE_MAP_CODEC = new UnboundedMapCodec<>(GlobalCodecs.BLOCK, TemplateBlockType.CODEC);
    private static final Codec<Map<Block, TemplateBlockPlacementSettings>> BLOCK_SETTINGS_MAP_CODEC = new UnboundedMapCodec<>(GlobalCodecs.BLOCK, TemplateBlockPlacementSettings.CODEC);
    private static final Codec<Map<ResourceLocation, ConfiguredEntrance>> ENTRANCE_TYPE_MAP_CODEC = new UnboundedMapCodec<>(ResourceLocation.CODEC, ConfiguredEntrance.CODEC);

    public static final Codec<BlueprintConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter(configuration -> configuration.template),

            BLOCK_TYPE_MAP_CODEC.optionalFieldOf("block_types").forGetter(configuration -> {
                final var types = configuration.useGlobalDefaultBlockTypes
                        ? StorageHelper.mapDifference(configuration.blockTypes, DEFAULT_BLOCK_TYPES) // Only serialize non-default types.
                        : configuration.blockTypes; // No default types. Need to serialize all specified ones.
                return types.isEmpty() ? Optional.empty() : Optional.of(types);
            }),

            Codec.BOOL.optionalFieldOf("use_global_default_block_types", Defaults.USE_GLOBAL_DEFAULT_BLOCK_TYPES)
                    .forGetter(blueprintConfiguration -> blueprintConfiguration.useGlobalDefaultBlockTypes),

            BLOCK_SETTINGS_MAP_CODEC.optionalFieldOf("placement_settings").forGetter(configuration -> {
                final var settings = configuration.useGlobalDefaultPlacementSettings
                        ? StorageHelper.mapDifference(configuration.placementSettings, DEFAULT_BLOCK_PLACEMENTS) // Only serialize non-default settings.
                        : configuration.placementSettings; // No default placement settings. Need to serialize all specified ones.
                return settings.isEmpty() ? Optional.empty() : Optional.of(settings);
            }),

            Codec.BOOL.optionalFieldOf("use_global_default_placement_settings", Defaults.USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS)
                    .forGetter(configuration -> configuration.useGlobalDefaultPlacementSettings),

            TemplateBlockPlacementSettings.CODEC.optionalFieldOf("default_placement_settings", TemplateBlockPlacementSettings.DEFAULT)
                    .forGetter(configuration -> configuration.defaultPlacementSettings),

            ENTRANCE_TYPE_MAP_CODEC.optionalFieldOf("entrance_types").forGetter(configuration -> {
                final var nonDefaultTypes = StorageHelper.mapDifference(configuration.entranceTypes, DEFAULT_ENTRANCE_TYPES);
                return nonDefaultTypes.isEmpty() ? Optional.empty() : Optional.of(nonDefaultTypes);
            }),

            BlueprintFeature.CODEC.listOf().optionalFieldOf("features").forGetter(configuration ->
                    configuration.features.isEmpty() ? Optional.empty() : Optional.of(configuration.features)),

            BlueprintMultipart.CODEC.listOf().optionalFieldOf("parts").forGetter(configuration ->
                    configuration.parts.isEmpty() ? Optional.empty() : Optional.of(configuration.parts))
    ).apply(instance, (template,
                       blockTypes,
                       useGlobalDefaultBlockTypes,
                       placementSettings,
                       useGlobalDefaultPlacementSettings,
                       defaultPlacementSettings,
                       entranceTypes,
                       features,
                       parts) ->
            new BlueprintConfiguration(template,
                    blockTypes.orElse(Map.of()),
                    useGlobalDefaultBlockTypes,
                    placementSettings.orElse(Map.of()),
                    useGlobalDefaultPlacementSettings,
                    defaultPlacementSettings,
                    entranceTypes.orElse(DEFAULT_ENTRANCE_TYPES),
                    features.orElse(List.of()),
                    parts.orElse(List.of()))
    ));

    /**
     * The location of the structure template to base the blueprint on.
     */
    protected final ResourceLocation template;
    /**
     * Custom template block types.
     */
    protected final Map<Block, TemplateBlockType> blockTypes;
    /**
     * Whether the global default template block types should be considered if no type is defined for a block.
     */
    protected final boolean useGlobalDefaultBlockTypes;
    /**
     * Custom placement settings for template blocks.
     */
    private final Map<Block, TemplateBlockPlacementSettings> placementSettings;
    /**
     * Whether the global default placement settings should be considered if no custom setting is found for a block.
     */
    private final boolean useGlobalDefaultPlacementSettings;
    /**
     * Custom default placement settings for template blocks.
     */
    private final TemplateBlockPlacementSettings defaultPlacementSettings;
    /**
     * Custom entrance types for anchor types.
     */
    protected final Map<ResourceLocation, ConfiguredEntrance> entranceTypes;
    /**
     * A list of all features for the blueprint.
     */
    public final List<BlueprintFeature> features;
    /**
     * A list of all multi-parts for the blueprint.
     */
    public final List<BlueprintMultipart> parts;

    private BlueprintConfiguration(ResourceLocation template,
                                   Map<Block, TemplateBlockType> blockTypes,
                                   boolean useGlobalDefaultBlockTypes,
                                   Map<Block, TemplateBlockPlacementSettings> placementSettings,
                                   boolean useGlobalDefaultPlacementSettings,
                                   TemplateBlockPlacementSettings defaultPlacementSettings,
                                   Map<ResourceLocation, ConfiguredEntrance> entranceTypes,
                                   List<BlueprintFeature> features,
                                   List<BlueprintMultipart> parts) {
        this.template = template;
        this.blockTypes = blockTypes;
        this.useGlobalDefaultBlockTypes = useGlobalDefaultBlockTypes;
        this.placementSettings = placementSettings;
        this.useGlobalDefaultPlacementSettings = useGlobalDefaultPlacementSettings;
        this.defaultPlacementSettings = defaultPlacementSettings;
        this.entranceTypes = entranceTypes;
        this.features = features;
        this.parts = parts;
    }

    private BlueprintConfiguration(Builder builder) {
        this.template = builder.template;
        this.blockTypes = ImmutableMap.copyOf(builder.blockTypes);
        this.useGlobalDefaultBlockTypes = builder.useDefaultBlockTypes;
        this.features = builder.features.build();
        this.parts = builder.parts.build();
        this.entranceTypes = builder.entrances == null ? DEFAULT_ENTRANCE_TYPES : builder.entrances.build();
        this.placementSettings = builder.placementSettings.build();
        this.defaultPlacementSettings = builder.defaultPlacementSettings != null ? builder.defaultPlacementSettings : TemplateBlockPlacementSettings.DEFAULT;
        this.useGlobalDefaultPlacementSettings = builder.useGlobalDefaultPlacementSettings;
    }

    protected TemplateBlockType blockType(BlockState block) {
        TemplateBlockType type = blockTypes.get(block.getBlock());
        if (type == null && useGlobalDefaultBlockTypes) {
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

    public enum EntranceType implements StringRepresentable {
        /**
         * Entrances that connect to corridors.
         */
        NORMAL,
        /**
         * Entrances that can be used as both normal and cluster entrances.
         */
        HYBRID,
        /**
         * Entrances that connect cluster nodes together.
         */
        CLUSTER;

        public static final Codec<EntranceType> CODEC = StringRepresentable.fromEnum(EntranceType::values);

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }

    protected record ConfiguredEntrance(@Nullable Entrance.Decoration decoration,
                                        @Nullable Entrance.CustomParts customParts,
                                        EntranceType type) {
        public static final Codec<ConfiguredEntrance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Entrance.Decoration.BY_NAME_CODEC.optionalFieldOf("decoration").forGetter(entrance -> Optional.ofNullable(entrance.decoration)),
                Entrance.CustomParts.CODEC.optionalFieldOf("custom_parts").forGetter(entrance -> Optional.ofNullable(entrance.customParts)),
                EntranceType.CODEC.optionalFieldOf("type", EntranceType.NORMAL).forGetter(ConfiguredEntrance::type)
        ).apply(instance, (decoration, customParts, type) ->
                new ConfiguredEntrance(decoration.orElse(null), customParts.orElse(null), type)));

        public Entrance make(Anchor placement) {
            return new Entrance(placement, decoration, customParts);
        }
    }

    public static class Builder {
        private ResourceLocation template;
        private boolean useDefaultBlockTypes = Defaults.USE_GLOBAL_DEFAULT_BLOCK_TYPES;
        private final HashMap<Block, TemplateBlockType> blockTypes = new HashMap<>();
        private final ImmutableList.Builder<BlueprintFeature> features = ImmutableList.builder();
        private final ImmutableList.Builder<BlueprintMultipart> parts = ImmutableList.builder();
        @Nullable
        private ImmutableMap.Builder<ResourceLocation, ConfiguredEntrance> entrances = null;
        private final ImmutableMap.Builder<Block, TemplateBlockPlacementSettings> placementSettings = ImmutableMap.builder();
        @Nullable
        private TemplateBlockPlacementSettings defaultPlacementSettings = null;
        private boolean useGlobalDefaultPlacementSettings = Defaults.USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS;

        public BlueprintConfiguration build() {
            Objects.requireNonNull(template);
            return new BlueprintConfiguration(this);
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
            return entranceType(anchorType, EntranceType.NORMAL, decoration, customParts);
        }

        public Builder entranceType(ResourceLocation anchorType, EntranceType type, @Nullable Entrance.Decoration decoration, @Nullable Entrance.CustomParts customParts) {
            return entranceType(anchorType, new ConfiguredEntrance(decoration, customParts, type));
        }

        private Builder entranceType(ResourceLocation anchorType, ConfiguredEntrance type) {
            if (entrances == null) {
                entrances = ImmutableMap.builder();
            }
            entrances.put(anchorType, type);
            return this;
        }
    }
}