package xiroc.dungeoncrawl.theme;

import java.util.HashMap;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class Theme {

	// THEME ID
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

	public static HashMap<String, Integer> BIOME_TO_THEME_MAP;
	public static HashMap<Integer, Theme> ID_TO_THEME_MAP;

	public static final Theme TEST = new Theme(() -> BlockRegistry.STONE_BRICKS, () -> BlockRegistry.STONE_BRICKS,
			() -> BlockRegistry.GRAVEL, () -> Blocks.OAK_STAIRS.getDefaultState(),
			() -> BlockRegistry.STAIRS_STONE_BRICKS, () -> Blocks.OAK_STAIRS.getDefaultState(),
			() -> BlockRegistry.OAK_LOG, () -> Blocks.OAK_TRAPDOOR.getDefaultState(),
			() -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(), () -> Blocks.OAK_DOOR.getDefaultState(),
			() -> Blocks.OAK_PLANKS.getDefaultState(), () -> Blocks.STONE_BRICK_WALL.getDefaultState(), null);

	public static final Theme DEFAULT = new Theme(() -> BlockRegistry.STONE_BRICKS,
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			() -> Blocks.OAK_STAIRS.getDefaultState(), BlockRegistry.STAIRS_STONE_COBBLESTONE,
			() -> Blocks.OAK_STAIRS.getDefaultState(), () -> BlockRegistry.OAK_LOG,
			() -> Blocks.OAK_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(),
			() -> Blocks.OAK_DOOR.getDefaultState(), () -> Blocks.OAK_PLANKS.getDefaultState(),
			BlockRegistry.STONE_WALL, null);

	public static final Theme NETHER = new Theme(() -> BlockRegistry.STONE_BRICKS, BlockRegistry.NETHERRACK_NETHERBRICK,
			BlockRegistry.NETHERRACK_NETHERBRICK_SOULSAND, BlockRegistry.NETHER_BRICK_STAIRS,
			BlockRegistry.NETHER_BRICK_STAIRS, BlockRegistry.NETHER_BRICK_STAIRS,
			() -> Blocks.QUARTZ_PILLAR.getDefaultState(), () -> Blocks.IRON_TRAPDOOR.getDefaultState(),
			() -> Blocks.REDSTONE_WALL_TORCH.getDefaultState(), () -> Blocks.IRON_DOOR.getDefaultState(),
			() -> Blocks.NETHERRACK.getDefaultState(), BlockRegistry.NETHER_WALL,
			() -> Blocks.NETHER_BRICKS.getDefaultState());

	public static final Theme SWAMP = new Theme(() -> BlockRegistry.STONE_BRICKS,
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			() -> Blocks.OAK_STAIRS.getDefaultState(), () -> Blocks.OAK_STAIRS.getDefaultState(),
			() -> Blocks.OAK_STAIRS.getDefaultState(), () -> Blocks.OAK_LOG.getDefaultState(),
			() -> Blocks.OAK_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_TORCH.getDefaultState(),
			() -> Blocks.OAK_DOOR.getDefaultState(), () -> Blocks.OAK_PLANKS.getDefaultState(),
			BlockRegistry.STONE_WALL, null);

	public static final Theme OCEAN = new Theme(Blocks.PRISMARINE.getDefaultState(),
			Blocks.PRISMARINE_BRICKS.getDefaultState(), Blocks.DARK_PRISMARINE.getDefaultState(),
			Blocks.DARK_PRISMARINE_STAIRS.getDefaultState(), Blocks.PRISMARINE_BRICK_STAIRS.getDefaultState(),
			Blocks.PRISMARINE_BRICK_STAIRS.getDefaultState(), Blocks.DARK_PRISMARINE.getDefaultState(),
			Blocks.CAVE_AIR.getDefaultState(), Blocks.CAVE_AIR.getDefaultState(), Blocks.JUNGLE_DOOR.getDefaultState(),
			Blocks.PRISMARINE.getDefaultState(), Blocks.PRISMARINE_WALL.getDefaultState(),
			Blocks.DARK_PRISMARINE.getDefaultState());

	public static final Theme FROZEN_OCEAN = OCEAN;

	public static final Theme JUNGLE = new Theme(() -> Blocks.STONE_BRICKS.getDefaultState(),
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			() -> Blocks.JUNGLE_STAIRS.getDefaultState(), BlockRegistry.STAIRS_STONE_COBBLESTONE,
			() -> Blocks.JUNGLE_STAIRS.getDefaultState(), () -> Blocks.JUNGLE_LOG.getDefaultState(),
			() -> Blocks.JUNGLE_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_TORCH.getDefaultState(),
			() -> Blocks.JUNGLE_DOOR.getDefaultState(), () -> Blocks.JUNGLE_PLANKS.getDefaultState(),
			BlockRegistry.STONE_WALL, () -> Blocks.JUNGLE_LOG.getDefaultState());

	public static final Theme BIRCH_FOREST = new Theme(() -> Blocks.STONE_BRICKS.getDefaultState(),
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			() -> Blocks.BIRCH_STAIRS.getDefaultState(), BlockRegistry.STAIRS_STONE_COBBLESTONE,
			() -> Blocks.BIRCH_STAIRS.getDefaultState(), () -> Blocks.BIRCH_LOG.getDefaultState(),
			() -> Blocks.BIRCH_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_TORCH.getDefaultState(),
			() -> Blocks.BIRCH_DOOR.getDefaultState(), () -> Blocks.BIRCH_PLANKS.getDefaultState(),
			BlockRegistry.STONE_WALL, null);

	public static final Theme SAVANNA = new Theme(() -> Blocks.STONE_BRICKS.getDefaultState(),
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			() -> Blocks.ACACIA_STAIRS.getDefaultState(), BlockRegistry.STAIRS_STONE_COBBLESTONE,
			() -> Blocks.ACACIA_STAIRS.getDefaultState(), () -> Blocks.ACACIA_LOG.getDefaultState(),
			() -> Blocks.ACACIA_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_TORCH.getDefaultState(),
			() -> Blocks.ACACIA_DOOR.getDefaultState(), () -> Blocks.ACACIA_PLANKS.getDefaultState(),
			BlockRegistry.STONE_WALL, null);

	public static final Theme OAK_FOREST = new Theme(() -> Blocks.STONE_BRICKS.getDefaultState(),
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			() -> Blocks.OAK_STAIRS.getDefaultState(), BlockRegistry.STAIRS_STONE_COBBLESTONE,
			() -> Blocks.OAK_STAIRS.getDefaultState(), () -> Blocks.OAK_LOG.getDefaultState(),
			() -> Blocks.OAK_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_TORCH.getDefaultState(),
			() -> Blocks.OAK_DOOR.getDefaultState(), () -> Blocks.OAK_PLANKS.getDefaultState(),
			BlockRegistry.STONE_WALL, null);

	public static final Theme DARK_OAK_FOREST = new Theme(() -> Blocks.STONE_BRICKS.getDefaultState(),
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			() -> Blocks.DARK_OAK_STAIRS.getDefaultState(), BlockRegistry.STAIRS_STONE_COBBLESTONE,
			() -> Blocks.DARK_OAK_STAIRS.getDefaultState(), () -> Blocks.DARK_OAK_LOG.getDefaultState(),
			() -> Blocks.DARK_OAK_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_TORCH.getDefaultState(),
			() -> Blocks.DARK_OAK_DOOR.getDefaultState(), () -> Blocks.DARK_OAK_PLANKS.getDefaultState(),
			BlockRegistry.STONE_WALL, null);

	public static final Theme TAIGA = new Theme(() -> Blocks.STONE_BRICKS.getDefaultState(),
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			() -> Blocks.SPRUCE_STAIRS.getDefaultState(), BlockRegistry.STAIRS_STONE_COBBLESTONE,
			() -> Blocks.SPRUCE_STAIRS.getDefaultState(), () -> Blocks.SPRUCE_LOG.getDefaultState(),
			() -> Blocks.SPRUCE_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_TORCH.getDefaultState(),
			() -> Blocks.SPRUCE_DOOR.getDefaultState(), () -> Blocks.SPRUCE_PLANKS.getDefaultState(),
			BlockRegistry.STONE_WALL, null);

	public static final Theme DESERT = new Theme(BlockRegistry.SANDSTONE_DEFAULT_CHSELED_SMOOTH,
			BlockRegistry.SANDSTONE_DEFAULT_CHSELED_SMOOTH, BlockRegistry.SANDSTONE_DEFAULT_SMOOTH_SAND,
			BlockRegistry.STAIRS_SANDSTONE_DEFAULT_SMOOTH, BlockRegistry.STAIRS_SANDSTONE_DEFAULT_SMOOTH,
			BlockRegistry.STAIRS_SANDSTONE_DEFAULT_SMOOTH, () -> Blocks.CHISELED_SANDSTONE.getDefaultState(),
			() -> Blocks.CAVE_AIR.getDefaultState(), () -> Blocks.REDSTONE_TORCH.getDefaultState(),
			() -> Blocks.OAK_DOOR.getDefaultState(), BlockRegistry.SANDSTONE_DEFAULT_CHSELED_SMOOTH,
			() -> Blocks.SANDSTONE_WALL.getDefaultState(), BlockRegistry.SANDSTONE_DEFAULT_CHSELED_SMOOTH);

	public static final Theme BADLANDS = new Theme(BlockRegistry.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH,
			BlockRegistry.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH, BlockRegistry.RED_SANDSTONE_DEFAULT_SMOOTH_RED_SAND,
			BlockRegistry.STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH, BlockRegistry.STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH,
			BlockRegistry.STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH, () -> Blocks.CHISELED_RED_SANDSTONE.getDefaultState(),
			() -> Blocks.CAVE_AIR.getDefaultState(), () -> Blocks.REDSTONE_TORCH.getDefaultState(),
			() -> Blocks.OAK_DOOR.getDefaultState(), BlockRegistry.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH,
			() -> Blocks.RED_SANDSTONE_WALL.getDefaultState(), BlockRegistry.RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH);

	public static final Theme ICE = new Theme(BlockRegistry.ICE_DEFAULT_PACKED, BlockRegistry.ICE_DEFAULT_PACKED,
			() -> Blocks.ICE.getDefaultState(), BlockRegistry.STAIRS_STONE_COBBLESTONE,
			BlockRegistry.ICE_DEFAULT_PACKED, BlockRegistry.ICE_DEFAULT_PACKED,
			() -> Blocks.PACKED_ICE.getDefaultState(), () -> Blocks.CAVE_AIR.getDefaultState(),
			() -> Blocks.CAVE_AIR.getDefaultState(), () -> Blocks.OAK_DOOR.getDefaultState(),
			BlockRegistry.ICE_DEFAULT_PACKED, BlockRegistry.ICE_DEFAULT_PACKED, BlockRegistry.ICE_DEFAULT_PACKED);

	public static final BuildTheme BUILD_STONE = new BuildTheme(
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE,
			BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE,
			BlockRegistry.STAIRS_STONE_COBBLESTONE);

	public static final BuildTheme BUILD_BRICKS = new BuildTheme(() -> Blocks.BRICK_WALL.getDefaultState(),
			() -> Blocks.BRICK_WALL.getDefaultState(), () -> Blocks.BRICKS.getDefaultState(),
			() -> Blocks.BRICK_STAIRS.getDefaultState());

	static {
		BIOME_TO_THEME_MAP = new HashMap<String, Integer>();

		// BIOME_TO_THEME_MAP.put("minecraft:plains", 0);
		// BIOME_TO_THEME_MAP.put("minecraft:forest", 0);
		// BIOME_TO_THEME_MAP.put("minecraft:river", 0);
		// BIOME_TO_THEME_MAP.put("minecraft:frozen_river", 0);
		// BIOME_TO_THEME_MAP.put("minecraft:sunflower_plains", 0);
		// BIOME_TO_THEME_MAP.put("minecraft:mountains", 0);
		// BIOME_TO_THEME_MAP.put("minecraft:gravelly_mountains", 0);
		// BIOME_TO_THEME_MAP.put("minecraft:snowy_mountains", 0);
		// BIOME_TO_THEME_MAP.put("minecraft:beach", 0);
		// BIOME_TO_THEME_MAP.put("minecraft:wooded_hills", 0);

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

		BIOME_TO_THEME_MAP.put("minecraft:jungle", 5);
		BIOME_TO_THEME_MAP.put("minecraft:jungle_edge", 5);
		BIOME_TO_THEME_MAP.put("minecraft:jungle_hills", 5);
		BIOME_TO_THEME_MAP.put("minecraft:modified_jungle", 5);
		BIOME_TO_THEME_MAP.put("minecraft:modified_jungle_edge", 5);
		BIOME_TO_THEME_MAP.put("minecraft:bamboo_jungle", 5);
		BIOME_TO_THEME_MAP.put("minecraft:bamboo_jungle_hills", 5);

		BIOME_TO_THEME_MAP.put("minecraft:birch_forest", 6);
		BIOME_TO_THEME_MAP.put("minecraft:birch_forest_hills", 6);
		BIOME_TO_THEME_MAP.put("minecraft:tall_birch_forest", 6);
		BIOME_TO_THEME_MAP.put("minecraft:tall_birch_hills", 6);
		BIOME_TO_THEME_MAP.put("minecraft:flower_forest", 6);

		BIOME_TO_THEME_MAP.put("minecraft:savanna", 7);
		BIOME_TO_THEME_MAP.put("minecraft:savanna_plateau", 7);
		BIOME_TO_THEME_MAP.put("minecraft:shattered_savanna", 7);
		BIOME_TO_THEME_MAP.put("minecraft:shattered_savanna_plateau", 7);

		BIOME_TO_THEME_MAP.put("minecraft:dark_forest", 9);
		BIOME_TO_THEME_MAP.put("minecraft:dark_forest_hills", 9);

		BIOME_TO_THEME_MAP.put("minecraft:taiga", 10);
		BIOME_TO_THEME_MAP.put("minecraft:snowy_tundra", 10);
		BIOME_TO_THEME_MAP.put("minecraft:taiga_hills", 10);
		BIOME_TO_THEME_MAP.put("minecraft:snowy_taiga", 10);
		BIOME_TO_THEME_MAP.put("minecraft:snowy_taga_hills", 10);
		BIOME_TO_THEME_MAP.put("minecraft:giant_tree_taiga", 10);
		BIOME_TO_THEME_MAP.put("minecraft:giant_tree_taiga_hills", 10);
		BIOME_TO_THEME_MAP.put("minecraft:taiga_mountains", 10);
		BIOME_TO_THEME_MAP.put("minecraft:snowy_taiga_mountains", 10);
		BIOME_TO_THEME_MAP.put("minecraft:giant_spruce_taiga", 10);
		BIOME_TO_THEME_MAP.put("minecraft:giant_spruce_taiga_hills", 10);

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
		ID_TO_THEME_MAP.put(2, SWAMP);
		ID_TO_THEME_MAP.put(3, OCEAN);
		ID_TO_THEME_MAP.put(4, OCEAN); // Frozen Ocean?
		ID_TO_THEME_MAP.put(5, JUNGLE);
		ID_TO_THEME_MAP.put(6, BIRCH_FOREST);
		ID_TO_THEME_MAP.put(7, SAVANNA);
		ID_TO_THEME_MAP.put(8, OAK_FOREST);
		ID_TO_THEME_MAP.put(9, DARK_OAK_FOREST);
		ID_TO_THEME_MAP.put(10, TAIGA);

		ID_TO_THEME_MAP.put(16, DESERT);
		ID_TO_THEME_MAP.put(17, BADLANDS);

		ID_TO_THEME_MAP.put(32, ICE);

	}

	public final IBlockStateProvider ceiling, wall, wallLog, floor, stairs, ceilingStairs, floorStairs,
			trapDoorDecoration, torchDark, door, material, vanillaWall, column;

	public Theme(IBlockStateProvider ceiling, IBlockStateProvider wall, IBlockStateProvider floor,
			IBlockStateProvider stairs, IBlockStateProvider ceilingStairs, IBlockStateProvider floorStairs,
			IBlockStateProvider wallLog, IBlockStateProvider trapDoorDecoration, IBlockStateProvider torchDark,
			IBlockStateProvider door, IBlockStateProvider material, IBlockStateProvider vanillaWall,
			IBlockStateProvider column) {
		this.ceiling = ceiling;
		this.wall = wall;
		this.wallLog = wallLog;
		this.floor = floor;
		this.stairs = stairs;
		this.ceilingStairs = ceilingStairs;
		this.floorStairs = floorStairs;
		this.trapDoorDecoration = trapDoorDecoration;
		this.torchDark = torchDark;
		this.door = door;
		this.material = material;
		this.vanillaWall = vanillaWall;
		this.column = column;
	}

	public Theme(BlockState ceiling, BlockState wall, BlockState floor, BlockState stairs, BlockState ceilingStairs,
			BlockState floorStairs, BlockState wallLog, BlockState trapDoorDecoration, BlockState torchDark,
			BlockState door, BlockState material, BlockState vanillaWall, BlockState column) {
		this.ceiling = () -> ceiling;
		this.wall = () -> wall;
		this.wallLog = () -> wallLog;
		this.floor = () -> floor;
		this.stairs = () -> stairs;
		this.ceilingStairs = () -> ceilingStairs;
		this.floorStairs = () -> floorStairs;
		this.trapDoorDecoration = () -> trapDoorDecoration;
		this.torchDark = () -> torchDark;
		this.door = () -> door;
		this.material = () -> material;
		this.vanillaWall = () -> vanillaWall;
		this.column = () -> column;
	}

	public static Theme get(int theme) {
		return ID_TO_THEME_MAP.getOrDefault(theme, DEFAULT);
	}

	public static class BiomeTheme {

		public IBlockStateProvider wallLog, stairs, floorStairs, trapDoorDecoration, torchDark, door, material, column;

		public BiomeTheme(IBlockStateProvider wallLog, IBlockStateProvider stairs, IBlockStateProvider floorStairs,
				IBlockStateProvider trapDoor, IBlockStateProvider torchDark, IBlockStateProvider door,
				IBlockStateProvider material, IBlockStateProvider column) {
			this.wallLog = wallLog;
			this.stairs = stairs;
			this.floorStairs = floorStairs;
			this.trapDoorDecoration = trapDoor;
			this.torchDark = torchDark;
			this.door = door;
			this.material = material;
			this.column = column;
		}

	}

	public static class BuildTheme {

		public IBlockStateProvider ceiling, wall, floor, ceilingStairs;

		public BuildTheme(IBlockStateProvider ceiling, IBlockStateProvider wall, IBlockStateProvider floor,
				IBlockStateProvider ceilingStairs) {
			this.ceiling = ceiling;
			this.wall = wall;
			this.floor = floor;
			this.ceilingStairs = ceilingStairs;
		}

	}

}
