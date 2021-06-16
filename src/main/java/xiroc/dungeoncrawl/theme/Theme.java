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

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.Blocks;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.jline.utils.InputStreamReader;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;
import xiroc.dungeoncrawl.util.IBlockStateProvider;
import xiroc.dungeoncrawl.util.IRandom;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

public class Theme {

    protected static Hashtable<String, IRandom<Theme>> BIOME_TO_THEME = new Hashtable<>();
    protected static Hashtable<String, IRandom<SecondaryTheme>> BIOME_TO_SECONDARY_THEME = new Hashtable<>();

    public static Hashtable<ResourceLocation, Theme> KEY_TO_THEME = new Hashtable<>();
    public static Hashtable<ResourceLocation, SecondaryTheme> KEY_TO_SECONDARY_THEME = new Hashtable<>();

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

    private static final String PRIMARY_THEME_MAPPINGS = "theming/mappings/primary";
    private static final String SECONDARY_THEME_MAPPINGS = "theming/mappings/secondary";

    private static ImmutableSet<ResourceLocation> THEME_KEYS, SECONDARY_THEME_KEYS;

    public static final Theme DEFAULT_THEME = new Theme(
            (pos, rotation) -> Blocks.STONE_BRICKS.getDefaultState(),
            (pos, rotation) -> Blocks.STONE_BRICKS.getDefaultState(),
            (pos, rotation) -> Blocks.COBBLESTONE.getDefaultState(),
            (pos, rotation) -> Blocks.GRAVEL.getDefaultState(),
            (pos, rotation) -> Blocks.STONE_BRICK_STAIRS.getDefaultState(),
            (pos, rotation) -> Blocks.COBBLESTONE_STAIRS.getDefaultState(),
            (pos, rotation) -> Blocks.COBBLESTONE.getDefaultState(),
            (pos, rotation) -> Blocks.COBBLESTONE_WALL.getDefaultState(),
            (pos, rotation) -> Blocks.COBBLESTONE_SLAB.getDefaultState(),
            (pos, rotation) -> Blocks.STONE_BRICK_SLAB.getDefaultState());

    public static final SecondaryTheme DEFAULT_SECONDARY_THEME = new SecondaryTheme(
            (pos, rotation) -> Blocks.OAK_LOG.getDefaultState(),
            (pos, rotation) -> Blocks.OAK_TRAPDOOR.getDefaultState(),
            (pos, rotation) -> Blocks.OAK_DOOR.getDefaultState(),
            (pos, rotation) -> Blocks.OAK_PLANKS.getDefaultState(),
            (pos, rotation) -> Blocks.OAK_STAIRS.getDefaultState(),
            (pos, rotation) -> Blocks.OAK_SLAB.getDefaultState(),
            (pos, rotation) -> Blocks.OAK_FENCE.getDefaultState(),
            (pos, rotation) -> Blocks.OAK_FENCE_GATE.getDefaultState(),
            (pos, rotation) -> Blocks.OAK_BUTTON.getDefaultState(),
            (pos, rotation) -> Blocks.OAK_PRESSURE_PLATE.getDefaultState());

    static {
        DEFAULT_THEME.key = DungeonCrawl.locate("builtin/default");
        DEFAULT_SECONDARY_THEME.key = DungeonCrawl.locate("builtin/default");
        KEY_TO_THEME.put(DEFAULT_THEME.key, DEFAULT_THEME);
        KEY_TO_SECONDARY_THEME.put(DEFAULT_SECONDARY_THEME.key, DEFAULT_SECONDARY_THEME);
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
        ID_TO_THEME.clear();
        ID_TO_SECONDARY_THEME.clear();
        KEY_TO_THEME.clear();
        KEY_TO_SECONDARY_THEME.clear();
        BIOME_TO_THEME.clear();
        BIOME_TO_SECONDARY_THEME.clear();

        JsonParser parser = new JsonParser();
        ImmutableSet.Builder<ResourceLocation> themeKeySetBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<ResourceLocation> secondaryThemeKeySetBuilder = new ImmutableSet.Builder<>();

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(DungeonCrawl.locate(SECONDARY_THEME_DIRECTORY).getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                ResourceLocation key = DungeonCrawl.key(resource, SECONDARY_THEME_DIRECTORY, ".json");
                JsonObject json = parser.parse(reader).getAsJsonObject();
                if (JSONUtils.areRequirementsMet(json)) {
                    SecondaryTheme theme = JsonThemeHandler.deserializeSubTheme(json, resource);
                    theme.key = key;
                    secondaryThemeKeySetBuilder.add(key);

                    KEY_TO_SECONDARY_THEME.put(key, theme);
                }
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
                JsonObject json = parser.parse(reader).getAsJsonObject();
                if (JSONUtils.areRequirementsMet(json)) {
                    Theme theme = JsonThemeHandler.deserializeTheme(json, resource);
                    theme.key = key;
                    themeKeySetBuilder.add(key);

                    KEY_TO_THEME.put(key, theme);
                }
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource.toString());
                e.printStackTrace();
            }
        }

        Hashtable<String, WeightedRandom.Builder<Theme>> themeMappingBuilders = new Hashtable<>();
        Hashtable<String, WeightedRandom.Builder<SecondaryTheme>> secondaryThemeMappingBuilders = new Hashtable<>();

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(PRIMARY_THEME_MAPPINGS, (s) -> s.endsWith(".json"))) {
            try {
                JsonThemeHandler.deserializeThemeMapping(
                        parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()))).getAsJsonObject(),
                        themeMappingBuilders, resource);
            } catch (IOException e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource);
                e.printStackTrace();
            }
        }

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(SECONDARY_THEME_MAPPINGS, (s) -> s.endsWith(".json"))) {
            try {
                JsonThemeHandler.deserializeSubThemeMapping(
                        parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()))).getAsJsonObject(),
                        secondaryThemeMappingBuilders, resource);
            } catch (IOException e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource);
                e.printStackTrace();
            }
        }

        themeMappingBuilders.forEach((biome, builder) -> BIOME_TO_THEME.put(biome, builder.build()));
        secondaryThemeMappingBuilders.forEach((biome, builder) -> BIOME_TO_SECONDARY_THEME.put(biome, builder.build()));

        THEME_KEYS = themeKeySetBuilder.build();
        SECONDARY_THEME_KEYS = secondaryThemeKeySetBuilder.build();
    }

    public static Theme getDefaultTheme() {
        return KEY_TO_THEME.getOrDefault(PRIMARY_THEME_DEFAULT, DEFAULT_THEME);
    }

    public static SecondaryTheme getDefaultSecondaryTheme() {
        return KEY_TO_SECONDARY_THEME.getOrDefault(SECONDARY_THEME_DEFAULT, DEFAULT_SECONDARY_THEME);
    }

    public static Theme randomTheme(String biome, Random rand) {
        return BIOME_TO_THEME.getOrDefault(biome, (r) -> KEY_TO_THEME.getOrDefault(PRIMARY_THEME_DEFAULT, DEFAULT_THEME)).roll(rand);
    }

    public static SecondaryTheme randomSecondaryTheme(String biome, Random rand) {
        return BIOME_TO_SECONDARY_THEME.getOrDefault(biome, (r) -> KEY_TO_SECONDARY_THEME.getOrDefault(SECONDARY_THEME_DEFAULT, DEFAULT_SECONDARY_THEME)).roll(rand);
    }

    public static Theme getTheme(ResourceLocation key) {
        return KEY_TO_THEME.getOrDefault(key, KEY_TO_THEME.getOrDefault(PRIMARY_THEME_DEFAULT, DEFAULT_THEME));
    }

    public static SecondaryTheme getSecondaryTheme(ResourceLocation key) {
        return KEY_TO_SECONDARY_THEME.getOrDefault(key, KEY_TO_SECONDARY_THEME.getOrDefault(SECONDARY_THEME_DEFAULT, DEFAULT_SECONDARY_THEME));
    }

    public static Theme getThemeByID(int theme) {
        return ID_TO_THEME.getOrDefault(theme, ID_TO_THEME.getOrDefault(0, DEFAULT_THEME));
    }

    public static SecondaryTheme getSubThemeByID(int id) {
        return ID_TO_SECONDARY_THEME.getOrDefault(id, ID_TO_SECONDARY_THEME.getOrDefault(0, DEFAULT_SECONDARY_THEME));
    }

    public static ImmutableSet<ResourceLocation> getThemeKeys() {
        return THEME_KEYS;
    }

    public static ImmutableSet<ResourceLocation> getSecondaryThemeKeys() {
        return SECONDARY_THEME_KEYS;
    }

}
