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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.block.provider.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.block.provider.pattern.CheckerboardPattern;
import xiroc.dungeoncrawl.dungeon.block.provider.pattern.TerracottaPattern;
import xiroc.dungeoncrawl.dungeon.decoration.DungeonDecoration;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class JsonTheming {

    /**
     * Convenience method to deserialize a theme from a json object
     *
     * @param object the json object
     * @return the resulting theme
     */
    protected static Theme deserializeTheme(JsonObject object, ResourceLocation file) {
        JsonObject themeObject = object.get("theme").getAsJsonObject();
        Theme.Builder themeBuilder = Theme.builder();

        themeBuilder.solid(JsonTheming.deserialize(themeObject, "solid", file))
                .generic(JsonTheming.deserialize(themeObject, "generic", file))
                .pillar(JsonTheming.deserialize(themeObject, "pillar", file))
                .fencing(JsonTheming.deserialize(themeObject, "fencing", file))
                .floor(JsonTheming.deserialize(themeObject, "floor", file))
                .fluid(JsonTheming.deserialize(themeObject, "fluid", file))
                .material(JsonTheming.deserialize(themeObject, "material", file))
                .stairs(JsonTheming.deserialize(themeObject, "stairs", file))
                .solidStairs(JsonTheming.deserialize(themeObject, "solid_stairs", file))
                .slab(JsonTheming.deserialize(themeObject, "slab", file))
                .solidSlab(JsonTheming.deserialize(themeObject, "solid_slab", file))
                .wall(JsonTheming.deserialize(themeObject, "wall", file));

        if (object.has("decorations")) {
            object.getAsJsonArray("decorations").forEach((element) ->
                    themeBuilder.addDecoration(DungeonDecoration.fromJson(element.getAsJsonObject(), file)));
        }

        if (object.has("secondary_theme")) {
            WeightedRandom.Builder<SecondaryTheme> builder = new WeightedRandom.Builder<>();
            object.getAsJsonArray("secondary_theme").forEach((element) -> {
                JsonObject instance = element.getAsJsonObject();
                ResourceLocation key = new ResourceLocation(instance.get("key").getAsString());
                if (Theme.KEY_TO_SECONDARY_THEME.containsKey(key)) {
                    builder.add(Theme.KEY_TO_SECONDARY_THEME.get(key), JSONUtils.getWeight(instance));
                } else {
                    throw new DatapackLoadException("Unknown secondary theme key " + key + " in " + file.toString());
                }
            });
            themeBuilder.secondaryTheme(builder.build());
        }

        if (object.has("id")) {
            int id = object.get("id").getAsInt();
            themeBuilder.legacyId(id);
        }

        return themeBuilder.build();
    }

    /**
     * Convenience method to deserialize a secondary theme from a json object
     *
     * @param object the json object
     * @return the resulting sub-theme
     */
    protected static SecondaryTheme deserializeSecondaryTheme(JsonObject object, ResourceLocation file) {
        JsonObject themeObject = object.get("theme").getAsJsonObject();

        SecondaryTheme.Builder themeBuilder = SecondaryTheme.builder();

        themeBuilder.pillar(JsonTheming.deserialize(themeObject, "pillar", file))
                .trapdoor(JsonTheming.deserialize(themeObject, "trapdoor", file))
                .door(JsonTheming.deserialize(themeObject, "door", file))
                .material(JsonTheming.deserialize(themeObject, "material", file))
                .stairs(JsonTheming.deserialize(themeObject, "stairs", file))
                .slab(JsonTheming.deserialize(themeObject, "slab", file))
                .fence(JsonTheming.deserialize(themeObject, "fence", file))
                .fenceGate(JsonTheming.deserialize(themeObject, "fence_gate", file))
                .button(JsonTheming.deserialize(themeObject, "button", file))
                .pressurePlate(JsonTheming.deserialize(themeObject, "pressure_plate", file));

        if (object.has("id")) {
            themeBuilder.legacyId(object.get("id").getAsInt());
        }

        return themeBuilder.build();
    }

    /**
     * Convenience method to deserialize a theme mapping from the given json object.
     *
     * @param object the json object
     * @param file   the location of the theme file
     */
    protected static void deserializeThemeMapping(JsonObject object, Map<String, WeightedRandom.Builder<Theme>> themeMappingBuilders, WeightedRandom.Builder<Theme> defaultBuilder, ResourceLocation file) {
        if (JSONUtils.areRequirementsMet(object)) {
            object.getAsJsonObject("mapping").entrySet().forEach((entry) -> {
                ArrayList<Tuple<ResourceLocation, Integer>> entries = listThemes(entry);
                entries.forEach((tuple) -> {
                    if (!Theme.KEY_TO_THEME.containsKey(tuple.getA())) {
                        throw new DatapackLoadException("Cannot resolve theme key " + tuple.getA() + " in " + file.toString());
                    }
                    themeMappingBuilders.computeIfAbsent(entry.getKey(), (key) -> new WeightedRandom.Builder<>()).add(Theme.KEY_TO_THEME.get(tuple.getA()), tuple.getB());
                });
            });
            if (object.has("default")) {
                object.getAsJsonArray("default").forEach((element) -> {
                    JsonObject entry = element.getAsJsonObject();
                    ResourceLocation theme = new ResourceLocation(entry.get("key").getAsString());
                    if (!Theme.KEY_TO_THEME.containsKey(theme)) {
                        throw new DatapackLoadException("Cannot resolve theme key " + theme + " in the default case of " + file);
                    }
                    defaultBuilder.add(Theme.KEY_TO_THEME.get(theme), JSONUtils.getWeight(entry));
                });
            }
        }
    }

    /**
     * Convenience method to deserialize a secondary theme mapping from the given json object.
     *
     * @param object the json object
     * @param file   the location of the sub-theme file
     */
    protected static void deserializeSecondaryThemeMapping(JsonObject object, Map<String, WeightedRandom.Builder<SecondaryTheme>> secondaryThemeMappingBuilders, WeightedRandom.Builder<SecondaryTheme> defaultBuilder, ResourceLocation file) {
        if (JSONUtils.areRequirementsMet(object)) {
            object.getAsJsonObject("mapping").entrySet().forEach((entry) -> {
                ArrayList<Tuple<ResourceLocation, Integer>> entries = listThemes(entry);
                entries.forEach((tuple) -> {
                    if (!Theme.KEY_TO_SECONDARY_THEME.containsKey(tuple.getA())) {
                        throw new DatapackLoadException("Cannot resolve secondary theme key " + tuple.getA() + " in " + file.toString());
                    }
                    secondaryThemeMappingBuilders.computeIfAbsent(entry.getKey(), (key) -> new WeightedRandom.Builder<>()).add(Theme.KEY_TO_SECONDARY_THEME.get(tuple.getA()), tuple.getB());
                });
            });
            if (object.has("default")) {
                object.getAsJsonArray("default").forEach((element) -> {
                    JsonObject entry = element.getAsJsonObject();
                    ResourceLocation theme = new ResourceLocation(entry.get("key").getAsString());
                    if (!Theme.KEY_TO_SECONDARY_THEME.containsKey(theme)) {
                        throw new DatapackLoadException("Cannot resolve secondary theme key " + theme + " in the default case of " + file);
                    }
                    defaultBuilder.add(Theme.KEY_TO_SECONDARY_THEME.get(theme), JSONUtils.getWeight(entry));
                });
            }
        }
    }

    private static ArrayList<Tuple<ResourceLocation, Integer>> listThemes(Map.Entry<String, JsonElement> entry) {
        ArrayList<Tuple<ResourceLocation, Integer>> entries = new ArrayList<>();
        entry.getValue().getAsJsonArray().forEach((element) -> {
            JsonObject jsonObject = element.getAsJsonObject();
            entries.add(new Tuple<>(new ResourceLocation(jsonObject.get("key").getAsString()), JSONUtils.getWeight(jsonObject)));
        });
        return entries;
    }

    protected static void deserializeRandomThemeFile(JsonObject object, WeightedRandom.Builder<Theme> themes, WeightedRandom.Builder<SecondaryTheme> secondaryThemes, ResourceLocation file) {
        if (JSONUtils.areRequirementsMet(object)) {
            if (object.has("primary_themes")) {
                object.getAsJsonArray("primary_themes").forEach((element) -> {
                    JsonObject entry = element.getAsJsonObject();
                    ResourceLocation key = new ResourceLocation(entry.get("key").getAsString());
                    if (Theme.KEY_TO_THEME.containsKey(key)) {
                        themes.add(Theme.KEY_TO_THEME.get(key), JSONUtils.getWeight(entry));
                    } else {
                        throw new DatapackLoadException("Cannot resolve primary theme key " + key + " in " + file);
                    }
                });
            }

            if (object.has("secondary_themes")) {
                object.getAsJsonArray("secondary_themes").forEach((element) -> {
                    JsonObject entry = element.getAsJsonObject();
                    ResourceLocation key = new ResourceLocation(entry.get("key").getAsString());
                    if (Theme.KEY_TO_SECONDARY_THEME.containsKey(key)) {
                        secondaryThemes.add(Theme.KEY_TO_SECONDARY_THEME.get(key), JSONUtils.getWeight(entry));
                    } else {
                        throw new DatapackLoadException("Cannot resolve secondary theme key " + key + " in " + file);
                    }
                });
            }
        }
    }

    public static BlockStateProvider deserialize(JsonObject base, String name, ResourceLocation file) {
        if (!base.has(name)) {
            DungeonCrawl.LOGGER.warn("Missing BlockState Provider \"{}\" in {}", name, file.toString());
            return null;
        }
        JsonObject object = (JsonObject) base.get(name);
        if (object.has("type")) {
            String type = object.get("type").getAsString();
            if (type.equalsIgnoreCase("random_block")) {
                JsonArray blockObjects = object.get("blocks").getAsJsonArray();
                WeightedRandom.Builder<BlockState> builder = new WeightedRandom.Builder<>();

                for (JsonElement blockElement : blockObjects) {
                    JsonObject blockObject = (JsonObject) blockElement;
                    Block block = ForgeRegistries.BLOCKS
                            .getValue(new ResourceLocation(blockObject.get("block").getAsString()));
                    if (block != null) {
                        BlockState state = JSONUtils.deserializeBlockStateProperties(block, blockObject);
                        builder.add(state, JSONUtils.getWeight(blockObject));
                    } else {
                        DungeonCrawl.LOGGER.error("Unknown block: {}", blockObject.get("block").getAsString());
                    }
                }
                return new WeightedRandomBlock(builder.build());
            } else if (type.equalsIgnoreCase("block")) {
                Block block = ForgeRegistries.BLOCKS
                        .getValue(new ResourceLocation(object.get("block").getAsString()));
                if (block != null) {
                    BlockState state = JSONUtils.deserializeBlockStateProperties(block, object);
                    return new SingleBlock(state);
                } else {
                    DungeonCrawl.LOGGER.error("Unknown block: {}", object.get("block").getAsString());
                    return new SingleBlock(Blocks.CAVE_AIR.defaultBlockState());
                }
            } else if (type.equalsIgnoreCase("pattern")) {
                switch (object.get("pattern_type").getAsString().toLowerCase(Locale.ROOT)) {
                    case "checkerboard":
                        return new CheckerboardPattern(deserialize(object, "block_1", file), deserialize(object, "block_2", file));
                    case "terracotta":
                        return new TerracottaPattern(deserialize(object, "block", file));
                    default:
                        DungeonCrawl.LOGGER.error("Unknown block pattern type: " + object.get("pattern_type").getAsString());
                        return null;
                }
            } else {
                DungeonCrawl.LOGGER.error("Failed to load BlockState Provider {}: Unknown type {}.", object, type);
                return null;
            }
        } else {
            DungeonCrawl.LOGGER.error("Invalid BlockState Provider \"{}\": Type not specified.", name);
            return null;
        }
    }

}
