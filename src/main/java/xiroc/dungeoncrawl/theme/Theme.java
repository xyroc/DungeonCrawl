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

    protected static Hashtable<String, IRandom<Theme>> BIOME_TO_THEME = new Hashtable<>();
    protected static Hashtable<String, IRandom<SecondaryTheme>> BIOME_TO_SECONDARY_THEME = new Hashtable<>();

    protected static Hashtable<ResourceLocation, Theme> KEY_TO_THEME = new Hashtable<>();
    protected static Hashtable<ResourceLocation, SecondaryTheme> KEY_TO_SECONDARY_THEME = new Hashtable<>();

    // Legacy, kept for backwards compatibility only.
    public static Hashtable<Integer, Theme> ID_TO_THEME = new Hashtable<>();
    public static Hashtable<Integer, SecondaryTheme> ID_TO_SECONDARY_THEME = new Hashtable<>();

    private static final ResourceLocation PRIMARY_THEME_DEFAULT = DungeonCrawl.locate("vanilla/default");
    private static final ResourceLocation SECONDARY_THEME_DEFAULT = DungeonCrawl.locate("vanilla/default");

    public static final ResourceLocation PRIMARY_CATACOMBS_DEFAULT = DungeonCrawl.locate("vanilla/catacombs/default");
    public static final ResourceLocation PRIMARY_HELL_DEFAULT = DungeonCrawl.locate("vanilla/hell/default");
    public static final ResourceLocation PRIMARY_HELL_MOSSY = DungeonCrawl.locate("vanilla/hell/mossy");

    private static final String PRIMARY_THEME_DIRECTORY = "theming/primary_themes";
    private static final String SECONDARY_THEME_DIRECTORY = "theming/secondary_themes";

    private static final ResourceLocation PRIMARY_THEME_MAPPINGS = DungeonCrawl.locate("theming/mappings/primary_themes.json");
    private static final ResourceLocation SECONDARY_THEME_MAPPINGS = DungeonCrawl.locate("theming/mappings/secondary_themes.json");

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

    public static final SecondaryTheme DEFAULT_SUB_THEME = new SecondaryTheme(
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
        DEFAULT_THEME.key = DungeonCrawl.locate("builtin/default");
        DEFAULT_SUB_THEME.key = DungeonCrawl.locate("builtin/default");
        KEY_TO_THEME.put(DEFAULT_THEME.key, DEFAULT_THEME);
        KEY_TO_SECONDARY_THEME.put(DEFAULT_SUB_THEME.key, DEFAULT_SUB_THEME);
    }

    public final IBlockStateProvider pillar, solid, generic, floor, solidStairs, stairs, material, wall, slab, solidSlab;

    public IRandom<SecondaryTheme> subTheme;

    private ResourceLocation key;

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

    public ResourceLocation getKey() {
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

    public static class SecondaryTheme {

        public final IBlockStateProvider pillar, trapDoor, door, material, stairs, slab, fence, fenceGate, button, pressurePlate;

        private ResourceLocation key;

        public SecondaryTheme(IBlockStateProvider pillar,
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

        public ResourceLocation getKey() {
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

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(DungeonCrawl.locate(SECONDARY_THEME_DIRECTORY).getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                ResourceLocation key = DungeonCrawl.key(resource, SECONDARY_THEME_DIRECTORY, ".json");
                SecondaryTheme theme = JsonThemeHandler.deserializeSubTheme(parser.parse(reader).getAsJsonObject(), resource);
                theme.key = key;
                DungeonCrawl.LOGGER.info("THEME: {} -> {}", resource, key);

                KEY_TO_SECONDARY_THEME.put(key, theme);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource.toString());
                e.printStackTrace();
            }
        }

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(DungeonCrawl.locate(PRIMARY_THEME_DIRECTORY).getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                ResourceLocation key = DungeonCrawl.key(resource, PRIMARY_THEME_DIRECTORY, ".json");
                Theme theme = JsonThemeHandler.deserializeTheme(parser.parse(reader).getAsJsonObject(), resource);
                theme.key = key;
                DungeonCrawl.LOGGER.info("SECONDARY THEME: {} -> {}", resource, key);

                KEY_TO_THEME.put(key, theme);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource.toString());
                e.printStackTrace();
            }
        }

        try {
            JsonThemeHandler.deserializeThemeMapping(
                    parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(PRIMARY_THEME_MAPPINGS).getInputStream()))).getAsJsonObject(),
                    PRIMARY_THEME_MAPPINGS);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load {}", PRIMARY_THEME_MAPPINGS);
            e.printStackTrace();
        }

        try {
            JsonThemeHandler.deserializeSubThemeMapping(
                    parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(SECONDARY_THEME_MAPPINGS).getInputStream()))).getAsJsonObject(),
                    SECONDARY_THEME_MAPPINGS);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load {}", SECONDARY_THEME_MAPPINGS);
            e.printStackTrace();
        }

    }

    public static Theme getDefaultTheme() {
        return KEY_TO_THEME.getOrDefault(PRIMARY_THEME_DEFAULT, DEFAULT_THEME);
    }

    public static SecondaryTheme getDefaultSubTheme() {
        return KEY_TO_SECONDARY_THEME.getOrDefault(SECONDARY_THEME_DEFAULT, DEFAULT_SUB_THEME);
    }

    public static Theme randomTheme(String biome, Random rand) {
        return BIOME_TO_THEME.computeIfAbsent(biome, (key) -> {
            Theme theme = KEY_TO_THEME.getOrDefault(PRIMARY_THEME_DEFAULT, DEFAULT_THEME);
            return (random) -> theme;
        }).roll(rand);
    }

    public static SecondaryTheme randomSecondaryTheme(String biome, Random rand) {
        return BIOME_TO_SECONDARY_THEME.computeIfAbsent(biome, (key) -> {
            SecondaryTheme theme = KEY_TO_SECONDARY_THEME.getOrDefault(SECONDARY_THEME_DEFAULT, DEFAULT_SUB_THEME);
            return (random) -> theme;
        }).roll(rand);
    }

    public static Theme getTheme(ResourceLocation key) {
        return KEY_TO_THEME.getOrDefault(key, KEY_TO_THEME.getOrDefault(PRIMARY_THEME_DEFAULT, DEFAULT_THEME));
    }

    public static SecondaryTheme getSecondaryTheme(ResourceLocation key) {
        return KEY_TO_SECONDARY_THEME.getOrDefault(key, KEY_TO_SECONDARY_THEME.getOrDefault(SECONDARY_THEME_DEFAULT, DEFAULT_SUB_THEME));
    }

    public static Theme getThemeByID(int theme) {
        return ID_TO_THEME.getOrDefault(theme, ID_TO_THEME.getOrDefault(0, DEFAULT_THEME));
    }

    public static SecondaryTheme getSubThemeByID(int id) {
        return ID_TO_SECONDARY_THEME.getOrDefault(id, ID_TO_SECONDARY_THEME.getOrDefault(0, DEFAULT_SUB_THEME));
    }

}
