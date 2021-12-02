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
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import org.jline.utils.InputStreamReader;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.decoration.DungeonDecoration;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.util.IRandom;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

public class Theme {

    /**
     * These builtin themes will only be used in case a theme can't be found
     * and the default theme can't be found either.
     */
    public static final Theme BUILTIN_DEFAULT_THEME = new Theme(
            (world, pos, rotation) -> Blocks.STONE_BRICKS.defaultBlockState(),
            (world, pos, rotation) -> Blocks.STONE_BRICKS.defaultBlockState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE.defaultBlockState(),
            (world, pos, rotation) -> Blocks.GRAVEL.defaultBlockState(),
            (world, pos, rotation) -> Blocks.STONE_BRICK_STAIRS.defaultBlockState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE_STAIRS.defaultBlockState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE.defaultBlockState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE_WALL.defaultBlockState(),
            (world, pos, rotation) -> Blocks.COBBLESTONE_SLAB.defaultBlockState(),
            (world, pos, rotation) -> Blocks.STONE_BRICK_SLAB.defaultBlockState(),
            (world, pos, rotation) -> Blocks.IRON_BARS.defaultBlockState(),
            (world, pos, rotation) -> Blocks.WATER.defaultBlockState());

    public static final SecondaryTheme BUILTIN_DEFAULT_SECONDARY_THEME = new SecondaryTheme(
            (world, pos, rotation) -> Blocks.OAK_LOG.defaultBlockState(),
            (world, pos, rotation) -> Blocks.OAK_TRAPDOOR.defaultBlockState(),
            (world, pos, rotation) -> Blocks.OAK_DOOR.defaultBlockState(),
            (world, pos, rotation) -> Blocks.OAK_PLANKS.defaultBlockState(),
            (world, pos, rotation) -> Blocks.OAK_STAIRS.defaultBlockState(),
            (world, pos, rotation) -> Blocks.OAK_SLAB.defaultBlockState(),
            (world, pos, rotation) -> Blocks.OAK_FENCE.defaultBlockState(),
            (world, pos, rotation) -> Blocks.OAK_FENCE_GATE.defaultBlockState(),
            (world, pos, rotation) -> Blocks.OAK_BUTTON.defaultBlockState(),
            (world, pos, rotation) -> Blocks.OAK_PRESSURE_PLATE.defaultBlockState());

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

    public final BlockStateProvider pillar, solid, generic, floor, solidStairs, stairs, material, wall, slab, solidSlab, fencing, fluid;

    public IRandom<SecondaryTheme> secondaryTheme;

    private ResourceLocation key;

    private DungeonDecoration[] decorations;

    public Theme(BlockStateProvider pillar,
                 BlockStateProvider solid,
                 BlockStateProvider generic,
                 BlockStateProvider floor,
                 BlockStateProvider solidStairs,
                 BlockStateProvider stairs,
                 BlockStateProvider material,
                 BlockStateProvider wall,
                 BlockStateProvider slab,
                 BlockStateProvider solidSlab,
                 BlockStateProvider fencing,
                 BlockStateProvider fluid) {
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
        this.fencing = fencing;
        this.fluid = fluid;
    }

    public void setDecorations(DungeonDecoration[] decorations) {
        this.decorations = decorations;
    }

    public boolean hasDecorations() {
        return decorations != null;
    }

    public ResourceLocation getKey() {
        return key;
    }

    public DungeonDecoration[] getDecorations() {
        return decorations;
    }

    public BlockStateProvider getPillar() {
        return pillar;
    }

    public BlockStateProvider getSolid() {
        return solid;
    }

    public BlockStateProvider getGeneric() {
        return generic;
    }

    public BlockStateProvider getFencing() {
        return fencing;
    }

    public BlockStateProvider getFloor() {
        return floor;
    }

    public BlockStateProvider getFluid() {
        return fluid;
    }

    public BlockStateProvider getSolidStairs() {
        return solidStairs;
    }

    public BlockStateProvider getStairs() {
        return stairs;
    }

    public BlockStateProvider getMaterial() {
        return material;
    }

    public BlockStateProvider getWall() {
        return wall;
    }

    public BlockStateProvider getSlab() {
        return slab;
    }

    public BlockStateProvider getSolidSlab() {
        return solidSlab;
    }

    public static class SecondaryTheme {

        public final BlockStateProvider pillar, trapDoor, door, material, stairs, slab, fence, fenceGate, button, pressurePlate;

        private ResourceLocation key;

        public SecondaryTheme(BlockStateProvider pillar,
                              BlockStateProvider trapDoor,
                              BlockStateProvider door,
                              BlockStateProvider material,
                              BlockStateProvider stairs,
                              BlockStateProvider slab,
                              BlockStateProvider fence,
                              BlockStateProvider fenceGate,
                              BlockStateProvider button,
                              BlockStateProvider pressurePlate) {
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

        public BlockStateProvider getPillar() {
            return pillar;
        }

        public BlockStateProvider getTrapDoor() {
            return trapDoor;
        }

        public BlockStateProvider getDoor() {
            return door;
        }

        public BlockStateProvider getMaterial() {
            return material;
        }

        public BlockStateProvider getStairs() {
            return stairs;
        }

        public BlockStateProvider getSlab() {
            return slab;
        }

        public BlockStateProvider getFence() {
            return fence;
        }

        public BlockStateProvider getFenceGate() {
            return fenceGate;
        }

        public BlockStateProvider getButton() {
            return button;
        }

        public BlockStateProvider getPressurePlate() {
            return pressurePlate;
        }

    }

    public static void loadJson(ResourceManager resourceManager) {
        ID_TO_THEME.clear();
        ID_TO_SECONDARY_THEME.clear();
        KEY_TO_THEME.clear();
        KEY_TO_SECONDARY_THEME.clear();
        BIOME_TO_THEME.clear();
        BIOME_TO_SECONDARY_THEME.clear();

        ImmutableSet.Builder<ResourceLocation> themeKeySetBuilder = new ImmutableSet.Builder<>();
        ImmutableSet.Builder<ResourceLocation> secondaryThemeKeySetBuilder = new ImmutableSet.Builder<>();

        for (ResourceLocation resource : resourceManager.listResources(DungeonCrawl.locate(SECONDARY_THEME_DIRECTORY).getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                ResourceLocation key = DungeonCrawl.key(resource, SECONDARY_THEME_DIRECTORY, ".json");
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
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

        for (ResourceLocation resource : resourceManager.listResources(DungeonCrawl.locate(PRIMARY_THEME_DIRECTORY).getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                ResourceLocation key = DungeonCrawl.key(resource, PRIMARY_THEME_DIRECTORY, ".json");
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
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

        for (ResourceLocation resource : resourceManager.listResources(PRIMARY_THEME_MAPPINGS_DIRECTORY, (s) -> s.endsWith(".json"))) {
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                JsonTheming.deserializeThemeMapping(JsonParser.parseReader(reader).getAsJsonObject(), themeMappingBuilders, primaryDefaultBuilder, resource);
            } catch (IOException e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource);
                e.printStackTrace();
            }
        }

        for (ResourceLocation resource : resourceManager.listResources(SECONDARY_THEME_MAPPINGS_DIRECTORY, (s) -> s.endsWith(".json"))) {
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                JsonTheming.deserializeSecondaryThemeMapping(JsonParser.parseReader(reader).getAsJsonObject(), secondaryThemeMappingBuilders, secondaryDefaultBuilder, resource);
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

    private static Tuple<WeightedRandom<Theme>, WeightedRandom<SecondaryTheme>> loadRandomThemeFiles(String directory, ResourceManager resourceManager) {
        WeightedRandom.Builder<Theme> primary = new WeightedRandom.Builder<>();
        WeightedRandom.Builder<SecondaryTheme> secondary = new WeightedRandom.Builder<>();
        for (ResourceLocation resource : resourceManager.listResources(directory, (s) -> s.endsWith(".json"))) {
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                JsonTheming.deserializeRandomThemeFile(JsonParser.parseReader(reader).getAsJsonObject(),
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
