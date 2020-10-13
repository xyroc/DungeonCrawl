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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jline.utils.InputStreamReader;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;

public class Theme {

    public static final Random RANDOM = new Random();

    public static HashMap<String, Integer> BIOME_TO_THEME_MAP = new HashMap<>();
    public static HashMap<String, Integer> BIOME_TO_SUBTHEME_MAP = new HashMap<>();
    ;
    public static HashMap<Integer, Theme> ID_TO_THEME_MAP = new HashMap<>();
    ;
    public static HashMap<Integer, SubTheme> ID_TO_SUBTHEME_MAP = new HashMap<>();

    public static HashMap<Integer, ThemeRandomizer> THEME_RANDOMIZERS = new HashMap<>(), SUB_THEME_RANDOMIZERS = new HashMap<>();

    /* **************************** */
    /*          BASE THEMES         */
    /* **************************** */

    //    public static final Theme MODEL = new Theme(null, () -> DungeonBlocks.STONE_BRICKS,
//            () -> DungeonBlocks.COBBLESTONE, () -> DungeonBlocks.GRAVEL, Blocks.STONE_STAIRS::getDefaultState,
//            Blocks.COBBLESTONE_STAIRS::getDefaultState, Blocks.STONE_BRICKS::getDefaultState,
//            Blocks.STONE_BRICK_WALL::getDefaultState, null, Blocks.OBSIDIAN::getDefaultState,
//            Blocks.COBBLESTONE_SLAB::getDefaultState, Blocks.STONE_BRICK_SLAB::getDefaultState);
//
    public static final Theme DEFAULT = new Theme(() -> DungeonBlocks.STONE_BRICKS,
            DungeonBlocks.STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE,
            DungeonBlocks.STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE, DungeonBlocks.STONE_BRICK_FLOOR,
            DungeonBlocks.STAIRS_STONE_COBBLESTONE, DungeonBlocks.STAIRS_STONE_COBBLESTONE,
            DungeonBlocks.STONE_BRICKS_GRAVEL_COBBLESTONE, DungeonBlocks.STONE_WALL, DungeonBlocks.STONE_BRICK_FLOOR,
            Blocks.OBSIDIAN::getDefaultState, Blocks.COBBLESTONE_SLAB::getDefaultState, Blocks.STONE_BRICK_SLAB::getDefaultState);
    //
//    public static final Theme OCEAN = new Theme(Blocks.PRISMARINE,
//            Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS,
//            Blocks.DARK_PRISMARINE, Blocks.PRISMARINE_BRICK_STAIRS,
//            Blocks.PRISMARINE_BRICK_STAIRS, Blocks.PRISMARINE,
//            Blocks.PRISMARINE_WALL, Blocks.DARK_PRISMARINE, Blocks.OBSIDIAN,
//            Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_BRICK_SLAB);
//
//    public static final Theme BRICKS = new Theme(null, DungeonBlocks.BRICKS_GRANITE,
//            DungeonBlocks.STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE,
//            DungeonBlocks.STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE, DungeonBlocks.STAIRS_BRICKS_GRANITE,
//            DungeonBlocks.STAIRS_STONE_COBBLESTONE, DungeonBlocks.BRICKS_GRANITE, DungeonBlocks.BRICKS_GRANITE_WALL,
//            DungeonBlocks.BRICKS_GRANITE, Blocks.OBSIDIAN::getDefaultState, Blocks.COBBLESTONE_SLAB::getDefaultState,
//            Blocks.BRICK_SLAB::getDefaultState);
//
//    public static final Theme BRICKS_2 = new Theme(null, DungeonBlocks.BRICKS_GRANITE, DungeonBlocks.BRICKS_GRANITE,
//            DungeonBlocks.STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE, DungeonBlocks.STAIRS_BRICKS_GRANITE,
//            DungeonBlocks.STAIRS_BRICKS_GRANITE, DungeonBlocks.BRICKS_GRANITE, DungeonBlocks.BRICKS_GRANITE_WALL,
//            DungeonBlocks.BRICKS_GRANITE, Blocks.OBSIDIAN::getDefaultState, Blocks.COBBLESTONE_SLAB::getDefaultState,
//            Blocks.BRICK_SLAB::getDefaultState);
//
//    public static final Theme ANDESITE = new Theme(DungeonBlocks.ANDESITE_STONE_BRICKS,
//            DungeonBlocks.ANDESITE_STONE_BRICKS, DungeonBlocks.ANDESITE_STONE_BRICKS,
//            DungeonBlocks.ANDESITE_STONE_BRICKS_COBBLESTONE, DungeonBlocks.STAIRS_ANDESITE_STONE_COBBLESTONE,
//            DungeonBlocks.STAIRS_ANDESITE_STONE_COBBLESTONE, DungeonBlocks.ANDESITE_STONE_BRICKS,
//            DungeonBlocks.ANDESITE_STONE_WALL, DungeonBlocks.ANDESITE_STONE_BRICKS_COBBLESTONE,
//            Blocks.OBSIDIAN::getDefaultState, Blocks.ANDESITE_SLAB::getDefaultState, Blocks.STONE_BRICK_SLAB::getDefaultState);
//
//    public static final Theme NETHER = new Theme(null, DungeonBlocks.NETHERRACK_NETHERBRICK,
//            DungeonBlocks.NETHERRACK_NETHERBRICK_FLOOR, DungeonBlocks.NETHERRACK_NETHERBRICK_FLOOR,
//            DungeonBlocks.NETHER_BRICK_STAIRS, DungeonBlocks.NETHER_BRICK_STAIRS, DungeonBlocks.NETHERRACK_NETHERBRICK,
//            DungeonBlocks.NETHER_WALL, DungeonBlocks.NETHERRACK_NETHERBRICK, Blocks.OBSIDIAN::getDefaultState,
//            Blocks.NETHER_BRICK_SLAB::getDefaultState, Blocks.RED_NETHER_BRICK_SLAB::getDefaultState);
//
//    public static final Theme DESERT = new Theme(DungeonBlocks.SANDSTONE_DEFAULT_CHSELED_SMOOTH,
//            DungeonBlocks.SANDSTONE_DEFAULT_CHSELED_SMOOTH, DungeonBlocks.SANDSTONE_DEFAULT_CHSELED_SMOOTH,
//            DungeonBlocks.SANDSTONE_DEFAULT_SMOOTH_SAND, DungeonBlocks.STAIRS_SANDSTONE_DEFAULT_SMOOTH,
//            DungeonBlocks.STAIRS_SANDSTONE_DEFAULT_SMOOTH, DungeonBlocks.SANDSTONE_DEFAULT_CHSELED_SMOOTH,
//            Blocks.SANDSTONE_WALL::getDefaultState, DungeonBlocks.SANDSTONE_DEFAULT_CHSELED_SMOOTH,
//            Blocks.OBSIDIAN::getDefaultState, Blocks.SMOOTH_SANDSTONE_SLAB::getDefaultState, Blocks.CUT_SANDSTONE_SLAB::getDefaultState);
//
//    public static final Theme BADLANDS = new Theme(DungeonBlocks.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH,
//            DungeonBlocks.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH, DungeonBlocks.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH,
//            DungeonBlocks.RED_SANDSTONE_DEFAULT_SMOOTH_RED_SAND, DungeonBlocks.STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH,
//            DungeonBlocks.STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH, DungeonBlocks.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH,
//            Blocks.RED_SANDSTONE_WALL::getDefaultState, DungeonBlocks.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH,
//            Blocks.OBSIDIAN::getDefaultState, Blocks.SMOOTH_RED_SANDSTONE_SLAB::getDefaultState, Blocks.CUT_RED_SANDSTONE_SLAB::getDefaultState);
//
//    public static final Theme ICE = new Theme(DungeonBlocks.ICE_DEFAULT_PACKED, DungeonBlocks.ICE_DEFAULT_PACKED,
//            DungeonBlocks.ICE_DEFAULT_PACKED, Blocks.ICE::getDefaultState, DungeonBlocks.ICE_DEFAULT_PACKED,
//            DungeonBlocks.ICE_DEFAULT_PACKED, DungeonBlocks.ICE_DEFAULT_PACKED, Blocks.CAVE_AIR::getDefaultState,
//            DungeonBlocks.ICE_DEFAULT_PACKED, Blocks.OBSIDIAN::getDefaultState, Blocks.ICE::getDefaultState, Blocks.ICE::getDefaultState);
//
//    public static final Theme MOSS = new Theme(DungeonBlocks.MOSS, DungeonBlocks.MOSS, DungeonBlocks.MOSS,
//            DungeonBlocks.MOSS_FLOOR, DungeonBlocks.MOSS_STAIRS, DungeonBlocks.MOSS_STAIRS, DungeonBlocks.MOSS,
//            DungeonBlocks.MOSS_WALL, DungeonBlocks.MOSS, Blocks.OBSIDIAN::getDefaultState,
//            Blocks.MOSSY_COBBLESTONE_SLAB::getDefaultState, Blocks.MOSSY_STONE_BRICK_SLAB::getDefaultState);
//
//    public static final Theme OBSIDIAN_MOSSY = new Theme(null, DungeonBlocks.OBSIDIAN_MOSSY,
//            DungeonBlocks.OBSIDIAN_MOSSY, DungeonBlocks.OBSIDIAN_MOSSY_FLOOR, DungeonBlocks.MOSS_STAIRS,
//            DungeonBlocks.MOSS_STAIRS, DungeonBlocks.OBSIDIAN_MOSSY, DungeonBlocks.MOSS_WALL,
//            DungeonBlocks.OBSIDIAN_MOSSY, Blocks.OBSIDIAN::getDefaultState, Blocks.MOSSY_COBBLESTONE_SLAB::getDefaultState,
//            Blocks.MOSSY_STONE_BRICK_SLAB::getDefaultState);
//
//    /* **************************** */
//    /*          SUB-THEMES          */
//    /* **************************** */
//
//    public static final SubTheme MODEL_SUB = new SubTheme(Blocks.OAK_LOG, Blocks.OAK_TRAPDOOR, Blocks.REDSTONE_WALL_TORCH,
//            Blocks.OAK_DOOR, Blocks.OAK_PLANKS, Blocks.OAK_STAIRS);
//
//    public static final SubTheme NETHER_SUB = new SubTheme(Blocks.MAGMA_BLOCK::getDefaultState,
//            Blocks.CAVE_AIR::getDefaultState, Blocks.REDSTONE_WALL_TORCH::getDefaultState,
//            Blocks.IRON_DOOR::getDefaultState, Blocks.NETHERRACK::getDefaultState,
//            DungeonBlocks.NETHER_BRICK_STAIRS, null, null, null, null, null);
//
//    public static final SubTheme OBSIDIAN_MOSSY_SUB = new SubTheme(Blocks.OBSIDIAN::getDefaultState,
//            Blocks.CAVE_AIR::getDefaultState, Blocks.CAVE_AIR::getDefaultState,
//            Blocks.CAVE_AIR::getDefaultState, DungeonBlocks.OBSIDIAN_MOSSY, DungeonBlocks.MOSS_STAIRS);
//
//    public static final SubTheme ACACIA = new SubTheme(Blocks.ACACIA_LOG::getDefaultState,
//            Blocks.ACACIA_TRAPDOOR::getDefaultState, Blocks.REDSTONE_WALL_TORCH::getDefaultState,
//            Blocks.ACACIA_DOOR::getDefaultState, Blocks.ACACIA_PLANKS::getDefaultState,
//            Blocks.ACACIA_STAIRS::getDefaultState);
//
    public static final SubTheme OAK = new SubTheme(Blocks.OAK_LOG::getDefaultState,
            Blocks.OAK_TRAPDOOR::getDefaultState, Blocks.REDSTONE_WALL_TORCH::getDefaultState,
            Blocks.OAK_DOOR::getDefaultState, Blocks.OAK_PLANKS::getDefaultState,
            Blocks.OAK_STAIRS::getDefaultState, Blocks.OAK_SLAB::getDefaultState,
            Blocks.OAK_FENCE::getDefaultState, Blocks.OAK_FENCE_GATE::getDefaultState,
            Blocks.OAK_BUTTON::getDefaultState, Blocks.OAK_PRESSURE_PLATE::getDefaultState);
//
//    public static final SubTheme BIRCH = new SubTheme(Blocks.BIRCH_LOG::getDefaultState,
//            Blocks.BIRCH_TRAPDOOR::getDefaultState, Blocks.REDSTONE_WALL_TORCH::getDefaultState,
//            Blocks.BIRCH_DOOR::getDefaultState, Blocks.BIRCH_PLANKS::getDefaultState,
//            Blocks.BIRCH_STAIRS::getDefaultState);
//
//    public static final SubTheme DARK_OAK = new SubTheme(Blocks.DARK_OAK_LOG::getDefaultState,
//            Blocks.DARK_OAK_TRAPDOOR::getDefaultState, Blocks.REDSTONE_WALL_TORCH::getDefaultState,
//            Blocks.DARK_OAK_DOOR::getDefaultState, Blocks.DARK_OAK_PLANKS::getDefaultState,
//            Blocks.DARK_OAK_STAIRS::getDefaultState);
//
//    public static final SubTheme JUNGLE = new SubTheme(Blocks.JUNGLE_LOG::getDefaultState,
//            Blocks.JUNGLE_TRAPDOOR::getDefaultState, Blocks.REDSTONE_WALL_TORCH::getDefaultState,
//            Blocks.JUNGLE_DOOR::getDefaultState, Blocks.JUNGLE_PLANKS::getDefaultState,
//            Blocks.JUNGLE_STAIRS::getDefaultState);
//
//    public static final SubTheme SPRUCE = new SubTheme(Blocks.SPRUCE_LOG::getDefaultState,
//            Blocks.SPRUCE_TRAPDOOR::getDefaultState, Blocks.REDSTONE_WALL_TORCH::getDefaultState,
//            Blocks.SPRUCE_DOOR::getDefaultState, Blocks.SPRUCE_PLANKS::getDefaultState,
//            Blocks.SPRUCE_STAIRS::getDefaultState);
//
//    public static final SubTheme DESERT_SUB = new SubTheme(Blocks.CHISELED_SANDSTONE, Blocks.CAVE_AIR,
//            Blocks.REDSTONE_WALL_TORCH, Blocks.CAVE_AIR, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_STAIRS);
//
//    public static final SubTheme ICE_SUB = new SubTheme(Blocks.ICE, Blocks.CAVE_AIR, Blocks.CAVE_AIR, Blocks.CAVE_AIR,
//            Blocks.CAVE_AIR, Blocks.CAVE_AIR);

    public static final ThemeRandomizer DEFAULT_RANDOMIZER = (rand, base) -> base;

    public final IBlockStateProvider ceiling, solid, normal, normal2, floor, solidStairs, stairs, material, vanillaWall, column, slab, solidSlab;
    public Integer subTheme;

    public IDungeonDecoration[] decorations;

    public Theme(IBlockStateProvider ceiling, IBlockStateProvider solid, IBlockStateProvider normal,
                 IBlockStateProvider floor, IBlockStateProvider solidStairs, IBlockStateProvider stairs,
                 IBlockStateProvider material, IBlockStateProvider vanillaWall, IBlockStateProvider column,
                 IBlockStateProvider normal2, IBlockStateProvider slab, IBlockStateProvider solidSlab) {
        this.ceiling = ceiling;
        this.solid = solid;
        this.normal = normal;
        this.floor = floor;
        this.solidStairs = solidStairs;
        this.stairs = stairs;
        this.material = material;
        this.vanillaWall = vanillaWall;
        this.column = column;
        this.normal2 = normal2;
        this.slab = slab;
        this.solidSlab = solidSlab;
    }

    public Theme(Block ceiling, Block solid, Block normal, Block floor, Block solidStairs, Block stairs, Block material,
                 Block vanillaWall, Block column, Block normal2, Block slab, Block solidSlab) {
        this.ceiling = ceiling::getDefaultState;
        this.solid = solid::getDefaultState;
        this.normal = normal::getDefaultState;
        this.floor = floor::getDefaultState;
        this.solidStairs = solidStairs::getDefaultState;
        this.stairs = stairs::getDefaultState;
        this.material = material::getDefaultState;
        this.vanillaWall = vanillaWall::getDefaultState;
        this.column = column::getDefaultState;
        this.normal2 = normal2::getDefaultState;
        this.slab = slab::getDefaultState;
        this.solidSlab = solidSlab::getDefaultState;
    }

    public Theme withDecorations(@Nullable IDungeonDecoration[] decorations) {
        this.decorations = decorations;
        return this;
    }

    public static class SubTheme {

        public final IBlockStateProvider wallLog, trapDoor, torchDark, door, material, stairs, slab, fence, fenceGate, button, pressurePlate;

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

        public SubTheme(Block wallLog, Block trapDoor, Block torchDark, Block door, Block material, Block stairs, Block slab, Block fence, Block fenceGate, Block button, Block pressurePlate) {
            this.wallLog = wallLog::getDefaultState;
            this.trapDoor = trapDoor::getDefaultState;
            this.torchDark = torchDark::getDefaultState;
            this.door = door::getDefaultState;
            this.material = material::getDefaultState;
            this.stairs = stairs::getDefaultState;
            this.slab = slab::getDefaultState;
            this.fence = fence::getDefaultState;
            this.fenceGate = fenceGate::getDefaultState;
            this.button = button::getDefaultState;
            this.pressurePlate = pressurePlate::getDefaultState;
        }

    }

    public static void loadJson(IResourceManager resourceManager) {
        JsonParser parser = new JsonParser();
        for (ResourceLocation resource : resourceManager
                .getAllResourceLocations(DungeonCrawl.locate("theming/").getPath(), (s) -> s.endsWith(".json"))) {
            DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
            try {
                JsonReader reader = new JsonReader(
                        new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                JsonThemeHandler.deserialize(parser.parse(reader).getAsJsonObject());
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource.toString());
                e.printStackTrace();
            }
        }
    }

//    public static void loadRandomizers(IResourceManager resourceManager) {
//        DungeonCrawl.LOGGER.info("Loading theme randomizers");
//        try {
//            for (ResourceLocation resource : resourceManager.getAllResourceLocations(
//                    DungeonCrawl.locate("theme_randomizers/").getPath(), (s) -> s.endsWith(".json"))) {
//                loadRandomizer(resource, resourceManager);
//            }
//        } catch (Exception e) {
//            DungeonCrawl.LOGGER.error("Failed to load the theme randomizers");
//            e.printStackTrace();
//        }
//    }
//
//    public static void loadRandomizer(ResourceLocation resource, IResourceManager resourceManager) {
//        String path = resource.toString();
////		if (!path.endsWith(".json"))
////			return;
//        DungeonCrawl.LOGGER.info("Loading Randomizer: {}", path);
//        try {
//            WeightedThemeRandomizer randomizer = DungeonCrawl.GSON.fromJson(
//                    new InputStreamReader(resourceManager.getResource(resource).getInputStream()),
//                    WeightedThemeRandomizer.class);
//            THEME_RANDOMIZERS.put(randomizer.base, randomizer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static int getTheme(String biome, Random rand) {
        int theme = BIOME_TO_THEME_MAP.getOrDefault(biome, 0);
        return THEME_RANDOMIZERS.getOrDefault(theme, DEFAULT_RANDOMIZER).randomize(rand, theme);
    }

    public static int getSubTheme(String biome, Random rand) {
        int subTheme = BIOME_TO_SUBTHEME_MAP.getOrDefault(biome, 0);
        return SUB_THEME_RANDOMIZERS.getOrDefault(subTheme, DEFAULT_RANDOMIZER).randomize(rand, subTheme);
    }

    public static Theme get(int theme) {
        return ID_TO_THEME_MAP.getOrDefault(theme, ID_TO_THEME_MAP.getOrDefault(0, DEFAULT));
    }

    public static SubTheme getSub(int id) {
        return ID_TO_SUBTHEME_MAP.getOrDefault(id, ID_TO_SUBTHEME_MAP.getOrDefault(0, OAK));
    }

    public static int randomizeTheme(int base, Random rand) {
        return THEME_RANDOMIZERS.getOrDefault(base, DEFAULT_RANDOMIZER).randomize(rand, base);
    }

    public static int randomizeSubTheme(int base, Random rand) {
        return SUB_THEME_RANDOMIZERS.getOrDefault(base, DEFAULT_RANDOMIZER).randomize(rand, base);
    }

    public static ThemeRandomizer createRandomizer(int... themes) {
        return (rand, base) -> themes[rand.nextInt(themes.length)];
    }

    @FunctionalInterface
    public static interface ThemeRandomizer {

        int randomize(Random rand, int base);

    }

}
