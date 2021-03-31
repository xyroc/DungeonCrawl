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
            (pos) -> Blocks.STONE_BRICKS.getDefaultState(),
            (pos) -> Blocks.STONE_BRICKS.getDefaultState(),
            (pos) -> Blocks.COBBLESTONE.getDefaultState(),
            (pos) -> Blocks.GRAVEL.getDefaultState(),
            (pos) -> Blocks.STONE_BRICK_STAIRS.getDefaultState(),
            (pos) -> Blocks.COBBLESTONE_STAIRS.getDefaultState(),
            (pos) -> Blocks.COBBLESTONE.getDefaultState(),
            (pos) -> Blocks.COBBLESTONE_WALL.getDefaultState(),
            (pos) -> Blocks.COBBLESTONE_SLAB.getDefaultState(),
            (pos) -> Blocks.STONE_BRICK_SLAB.getDefaultState());

    public static final SubTheme DEFAULT_SUB_THEME = new SubTheme(
            (pos) -> Blocks.OAK_LOG.getDefaultState(),
            (pos) -> Blocks.OAK_TRAPDOOR.getDefaultState(),
            (pos) -> Blocks.OAK_DOOR.getDefaultState(),
            (pos) -> Blocks.OAK_PLANKS.getDefaultState(),
            (pos) -> Blocks.OAK_STAIRS.getDefaultState(),
            (pos) -> Blocks.OAK_SLAB.getDefaultState(),
            (pos) -> Blocks.OAK_FENCE.getDefaultState(),
            (pos) -> Blocks.OAK_FENCE_GATE.getDefaultState(),
            (pos) -> Blocks.OAK_BUTTON.getDefaultState(),
            (pos) -> Blocks.OAK_PRESSURE_PLATE.getDefaultState());

    static {
        DEFAULT_THEME.key = "builtin:default";
        DEFAULT_SUB_THEME.key = "builtin:default";
        KEY_TO_THEME.put(DEFAULT_THEME.key, DEFAULT_THEME);
        KEY_TO_SUB_THEME.put(DEFAULT_SUB_THEME.key, DEFAULT_SUB_THEME);
    }

    public final IBlockStateProvider pillar, solid, generic, floor, solidStairs, stairs, material, wall, slab, solidSlab;

    public IRandom<SubTheme> subTheme;

    private String key;

    private IDungeonDecoration[] decorations;

    public Theme(IBlockStateProvider pillar,
                 IBlockStateProvider solid,
                 IBlockStateProvider generic,
                 IBlockStateProvider floor,
                 IBlockStateProvider solidStairs,
                 IBlockStateProvider stairs,
                 IBlockStateProvider material,
                 IBlockStateProvider wall,
                 IBlockStateProvider slab,
                 IBlockStateProvider solidSlab) {
        this.solid = solid;
        this.material = material;
        this.generic = generic;
        this.pillar = pillar;
        this.floor = floor;
        this.stairs = stairs;
        this.solidStairs = solidStairs;
        this.slab = slab;
        this.solidSlab = solidSlab;
        this.wall = wall;
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

    public IBlockStateProvider getPillar() {
        return pillar;
    }

    public IBlockStateProvider getSolid() {
        return solid;
    }

    public IBlockStateProvider getGeneric() {
        return generic;
    }

    public IBlockStateProvider getFloor() {
        return floor;
    }

    public IBlockStateProvider getSolidStairs() {
        return solidStairs;
    }

    public IBlockStateProvider getStairs() {
        return stairs;
    }

    public IBlockStateProvider getMaterial() {
        return material;
    }

    public IBlockStateProvider getWall() {
        return wall;
    }

    public IBlockStateProvider getSlab() {
        return slab;
    }

    public IBlockStateProvider getSolidSlab() {
        return solidSlab;
    }

    public static class SubTheme {

        public final IBlockStateProvider pillar, trapDoor, door, material, stairs, slab, fence, fenceGate, button, pressurePlate;

        private String key;

        public SubTheme(IBlockStateProvider pillar,
                        IBlockStateProvider trapDoor,
                        IBlockStateProvider door,
                        IBlockStateProvider material,
                        IBlockStateProvider stairs,
                        IBlockStateProvider slab,
                        IBlockStateProvider fence,
                        IBlockStateProvider fenceGate,
                        IBlockStateProvider button,
                        IBlockStateProvider pressurePlate) {
            this.pillar = pillar;
            this.trapDoor = trapDoor;
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

        public IBlockStateProvider getPillar() {
            return pillar;
        }

        public IBlockStateProvider getTrapDoor() {
            return trapDoor;
        }

        public IBlockStateProvider getDoor() {
            return door;
        }

        public IBlockStateProvider getMaterial() {
            return material;
        }

        public IBlockStateProvider getStairs() {
            return stairs;
        }

        public IBlockStateProvider getSlab() {
            return slab;
        }

        public IBlockStateProvider getFence() {
            return fence;
        }

        public IBlockStateProvider getFenceGate() {
            return fenceGate;
        }

        public IBlockStateProvider getButton() {
            return button;
        }

        public IBlockStateProvider getPressurePlate() {
            return pressurePlate;
        }

    }

    public static void loadJson(IResourceManager resourceManager) {
        JsonParser parser = new JsonParser();

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(DungeonCrawl.locate("theming/sub_themes").getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                String key = resource.getPath().substring(19, resource.getPath().length() - 5); // cut off "theming/sub_themes/" and the file ending from the path
                SubTheme theme = JsonThemeHandler.deserializeSubTheme(parser.parse(reader).getAsJsonObject(), resource);
                theme.key = key;
                KEY_TO_SUB_THEME.put(key, theme);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource.toString());
                e.printStackTrace();
            }
        }

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(DungeonCrawl.locate("theming/themes").getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                String key = resource.getPath().substring(15, resource.getPath().length() - 5); // cut off "theming/themes/" and the file ending from the path
                Theme theme = JsonThemeHandler.deserializeTheme(parser.parse(reader).getAsJsonObject(), resource);
                theme.key = key;
                KEY_TO_THEME.put(key, theme);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource.toString());
                e.printStackTrace();
            }
        }

        ResourceLocation themeMapping = DungeonCrawl.locate("theming/mappings/themes.json");
        try {
            JsonThemeHandler.deserializeThemeMapping(
                    parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(themeMapping).getInputStream()))).getAsJsonObject(),
                    themeMapping);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load {}", themeMapping.toString());
            e.printStackTrace();
        }

        ResourceLocation subThemeMapping = DungeonCrawl.locate("theming/mappings/sub_themes.json");
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
            Theme theme = KEY_TO_THEME.getOrDefault("vanilla/default", DEFAULT_THEME);
            return (random) -> theme;
        }).roll(rand);
    }

    public static SubTheme randomSubTheme(String biome, Random rand) {
        return BIOME_TO_SUB_THEME.computeIfAbsent(biome, (key) -> {
            SubTheme theme = KEY_TO_SUB_THEME.getOrDefault("vanilla/default", DEFAULT_SUB_THEME);
            return (random) -> theme;
        }).roll(rand);
    }

    public static Theme getTheme(String key) {
        return KEY_TO_THEME.getOrDefault(key, KEY_TO_THEME.getOrDefault("vanilla/default", DEFAULT_THEME));
    }

    public static SubTheme getSubTheme(String key) {
        return KEY_TO_SUB_THEME.getOrDefault(key, KEY_TO_SUB_THEME.getOrDefault("vanilla/default", DEFAULT_SUB_THEME));
    }

    public static Theme getThemeByID(int theme) {
        return ID_TO_THEME.getOrDefault(theme, ID_TO_THEME.getOrDefault(0, DEFAULT_THEME));
    }

    public static SubTheme getSubThemeByID(int id) {
        return ID_TO_SUB_THEME.getOrDefault(id, ID_TO_SUB_THEME.getOrDefault(0, DEFAULT_SUB_THEME));
    }

}
