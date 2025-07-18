package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.FixedTemplateBlockType;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlueprintConfiguration {
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

    // Default values for optional fields
    private interface Defaults {
        boolean USE_GLOBAL_DEFAULT_BLOCK_TYPES = true;
        boolean USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS = true;
    }

    private static final Codec<Map<Block, TemplateBlockType>> BLOCK_TYPE_MAP_CODEC = new UnboundedMapCodec<>(GlobalCodecs.BLOCK, TemplateBlockType.CODEC);
    private static final Codec<Map<Block, TemplateBlockPlacementSettings>> BLOCK_SETTINGS_MAP_CODEC = new UnboundedMapCodec<>(GlobalCodecs.BLOCK, TemplateBlockPlacementSettings.CODEC);
    private static final Codec<Map<ResourceLocation, EntranceType>> ENTRANCE_TYPE_MAP_CODEC = new UnboundedMapCodec<>(ResourceLocation.CODEC, EntranceType.CODEC);

    public static final Codec<BlueprintConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter(configuration -> configuration.template),

            BLOCK_TYPE_MAP_CODEC.optionalFieldOf("block_types").forGetter(configuration -> {
                final var nonDefaultTypes = configuration.blockTypes.entrySet().stream()
                        .filter(entry -> entry.getValue() != DEFAULT_BLOCK_TYPES.get(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                return nonDefaultTypes.isEmpty() ? Optional.empty() : Optional.of(nonDefaultTypes);
            }),

            Codec.BOOL.optionalFieldOf("use_global_default_block_types").forGetter(configuration ->
                    configuration.useGlobalDefaultBlockTypes == Defaults.USE_GLOBAL_DEFAULT_BLOCK_TYPES ? Optional.empty() : Optional.of(configuration.useGlobalDefaultBlockTypes)),

            BLOCK_SETTINGS_MAP_CODEC.optionalFieldOf("placement_settings").forGetter(configuration -> {
                final var nonDefaultSettings = configuration.placementSettings.entrySet().stream()
                        .filter(entry -> entry.getValue() != DEFAULT_BLOCK_PLACEMENTS.get(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                return nonDefaultSettings.isEmpty() ? Optional.empty() : Optional.of(nonDefaultSettings);
            }),

            Codec.BOOL.optionalFieldOf("use_global_default_placement_settings").forGetter(configuration ->
                    configuration.useGlobalDefaultPlacementSettings == Defaults.USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS ? Optional.empty() : Optional.of(configuration.useGlobalDefaultPlacementSettings)),

            TemplateBlockPlacementSettings.CODEC.optionalFieldOf("default_placement_settings").forGetter(configuration -> {
                final var settings = configuration.defaultPlacementSettings;
                return settings == TemplateBlockPlacementSettings.DEFAULT ? Optional.empty() : Optional.of(settings);
            }),

            ENTRANCE_TYPE_MAP_CODEC.optionalFieldOf("entrance_types").forGetter(configuration ->
                    configuration.entranceTypes.equals(DEFAULT_ENTRANCE_TYPES) ? Optional.empty() : Optional.of(configuration.entranceTypes)),

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
                    useGlobalDefaultBlockTypes.orElse(Defaults.USE_GLOBAL_DEFAULT_BLOCK_TYPES),
                    placementSettings.orElse(Map.of()),
                    useGlobalDefaultPlacementSettings.orElse(Defaults.USE_GLOBAL_DEFAULT_PLACEMENT_SETTINGS),
                    defaultPlacementSettings.orElse(TemplateBlockPlacementSettings.DEFAULT),
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
    protected final Map<ResourceLocation, EntranceType> entranceTypes;
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
                                   Map<ResourceLocation, EntranceType> entranceTypes,
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
        this.entranceTypes = builder.entranceTypes == null ? DEFAULT_ENTRANCE_TYPES : builder.entranceTypes.build();
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

    protected record EntranceType(Optional<Entrance.Decoration> decoration, Optional<Entrance.CustomParts> customParts) {
        public static final Codec<EntranceType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Entrance.Decoration.BY_NAME_CODEC.optionalFieldOf("decoration").forGetter(EntranceType::decoration),
                Entrance.CustomParts.CODEC.optionalFieldOf("custom_parts").forGetter(EntranceType::customParts)
        ).apply(instance, EntranceType::new));

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
        private ImmutableMap.Builder<ResourceLocation, EntranceType> entranceTypes = null;
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