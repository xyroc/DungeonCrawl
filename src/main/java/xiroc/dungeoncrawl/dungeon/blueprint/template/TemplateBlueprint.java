package xiroc.dungeoncrawl.dungeon.blueprint.template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.mixin.accessor.StructureTemplateAccessor;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record TemplateBlueprint(Vec3i size, ImmutableList<TemplateBlock> blocks, ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors,
                                BlueprintSettings settings, ImmutableList<FeatureConfiguration> features, ImmutableList<BlueprintMultipart> parts) implements Blueprint {

    public static final Gson GSON = FeatureConfiguration.gsonAdapters(new GsonBuilder())
            .registerTypeAdapter(TemplateBlock.PlacementProperties.class, new TemplateBlock.PlacementProperties.Serializer())
            .registerTypeAdapter(TemplateBlueprintConfiguration.class, new TemplateBlueprintConfiguration.Serializer())
            .registerTypeAdapter(BlueprintMultipart.class, new BlueprintMultipart.Serializer())
            .registerTypeAdapter(BlueprintSettings.class, new BlueprintSettings.Serializer()).create();

    public static TemplateBlueprint load(ResourceManager resourceManager, ResourceLocation key, Reader file) {
        try {
            TemplateBlueprintConfiguration configuration = GSON.fromJson(file, TemplateBlueprintConfiguration.class);
            Optional<StructureTemplate> template = loadTemplate(resourceManager, configuration.template);
            if (template.isEmpty()) {
                throw new DatapackLoadException("Could not find structure template: " + configuration.template);
            }
            StructureTemplateAccessor accessor = (StructureTemplateAccessor) template.get();

            ImmutableList.Builder<TemplateBlock> blocks = ImmutableList.builder();
            HashMap<ResourceLocation, ImmutableList.Builder<Anchor>> anchors = new HashMap<>();

            accessor.palettes().get(0).blocks().forEach((info) ->
                    loadBlock(configuration, info, blocks::add, (type, anchor) -> anchors.computeIfAbsent(type, (k) -> ImmutableList.builder()).add(anchor)));

            ImmutableMap.Builder<ResourceLocation, ImmutableList<Anchor>> immutableAnchors = ImmutableMap.builder();
            anchors.forEach((type, builder) -> immutableAnchors.put(type, builder.build()));

            return new TemplateBlueprint(template.get().getSize(), blocks.build(), immutableAnchors.build(), configuration.settings, configuration.features, configuration.parts);
        } catch (Exception e) {
            throw new DatapackLoadException("Failed to load " + key + ": " + e.getMessage());
        }
    }

    private static void loadBlock(TemplateBlueprintConfiguration configuration, StructureTemplate.StructureBlockInfo info, Consumer<TemplateBlock> blocks, BiConsumer<ResourceLocation, Anchor> anchors) {
        BlockState state = info.state;
        if (state.getBlock() == Blocks.JIGSAW) {
            if (info.nbt == null) {
                throw new DatapackLoadException("Jigsaw block without nbt data at " + info.pos.getX() + ',' + info.pos.getY() + ',' + info.pos.getZ());
            }
            state = parseBlockState(info.nbt.getString(JigsawBlockEntity.FINAL_STATE));
            ResourceLocation anchorType = new ResourceLocation(info.nbt.getString(JigsawBlockEntity.NAME));
            anchors.accept(anchorType, new Anchor(info.pos, info.state.getValue(BlockStateProperties.ORIENTATION).front()));
        }
        TemplateBlock.PlacementProperties properties = configuration.blockType(state.getBlock());
        TemplateBlock block = new TemplateBlock(properties, info.pos, state.getBlock(), TemplateBlock.properties(state));
        blocks.accept(block);
    }

    private static BlockState parseBlockState(String stateString) {
        BlockStateParser parser = new BlockStateParser(new StringReader(stateString), false);
        try {
            parser.parse(true);
            BlockState state = parser.getState();
            if (state == null) {
                throw new DatapackLoadException("Error while parsing block state: " + stateString);
            }
            return state;
        } catch (CommandSyntaxException e) {
            return Blocks.AIR.defaultBlockState();
        }
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

    @Override
    public void build(LevelAccessor level, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        CoordinateSpace coordinateSpace = coordinateSpace(position);
        this.blocks.forEach((block) -> {
            boolean solid = block.placementProperties().isSolid();
            BlockPos pos = coordinateSpace.rotateAndTranslateToOrigin(block.position(), rotation);
            BlockState state = block.placementProperties().blockType().blockFactory.get(block, level, pos, primaryTheme, secondaryTheme, random).rotate(level, pos, rotation);
            WorldEditor.placeBlock(level, state, pos, worldGenBounds, solid, true, true);
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