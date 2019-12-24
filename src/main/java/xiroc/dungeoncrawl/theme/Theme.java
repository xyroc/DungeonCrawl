package xiroc.dungeoncrawl.theme;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class Theme {

	// OLD THEME IDS
	// test -1
	// default 0
	// nether 1
	// swamp 2
	// ocean 3
	// frozen ocean 4
	// jungle 5
	// birch forest 6
	// savanna 7
	// oak forest 8
	// dark oak forest 9
	// taiga 10

	// desert 16
	// badlands 17

	// ice 32

	// bricks 48
	// andesite 49
	// bricks birch 50
	// andesite birch 51
	// bricks jungle 52
	// andesite jungle 53
	// bricks savanna 54
	// andesite savanna 55
	// bricks dark oak 56
	// andesite dark oak 57
	// bricks spruce 58
	// andesite spruce 59

	// moss default 80
	// moss andesite 81

	public static final Random RANDOM = new Random();

	public static HashMap<String, Integer> BIOME_TO_THEME_MAP;
	public static HashMap<String, Integer> BIOME_TO_SUBTHEME_MAP;
	public static HashMap<Integer, Theme> ID_TO_THEME_MAP;
	public static HashMap<Integer, SubTheme> ID_TO_SUBTHEME_MAP;

	public static HashMap<Integer, ThemeRandomizer> RANDOMIZERS;

	// | *************************** |
	// | - - - - Base Themes - - - - |
	// | *************************** |

	public static final Theme TEST = new Theme(() -> BlockRegistry.STONE_BRICKS, () -> BlockRegistry.STONE_BRICKS,
			() -> BlockRegistry.GRAVEL, () -> Blocks.STONE_STAIRS.getDefaultState(),
			() -> Blocks.STONE_BRICKS.getDefaultState(), () -> Blocks.STONE_BRICK_WALL.getDefaultState(),
			() -> Blocks.STONE.getDefaultState());

	public static final Theme DEFAULT = new Theme(() -> BlockRegistry.STONE_BRICKS,
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			BlockRegistry.STAIRS_STONE_COBBLESTONE, BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE,
			BlockRegistry.STONE_WALL, BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE);

	public static final Theme OCEAN = new Theme(Blocks.PRISMARINE.getDefaultState(),
			Blocks.PRISMARINE_BRICKS.getDefaultState(), Blocks.DARK_PRISMARINE.getDefaultState(),
			Blocks.PRISMARINE_BRICK_STAIRS.getDefaultState(), Blocks.PRISMARINE.getDefaultState(),
			Blocks.PRISMARINE_WALL.getDefaultState(), Blocks.DARK_PRISMARINE.getDefaultState());

	public static final Theme BRICKS = new Theme(BlockRegistry.BRICKS_GRANITE, BlockRegistry.BRICKS_GRANITE,
			BlockRegistry.BRICKS_GRANITE_FLOOR, BlockRegistry.STAIRS_BRICKS_GRANITE, BlockRegistry.BRICKS_GRANITE,
			BlockRegistry.BRICKS_GRANITE_WALL, BlockRegistry.BRICKS_GRANITE);

	public static final Theme ANDESITE = new Theme(BlockRegistry.ANDESITE_STONE_BRICKS,
			BlockRegistry.ANDESITE_STONE_BRICKS, BlockRegistry.ANDESITE_STONE_BRICKS_COBBLESTONE,
			BlockRegistry.STAIRS_ANDESITE_STONE_COBBLESTONE, BlockRegistry.ANDESITE_STONE_BRICKS,
			BlockRegistry.ANDESITE_STONE_WALL, BlockRegistry.ANDESITE_STONE_BRICKS_COBBLESTONE);

	public static final Theme NETHER = new Theme(null, BlockRegistry.NETHERRACK_NETHERBRICK,
			BlockRegistry.NETHERRACK_NETHERBRICK_SOULSAND, BlockRegistry.NETHER_BRICK_STAIRS,
			BlockRegistry.NETHERRACK_NETHERBRICK, BlockRegistry.NETHER_WALL, BlockRegistry.NETHERRACK_NETHERBRICK);

	public static final Theme DESERT = new Theme(BlockRegistry.SANDSTONE_DEFAULT_CHSELED_SMOOTH,
			BlockRegistry.SANDSTONE_DEFAULT_CHSELED_SMOOTH, BlockRegistry.SANDSTONE_DEFAULT_SMOOTH_SAND,
			BlockRegistry.STAIRS_SANDSTONE_DEFAULT_SMOOTH, BlockRegistry.SANDSTONE_DEFAULT_CHSELED_SMOOTH,
			() -> Blocks.SANDSTONE_WALL.getDefaultState(), BlockRegistry.SANDSTONE_DEFAULT_CHSELED_SMOOTH);

	public static final Theme BADLANDS = new Theme(BlockRegistry.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH,
			BlockRegistry.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH, BlockRegistry.RED_SANDSTONE_DEFAULT_SMOOTH_RED_SAND,
			BlockRegistry.STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH, BlockRegistry.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH,
			() -> Blocks.RED_SANDSTONE_WALL.getDefaultState(), BlockRegistry.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH);

	public static final Theme ICE = new Theme(BlockRegistry.ICE_DEFAULT_PACKED, BlockRegistry.ICE_DEFAULT_PACKED,
			() -> Blocks.ICE.getDefaultState(), BlockRegistry.ICE_DEFAULT_PACKED, BlockRegistry.ICE_DEFAULT_PACKED,
			() -> Blocks.CAVE_AIR.getDefaultState(), BlockRegistry.ICE_DEFAULT_PACKED);

	public static final Theme MOSS = new Theme(BlockRegistry.MOSS, BlockRegistry.MOSS, BlockRegistry.MOSS_FLOOR,
			BlockRegistry.MOSS_STAIRS, BlockRegistry.MOSS, BlockRegistry.MOSS_WALL, BlockRegistry.MOSS);

	// | ************************** |
	// | - - - - Sub-Themes - - - - |
	// | ************************** |

	public static final SubTheme ACACIA = new SubTheme(() -> Blocks.ACACIA_LOG.getDefaultState(),
			() -> Blocks.ACACIA_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(),
			() -> Blocks.ACACIA_DOOR.getDefaultState(), () -> Blocks.ACACIA_PLANKS.getDefaultState());

	public static final SubTheme OAK = new SubTheme(() -> Blocks.OAK_LOG.getDefaultState(),
			() -> Blocks.OAK_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(),
			() -> Blocks.OAK_DOOR.getDefaultState(), () -> Blocks.OAK_PLANKS.getDefaultState());

	public static final SubTheme BIRCH = new SubTheme(() -> Blocks.BIRCH_LOG.getDefaultState(),
			() -> Blocks.BIRCH_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(),
			() -> Blocks.BIRCH_DOOR.getDefaultState(), () -> Blocks.BIRCH_PLANKS.getDefaultState());

	public static final SubTheme DARK_OAK = new SubTheme(() -> Blocks.DARK_OAK_LOG.getDefaultState(),
			() -> Blocks.DARK_OAK_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(),
			() -> Blocks.DARK_OAK_DOOR.getDefaultState(), () -> Blocks.DARK_OAK_PLANKS.getDefaultState());

	public static final SubTheme JUNGLE = new SubTheme(() -> Blocks.JUNGLE_LOG.getDefaultState(),
			() -> Blocks.JUNGLE_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(),
			() -> Blocks.JUNGLE_DOOR.getDefaultState(), () -> Blocks.JUNGLE_PLANKS.getDefaultState());

	public static final SubTheme SPRUCE = new SubTheme(() -> Blocks.SPRUCE_LOG.getDefaultState(),
			() -> Blocks.SPRUCE_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(),
			() -> Blocks.SPRUCE_DOOR.getDefaultState(), () -> Blocks.SPRUCE_PLANKS.getDefaultState());

	private static final ThemeRandomizer DEFAULT_RANDOMIZER = (rand, base) -> base;

	static {

		// [Base Themes]

		BIOME_TO_THEME_MAP = new HashMap<String, Integer>();

		BIOME_TO_THEME_MAP.put("minecraft:swamp", 2);
		BIOME_TO_THEME_MAP.put("minecraft:swamp_hills", 2);

		BIOME_TO_THEME_MAP.put("minecraft:ocean", 3);
		BIOME_TO_THEME_MAP.put("minecraft:deep_ocean", 3);
		BIOME_TO_THEME_MAP.put("minecraft:cold_ocean", 3);
		BIOME_TO_THEME_MAP.put("minecraft:deep_cold_ocean", 3);
		BIOME_TO_THEME_MAP.put("minecraft:lukewarm_ocean", 3);
		BIOME_TO_THEME_MAP.put("minecraft:deep_lukewarm_ocean", 3);
		BIOME_TO_THEME_MAP.put("minecraft:warm_ocean", 3);
		BIOME_TO_THEME_MAP.put("minecraft:deep_warm_ocean", 3);

		BIOME_TO_THEME_MAP.put("minecraft:frozen_ocean", 4);
		BIOME_TO_THEME_MAP.put("mineraft:deep_frozen_ocean", 4);

		BIOME_TO_THEME_MAP.put("minecraft:desert", 16);
		BIOME_TO_THEME_MAP.put("minecraft:desert_hills", 16);
		BIOME_TO_THEME_MAP.put("minecraft:desert_lakes", 16);

		BIOME_TO_THEME_MAP.put("minecraft:badlands", 17);
		BIOME_TO_THEME_MAP.put("minecraft:wooded_badlands_plateau", 17);
		BIOME_TO_THEME_MAP.put("minecraft:badlands_plateau", 17);
		BIOME_TO_THEME_MAP.put("minecraft:eroded_badlands", 17);
		BIOME_TO_THEME_MAP.put("minecraft:modified_wooded_badlands_plateau", 17);
		BIOME_TO_THEME_MAP.put("minecraft:modified_badlands_plateau", 17);

		BIOME_TO_THEME_MAP.put("minecraft:ice_spikes", 32);

		ID_TO_THEME_MAP = new HashMap<Integer, Theme>();

		ID_TO_THEME_MAP.put(-1, TEST);
		ID_TO_THEME_MAP.put(0, DEFAULT);
		ID_TO_THEME_MAP.put(1, NETHER);
		ID_TO_THEME_MAP.put(2, OCEAN);

		ID_TO_THEME_MAP.put(16, DESERT);
		ID_TO_THEME_MAP.put(17, BADLANDS);

		ID_TO_THEME_MAP.put(32, ICE);

		ID_TO_THEME_MAP.put(48, BRICKS);
		ID_TO_THEME_MAP.put(49, ANDESITE);
		
		ID_TO_THEME_MAP.put(80, MOSS);

		// [Sub-Themes]

		BIOME_TO_SUBTHEME_MAP = new HashMap<String, Integer>();

		BIOME_TO_SUBTHEME_MAP.put("minecraft:jungle", 1);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:jungle_edge", 1);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:jungle_hills", 1);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:modified_jungle", 1);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:modified_jungle_edge", 1);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:bamboo_jungle", 1);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:bamboo_jungle_hills", 1);

		BIOME_TO_SUBTHEME_MAP.put("minecraft:birch_forest", 2);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:birch_forest_hills", 2);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:tall_birch_forest", 2);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:tall_birch_hills", 2);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:flower_forest", 2);

		BIOME_TO_SUBTHEME_MAP.put("minecraft:savanna", 3);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:savanna_plateau", 3);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:shattered_savanna", 3);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:shattered_savanna_plateau", 3);

		BIOME_TO_SUBTHEME_MAP.put("minecraft:dark_forest", 4);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:dark_forest_hills", 4);

		BIOME_TO_SUBTHEME_MAP.put("minecraft:taiga", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:snowy_tundra", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:taiga_hills", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:snowy_taiga", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:snowy_taga_hills", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:giant_tree_taiga", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:giant_tree_taiga_hills", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:taiga_mountains", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:snowy_taiga_mountains", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:giant_spruce_taiga", 5);
		BIOME_TO_SUBTHEME_MAP.put("minecraft:giant_spruce_taiga_hills", 5);

		ID_TO_SUBTHEME_MAP = new HashMap<Integer, SubTheme>();

		ID_TO_SUBTHEME_MAP.put(0, OAK);
		ID_TO_SUBTHEME_MAP.put(1, JUNGLE);
		ID_TO_SUBTHEME_MAP.put(2, BIRCH);
		ID_TO_SUBTHEME_MAP.put(3, ACACIA);
		ID_TO_SUBTHEME_MAP.put(4, DARK_OAK);
		ID_TO_SUBTHEME_MAP.put(5, SPRUCE);

		RANDOMIZERS = new HashMap<Integer, ThemeRandomizer>();

		RANDOMIZERS.put(0, createRandomizer(0, 48, 49));

	}

	public final IBlockStateProvider ceiling, wall, floor, stairs, material, vanillaWall, column;

	public Theme(IBlockStateProvider ceiling, IBlockStateProvider wall, IBlockStateProvider floor,
			IBlockStateProvider stairs, IBlockStateProvider material, IBlockStateProvider vanillaWall,
			IBlockStateProvider column) {
		this.ceiling = ceiling;
		this.wall = wall;
		this.floor = floor;
		this.stairs = stairs;
		this.material = material;
		this.vanillaWall = vanillaWall;
		this.column = column;
	}

	public Theme(BlockState ceiling, BlockState wall, BlockState floor, BlockState stairs, BlockState material,
			BlockState vanillaWall, BlockState column) {
		this.ceiling = () -> ceiling;
		this.wall = () -> wall;
		this.floor = () -> floor;
		this.stairs = () -> stairs;
		this.material = () -> material;
		this.vanillaWall = () -> vanillaWall;
		this.column = () -> column;
	}
	
	public Theme(Block ceiling, Block wall, Block floor, Block stairs, Block material,
			Block vanillaWall, Block column) {
		this.ceiling = () -> ceiling.getDefaultState();
		this.wall = () -> wall.getDefaultState();
		this.floor = () -> floor.getDefaultState();
		this.stairs = () -> stairs.getDefaultState();
		this.material = () -> material.getDefaultState();
		this.vanillaWall = () -> vanillaWall.getDefaultState();
		this.column = () -> column.getDefaultState();
	}

	public static class SubTheme {

		public final IBlockStateProvider wallLog, trapDoor, torchDark, door, material;

		public SubTheme(IBlockStateProvider wallLog, IBlockStateProvider trapDoor, IBlockStateProvider torchDark,
				IBlockStateProvider door, IBlockStateProvider material) {
			this.wallLog = wallLog;
			this.trapDoor = trapDoor;
			this.torchDark = torchDark;
			this.door = door;
			this.material = material;
		}
		
		public SubTheme(BlockState wallLog, BlockState trapDoor, BlockState torchDark,
				BlockState door, BlockState material) {
			this.wallLog = () -> wallLog;
			this.trapDoor = () -> trapDoor;
			this.torchDark = () -> torchDark;
			this.door = () -> door;
			this.material = ()-> material;
		}
		
		public SubTheme(Block wallLog, Block trapDoor, Block torchDark,
				Block door, Block material) {
			this.wallLog = () -> wallLog.getDefaultState();
			this.trapDoor = () -> trapDoor.getDefaultState();
			this.torchDark = () -> torchDark.getDefaultState();
			this.door = () -> door.getDefaultState();
			this.material = ()-> material.getDefaultState();
		}

	}

	public static int getTheme(String biome) {
		int theme = BIOME_TO_THEME_MAP.getOrDefault(biome, 0);
		return RANDOMIZERS.getOrDefault(theme, DEFAULT_RANDOMIZER).randomize(RANDOM, theme);
	}

	public static int getSubTheme(String biome) {
		return BIOME_TO_SUBTHEME_MAP.getOrDefault(biome, 0);
	}

	public static Theme get(int theme) {
		return ID_TO_THEME_MAP.getOrDefault(theme, DEFAULT);
	}

	public static SubTheme getSub(int id) {
		return ID_TO_SUBTHEME_MAP.getOrDefault(id, OAK);
	}

	public static ThemeRandomizer createRandomizer(int... themes) {
		return (rand, base) -> themes[rand.nextInt(themes.length)];

	}

	@FunctionalInterface
	public static interface ThemeRandomizer {

		int randomize(Random rand, int base);

	}

}
