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
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.mixin.accessor.StructureTemplateAccessor;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public record TemplateBlueprint(ResourceLocation key, Vec3i size, ImmutableList<TemplateBlock> blocks, ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors, BlueprintSettings settings,
                                ImmutableList<FeatureConfiguration> features, ImmutableList<BlueprintMultipart> parts) implements Blueprint {
    public static final Gson GSON = FeatureConfiguration.gsonAdapters(new GsonBuilder())
            .registerTypeAdapter(TemplateBlock.PlacementProperties.class, new TemplateBlock.PlacementProperties.Serializer())
            .registerTypeAdapter(TemplateBlueprintConfiguration.class, new TemplateBlueprintConfiguration.Serializer())
            .registerTypeAdapter(BlueprintMultipart.class, new BlueprintMultipart.Serializer())
            .registerTypeAdapter(BlueprintSettings.class, new BlueprintSettings.Serializer()).create();

    public static TemplateBlueprint load(ResourceManager resourceManager, ResourceLocation file, ResourceLocation key, Function<ResourceLocation, Optional<StructureTemplate>> templateLookup) {
        try {
            TemplateBlueprintConfiguration configuration = GSON.fromJson(new InputStreamReader(resourceManager.getResource(file).getInputStream()), TemplateBlueprintConfiguration.class);
            Optional<StructureTemplate> template = templateLookup.apply(configuration.template);
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

            return new TemplateBlueprint(key, template.get().getSize(), blocks.build(), immutableAnchors.build(), configuration.settings, configuration.features, configuration.parts);
        } catch (Exception e) {
            throw new DatapackLoadException("Failed to load " + file + ": " + e.getMessage());
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
        TemplateBlock.PlacementProperties properties = configuration.blockType(ForgeRegistries.BLOCKS.getKey(state.getBlock()));
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

    @Override
    public void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        WorldEditor editor = new WorldEditor(world, coordinateSpace(position), rotation);
        this.blocks.forEach((block) ->
                editor.placeBlock(block.placementProperties().blockType().blockFactory.get(block, rotation, world, position, primaryTheme, secondaryTheme, random, stage),
                        block.position(), worldGenBounds, block.placementProperties().isSolid(), true, true));
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