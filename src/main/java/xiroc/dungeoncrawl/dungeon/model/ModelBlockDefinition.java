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
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Model block definitions allow customization of which blocks get
 * interpreted as which {@link DungeonModelBlockType} when a model is saved.
 */
public class ModelBlockDefinition {

    private static final String PATH = "block_definitions";

    public static final Hashtable<String, ModelBlockDefinition> DEFINITIONS = new Hashtable<>();

    private static final HashMap<Block, DungeonModelBlockType> DEFAULT = new HashMap<>();

    public static final ModelBlockDefinition DEFAULT_DEFINITION;

    static {
        DEFAULT.put(Blocks.AIR, null);
        DEFAULT.put(Blocks.BARRIER, DungeonModelBlockType.NONE);

        DEFAULT.put(Blocks.STONE_BRICK_STAIRS, DungeonModelBlockType.SOLID_STAIRS);
        DEFAULT.put(Blocks.COBBLESTONE, DungeonModelBlockType.NORMAL);
        DEFAULT.put(Blocks.OBSIDIAN, DungeonModelBlockType.NORMAL_2);
        DEFAULT.put(Blocks.STONE_BRICKS, DungeonModelBlockType.SOLID);
        DEFAULT.put(Blocks.STONE_BRICK_WALL, DungeonModelBlockType.VANILLA_WALL);

        DEFAULT.put(Blocks.OAK_PLANKS, DungeonModelBlockType.MATERIAL);
        DEFAULT.put(Blocks.OAK_LOG, DungeonModelBlockType.PILLAR);
        DEFAULT.put(Blocks.OAK_DOOR, DungeonModelBlockType.DOOR);
        DEFAULT.put(Blocks.OAK_STAIRS, DungeonModelBlockType.MATERIAL_STAIRS);
        DEFAULT.put(Blocks.OAK_SLAB, DungeonModelBlockType.WOODEN_SLAB);
        DEFAULT.put(Blocks.OAK_BUTTON, DungeonModelBlockType.WOODEN_BUTTON);
        DEFAULT.put(Blocks.OAK_FENCE, DungeonModelBlockType.FENCE);
        DEFAULT.put(Blocks.OAK_FENCE_GATE, DungeonModelBlockType.FENCE_GATE);
        DEFAULT.put(Blocks.OAK_PRESSURE_PLATE, DungeonModelBlockType.WOODEN_PRESSURE_PLATE);
        DEFAULT.put(Blocks.OAK_TRAPDOOR, DungeonModelBlockType.TRAPDOOR);

        DEFAULT.put(Blocks.GRAVEL, DungeonModelBlockType.FLOOR);
        DEFAULT.put(Blocks.COBBLESTONE_STAIRS, DungeonModelBlockType.STAIRS);
        DEFAULT.put(Blocks.COBBLESTONE_SLAB, DungeonModelBlockType.SLAB);

        DEFAULT.put(Blocks.SPAWNER, DungeonModelBlockType.SPAWNER);
//        DEFAULT.put(Blocks.CHEST, DungeonModelBlockType.CHEST);
//        DEFAULT.put(Blocks.BARREL, DungeonModelBlockType.BARREL);
        DEFAULT.put(Blocks.SKELETON_SKULL, DungeonModelBlockType.SKULL);

        DEFAULT_DEFINITION = new ModelBlockDefinition(DEFAULT);

        DEFINITIONS.put("default", DEFAULT_DEFINITION);
    }

    public ModelBlockDefinition fallback;
    public HashMap<Block, DungeonModelBlockType> definition;
    public HashMap<DungeonModelBlockType, Block> invertedDefinition;

    public ModelBlockDefinition(HashMap<Block, DungeonModelBlockType> definition) {
        this.definition = definition;
        this.invertedDefinition = new HashMap<>();
        this.definition.forEach((block, type) -> this.invertedDefinition.put(type, block));
    }

    @Nullable
    public Block getBlock(DungeonModelBlock block) {
        if (block.type == DungeonModelBlockType.OTHER) {
            return ForgeRegistries.BLOCKS.getValue(block.resource);
        } else if (block.type == DungeonModelBlockType.CARPET) {
            return DungeonBlocks.CARPET[block.variation];
        }
        if (invertedDefinition.containsKey(block.type)) {
            return invertedDefinition.get(block.type);
        }
        // No recursion here
        if (fallback != null && fallback.invertedDefinition.containsKey(block.type)) {
            return fallback.invertedDefinition.get(block.type);
        }
        return null;
    }

    public static void loadJson(IResourceManager resourceManager) {
        List<Tuple<ModelBlockDefinition, String>> referencesToUpdate = Lists.newArrayList();
        resourceManager.getAllResourceLocations(PATH, (s) -> s.endsWith(".json"))
                .forEach((resource) -> loadDefinition(resourceManager, resource, referencesToUpdate));

        for (Tuple<ModelBlockDefinition, String> reference : referencesToUpdate) {
            String key = reference.getB();
            if (DEFINITIONS.containsKey(key)) {
                reference.getA().fallback = DEFINITIONS.get(key);
            } else {
                DungeonCrawl.LOGGER.warn("Unknown fallback model block definition: {}", key);
            }
        }
    }

    /**
     * Convenience method to load a single model block definition file.
     */
    private static void loadDefinition(IResourceManager resourceManager, ResourceLocation resourceLocation, List<Tuple<ModelBlockDefinition, String>> referencesToUpdate) {
        DungeonCrawl.LOGGER.debug("Loading {}", resourceLocation);
        JsonParser parser = new JsonParser();
        HashMap<Block, DungeonModelBlockType> definition = new HashMap<>();
        try {
            JsonObject object = parser.parse(new InputStreamReader(resourceManager.getResource(resourceLocation).getInputStream())).getAsJsonObject();
            object.getAsJsonObject("definition").entrySet().forEach((entry) -> {
                String key = entry.getKey();
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(key));
                if (block != null) {
                    String value = entry.getValue().getAsString().toUpperCase();
                    if (DungeonModelBlockType.NAME_TO_TYPE.containsKey(value)) {
                        definition.put(block, DungeonModelBlockType.valueOf(value));
                    } else {
                        DungeonCrawl.LOGGER.warn("Unknown model block type: {}", value);
                    }
                } else {
                    DungeonCrawl.LOGGER.warn("Unknown block: {}", key);
                }
            });

            ModelBlockDefinition blockDefinition = new ModelBlockDefinition(definition);

            if (object.has("fallback")) {
                referencesToUpdate.add(new Tuple<>(blockDefinition, object.get("fallback").getAsString()));
            }

            String key = resourceLocation.getPath().substring(PATH.length() + 1, resourceLocation.getPath().length() - ".json".length());
            DEFINITIONS.put(key, blockDefinition);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
