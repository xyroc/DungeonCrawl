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

package xiroc.dungeoncrawl.theme;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.block.pattern.CheckedPattern;
import xiroc.dungeoncrawl.dungeon.block.pattern.TerracottaPattern;
import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;
import xiroc.dungeoncrawl.util.IBlockStateProvider;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

public class JsonThemeHandler {

    private static final Logger LOGGER = LogManager.getLogger("DungeonCrawl/JsonThemeHandler");

    /**
     * Convenience method to deserialize a theme from a json object
     *
     * @param object the json object
     * @return the resulting theme
     */
    public static Theme deserializeTheme(JsonObject object, ResourceLocation file) {
        JsonObject themeObject = object.get("theme").getAsJsonObject();

        IBlockStateProvider solid = JsonThemeHandler.deserialize(themeObject, "solid", file);

        IBlockStateProvider generic = JsonThemeHandler.deserialize(themeObject, "generic", file);

        IBlockStateProvider pillar = JsonThemeHandler.deserialize(themeObject, "pillar", file);

        IBlockStateProvider floor = JsonThemeHandler.deserialize(themeObject, "floor", file);

        IBlockStateProvider stairs = JsonThemeHandler.deserialize(themeObject, "stairs", file);
        IBlockStateProvider solidStairs = JsonThemeHandler.deserialize(themeObject, "solid_stairs", file);

        IBlockStateProvider slab = JsonThemeHandler.deserialize(themeObject, "slab", file);
        IBlockStateProvider solidSlab = JsonThemeHandler.deserialize(themeObject, "solid_slab", file);

        IBlockStateProvider material = JsonThemeHandler.deserialize(themeObject, "material", file);
        IBlockStateProvider vanillaWall = JsonThemeHandler.deserialize(themeObject, "wall", file);

        Theme theme = new Theme(pillar, solid, generic, floor, solidStairs, stairs, material, vanillaWall, slab, solidSlab);

        if (object.has("decorations")) {
            JsonArray array = object.getAsJsonArray("decorations");
            IDungeonDecoration[] decorations = new IDungeonDecoration[array.size()];
            for (int i = 0; i < decorations.length; i++) {
                decorations[i] = IDungeonDecoration.fromJson(array.get(i).getAsJsonObject(), file);
            }
            theme.setDecorations(decorations);
        }

        if (object.has("sub_theme")) {
            WeightedRandom.Builder<SubTheme> builder = new WeightedRandom.Builder<>();
            object.getAsJsonArray("sub_theme").forEach((element) -> {
                JsonObject instance = element.getAsJsonObject();
                String key = instance.get("key").getAsString();
                if (Theme.KEY_TO_SUB_THEME.containsKey(key)) {
                    builder.add(Theme.KEY_TO_SUB_THEME.get(key), JSONUtils.getWeightOrDefault(instance));
                } else {
                    throw new NoSuchElementException("Unknown sub-theme key " + key + " in " + file.toString());
                }
            });
            theme.subTheme = builder.build();
        }

        if (object.has("id")) {
            Theme.ID_TO_THEME.put(object.get("id").getAsInt(), theme);
        }

        return theme;
    }

    /**
     * Convenience method to deserialize a sub-theme from a json object
     *
     * @param object the json object
     * @return the resulting sub-theme
     */
    public static SubTheme deserializeSubTheme(JsonObject object, ResourceLocation file) {

        JsonObject themeObject = object.get("theme").getAsJsonObject();

        IBlockStateProvider wallLog = JsonThemeHandler.deserialize(themeObject, "pillar", file);
        IBlockStateProvider trapDoor = JsonThemeHandler.deserialize(themeObject, "trapdoor", file);
        IBlockStateProvider door = JsonThemeHandler.deserialize(themeObject, "door", file);
        IBlockStateProvider material = JsonThemeHandler.deserialize(themeObject, "material", file);
        IBlockStateProvider stairs = JsonThemeHandler.deserialize(themeObject, "stairs", file);
        IBlockStateProvider slab = JsonThemeHandler.deserialize(themeObject, "slab", file);
        IBlockStateProvider fence = JsonThemeHandler.deserialize(themeObject, "fence", file);
        IBlockStateProvider fenceGate = JsonThemeHandler.deserialize(themeObject, "fence_gate", file);
        IBlockStateProvider button = JsonThemeHandler.deserialize(themeObject, "button", file);
        IBlockStateProvider pressurePlate = JsonThemeHandler.deserialize(themeObject, "pressure_plate", file);

        SubTheme subTheme = new SubTheme(wallLog, trapDoor, door, material, stairs, slab, fence, fenceGate, button, pressurePlate);

        if (object.has("id")) {
            Theme.ID_TO_SUB_THEME.put(object.get("id").getAsInt(), subTheme);
        }

        return subTheme;
    }

    /**
     * Convenience method to deserialize a theme mapping from the given json object.
     *
     * @param object the json object
     * @param file   the location of the theme file
     */
    public static void deserializeThemeMapping(JsonObject object, ResourceLocation file) {
        object.getAsJsonObject("mapping").entrySet().forEach((entry) -> {
            ArrayList<Tuple<String, Integer>> entries = checkAndListThemes(entry);

            WeightedRandom.Builder<Theme> builder = new WeightedRandom.Builder<>();
            entries.forEach((tuple) -> {
                if (!Theme.KEY_TO_THEME.containsKey(tuple.getA())) {
                    throw new RuntimeException("Reference to unknown theme " + tuple.getA() + " in " + file.toString());
                }
                builder.add(Theme.KEY_TO_THEME.get(tuple.getA()), tuple.getB());
            });
            Theme.BIOME_TO_THEME.put(entry.getKey(), builder.build());
        });
    }

    /**
     * Convenience method to deserialize a theme mapping from the given json object.
     *
     * @param object the json object
     * @param file   the location of the sub-theme file
     */

    public static void deserializeSubThemeMapping(JsonObject object, ResourceLocation file) {
        object.getAsJsonObject("mapping").entrySet().forEach((entry) -> {
            ArrayList<Tuple<String, Integer>> entries = checkAndListThemes(entry);

            WeightedRandom.Builder<SubTheme> builder = new WeightedRandom.Builder<>();
            entries.forEach((tuple) -> {
                if (!Theme.KEY_TO_SUB_THEME.containsKey(tuple.getA())) {
                    throw new RuntimeException("Reference to unknown sub-theme " + tuple.getA() + " in " + file.toString());
                }
                builder.add(Theme.KEY_TO_SUB_THEME.get(tuple.getA()), tuple.getB());
            });
            Theme.BIOME_TO_SUB_THEME.put(entry.getKey(), builder.build());
        });
    }

    private static ArrayList<Tuple<String, Integer>> checkAndListThemes(Map.Entry<String, JsonElement> entry) {
        if (!ForgeRegistries.BIOMES.containsKey(new ResourceLocation(entry.getKey()))) {
            DungeonCrawl.LOGGER.warn("The biome {} does not exist.", entry.getKey());
        }

        ArrayList<Tuple<String, Integer>> entries = new ArrayList<>();
        entry.getValue().getAsJsonArray().forEach((element) -> {
            JsonObject jsonObject = element.getAsJsonObject();
            entries.add(new Tuple<>(jsonObject.get("key").getAsString(), JSONUtils.getWeightOrDefault(jsonObject)));
        });
        return entries;
    }

    public static IBlockStateProvider deserialize(JsonObject base, String name, ResourceLocation file) {
        if (!base.has(name)) {
            LOGGER.warn("Missing BlockState Provider \"{}\" in {}", name, file.toString());
            return null;
        }
        JsonObject object = (JsonObject) base.get(name);
        if (object.has("type")) {
            String type = object.get("type").getAsString();
            if (type.equalsIgnoreCase("random_block")) {
                JsonArray blockObjects = object.get("blocks").getAsJsonArray();
                TupleIntBlock[] blocks = new TupleIntBlock[blockObjects.size()];

                int i = 0;
                for (JsonElement blockObject : blockObjects) {
                    JsonObject element = (JsonObject) blockObject;
                    Block block = ForgeRegistries.BLOCKS
                            .getValue(new ResourceLocation(element.get("block").getAsString()));
                    if (block != null) {
                        BlockState state = JSONUtils.getBlockState(block, element);
                        blocks[i++] = new TupleIntBlock(element.has("weight") ? element.get("weight").getAsInt() : 1, state);
                    } else {
                        LOGGER.error("Unknown block: {}", element.get("block").getAsString());
                    }
                }
                return new WeightedRandomBlock(blocks);
            } else if (type.equalsIgnoreCase("block")) {
                Block block = ForgeRegistries.BLOCKS
                        .getValue(new ResourceLocation(object.get("block").getAsString()));
                if (block != null) {
                    BlockState state = JSONUtils.getBlockState(block, object);
                    return (pos) -> state;
                } else {
                    LOGGER.error("Unknown block: {}", object.get("block").getAsString());
                    return (pos) -> Blocks.CAVE_AIR.getDefaultState();
                }
            } else if (type.equalsIgnoreCase("pattern")) {
                switch (object.get("pattern_type").getAsString().toLowerCase(Locale.ROOT)) {
                    case "checked":
                        return new CheckedPattern(deserialize(object, "block_1", file), deserialize(object, "block_2", file));
                    case "terracotta":
                        ResourceLocation block = new ResourceLocation(object.get("block").getAsString());
                        if (ForgeRegistries.BLOCKS.containsKey(block)) {
                            return new TerracottaPattern(ForgeRegistries.BLOCKS.getValue(block));
                        } else {
                            LOGGER.error("Unknown block: {}", object.get("block").getAsString());
                            return null;
                        }
                    default:
                        LOGGER.error("Unknown block pattern type: " + object.get("pattern_type").getAsString());
                        return null;
                }
            } else {
                LOGGER.error("Failed to load BlockState Provider {}: Unknown type {}.", object, type);
                return null;
            }
        } else {
            LOGGER.error("Invalid BlockState Provider \"{}\": Type not specified.", name);
            return null;
        }
    }

    private static final class TupleIntBlock extends Tuple<Integer, BlockState> {

        public TupleIntBlock(Integer aIn, BlockState bIn) {
            super(aIn, bIn);
        }

    }

}
