package xiroc.dungeoncrawl.theme;


/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

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

    public static HashMap<String, Integer> BIOME_TO_THEME_MAP;
    public static HashMap<String, Integer> BIOME_TO_SUBTHEME_MAP;
    public static HashMap<Integer, Theme> ID_TO_THEME_MAP;
    public static HashMap<Integer, SubTheme> ID_TO_SUBTHEME_MAP;

    public static HashMap<Integer, ThemeRandomizer> THEME_RANDOMIZERS, SUB_THEME_RANDOMIZERS;

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

    private static final ThemeRandomizer DEFAULT_RANDOMIZER = (rand, base) -> base;

    static {

        // Base Themes

        BIOME_TO_THEME_MAP = new HashMap<String, Integer>();

//        BIOME_TO_THEME_MAP.put("minecraft:ocean", 2);
//        BIOME_TO_THEME_MAP.put("minecraft:deep_ocean", 2);
//        BIOME_TO_THEME_MAP.put("minecraft:cold_ocean", 2);
//        BIOME_TO_THEME_MAP.put("minecraft:deep_cold_ocean", 2);
//        BIOME_TO_THEME_MAP.put("minecraft:lukewarm_ocean", 2);
//        BIOME_TO_THEME_MAP.put("minecraft:deep_lukewarm_ocean", 2);
//        BIOME_TO_THEME_MAP.put("minecraft:warm_ocean", 2);
//        BIOME_TO_THEME_MAP.put("minecraft:deep_warm_ocean", 2);
//
//        BIOME_TO_THEME_MAP.put("minecraft:frozen_ocean", 2);
//        BIOME_TO_THEME_MAP.put("mineraft:deep_frozen_ocean", 2);
//
//        BIOME_TO_THEME_MAP.put("minecraft:desert", 16);
//        BIOME_TO_THEME_MAP.put("minecraft:desert_hills", 16);
//        BIOME_TO_THEME_MAP.put("minecraft:desert_lakes", 16);
//
//        BIOME_TO_THEME_MAP.put("minecraft:badlands", 17);
//        BIOME_TO_THEME_MAP.put("minecraft:wooded_badlands_plateau", 17);
//        BIOME_TO_THEME_MAP.put("minecraft:badlands_plateau", 17);
//        BIOME_TO_THEME_MAP.put("minecraft:eroded_badlands", 17);
//        BIOME_TO_THEME_MAP.put("minecraft:modified_wooded_badlands_plateau", 17);
//        BIOME_TO_THEME_MAP.put("minecraft:modified_badlands_plateau", 17);
//
//        BIOME_TO_THEME_MAP.put("minecraft:ice_spikes", 32);
//
//        BIOME_TO_THEME_MAP.put("minecraft:swamp", 80);
//        BIOME_TO_THEME_MAP.put("minecraft:swamp_hills", 80);

        ID_TO_THEME_MAP = new HashMap<>();

//        ID_TO_THEME_MAP.put(-1, MODEL);
//        ID_TO_THEME_MAP.put(0, DEFAULT);
//        ID_TO_THEME_MAP.put(1, NETHER);
//        ID_TO_THEME_MAP.put(2, OCEAN);
//
//        ID_TO_THEME_MAP.put(16, DESERT);
//        ID_TO_THEME_MAP.put(17, BADLANDS);
//
//        //ID_TO_THEME_MAP.put(32, ICE);
//
//        ID_TO_THEME_MAP.put(48, BRICKS);
//        ID_TO_THEME_MAP.put(49, BRICKS_2);
//        ID_TO_THEME_MAP.put(50, ANDESITE);
//
//        ID_TO_THEME_MAP.put(80, MOSS);
//        ID_TO_THEME_MAP.put(81, OBSIDIAN_MOSSY);

        // Sub-Themes

        BIOME_TO_SUBTHEME_MAP = new HashMap<String, Integer>();

//        BIOME_TO_SUBTHEME_MAP.put("minecraft:jungle", 1);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:jungle_edge", 1);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:jungle_hills", 1);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:modified_jungle", 1);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:modified_jungle_edge", 1);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:bamboo_jungle", 1);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:bamboo_jungle_hills", 1);
//
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:birch_forest", 2);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:birch_forest_hills", 2);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:tall_birch_forest", 2);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:tall_birch_hills", 2);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:flower_forest", 2);
//
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:savanna", 3);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:savanna_plateau", 3);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:shattered_savanna", 3);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:shattered_savanna_plateau", 3);
//
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:dark_forest", 4);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:dark_forest_hills", 4);
//
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:taiga", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:snowy_tundra", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:taiga_hills", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:snowy_taiga", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:snowy_taga_hills", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:giant_tree_taiga", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:giant_tree_taiga_hills", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:taiga_mountains", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:snowy_taiga_mountains", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:giant_spruce_taiga", 5);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:giant_spruce_taiga_hills", 5);
//
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:desert", 6);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:desert_hills", 6);
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:desert_lakes", 6);
//
//        BIOME_TO_SUBTHEME_MAP.put("minecraft:ice_spikes", 7);

        ID_TO_SUBTHEME_MAP = new HashMap<Integer, SubTheme>();

//        ID_TO_SUBTHEME_MAP.put(0, OAK);
//        ID_TO_SUBTHEME_MAP.put(1, JUNGLE);
//        ID_TO_SUBTHEME_MAP.put(2, BIRCH);
//        ID_TO_SUBTHEME_MAP.put(3, ACACIA);
//        ID_TO_SUBTHEME_MAP.put(4, DARK_OAK);
//        ID_TO_SUBTHEME_MAP.put(5, SPRUCE);
//        ID_TO_SUBTHEME_MAP.put(6, DESERT_SUB);
//        //ID_TO_SUBTHEME_MAP.put(7, ICE_SUB);
//
//        ID_TO_SUBTHEME_MAP.put(8, NETHER_SUB);
//        ID_TO_SUBTHEME_MAP.put(9, OBSIDIAN_MOSSY_SUB);

        THEME_RANDOMIZERS = new HashMap<>();
        SUB_THEME_RANDOMIZERS = new HashMap<>();

//		ThemeRandomizer randomizer = createRandomizer(0, 0, 48, 49, 50);
//
//		RANDOMIZERS.put(0, randomizer);

    }

    public final IBlockStateProvider ceiling, solid, normal, normal2, floor, solidStairs, stairs, material, vanillaWall, column, slab, solidSlab;
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
        DungeonCrawl.LOGGER.info("Loading themes from JSON");
        JsonParser parser = new JsonParser();
        try {
            for (ResourceLocation resource : resourceManager
                    .getAllResourceLocations(DungeonCrawl.locate("theming/").getPath(), (s) -> s.endsWith(".json"))) {
                DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
                JsonReader reader = new JsonReader(
                        new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                JsonThemeHandler.deserialize(parser.parse(reader).getAsJsonObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static ThemeRandomizer createRandomizer(int... themes) {
        return (rand, base) -> themes[rand.nextInt(themes.length)];

    }

    @FunctionalInterface
    public static interface ThemeRandomizer {

        int randomize(Random rand, int base);

    }

}
