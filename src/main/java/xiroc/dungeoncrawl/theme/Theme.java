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

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.Blocks;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jline.utils.InputStreamReader;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;
import xiroc.dungeoncrawl.util.IBlockStateProvider;
import xiroc.dungeoncrawl.util.IRandom;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

public class Theme {

    public static Hashtable<String, IRandom<Theme>> BIOME_TO_THEME = new Hashtable<>();
    public static Hashtable<String, IRandom<SubTheme>> BIOME_TO_SUB_THEME = new Hashtable<>();

    public static Hashtable<String, Theme> KEY_TO_THEME = new Hashtable<>();
    public static Hashtable<String, SubTheme> KEY_TO_SUB_THEME = new Hashtable<>();

    // Legacy, kept  for backwards compatibility only.
    public static Hashtable<Integer, Theme> ID_TO_THEME = new Hashtable<>();
    public static Hashtable<Integer, SubTheme> ID_TO_SUB_THEME = new Hashtable<>();

    public static final Theme DEFAULT_THEME = new Theme(
            (pos) -> DungeonBlocks.STONE_BRICKS,
            DungeonBlocks.STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE,
            DungeonBlocks.STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE,
            DungeonBlocks.STONE_BRICK_FLOOR,
            DungeonBlocks.STAIRS_STONE_COBBLESTONE,
            DungeonBlocks.STAIRS_STONE_COBBLESTONE,
            DungeonBlocks.STONE_BRICKS_GRAVEL_COBBLESTONE,
            DungeonBlocks.STONE_WALL,
            DungeonBlocks.STONE_BRICK_FLOOR,
            (pos) -> Blocks.OBSIDIAN.getDefaultState(),
            (pos) -> Blocks.COBBLESTONE_SLAB.getDefaultState(),
            (pos) -> Blocks.STONE_BRICK_SLAB.getDefaultState());

    public static final SubTheme DEFAULT_SUB_THEME = new SubTheme(
            (pos) -> Blocks.OAK_LOG.getDefaultState(),
            (pos) -> Blocks.OAK_TRAPDOOR.getDefaultState(),
            (pos) -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(),
            (pos) -> Blocks.OAK_DOOR.getDefaultState(),
            (pos) -> Blocks.OAK_PLANKS.getDefaultState(),
            (pos) -> Blocks.OAK_STAIRS.getDefaultState(),
            (pos) -> Blocks.OAK_SLAB.getDefaultState(),
            (pos) -> Blocks.OAK_FENCE.getDefaultState(),
            (pos) -> Blocks.OAK_FENCE_GATE.getDefaultState(),
            (pos) -> Blocks.OAK_BUTTON.getDefaultState(),
            (pos) -> Blocks.OAK_PRESSURE_PLATE.getDefaultState());

    public final IBlockStateProvider ceiling, solid, generic, generic2, floor, solidStairs, stairs, material, vanillaWall, column, slab, solidSlab;
    public IRandom<SubTheme> subTheme;

    private String key;

    private IDungeonDecoration[] decorations;

    public Theme(IBlockStateProvider ceiling, IBlockStateProvider solid, IBlockStateProvider generic,
                 IBlockStateProvider floor, IBlockStateProvider solidStairs, IBlockStateProvider stairs,
                 IBlockStateProvider material, IBlockStateProvider vanillaWall, IBlockStateProvider column,
                 IBlockStateProvider generic2, IBlockStateProvider slab, IBlockStateProvider solidSlab) {
        this.ceiling = ceiling;
        this.solid = solid;
        this.generic = generic;
        this.floor = floor;
        this.solidStairs = solidStairs;
        this.stairs = stairs;
        this.material = material;
        this.vanillaWall = vanillaWall;
        this.column = column;
        this.generic2 = generic2;
        this.slab = slab;
        this.solidSlab = solidSlab;
    }

    public void setDecorations(IDungeonDecoration[] decorations) {
        this.decorations = decorations;
    }

    public boolean hasDecorations() {
        return decorations != null;
    }

    public String getKey() {
        return key;
    }

    public IDungeonDecoration[] getDecorations() {
        return decorations;
    }

    public static class SubTheme {

        public final IBlockStateProvider wallLog, trapDoor, torchDark, door, material, stairs, slab, fence, fenceGate, button, pressurePlate;

        private String key;

        public SubTheme(IBlockStateProvider wallLog, IBlockStateProvider trapDoor, IBlockStateProvider torchDark,
                        IBlockStateProvider door, IBlockStateProvider material, IBlockStateProvider stairs, IBlockStateProvider slab,
                        IBlockStateProvider fence, IBlockStateProvider fenceGate, IBlockStateProvider button, IBlockStateProvider pressurePlate) {
            this.wallLog = wallLog;
            this.trapDoor = trapDoor;
            this.torchDark = torchDark;
            this.door = door;
            this.material = material;
            this.stairs = stairs;
            this.slab = slab;
            this.fence = fence;
            this.fenceGate = fenceGate;
            this.button = button;
            this.pressurePlate = pressurePlate;
        }

        public String getKey() {
            return key;
        }

    }

    public static void loadJson(IResourceManager resourceManager) {
        JsonParser parser = new JsonParser();

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(DungeonCrawl.locate("theming/themes").getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                String key = resource.getPath().substring(15, resource.getPath().length() - 5); // cut off "theming/themes/" and the file ending from the path
                Theme theme = JsonThemeHandler.deserializeTheme(parser.parse(reader).getAsJsonObject());
                theme.key = key;
                KEY_TO_THEME.put(key, theme);
                DungeonCrawl.LOGGER.info("Theme {} has key {}", resource.toString(), key);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource.toString());
                e.printStackTrace();
            }
        }

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(DungeonCrawl.locate("theming/sub_themes").getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                String key = resource.getPath().substring(19, resource.getPath().length() - 5); // cut off "theming/sub_themes/" and the file ending from the path
                SubTheme theme = JsonThemeHandler.deserializeSubTheme(parser.parse(reader).getAsJsonObject());
                theme.key = key;
                KEY_TO_SUB_THEME.put(key, theme);
                DungeonCrawl.LOGGER.info("Sub Theme {} has key {}", resource.toString(), key);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource.toString());
                e.printStackTrace();
            }
        }

        ResourceLocation themeMapping = DungeonCrawl.locate("theming/theme_mapping.json");
        try {
            JsonThemeHandler.deserializeThemeMapping(
                    parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(themeMapping).getInputStream()))).getAsJsonObject(),
                    themeMapping);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load {}", themeMapping.toString());
            e.printStackTrace();
        }

        ResourceLocation subThemeMapping = DungeonCrawl.locate("theming/sub_theme_mapping.json");
        try {
            JsonThemeHandler.deserializeSubThemeMapping(
                    parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(subThemeMapping).getInputStream()))).getAsJsonObject(),
                    subThemeMapping);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load {}", subThemeMapping.toString());
            e.printStackTrace();
        }

    }

    public static Theme getDefaultTheme() {
        return KEY_TO_THEME.getOrDefault("default", DEFAULT_THEME);
    }

    public static SubTheme getDefaultSubTheme() {
        return KEY_TO_SUB_THEME.getOrDefault("default", DEFAULT_SUB_THEME);
    }

    public static Theme randomTheme(String biome, Random rand) {
        return BIOME_TO_THEME.computeIfAbsent(biome, (key) -> {
            Theme theme = KEY_TO_THEME.getOrDefault("default", DEFAULT_THEME);
            return (random) -> theme;
        }).roll(rand);
    }

    public static SubTheme randomSubTheme(String biome, Random rand) {
        return BIOME_TO_SUB_THEME.computeIfAbsent(biome, (key) -> {
            SubTheme theme = KEY_TO_SUB_THEME.getOrDefault("default", DEFAULT_SUB_THEME);
            return (random) -> theme;
        }).roll(rand);
    }

    public static Theme getTheme(String key) {
        return KEY_TO_THEME.getOrDefault(key, KEY_TO_THEME.getOrDefault("default", DEFAULT_THEME));
    }

    public static SubTheme getSubTheme(String key) {
        return KEY_TO_SUB_THEME.getOrDefault(key, KEY_TO_SUB_THEME.getOrDefault("default", DEFAULT_SUB_THEME));
    }

    public static Theme getThemeByID(int theme) {
        return ID_TO_THEME.getOrDefault(theme, ID_TO_THEME.getOrDefault(0, DEFAULT_THEME));
    }

    public static SubTheme getSubThemeByID(int id) {
        return ID_TO_SUB_THEME.getOrDefault(id, ID_TO_SUB_THEME.getOrDefault(0, DEFAULT_SUB_THEME));
    }

}
