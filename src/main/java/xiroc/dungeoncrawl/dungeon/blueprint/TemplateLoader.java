package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.MetaBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.type.AssemblyTypeBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockColumn;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.mixin.accessor.StructureTemplateAccessor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles the parsing process of structure templates and retroactively updating blueprints with the results.
 * This is done after blueprints are loaded since structure templates are not available at that point.
 */
public interface TemplateLoader {
    /**
     * Block coordinates must be smaller than 2^16 because x and z are packed into a 32-bit integer when loading blueprints.
     * No realistic blueprint should ever get even remotely close to this limit, but we are checking regardless.
     */
    int THEORETICAL_MAX_BLUEPRINT_SIZE = (1 << 16) - 1;

    FileToIdConverter ID_CONVERTER = new FileToIdConverter(StructureTemplateManager.STRUCTURE_RESOURCE_DIRECTORY_NAME, ".nbt");

    static void loadTemplateForBlueprint(ResourceManager resourceManager, Blueprint blueprint) {
        BlueprintConfiguration configuration = blueprint.configuration;
        Optional<StructureTemplate> template = loadTemplate(resourceManager, configuration.template);
        if (template.isEmpty()) {
            throw new DatapackLoadException("Could not find structure template: " + configuration.template);
        }
        Vec3i templateSize = template.get().getSize();
        if (templateSize.getX() > THEORETICAL_MAX_BLUEPRINT_SIZE || templateSize.getZ() > THEORETICAL_MAX_BLUEPRINT_SIZE) {
            throw new DatapackLoadException("Blueprint is too large! The maximum width/length is " + THEORETICAL_MAX_BLUEPRINT_SIZE);
        }
        blueprint.size = template.get().getSize();

        StructureTemplateAccessor accessor = (StructureTemplateAccessor) template.get();

        HashMap<ResourceLocation, ImmutableList.Builder<Anchor>> anchors = new HashMap<>();
        TemplateBlockColumn.Builder[] columns = new TemplateBlockColumn.Builder[templateSize.getX() * templateSize.getZ()];

        AssemblyTypeBuilder assemblyTypeBuilder = configuration.assemblyConfig.makeTypeBuilder();

        accessor.palettes().getFirst().blocks().forEach((info) -> loadBlock(configuration, info, block -> {
            int x = block.position().getX();
            int z = block.position().getZ();

            if (x < 0 || x >= templateSize.getX() || z < 0 || z >= templateSize.getZ()) {
                return;
            }

            int index = blueprint.columnIndex(x, z);
            TemplateBlockColumn.Builder builder = columns[index];
            if (builder == null) {
                builder = new TemplateBlockColumn.Builder(x, z);
                columns[index] =  builder;
            }

            builder.addBlock(block);
        }, (anchorType, anchor) -> {
            anchors.computeIfAbsent(anchorType, (k) -> ImmutableList.builder()).add(anchor);
            assemblyTypeBuilder.noteAnchor(anchorType, anchor);
        }));

        ImmutableMap.Builder<ResourceLocation, List<Anchor>> immutableAnchors = ImmutableMap.builder();
        anchors.forEach((type, builder) -> immutableAnchors.put(type, builder.build()));

        blueprint.blockColumns = Arrays.stream(columns).map(builder -> {
            if (builder == null) {
                return null;
            }
            return builder.build();
        }).toList();
        blueprint.anchors = immutableAnchors.build();
        blueprint.assemblyType = assemblyTypeBuilder.buildAssemblyType(blueprint);
    }

    private static Optional<StructureTemplate> loadTemplate(ResourceManager resourceManager, ResourceLocation key) {
        try {
            ResourceLocation path = ID_CONVERTER.idToFile(key);
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

    private static void loadBlock(BlueprintConfiguration configuration, StructureTemplate.StructureBlockInfo info, Consumer<TemplateBlock> blocks, BiConsumer<ResourceLocation, Anchor> anchors) {
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
            // Ignore jigsaw blocks that turn into structure void.
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
}
