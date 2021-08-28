/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.dungeon.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

/**
 * Model block definitions allow customization of which blocks get
 * interpreted as which {@link DungeonModelBlockType} when a model is saved.
 */
public class ModelBlockDefinition {

    private static final String DIRECTORY = "block_definitions";

    public static final Hashtable<ResourceLocation, ModelBlockDefinition> DEFINITIONS = new Hashtable<>();

    private static final Hashtable<Block, DungeonModelBlockType> DEFAULT = new Hashtable<>();

    private static final ModelBlockDefinition DEFAULT_DEFINITION;

    private static final ResourceLocation DEFAULT_LOCATION = DungeonCrawl.locate("default");

    static {
        DEFAULT.put(Blocks.AIR, DungeonModelBlockType.AIR);
        DEFAULT.put(Blocks.CAVE_AIR, DungeonModelBlockType.AIR);
        DEFAULT.put(Blocks.CHEST, DungeonModelBlockType.CHEST);

        DEFAULT.put(Blocks.STONE_BRICK_STAIRS, DungeonModelBlockType.SOLID_STAIRS);
        DEFAULT.put(Blocks.COBBLESTONE, DungeonModelBlockType.GENERIC);
        DEFAULT.put(Blocks.STONE_BRICKS, DungeonModelBlockType.SOLID);
        DEFAULT.put(Blocks.STONE_BRICK_WALL, DungeonModelBlockType.WALL);
        DEFAULT.put(Blocks.PURPUR_PILLAR, DungeonModelBlockType.SOLID_PILLAR);
        DEFAULT.put(Blocks.IRON_BARS, DungeonModelBlockType.FENCING);
        DEFAULT.put(Blocks.WATER, DungeonModelBlockType.FLUID);

        DEFAULT.put(Blocks.OAK_LOG, DungeonModelBlockType.PILLAR);
        DEFAULT.put(Blocks.OAK_PLANKS, DungeonModelBlockType.MATERIAL);
        DEFAULT.put(Blocks.OAK_DOOR, DungeonModelBlockType.DOOR);
        DEFAULT.put(Blocks.OAK_STAIRS, DungeonModelBlockType.MATERIAL_STAIRS);
        DEFAULT.put(Blocks.OAK_SLAB, DungeonModelBlockType.MATERIAL_SLAB);
        DEFAULT.put(Blocks.OAK_BUTTON, DungeonModelBlockType.MATERIAL_BUTTON);
        DEFAULT.put(Blocks.OAK_FENCE, DungeonModelBlockType.FENCE);
        DEFAULT.put(Blocks.OAK_FENCE_GATE, DungeonModelBlockType.FENCE_GATE);
        DEFAULT.put(Blocks.OAK_PRESSURE_PLATE, DungeonModelBlockType.MATERIAL_PRESSURE_PLATE);
        DEFAULT.put(Blocks.OAK_TRAPDOOR, DungeonModelBlockType.TRAPDOOR);

        DEFAULT.put(Blocks.GRAVEL, DungeonModelBlockType.FLOOR);
        DEFAULT.put(Blocks.CRACKED_STONE_BRICKS, DungeonModelBlockType.SOLID_FLOOR);
        DEFAULT.put(Blocks.COBBLESTONE_STAIRS, DungeonModelBlockType.STAIRS);
        DEFAULT.put(Blocks.COBBLESTONE_SLAB, DungeonModelBlockType.SLAB);

        DEFAULT.put(Blocks.SKELETON_SKULL, DungeonModelBlockType.SKULL);

        DEFAULT_DEFINITION = new ModelBlockDefinition(DEFAULT);

        DEFINITIONS.put(DungeonCrawl.locate("builtin/default"), DEFAULT_DEFINITION);
    }

    public ModelBlockDefinition fallback;
    private final Hashtable<Block, DungeonModelBlockType> definition;
    private final Hashtable<DungeonModelBlockType, Block> invertedDefinition;

    private static ImmutableSet<ResourceLocation> KEYS;
    private static ImmutableSet.Builder<ResourceLocation> keySetBuilder;

    public ModelBlockDefinition(Hashtable<Block, DungeonModelBlockType> definition) {
        this.definition = definition;
        this.invertedDefinition = new Hashtable<>();
        this.definition.forEach((block, type) -> this.invertedDefinition.put(type, block));
    }

    public boolean containsBlock(Block block) {
        return definition.containsKey(block);
    }

    public DungeonModelBlockType getType(Block block) {
        return definition.getOrDefault(block, DungeonModelBlockType.OTHER);
    }

    public Block getBlock(DungeonModelBlock block) {
        if (block.type == DungeonModelBlockType.OTHER) {
            return ForgeRegistries.BLOCKS.getValue(block.blockName);
        } else if (block.type == DungeonModelBlockType.CARPET) {
            if (block.block != null) {
                return block.block;
            } else {
                return Blocks.WHITE_CARPET;
            }
        }
        if (invertedDefinition.containsKey(block.type)) {
            return invertedDefinition.get(block.type);
        }
        // No recursion here
        if (fallback != null && fallback.invertedDefinition.containsKey(block.type)) {
            return fallback.invertedDefinition.get(block.type);
        }
        return Blocks.AIR;
    }

    public static void loadJson(IResourceManager resourceManager) {
        keySetBuilder = new ImmutableSet.Builder<>();
        List<Tuple<ModelBlockDefinition, ResourceLocation>> referencesToUpdate = Lists.newArrayList();
        resourceManager.listResources(DIRECTORY, (s) -> s.endsWith(".json"))
                .forEach((resource) -> loadDefinition(resourceManager, resource, referencesToUpdate));

        for (Tuple<ModelBlockDefinition, ResourceLocation> reference : referencesToUpdate) {
            ResourceLocation key = reference.getB();
            if (DEFINITIONS.containsKey(key)) {
                reference.getA().fallback = DEFINITIONS.get(key);
            } else {
                DungeonCrawl.LOGGER.warn("Unknown fallback model block definition: {}", key);
            }
        }
        KEYS = keySetBuilder.build();
    }

    /**
     * Convenience method to load a single model block definition file.
     */
    private static void loadDefinition(IResourceManager resourceManager, ResourceLocation resourceLocation, List<Tuple<ModelBlockDefinition, ResourceLocation>> referencesToUpdate) {
        DungeonCrawl.LOGGER.debug("Loading {}", resourceLocation);
        JsonParser parser = new JsonParser();
        Hashtable<Block, DungeonModelBlockType> definition = new Hashtable<>();
        try {
            JsonObject object = parser.parse(new InputStreamReader(resourceManager.getResource(resourceLocation).getInputStream())).getAsJsonObject();
            object.getAsJsonObject("definition").entrySet().forEach((entry) -> {
                String key = entry.getKey();
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key));
                if (block != null) {
                    String value = entry.getValue().getAsString().toUpperCase();
                    if (DungeonModelBlockType.NAME_TO_TYPE.containsKey(value)) {
                        definition.put(block, DungeonModelBlockType.NAME_TO_TYPE.get(value));
                    } else {
                        DungeonCrawl.LOGGER.warn("Unknown model block type: {}", value);
                    }
                } else {
                    DungeonCrawl.LOGGER.warn("Unknown block: {}", key);
                }
            });

            ModelBlockDefinition blockDefinition = new ModelBlockDefinition(definition);

            if (object.has("fallback")) {
                referencesToUpdate.add(new Tuple<>(blockDefinition, new ResourceLocation(object.get("fallback").getAsString())));
            }

            ResourceLocation key = DungeonCrawl.key(resourceLocation, DIRECTORY, ".json");
            DEFINITIONS.put(key, blockDefinition);
            keySetBuilder.add(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ModelBlockDefinition getDefaultDefinition() {
        return DEFINITIONS.getOrDefault(DEFAULT_LOCATION, DEFAULT_DEFINITION);
    }

    public static ImmutableSet<ResourceLocation> getKeys() {
        return KEYS;
    }
}
