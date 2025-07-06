package xiroc.dungeoncrawl.dungeon.blueprint.template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.MetaBlock;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockColumn;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.mixin.accessor.StructureTemplateAccessor;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record TemplateBlueprint(Vec3i size,
                                List<TemplateBlockColumn> blockColumns,
                                ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors,
                                ImmutableList<BlueprintFeature> features,
                                ImmutableList<BlueprintMultipart> parts,
                                ImmutableList<Entrance> entrances) implements Blueprint {
    /**
     * Block coordinates must be smaller than 2^16 because x and z are packed into a 32-bit integer when loading blueprints.
     * No realistic blueprint should ever get even remotely close to this limit, but we are checking regardless.
     */
    private static final int THEORETICAL_MAX_BLUEPRINT_SIZE = (1 << 16) - 1;

    public static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(TemplateBlueprintConfiguration.class, new TemplateBlueprintConfiguration.Serializer())
                .registerTypeAdapter(TemplateBlueprintConfiguration.EntranceType.class, new TemplateBlueprintConfiguration.EntranceType.Serializer())
                .registerTypeAdapter(Entrance.CustomParts.class, new Entrance.CustomParts.Serializer())
                .registerTypeAdapter(BlueprintMultipart.class, new BlueprintMultipart.Serializer());
    }

    public static TemplateBlueprint load(ResourceManager resourceManager, ResourceLocation key, Reader file) {
        try {
            TemplateBlueprintConfiguration configuration = JSONUtils.GSON.fromJson(file, TemplateBlueprintConfiguration.class);
            Optional<StructureTemplate> template = loadTemplate(resourceManager, configuration.template);
            if (template.isEmpty()) {
                throw new DatapackLoadException("Could not find structure template: " + configuration.template);
            }
            Vec3i templateSize = template.get().getSize();
            if (templateSize.getX() > THEORETICAL_MAX_BLUEPRINT_SIZE || templateSize.getZ() > THEORETICAL_MAX_BLUEPRINT_SIZE) {
                throw new DatapackLoadException("Blueprint is too large! The maximum width/length is " + THEORETICAL_MAX_BLUEPRINT_SIZE);
            }

            StructureTemplateAccessor accessor = (StructureTemplateAccessor) template.get();

            HashMap<ResourceLocation, ImmutableList.Builder<Anchor>> anchors = new HashMap<>();
            ImmutableList.Builder<Entrance> entrances = ImmutableList.builder();

            Map<Integer, TemplateBlockColumn.Builder> blockColumns = new HashMap<>();

            accessor.palettes().getFirst().blocks().forEach((info) -> loadBlock(configuration, info, block -> {
                int x = block.position().getX();
                int z = block.position().getZ();
                // Pack x into the 16 most significant bits and z into the 16 least significant bits.
                int columnId = x << 16 | z;
                blockColumns.computeIfAbsent(columnId, ignored -> new TemplateBlockColumn.Builder(x, z)).addBlock(block);
            }, (anchorType, anchor) -> {
                anchors.computeIfAbsent(anchorType, (k) -> ImmutableList.builder()).add(anchor);
                var entranceType = configuration.entranceTypes.get(anchorType);
                if (entranceType != null) {
                    entrances.add(entranceType.make(anchor));
                }
            }));

            ImmutableMap.Builder<ResourceLocation, ImmutableList<Anchor>> immutableAnchors = ImmutableMap.builder();
            anchors.forEach((type, builder) -> immutableAnchors.put(type, builder.build()));

            var actualColumns = blockColumns.values().stream().map(TemplateBlockColumn.Builder::build).toList();
            return new TemplateBlueprint(template.get().getSize(), actualColumns, immutableAnchors.build(),
                    configuration.features,
                    configuration.parts,
                    entrances.build());
        } catch (Exception e) {
            throw new DatapackLoadException("Failed to load " + key + ": " + e.getMessage());
        }
    }

    private static void loadBlock(TemplateBlueprintConfiguration configuration, StructureTemplate.StructureBlockInfo info, Consumer<TemplateBlock> blocks, BiConsumer<ResourceLocation, Anchor> anchors) {
        BlockState state = info.state();
        if (state.getBlock() == Blocks.JIGSAW) {
            if (info.nbt() == null) {
                return;
            }
            state = parseBlockState(info.nbt().getString(JigsawBlockEntity.FINAL_STATE));
            ResourceLocation anchorType = ResourceLocation.parse(info.nbt().getString(JigsawBlockEntity.NAME));
            anchors.accept(anchorType, new Anchor(info.pos(), info.state().getValue(BlockStateProperties.ORIENTATION).front()));
        }
        if (state.getBlock() == Blocks.STRUCTURE_VOID) {
            // Ignore jigsaw blockColumns that turn into structure void.
            return;
        }
        TemplateBlockType type = configuration.blockType(state);
        TemplateBlock block = new TemplateBlock(type, info.pos(), new MetaBlock(state), configuration.blockPlacement(state.getBlock()));
        blocks.accept(block);
    }

    private static BlockState parseBlockState(String stateString) {
        try {
            BlockStateParser.BlockResult result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), new StringReader(stateString), false);
            return result.blockState();
        } catch (CommandSyntaxException e) {
            return Blocks.AIR.defaultBlockState();
        }
    }

    private static Optional<StructureTemplate> loadTemplate(ResourceManager resourceManager, ResourceLocation key) {
        try {
            ResourceLocation path = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "structures/" + key.getPath() + ".nbt");
            if (resourceManager.getResource(path).isEmpty()) {
                return Optional.empty();
            }
            CompoundTag nbt = NbtIo.readCompressed(resourceManager.getResource(path).get().open(), NbtAccounter.unlimitedHeap());
            StructureTemplate template = new StructureTemplate();
            template.load(BuiltInRegistries.BLOCK.asLookup(), nbt);
            return Optional.of(template);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load the structure template {} : {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void build(LevelAccessor level, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        PrimaryTheme primaryTheme = worldGenContext.primaryTheme().get();
        SecondaryTheme secondaryTheme = worldGenContext.secondaryTheme().get();
        CoordinateSpace coordinateSpace = coordinateSpace(position);

        this.blockColumns.forEach((column) -> {
            BlockPos columnPos = coordinateSpace.rotateAndTranslateToOrigin(column.x(), column.lowestY(), column.z(), rotation);
            if (!worldGenBounds.isInside(columnPos)) {
                return;
            }
            for (TemplateBlock templateBlock : column.blocks()) {
                BlockPos blockPosition = coordinateSpace.rotateAndTranslateToOrigin(templateBlock.position(), rotation);
                if (!worldGenBounds.isInside(blockPosition)) {
                    return;
                }
                boolean isAir = level.getBlockState(blockPosition).isAir();
                TemplateBlockPlacementSettings placementSettings = templateBlock.settings();
                TemplateBlockType templateBlockType = placementSettings.canPlace(isAir) ? templateBlock.type() : placementSettings.alternative();
                if (templateBlockType == null) {
                    continue;
                }

                BlockStateProvider provider = templateBlockType.chooseProvider(primaryTheme, secondaryTheme);
                BlockState state = templateBlock.block()
                        .applyProperties(provider.get(position, random))
                        .rotate(level, position, rotation);
                templateBlockType.handlePlacement(level, blockPosition, state, true);
            }
            if (columnPos.getY() <= worldGenContext.foundationHeight() && !level.getBlockState(columnPos).isAir()) {
                WorldEditor.buildFoundation(level, columnPos, random, worldGenBounds, worldGenContext);
            }
        });
    }

    @Override
    public int xSpan() {
        return this.size.getX();
    }

    @Override
    public int ySpan() {
        return this.size.getY();
    }

    @Override
    public int zSpan() {
        return this.size.getZ();
    }
}