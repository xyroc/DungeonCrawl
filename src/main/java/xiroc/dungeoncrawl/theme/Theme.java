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
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.Blocks;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import org.jline.utils.InputStreamReader;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.IBlockStateProvider;
import xiroc.dungeoncrawl.util.IRandom;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

public class Theme {

    /**
     * These builtin themes will only be used as a last resort,
     * in case a theme can't be found and the default theme can't be found either.
     */
    public static final Theme BUILTIN_DEFAULT_THEME = new Theme(
            (world, pos, rotation) -> Blocks.STONE_BRICKS.getDefaultState(),
            (world, pos, rotation) -> Blocks.STONE_BRICKS.getDefaultState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE.getDefaultState(),
            (world, pos, rotation) -> Blocks.GRAVEL.getDefaultState(),
            (world, pos, rotation) -> Blocks.STONE_BRICK_STAIRS.getDefaultState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE_STAIRS.getDefaultState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE.getDefaultState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE_WALL.getDefaultState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE_SLAB.getDefaultState(),
            (world, pos, rotation) -> Blocks.STONE_BRICK_SLAB.getDefaultState());

    public static final SecondaryTheme BUILTIN_DEFAULT_SECONDARY_THEME = new SecondaryTheme(
            (world, pos, rotation) -> Blocks.OAK_LOG.getDefaultState(),
            (world, pos, rotation) -> Blocks.OAK_TRAPDOOR.getDefaultState(),
            (world, pos, rotation) -> Blocks.OAK_DOOR.getDefaultState(),
            (world, pos, rotation) -> Blocks.OAK_PLANKS.getDefaultState(),
            (world, pos, rotation) -> Blocks.OAK_STAIRS.getDefaultState(),
            (world, pos, rotation) -> Blocks.OAK_SLAB.getDefaultState(),
            (world, pos, rotation) -> Blocks.OAK_FENCE.getDefaultState(),
            (world, pos, rotation) -> Blocks.OAK_FENCE_GATE.getDefaultState(),
            (world, pos, rotation) -> Blocks.OAK_BUTTON.getDefaultState(),
            (world, pos, rotation) -> Blocks.OAK_PRESSURE_PLATE.getDefaultState());

    protected static final Hashtable<String, IRandom<Theme>> BIOME_TO_THEME = new Hashtable<>();
    protected static final Hashtable<String, IRandom<SecondaryTheme>> BIOME_TO_SECONDARY_THEME = new Hashtable<>();

    public static final Hashtable<ResourceLocation, Theme> KEY_TO_THEME = new Hashtable<>();
    public static final Hashtable<ResourceLocation, SecondaryTheme> KEY_TO_SECONDARY_THEME = new Hashtable<>();

    private static WeightedRandom<Theme> DEFAULT_TOP_THEME = null;
    private static WeightedRandom<SecondaryTheme> DEFAULT_SECONDARY_TOP_THEME = null;

    private static IRandom<Theme> CATACOMBS_THEME = (rand) -> {
        throw new IllegalStateException();
    };
    private static IRandom<Theme> LOWER_CATACOMBS_THEME = (rand) -> {
        throw new IllegalStateException();
    };
    private static IRandom<Theme> HELL_THEME = (rand) -> {
        throw new IllegalStateException();
    };

    private static IRandom<SecondaryTheme> CATACOMBS_SECONDARY_THEME = (rand) -> {
        throw new IllegalStateException();
    };
    private static IRandom<SecondaryTheme> LOWER_CATACOMBS_SECONDARY_THEME = (rand) -> {
        throw new IllegalStateException();
    };
    private static IRandom<SecondaryTheme> HELL_SECONDARY_THEME = (rand) -> {
        throw new IllegalStateException();
    };

    // Legacy, kept for backwards compatibility only.
    public static final Hashtable<Integer, Theme> ID_TO_THEME = new Hashtable<>();
    public static final Hashtable<Integer, SecondaryTheme> ID_TO_SECONDARY_THEME = new Hashtable<>();

    // Fallback themes, used if there is no default case in the theme mappings.
    private static final ResourceLocation PRIMARY_THEME_FALLBACK = DungeonCrawl.locate("vanilla/default");
    private static final ResourceLocation SECONDARY_THEME_FALLBACK = DungeonCrawl.locate("vanilla/oak");

    public static final ResourceLocation PRIMARY_HELL_MOSSY = DungeonCrawl.locate("vanilla/hell/mossy");

    private static final String PRIMARY_THEME_DIRECTORY = "theming/primary_themes";
    private static final String SECONDARY_THEME_DIRECTORY = "theming/secondary_themes";

    private static final String PRIMARY_THEME_MAPPINGS_DIRECTORY = "theming/mappings/primary";
    private static final String SECONDARY_THEME_MAPPINGS_DIRECTORY = "theming/mappings/secondary";

    private static final String UPPER_CATACOMBS_THEMES_DIRECTORY = "theming/lower_layers/upper_catacombs";
    private static final String CATACOMBS_THEMES_DIRECTORY = "theming/lower_layers/catacombs";
    private static final String HELL_THEMES_DIRECTORY = "theming/lower_layers/hell";

    private static ImmutableSet<ResourceLocation> THEME_KEYS, SECONDARY_THEME_KEYS;

    static {
        BUILTIN_DEFAULT_THEME.key = new ResourceLocation("builtin:default");
        BUILTIN_DEFAULT_SECONDARY_THEME.key = new ResourceLocation("builtin:default");
        KEY_TO_THEME.put(BUILTIN_DEFAULT_THEME.key, BUILTIN_DEFAULT_THEME);
        KEY_TO_SECONDARY_THEME.put(BUILTIN_DEFAULT_SECONDARY_THEME.key, BUILTIN_DEFAULT_SECONDARY_THEME);
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

        ImmutableSet.Builder<ResourceLocation> themeKeySetBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<ResourceLocation> secondaryThemeKeySetBuilder = new ImmutableSet.Builder<>();

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(DungeonCrawl.locate(SECONDARY_THEME_DIRECTORY).getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                ResourceLocation key = DungeonCrawl.key(resource, SECONDARY_THEME_DIRECTORY, ".json");
                JsonObject json = DungeonCrawl.JSON_PARSER.parse(reader).getAsJsonObject();
                if (JSONUtils.areRequirementsMet(json)) {
                    SecondaryTheme theme = JsonTheming.deserializeSecondaryTheme(json, resource);
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
                JsonObject json = DungeonCrawl.JSON_PARSER.parse(reader).getAsJsonObject();
                if (JSONUtils.areRequirementsMet(json)) {
                    Theme theme = JsonTheming.deserializeTheme(json, resource);
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

        WeightedRandom.Builder<Theme> primaryDefaultBuilder = new WeightedRandom.Builder<>();
        WeightedRandom.Builder<SecondaryTheme> secondaryDefaultBuilder = new WeightedRandom.Builder<>();

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(PRIMARY_THEME_MAPPINGS_DIRECTORY, (s) -> s.endsWith(".json"))) {
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                JsonTheming.deserializeThemeMapping(DungeonCrawl.JSON_PARSER.parse(reader).getAsJsonObject(), themeMappingBuilders, primaryDefaultBuilder, resource);
            } catch (IOException e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource);
                e.printStackTrace();
            }
        }

        for (ResourceLocation resource : resourceManager.getAllResourceLocations(SECONDARY_THEME_MAPPINGS_DIRECTORY, (s) -> s.endsWith(".json"))) {
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                JsonTheming.deserializeSecondaryThemeMapping(DungeonCrawl.JSON_PARSER.parse(reader).getAsJsonObject(), secondaryThemeMappingBuilders, secondaryDefaultBuilder, resource);
            } catch (IOException e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource);
                e.printStackTrace();
            }
        }

        themeMappingBuilders.forEach((biome, builder) -> BIOME_TO_THEME.put(biome, builder.build()));
        secondaryThemeMappingBuilders.forEach((biome, builder) -> BIOME_TO_SECONDARY_THEME.put(biome, builder.build()));

        DEFAULT_TOP_THEME = primaryDefaultBuilder.build();
        DEFAULT_SECONDARY_TOP_THEME = secondaryDefaultBuilder.build();

        if (DEFAULT_TOP_THEME.isEmpty()) {
            throw new DatapackLoadException("No default primary themes are specified in the mappings.");
        }

        if (DEFAULT_SECONDARY_TOP_THEME.isEmpty()) {
            throw new DatapackLoadException("No default secondary themes are specified in the mappings.");
        }

        THEME_KEYS = themeKeySetBuilder.build();
        SECONDARY_THEME_KEYS = secondaryThemeKeySetBuilder.build();

        Tuple<WeightedRandom<Theme>, WeightedRandom<SecondaryTheme>> catacombs = loadRandomThemeFiles(UPPER_CATACOMBS_THEMES_DIRECTORY, resourceManager);
        Tuple<WeightedRandom<Theme>, WeightedRandom<SecondaryTheme>> lowerCatacombs = loadRandomThemeFiles(CATACOMBS_THEMES_DIRECTORY, resourceManager);
        Tuple<WeightedRandom<Theme>, WeightedRandom<SecondaryTheme>> hell = loadRandomThemeFiles(HELL_THEMES_DIRECTORY, resourceManager);

        CATACOMBS_THEME = catacombs.getA();
        CATACOMBS_SECONDARY_THEME = catacombs.getB();

        LOWER_CATACOMBS_THEME = lowerCatacombs.getA();
        LOWER_CATACOMBS_SECONDARY_THEME = lowerCatacombs.getB();

        HELL_THEME = hell.getA();
        HELL_SECONDARY_THEME = hell.getB();
    }

    private static Tuple<WeightedRandom<Theme>, WeightedRandom<SecondaryTheme>> loadRandomThemeFiles(String directory, IResourceManager resourceManager) {
        WeightedRandom.Builder<Theme> primary = new WeightedRandom.Builder<>();
        WeightedRandom.Builder<SecondaryTheme> secondary = new WeightedRandom.Builder<>();
        for (ResourceLocation resource : resourceManager.getAllResourceLocations(directory, (s) -> s.endsWith(".json"))) {
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                JsonTheming.deserializeRandomThemeFile(DungeonCrawl.JSON_PARSER.parse(reader).getAsJsonObject(),
                        primary, secondary, resource);
            } catch (IOException e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource);
                e.printStackTrace();
            }
        }
        if (primary.entries.isEmpty()) {
            throw new DatapackLoadException("No primary themes were present after loading " + directory);
        }
        if (secondary.entries.isEmpty()) {
            throw new DatapackLoadException("No secondary themes were present after loading " + directory);
        }
        return new Tuple<>(primary.build(), secondary.build());
    }

    public static Theme getBuiltinDefaultTheme() {
        return KEY_TO_THEME.getOrDefault(PRIMARY_THEME_FALLBACK, BUILTIN_DEFAULT_THEME);
    }

    public static SecondaryTheme getBuiltinDefaultSecondaryTheme() {
        return KEY_TO_SECONDARY_THEME.getOrDefault(SECONDARY_THEME_FALLBACK, BUILTIN_DEFAULT_SECONDARY_THEME);
    }

    public static Theme randomTheme(String biome, Random rand) {
        return BIOME_TO_THEME.getOrDefault(biome, DEFAULT_TOP_THEME).roll(rand);
    }

    public static SecondaryTheme randomSecondaryTheme(String biome, Random rand) {
        return BIOME_TO_SECONDARY_THEME.getOrDefault(biome, DEFAULT_SECONDARY_TOP_THEME).roll(rand);
    }

    public static Theme randomCatacombsTheme(Random rand) {
        return CATACOMBS_THEME.roll(rand);
    }

    public static SecondaryTheme randomCatacombsSecondaryTheme(Random rand) {
        return CATACOMBS_SECONDARY_THEME.roll(rand);
    }

    public static Theme randomLowerCatacombsTheme(Random rand) {
        return LOWER_CATACOMBS_THEME.roll(rand);
    }

    public static SecondaryTheme randomLowerCatacombsSecondaryTheme(Random rand) {
        return LOWER_CATACOMBS_SECONDARY_THEME.roll(rand);
    }

    public static Theme randomHellTheme(Random rand) {
        return HELL_THEME.roll(rand);
    }

    public static SecondaryTheme randomHellSecondaryTheme(Random rand) {
        return HELL_SECONDARY_THEME.roll(rand);
    }

    public static Theme getTheme(ResourceLocation key) {
        return KEY_TO_THEME.getOrDefault(key, KEY_TO_THEME.getOrDefault(PRIMARY_THEME_FALLBACK, BUILTIN_DEFAULT_THEME));
    }

    public static SecondaryTheme getSecondaryTheme(ResourceLocation key) {
        return KEY_TO_SECONDARY_THEME.getOrDefault(key, KEY_TO_SECONDARY_THEME.getOrDefault(SECONDARY_THEME_FALLBACK, BUILTIN_DEFAULT_SECONDARY_THEME));
    }

    public static Theme getThemeByID(int theme) {
        return ID_TO_THEME.getOrDefault(theme, ID_TO_THEME.getOrDefault(0, BUILTIN_DEFAULT_THEME));
    }

    public static SecondaryTheme getSubThemeByID(int id) {
        return ID_TO_SECONDARY_THEME.getOrDefault(id, ID_TO_SECONDARY_THEME.getOrDefault(0, BUILTIN_DEFAULT_SECONDARY_THEME));
    }

    public static ImmutableSet<ResourceLocation> getThemeKeys() {
        return THEME_KEYS;
    }

    public static ImmutableSet<ResourceLocation> getSecondaryThemeKeys() {
        return SECONDARY_THEME_KEYS;
    }

}
