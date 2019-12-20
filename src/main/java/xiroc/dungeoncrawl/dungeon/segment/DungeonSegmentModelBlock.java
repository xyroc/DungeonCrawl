package xiroc.dungeoncrawl.dungeon.segment;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.HashMap;
import java.util.Locale;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;

public class DungeonSegmentModelBlock {

	public static HashMap<DungeonSegmentModelBlockType, DungeonSegmentBlockStateProvider> PROVIDERS = new HashMap<DungeonSegmentModelBlockType, DungeonSegmentBlockStateProvider>();

	private static final HashMap<String, PropertyLoader> LOADERS;

	static {
		LOADERS = new HashMap<String, PropertyLoader>();

		LOADERS.put("facing", (block, nbt) -> block.facing = read(Direction.class, "facing", nbt, Direction::byName));
		LOADERS.put("axis",
				(block, nbt) -> block.axis = read(Direction.Axis.class, "axis", nbt, Direction.Axis::byName));

		LOADERS.put("upsideDown", (block, nbt) -> block.upsideDown = nbt.getBoolean("upsideDown"));
		LOADERS.put("north", (block, nbt) -> block.north = nbt.getBoolean("north"));
		LOADERS.put("east", (block, nbt) -> block.east = nbt.getBoolean("east"));
		LOADERS.put("south", (block, nbt) -> block.south = nbt.getBoolean("south"));
		LOADERS.put("west", (block, nbt) -> block.west = nbt.getBoolean("west"));
		LOADERS.put("waterlogged", (block, nbt) -> block.waterlogged = nbt.getBoolean("waterlogged"));
		LOADERS.put("lit", (block, nbt) -> block.lit = nbt.getBoolean("lit"));
		LOADERS.put("open", (block, nbt) -> block.open = nbt.getBoolean("open"));
		LOADERS.put("disarmed", (block, nbt) -> block.disarmed = nbt.getBoolean("disarmed"));
		LOADERS.put("attached", (block, nbt) -> block.attached = nbt.getBoolean("attached"));

		LOADERS.put("half", (block, nbt) -> block.half = read(Half.class, "half", nbt, Half::valueOf));
		LOADERS.put("doubleBlockHalf", (block, nbt) -> block.doubleBlockHalf = read(DoubleBlockHalf.class,
				"doubleBlockHalf", nbt, DoubleBlockHalf::valueOf));
		LOADERS.put("attachFace",
				(block, nbt) -> block.attachFace = read(AttachFace.class, "attachFace", nbt, AttachFace::valueOf));
		LOADERS.put("bedPart", (block, nbt) -> block.bedPart = read(BedPart.class, "bedPart", nbt, BedPart::valueOf));
		LOADERS.put("doorHinge",
				(block, nbt) -> block.hinge = read(DoorHingeSide.class, "doorHinge", nbt, DoorHingeSide::valueOf));
	}

	public DungeonSegmentModelBlockType type;

	public Direction facing;
	public Direction.Axis axis;
	public Boolean upsideDown, north, east, south, west, waterlogged, lit, open, disarmed, attached;

	public ResourceLocation registryName;
	public String resourceName;
	public Half half;
	public DoubleBlockHalf doubleBlockHalf;
	public AttachFace attachFace;
	public BedPart bedPart;
	public DoorHingeSide hinge;

	public DungeonSegmentModelBlock(DungeonSegmentModelBlockType type) {
		this.type = type;
	}

	/**
	 * A method to transform a DungeonSegmentModelBlock into a CompoundNBT for the
	 * new model type.
	 * 
	 * @return The CompoundNBT
	 */
	public CompoundNBT getAsNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putString("type", type.toString());
		ListNBT properties = new ListNBT();

		if (resourceName != null)
			tag.putString("resourceName", resourceName);

		add(tag, properties, facing, "facing");
		add(tag, properties, axis, "axis");

		addBoolean(tag, properties, upsideDown, "upsideDown");
		addBoolean(tag, properties, north, "north");
		addBoolean(tag, properties, east, "east");
		addBoolean(tag, properties, south, "south");
		addBoolean(tag, properties, west, "west");
		addBoolean(tag, properties, waterlogged, "waterlogged");
		addBoolean(tag, properties, lit, "lit");
		addBoolean(tag, properties, open, "open");
		addBoolean(tag, properties, disarmed, "disarmed");
		addBoolean(tag, properties, attached, "attached");

		add(tag, properties, half, "half");
		add(tag, properties, doubleBlockHalf, "doubleBlockHalf");
		add(tag, properties, attachFace, "attachFace");
		add(tag, properties, bedPart, "bedPart");
		add(tag, properties, hinge, "doorHinge");

		if (properties.size() > 0)
			tag.put("properties", properties);

		return tag;
	}

	public static DungeonSegmentModelBlock fromNBT(CompoundNBT nbt) {
		if (!nbt.contains("type"))
			return null;
		DungeonSegmentModelBlock block = new DungeonSegmentModelBlock(
				DungeonSegmentModelBlockType.valueOf(nbt.getString("type")));

		if (nbt.contains("resourceName"))
			block.resourceName = nbt.getString("resourceName");

		if (nbt.contains("properties")) {
			ListNBT properties = nbt.getList("properties", 8);

			for (int i = 0; i < properties.size(); i++)
				LOADERS.get(properties.getString(i)).load(block, nbt);
		}
		return block;
	}

	private void add(CompoundNBT tag, ListNBT properties, @Nullable Object object, String name) {
		if (object != null) {
			tag.putString(name, object.toString());
			properties.add(new StringNBT(name));
		}
	}

	private void addBoolean(CompoundNBT tag, ListNBT properties, @Nullable Boolean bool, String name) {
		if (bool != null) {
			tag.putBoolean(name, bool);
			properties.add(new StringNBT(name));
		}
	}

	private static <T> T read(Class<T> c, String name, CompoundNBT nbt, Function<String, T> f) {
		return f.apply(nbt.getString(name).toUpperCase(Locale.ROOT));
	}

	/**
	 * Loads all existing properties from the given BlockState.
	 */
	public DungeonSegmentModelBlock set(BlockState state) {
		if (state.has(BlockStateProperties.FACING))
			facing = state.get(BlockStateProperties.FACING);
		else if (state.has(BlockStateProperties.HORIZONTAL_FACING))
			facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
		else if (state.has(BlockStateProperties.AXIS))
			axis = state.get(BlockStateProperties.AXIS);
		else if (state.has(BlockStateProperties.HORIZONTAL_AXIS))
			axis = state.get(BlockStateProperties.HORIZONTAL_AXIS);
		if (state.has(BlockStateProperties.NORTH))
			north = state.get(BlockStateProperties.NORTH);
		if (state.has(BlockStateProperties.EAST))
			east = state.get(BlockStateProperties.EAST);
		if (state.has(BlockStateProperties.SOUTH))
			south = state.get(BlockStateProperties.SOUTH);
		if (state.has(BlockStateProperties.WEST))
			west = state.get(BlockStateProperties.WEST);
		if (state.has(BlockStateProperties.WATERLOGGED))
			waterlogged = state.get(BlockStateProperties.WATERLOGGED);
		if (state.has(BlockStateProperties.INVERTED))
			upsideDown = state.get(BlockStateProperties.INVERTED);
		if (state.has(BlockStateProperties.DISARMED))
			disarmed = state.get(BlockStateProperties.DISARMED);
		if (state.has(BlockStateProperties.ATTACHED))
			attached = state.get(BlockStateProperties.ATTACHED);
		if (state.has(BlockStateProperties.LIT))
			lit = state.get(BlockStateProperties.LIT);
		if (state.has(BlockStateProperties.OPEN))
			open = state.get(BlockStateProperties.OPEN);
		if (state.has(BlockStateProperties.HALF))
			half = state.get(BlockStateProperties.HALF);
		if (state.has(BlockStateProperties.DOUBLE_BLOCK_HALF))
			doubleBlockHalf = state.get(BlockStateProperties.DOUBLE_BLOCK_HALF);
		if (state.has(BlockStateProperties.FACE))
			attachFace = state.get(BlockStateProperties.FACE);
		if (state.has(BlockStateProperties.BED_PART))
			bedPart = state.get(BlockStateProperties.BED_PART);
		if (state.has(BlockStateProperties.DOOR_HINGE))
			hinge = state.get(BlockStateProperties.DOOR_HINGE);
		if (type == DungeonSegmentModelBlockType.OTHER)
			resourceName = state.getBlock().getRegistryName().toString();
		return this;
	}

	/**
	 * Applies all existing properties to the given BlockState. Hardcoded :D
	 */
	public BlockState create(BlockState state) {
		if (facing != null && state.has(BlockStateProperties.FACING))
			state = state.with(BlockStateProperties.FACING, facing);
		else if (facing != null && state.has(BlockStateProperties.HORIZONTAL_FACING))
			state = state.with(BlockStateProperties.HORIZONTAL_FACING, facing);
		else if (axis != null && state.has(BlockStateProperties.AXIS))
			state = state.with(BlockStateProperties.AXIS, axis);
		else if (axis != null && state.has(BlockStateProperties.HORIZONTAL_AXIS))
			state = state.with(BlockStateProperties.HORIZONTAL_AXIS, axis);
		if (north != null && state.has(BlockStateProperties.NORTH))
			state = state.with(BlockStateProperties.NORTH, north);
		if (east != null && state.has(BlockStateProperties.EAST))
			state = state.with(BlockStateProperties.EAST, east);
		if (south != null && state.has(BlockStateProperties.SOUTH))
			state = state.with(BlockStateProperties.SOUTH, south);
		if (west != null && state.has(BlockStateProperties.WEST))
			state = state.with(BlockStateProperties.WEST, west);
		if (waterlogged != null && state.has(BlockStateProperties.WATERLOGGED))
			state = state.with(BlockStateProperties.WATERLOGGED, waterlogged);
		if (upsideDown != null && state.has(BlockStateProperties.INVERTED))
			state = state.with(BlockStateProperties.INVERTED, upsideDown);
		if (disarmed != null && state.has(BlockStateProperties.DISARMED))
			state = state.with(BlockStateProperties.DISARMED, disarmed);
		if (attached != null && state.has(BlockStateProperties.ATTACHED))
			state = state.with(BlockStateProperties.ATTACHED, attached);
		if (lit != null && state.has(BlockStateProperties.LIT))
			state = state.with(BlockStateProperties.LIT, lit);
		if (open != null && state.has(BlockStateProperties.OPEN))
			state = state.with(BlockStateProperties.OPEN, open);
		if (half != null && state.has(BlockStateProperties.HALF))
			state = state.with(BlockStateProperties.HALF, half);
		if (attachFace != null && state.has(BlockStateProperties.FACE))
			state = state.with(BlockStateProperties.FACE, attachFace);
		if (doubleBlockHalf != null && state.has(BlockStateProperties.DOUBLE_BLOCK_HALF))
			state = state.with(BlockStateProperties.DOUBLE_BLOCK_HALF, doubleBlockHalf);
		if (bedPart != null && state.has(BlockStateProperties.BED_PART))
			state = state.with(BlockStateProperties.BED_PART, bedPart);
		if (hinge != null && state.has(BlockStateProperties.DOOR_HINGE))
			state = state.with(BlockStateProperties.DOOR_HINGE, hinge);
		return state;
	}

	/**
	 * Registers all BlockState providers.
	 */
	public static void load() {
		PROVIDERS.put(DungeonSegmentModelBlockType.NONE, (block, theme, subTheme, rand, stage) -> null);
		PROVIDERS.put(DungeonSegmentModelBlockType.BARREL, (block, theme, subTheme, rand, stage) -> Blocks.BARREL
				.getDefaultState().with(BarrelBlock.PROPERTY_FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.MATERIAL,
				(block, theme, subTheme, rand, stage) -> block.create(theme.material.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.CEILING, (block, theme, subTheme, rand, stage) -> theme.ceiling.get());
		PROVIDERS.put(DungeonSegmentModelBlockType.CEILING_STAIRS,
				(block, theme, subTheme, rand, stage) -> block.create(theme.stairs.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.CHEST,
				(block, theme, subTheme, rand, stage) -> Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.DISPENSER, (block, theme, subTheme, rand, stage) -> Blocks.DISPENSER
				.getDefaultState().with(DispenserBlock.FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.FLOOR, (block, theme, subTheme, rand, stage) -> theme.floor.get());
		PROVIDERS.put(DungeonSegmentModelBlockType.FLOOR_STAIRS,
				(block, theme, subTheme, rand, stage) -> block.create(theme.stairs.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, (block, theme, subTheme, rand, stage) -> {
			int i = rand.nextInt(10);
			if (i < 1 + stage)
				return BlockRegistry.SPAWNER;
			if (i == 5)
				return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing);
			return theme.floor.get();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_FLOOR_LAVA, (block, theme, subTheme, rand, stage) -> {
			switch (rand.nextInt(2)) {
			case 0:
				return theme.floor.get();
			case 1:
				return Blocks.LAVA.getDefaultState();
			default:
				return Blocks.LAVA.getDefaultState();
			}
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_FLOOR_WATER, (block, theme, subTheme, rand, stage) -> {
			switch (rand.nextInt(2)) {
			case 0:
				return theme.floor.get();
			case 1:
				return Blocks.WATER.getDefaultState();
			default:
				return Blocks.WATER.getDefaultState();
			}
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_WALL_AIR, (block, theme, subTheme, rand, stage) -> {
			if (rand.nextFloat() < 0.75)
				return theme.wall.get();
			return Blocks.CAVE_AIR.getDefaultState();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_WALL_SPAWNER, (block, theme, subTheme, rand, stage) -> {
			if (rand.nextInt(2 + (2 - stage)) == 0)
				return BlockRegistry.SPAWNER;
			return theme.wall.get();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_COBWEB_AIR, (block, theme, subTheme, rand, stage) -> {
			if (rand.nextInt(5) == 0)
				return Blocks.CAVE_AIR.getDefaultState();
			return Blocks.COBWEB.getDefaultState();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_BOOKSHELF_COBWEB, (block, theme, subTheme, rand, stage) -> {
			int roll = rand.nextInt(10);
			if (roll > 2)
				return Blocks.BOOKSHELF.getDefaultState();
			return Blocks.COBWEB.getDefaultState();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.STAIRS,
				(block, theme, subTheme, rand, stage) -> block.create(theme.stairs.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.TRAPDOOR,
				(block, theme, subTheme, rand, stage) -> block.create(subTheme.trapDoor.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.WALL, (block, theme, subTheme, rand, stage) -> theme.wall.get());
		PROVIDERS.put(DungeonSegmentModelBlockType.WALL_LOG,
				(block, theme, subTheme, rand, stage) -> block.create(subTheme.wallLog.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.DOOR, (block, theme, subTheme, rand, stage) -> block.create(subTheme.door.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.OTHER, (block, theme, subTheme, rand, stage) -> block
				.create(ForgeRegistries.BLOCKS.getValue(block.registryName).getDefaultState()));
	}

	public void readResourceLocation() {
		this.registryName = new ResourceLocation(resourceName);
	}

	/**
	 * Creates a BlockState from a DungeonSegmentModelBlock using a
	 * DungeonSegmentBlockStateProvider from the provider-map.
	 */
	public static BlockState getBlockState(DungeonSegmentModelBlock block, Theme theme, SubTheme subTheme, Random rand,
			int stage) {
		DungeonSegmentBlockStateProvider provider = PROVIDERS.get(block.type);
		if (provider == null)
			return Blocks.CAVE_AIR.getDefaultState();
		BlockState state = provider.get(block, theme, subTheme, rand, stage);
		if (state == null)
			return null;
		return state;
	}

	/**
	 * Creates a rotated BlockState from a DungeonSegmentModelBlock using a
	 * DungeonSegmentBlockStateProvider from the provider-map.
	 */
	public static BlockState getBlockState(DungeonSegmentModelBlock block, Theme theme, SubTheme subTheme, Random rand,
			int stage, Rotation rotation) {
		BlockState state = getBlockState(block, theme, subTheme, rand, stage);
		return state == null ? null : state.rotate(rotation);
	}

	/**
	 * A functional interface used to generate a BlockState from a
	 * DungeonSegmentModelBlock.
	 */
	@FunctionalInterface
	public static interface DungeonSegmentBlockStateProvider {

		BlockState get(DungeonSegmentModelBlock block, Theme theme, SubTheme subTheme, Random rand, int stage);

	}

	@FunctionalInterface
	private interface PropertyLoader {

		void load(DungeonSegmentModelBlock block, CompoundNBT nbt);

	}

}
