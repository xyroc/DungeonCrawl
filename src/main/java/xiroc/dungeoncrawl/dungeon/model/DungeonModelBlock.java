package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.block.BlockRegistry;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;

public class DungeonModelBlock {

	public static HashMap<DungeonModelBlockType, DungeonSegmentBlockStateProvider> PROVIDERS = new HashMap<DungeonModelBlockType, DungeonSegmentBlockStateProvider>();

//	private static final HashMap<String, PropertyLoader> LOADERS;
//
//	static {
//		LOADERS = new HashMap<String, PropertyLoader>();
//
//		LOADERS.put("facing", (block, nbt) -> block.facing = read(Direction.class, "facing", nbt, Direction::byName));
//		LOADERS.put("axis",
//				(block, nbt) -> block.axis = read(Direction.Axis.class, "axis", nbt, Direction.Axis::byName));
//
//		LOADERS.put("upsideDown", (block, nbt) -> block.upsideDown = nbt.getBoolean("upsideDown"));
//		LOADERS.put("north", (block, nbt) -> block.north = nbt.getBoolean("north"));
//		LOADERS.put("east", (block, nbt) -> block.east = nbt.getBoolean("east"));
//		LOADERS.put("south", (block, nbt) -> block.south = nbt.getBoolean("south"));
//		LOADERS.put("west", (block, nbt) -> block.west = nbt.getBoolean("west"));
//		LOADERS.put("waterlogged", (block, nbt) -> block.waterlogged = nbt.getBoolean("waterlogged"));
//		LOADERS.put("lit", (block, nbt) -> block.lit = nbt.getBoolean("lit"));
//		LOADERS.put("open", (block, nbt) -> block.open = nbt.getBoolean("open"));
//		LOADERS.put("disarmed", (block, nbt) -> block.disarmed = nbt.getBoolean("disarmed"));
//		LOADERS.put("attached", (block, nbt) -> block.attached = nbt.getBoolean("attached"));
//
//		LOADERS.put("half", (block, nbt) -> block.half = read(Half.class, "half", nbt, Half::valueOf));
//		LOADERS.put("doubleBlockHalf", (block, nbt) -> block.doubleBlockHalf = read(DoubleBlockHalf.class,
//				"doubleBlockHalf", nbt, DoubleBlockHalf::valueOf));
//		LOADERS.put("attachFace",
//				(block, nbt) -> block.attachFace = read(AttachFace.class, "attachFace", nbt, AttachFace::valueOf));
//		LOADERS.put("bedPart", (block, nbt) -> block.bedPart = read(BedPart.class, "bedPart", nbt, BedPart::valueOf));
//		LOADERS.put("doorHinge",
//				(block, nbt) -> block.hinge = read(DoorHingeSide.class, "doorHinge", nbt, DoorHingeSide::valueOf));
//	}

	public DungeonModelBlockType type;

//	public List<PropertyHolder> properties = Lists.newArrayList();
	public PropertyHolder[] properties;

	public ResourceLocation resource;
	public String resourceName;

//	public Direction facing;
//	public Direction.Axis axis;
//	public Boolean upsideDown, north, east, south, west, waterlogged, lit, open, disarmed, attached;
//

//	public Half half;
//	public DoubleBlockHalf doubleBlockHalf;
//	public AttachFace attachFace;
//	public BedPart bedPart;
//	public DoorHingeSide hinge;

	public DungeonModelBlock(DungeonModelBlockType type) {
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

		if (resourceName != null)
			tag.putString("resourceName", resourceName);

		if (this.properties != null) {
			ListNBT properties = new ListNBT();
			for (PropertyHolder holder : this.properties) {
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString("property", holder.property.getName());

				String value = holder.value.toString().toLowerCase(Locale.ROOT);
				nbt.putString("value", value);
				properties.add(nbt);
//				DungeonCrawl.LOGGER.debug("NBT [ Property: {} , Value: {} ]", holder.property.getName(), value);
			}
			if (properties.size() > 0)
				tag.put("properties", properties);

		}

//		add(tag, properties, facing, "facing");
//		add(tag, properties, axis, "axis");
//
//		addBoolean(tag, properties, upsideDown, "upsideDown");
//		addBoolean(tag, properties, north, "north");
//		addBoolean(tag, properties, east, "east");
//		addBoolean(tag, properties, south, "south");
//		addBoolean(tag, properties, west, "west");
//		addBoolean(tag, properties, waterlogged, "waterlogged");
//		addBoolean(tag, properties, lit, "lit");
//		addBoolean(tag, properties, open, "open");
//		addBoolean(tag, properties, disarmed, "disarmed");
//		addBoolean(tag, properties, attached, "attached");
//
//		add(tag, properties, half, "half");
//		add(tag, properties, doubleBlockHalf, "doubleBlockHalf");
//		add(tag, properties, attachFace, "attachFace");
//		add(tag, properties, bedPart, "bedPart");
//		add(tag, properties, hinge, "doorHinge");

		return tag;
	}

	public static DungeonModelBlock fromNBT(CompoundNBT nbt) {
		if (!nbt.contains("type"))
			return null;
		String type = nbt.getString("type");
//		if (type.equalsIgnoreCase("WALL_LOG"))
//			type = "PILLAR";
		DungeonModelBlock block = new DungeonModelBlock(DungeonModelBlockType.valueOf(type));

		if (nbt.contains("resourceName"))
			block.resourceName = nbt.getString("resourceName");

		if (nbt.contains("properties")) {
			ListNBT properties = nbt.getList("properties", 10);

			block.properties = new PropertyHolder[properties.size()];

			for (int i = 0; i < properties.size(); i++) {
//				LOADERS.get(properties.getString(i)).load(block, nbt);
				CompoundNBT data = (CompoundNBT) properties.get(i);
				block.properties[i] = new PropertyHolder(data.getString("property"), data.getString("value"));
//				DungeonCrawl.LOGGER.debug("NBT [ Property: {} , Value: {} ]", block.properties[i].propertyName, block.properties[i].valueName);
			}
//			DungeonCrawl.LOGGER.debug("Total Properties: {}", block.properties.length);
		}
		return block;
	}

//	public static DungeonModelBlock convert(DungeonModelBlock block) {
//		if (block.facing != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.FACING, block.facing));
////		else if (facing != null && state.has(BlockStateProperties.HORIZONTAL_FACING))
////			state = state.with(BlockStateProperties.HORIZONTAL_FACING, facing);
//		if (block.axis != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.AXIS, block.axis));
////		else if (axis != null && state.has(BlockStateProperties.HORIZONTAL_AXIS))
////			state = state.with(BlockStateProperties.HORIZONTAL_AXIS, axis);
//
//		if (block.north != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.NORTH, block.north));
//		if (block.east != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.EAST, block.east));
//		if (block.south != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.SOUTH, block.south));
//		if (block.west != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.WEST, block.west));
//
//		if (block.waterlogged != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.WATERLOGGED, block.waterlogged));
//
//		if (block.upsideDown != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.INVERTED, block.upsideDown));
//
//		if (block.disarmed != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.DISARMED, block.disarmed));
//
//		if (block.attached != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.ATTACHED, block.attached));
//
//		if (block.lit != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.LIT, block.lit));
//
//		if (block.open != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.OPEN, block.open));
//
//		if (block.half != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.HALF, block.half));
//
//		if (block.attachFace != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.FACE, block.attachFace));
//
//		if (block.doubleBlockHalf != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.DOUBLE_BLOCK_HALF, block.doubleBlockHalf));
//
//		if (block.bedPart != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.BED_PART, block.bedPart));
//
//		if (block.hinge != null)
//			block.properties.add(new PropertyHolder(BlockStateProperties.DOOR_HINGE, block.hinge));
//		return block;
//	}

//	private void add(CompoundNBT tag, ListNBT properties, @Nullable Object object, String name) {
//		if (object != null) {
//			tag.putString(name, object.toString());
//			properties.add(new StringNBT(name));
//		}
//	}
//
//	private void addBoolean(CompoundNBT tag, ListNBT properties, @Nullable Boolean bool, String name) {
//		if (bool != null) {
//			tag.putBoolean(name, bool);
//			properties.add(new StringNBT(name));
//		}
//	}

//	private static <T> T read(Class<T> c, String name, CompoundNBT nbt, Function<String, T> f) {
//		return f.apply(nbt.getString(name).toUpperCase(Locale.ROOT));
//	}

	/**
	 * Loads all existing properties from the given BlockState.
	 */
	public DungeonModelBlock loadDataFromState(BlockState state) {
		List<PropertyHolder> properties = Lists.newArrayList();
		for (IProperty<?> property : state.getProperties()) {
			properties.add(new PropertyHolder(property, state.get(property)));
		}
		this.properties = properties.toArray(new PropertyHolder[properties.size()]);

//		if (state.has(BlockStateProperties.FACING))
//			facing = state.get(BlockStateProperties.FACING);
//		else if (state.has(BlockStateProperties.HORIZONTAL_FACING))
//			facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
//		else if (state.has(BlockStateProperties.AXIS))
//			axis = state.get(BlockStateProperties.AXIS);
//		else if (state.has(BlockStateProperties.HORIZONTAL_AXIS))
//			axis = state.get(BlockStateProperties.HORIZONTAL_AXIS);
//		if (state.has(BlockStateProperties.NORTH))
//			north = state.get(BlockStateProperties.NORTH);
//		if (state.has(BlockStateProperties.EAST))
//			east = state.get(BlockStateProperties.EAST);
//		if (state.has(BlockStateProperties.SOUTH))
//			south = state.get(BlockStateProperties.SOUTH);
//		if (state.has(BlockStateProperties.WEST))
//			west = state.get(BlockStateProperties.WEST);
//		if (state.has(BlockStateProperties.WATERLOGGED))
//			waterlogged = state.get(BlockStateProperties.WATERLOGGED);
//		if (state.has(BlockStateProperties.INVERTED))
//			upsideDown = state.get(BlockStateProperties.INVERTED);
//		if (state.has(BlockStateProperties.DISARMED))
//			disarmed = state.get(BlockStateProperties.DISARMED);
//		if (state.has(BlockStateProperties.ATTACHED))
//			attached = state.get(BlockStateProperties.ATTACHED);
//		if (state.has(BlockStateProperties.LIT))
//			lit = state.get(BlockStateProperties.LIT);
//		if (state.has(BlockStateProperties.OPEN))
//			open = state.get(BlockStateProperties.OPEN);
//		if (state.has(BlockStateProperties.HALF))
//			half = state.get(BlockStateProperties.HALF);
//		if (state.has(BlockStateProperties.DOUBLE_BLOCK_HALF))
//			doubleBlockHalf = state.get(BlockStateProperties.DOUBLE_BLOCK_HALF);
//		if (state.has(BlockStateProperties.FACE))
//			attachFace = state.get(BlockStateProperties.FACE);
//		if (state.has(BlockStateProperties.BED_PART))
//			bedPart = state.get(BlockStateProperties.BED_PART);
//		if (state.has(BlockStateProperties.DOOR_HINGE))
//			hinge = state.get(BlockStateProperties.DOOR_HINGE);
//		if (type == DungeonModelBlockType.OTHER)
//			resourceName = state.getBlock().getRegistryName().toString();

		return this;
	}

	/**
	 * Applies all existing properties to the given BlockState.
	 */
	public <T extends Comparable<T>> BlockState create(BlockState state, IWorld world, BlockPos pos) {
		boolean changed = false;
		if (properties != null) {
			for (PropertyHolder holder : properties) {
				state = holder.apply(state);
				changed = true;
			}
		}

		if (changed) {
			world.getChunk(pos).markBlockForPostprocessing(pos);
		}

//		if (facing != null && state.has(BlockStateProperties.FACING))
//			state = state.with(BlockStateProperties.FACING, facing);
//		else if (facing != null && state.has(BlockStateProperties.HORIZONTAL_FACING))
//			state = state.with(BlockStateProperties.HORIZONTAL_FACING, facing);
//		else if (axis != null && state.has(BlockStateProperties.AXIS))
//			state = state.with(BlockStateProperties.AXIS, axis);
//		else if (axis != null && state.has(BlockStateProperties.HORIZONTAL_AXIS))
//			state = state.with(BlockStateProperties.HORIZONTAL_AXIS, axis);
//		if (north != null && state.has(BlockStateProperties.NORTH))
//			state = state.with(BlockStateProperties.NORTH, north);
//		if (east != null && state.has(BlockStateProperties.EAST))
//			state = state.with(BlockStateProperties.EAST, east);
//		if (south != null && state.has(BlockStateProperties.SOUTH))
//			state = state.with(BlockStateProperties.SOUTH, south);
//		if (west != null && state.has(BlockStateProperties.WEST))
//			state = state.with(BlockStateProperties.WEST, west);
//		if (waterlogged != null && state.has(BlockStateProperties.WATERLOGGED))
//			state = state.with(BlockStateProperties.WATERLOGGED, waterlogged);
//		if (upsideDown != null && state.has(BlockStateProperties.INVERTED))
//			state = state.with(BlockStateProperties.INVERTED, upsideDown);
//		if (disarmed != null && state.has(BlockStateProperties.DISARMED))
//			state = state.with(BlockStateProperties.DISARMED, disarmed);
//		if (attached != null && state.has(BlockStateProperties.ATTACHED))
//			state = state.with(BlockStateProperties.ATTACHED, attached);
//		if (lit != null && state.has(BlockStateProperties.LIT))
//			state = state.with(BlockStateProperties.LIT, lit);
//		if (open != null && state.has(BlockStateProperties.OPEN))
//			state = state.with(BlockStateProperties.OPEN, open);
//		if (half != null && state.has(BlockStateProperties.HALF))
//			state = state.with(BlockStateProperties.HALF, half);
//		if (attachFace != null && state.has(BlockStateProperties.FACE))
//			state = state.with(BlockStateProperties.FACE, attachFace);
//		if (doubleBlockHalf != null && state.has(BlockStateProperties.DOUBLE_BLOCK_HALF))
//			state = state.with(BlockStateProperties.DOUBLE_BLOCK_HALF, doubleBlockHalf);
//		if (bedPart != null && state.has(BlockStateProperties.BED_PART))
//			state = state.with(BlockStateProperties.BED_PART, bedPart);
//		if (hinge != null && state.has(BlockStateProperties.DOOR_HINGE))
//			state = state.with(BlockStateProperties.DOOR_HINGE, hinge);

		return state;
	}

	/**
	 * Creates all BlockState providers.
	 */
	public static void createProviders() {
		PROVIDERS.put(DungeonModelBlockType.NONE, (block, rotation, world, pos, theme, subTheme, rand, stage) -> null);
		PROVIDERS.put(DungeonModelBlockType.BARREL, (block, rotation, world, pos, theme, subTheme, rand, stage) -> block
				.create(Blocks.BARREL.getDefaultState(), world, pos).rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.MATERIAL, (block, rotation, world, pos, theme, subTheme, rand,
				stage) -> block.create(subTheme.material.get(), world, pos).rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.SOLID,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> theme.solid.get().rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.WALL,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> theme.normal.get().rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.STAIRS, (block, rotation, world, pos, theme, subTheme, rand, stage) -> block
				.create(theme.stairs.get(), world, pos).rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.SOLID_STAIRS, (block, rotation, world, pos, theme, subTheme, rand,
				stage) -> block.create(theme.solidStairs.get(), world, pos).rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.MATERIAL_STAIRS, (block, rotation, world, pos, theme, subTheme, rand,
				stage) -> block.create(subTheme.stairs.get(), world, pos).rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.CHEST, (block, rotation, world, pos, theme, subTheme, rand, stage) -> block
				.create(BlockRegistry.CHEST, world, pos).rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.RARE_CHEST,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> rand.nextFloat() < 0.15
						? block.create(BlockRegistry.CHEST, world, pos).rotate(rotation)
						: BlockRegistry.CAVE_AIR);
		PROVIDERS.put(DungeonModelBlockType.CHEST_50,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> rand.nextFloat() < 0.5
						? block.create(BlockRegistry.CHEST, world, pos).rotate(rotation)
						: BlockRegistry.CAVE_AIR);
		PROVIDERS.put(DungeonModelBlockType.DISPENSER, (block, rotation, world, pos, theme, subTheme, rand,
				stage) -> block.create(Blocks.DISPENSER.getDefaultState(), world, pos).rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.FLOOR,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> theme.floor.get().rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.SPAWNER,
				Config.NO_SPAWNERS.get()
						? (block, rotation, world, pos, theme, subTheme, rand, stage) -> BlockRegistry.CAVE_AIR
						: (block, rotation, world, pos, theme, subTheme, rand, stage) -> {
							return BlockRegistry.SPAWNER.rotate(rotation);
						});
		PROVIDERS.put(DungeonModelBlockType.RARE_SPAWNER,
				Config.NO_SPAWNERS.get()
						? (block, rotation, world, pos, theme, subTheme, rand, stage) -> theme.solid.get()
						: (block, rotation, world, pos, theme, subTheme, rand, stage) -> rand.nextFloat() < 0.15
								? BlockRegistry.SPAWNER.rotate(rotation)
								: BlockRegistry.CAVE_AIR);
		PROVIDERS.put(DungeonModelBlockType.RAND_FLOOR_CHEST_SPAWNER,
				Config.NO_SPAWNERS.get() ? (block, rotation, world, pos, theme, subTheme, rand, stage) -> {
					if (rand.nextInt(10) == 5)
						return block.create(Blocks.CHEST.getDefaultState(), world, pos).rotate(rotation);
					return theme.floor.get().rotate(rotation);
				} : (block, rotation, world, pos, theme, subTheme, rand, stage) -> {
					int i = rand.nextInt(20);
					if (i < 1 + stage)
						return BlockRegistry.SPAWNER.rotate(rotation);
					if (i == 5)
						return block.create(BlockRegistry.CHEST, world, pos).rotate(rotation);
					return theme.floor.get().rotate(rotation);
				});
		PROVIDERS.put(DungeonModelBlockType.RAND_FLOOR_LAVA,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> {
					switch (rand.nextInt(2)) {
					case 0:
						return theme.floor.get().rotate(rotation);
					case 1:
						return Blocks.LAVA.getDefaultState().rotate(rotation);
					default:
						return Blocks.LAVA.getDefaultState().rotate(rotation);
					}
				});
		PROVIDERS.put(DungeonModelBlockType.RAND_FLOOR_WATER,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> {
					if (stage > 1)
						return theme.floor.get().rotate(rotation);
					switch (rand.nextInt(2)) {
					case 0:
						return theme.floor.get().rotate(rotation);
					case 1:
						return Blocks.WATER.getDefaultState().rotate(rotation);
					default:
						return Blocks.WATER.getDefaultState().rotate(rotation);
					}
				});
		PROVIDERS.put(DungeonModelBlockType.RAND_WALL_AIR,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> {
					if (rand.nextFloat() < 0.75)
						return theme.solid.get().rotate(rotation);
					return Blocks.CAVE_AIR.getDefaultState();
				});
		PROVIDERS.put(DungeonModelBlockType.RAND_WALL_SPAWNER,
				Config.NO_SPAWNERS.get() ? (block, rotation, world, pos, theme, subTheme, rand, stage) -> {
					return theme.solid.get().rotate(rotation);
				} : (block, rotation, world, pos, theme, subTheme, rand, stage) -> {
					if (rand.nextInt(4 + (3 - stage)) == 0)
						return BlockRegistry.SPAWNER;
					return theme.solid.get().rotate(rotation);
				});
		PROVIDERS.put(DungeonModelBlockType.RAND_COBWEB_AIR,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> {
					if (rand.nextInt(5) == 0)
						return Blocks.CAVE_AIR.getDefaultState();
					return Blocks.COBWEB.getDefaultState();
				});
		PROVIDERS.put(DungeonModelBlockType.RAND_BOOKSHELF_COBWEB,
				(block, rotation, world, pos, theme, subTheme, rand, stage) -> {
					int roll = rand.nextInt(10);
					if (roll > 2)
						return Blocks.BOOKSHELF.getDefaultState();
					return Blocks.COBWEB.getDefaultState();
				});
		PROVIDERS.put(DungeonModelBlockType.TRAPDOOR, (block, rotation, world, pos, theme, subTheme, rand,
				stage) -> block.create(subTheme.trapDoor.get(), world, pos).rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.PILLAR, (block, rotation, world, pos, theme, subTheme, rand, stage) -> {
			BlockState state = block.create(subTheme.wallLog.get(), world, pos).rotate(rotation);
//			if (state.has(BlockStateProperties.SLAB_TYPE)) {
//				state = state.with(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE);
//			}
			return state;
		});
		PROVIDERS.put(DungeonModelBlockType.DOOR, (block, rotation, world, pos, theme, subTheme, rand, stage) -> block
				.create(subTheme.door.get(), world, pos).rotate(rotation));
		PROVIDERS.put(DungeonModelBlockType.OTHER, (block, rotation, world, pos, theme, subTheme, rand, stage) -> block
				.create(ForgeRegistries.BLOCKS.getValue(block.resource).getDefaultState(), world, pos).rotate(rotation));
	}

	public void loadResource() {
		this.resource = new ResourceLocation(resourceName);
	}

	/**
	 * Creates a BlockState from a DungeonSegmentModelBlock using a
	 * DungeonSegmentBlockStateProvider from the provider-map.
	 */
	public static BlockState getBlockState(DungeonModelBlock block, Rotation rotation, IWorld world, BlockPos pos,
			Theme theme, SubTheme subTheme, Random rand, int stage) {
		DungeonSegmentBlockStateProvider provider = PROVIDERS.get(block.type);
		if (provider == null)
			return Blocks.CAVE_AIR.getDefaultState();
		BlockState state = provider.get(block, rotation, world, pos, theme, subTheme, rand, stage);
		return state;
	}

	/**
	 * A functional interface used to generate a BlockState from a
	 * DungeonSegmentModelBlock.
	 */
	@FunctionalInterface
	public static interface DungeonSegmentBlockStateProvider {

		BlockState get(DungeonModelBlock block, Rotation rotation, IWorld world, BlockPos pos, Theme theme,
				SubTheme subTheme, Random rand, int stage);

	}

	@FunctionalInterface
	private interface PropertyLoader {

		void load(DungeonModelBlock block, CompoundNBT nbt);

	}

	private static class PropertyHolder {

		public String propertyName, valueName;

		public IProperty<?> property;
		public Object value;

		public PropertyHolder(String propertyName, String valueName) {
			this.propertyName = propertyName;
			this.valueName = valueName;
		}

		public PropertyHolder(IProperty<?> property, Object value) {
			this.property = property;
			this.value = value;
		}

		@SuppressWarnings("unchecked")
		public <T extends Comparable<T>> BlockState apply(BlockState state) {
			if (property == null) {
				for (IProperty<?> p : state.getProperties()) {
					if (p.getName().equals(propertyName)) {
//						DungeonCrawl.LOGGER.debug("Loading Property {}", p.getName());
						this.property = p;
						Optional<?> optional = p.parseValue(valueName);
						if (optional.isPresent()) {
							this.value = optional.get();
						} else {
							DungeonCrawl.LOGGER.error("Property {} couldn't parse {}", p.getName(), valueName);
						}
					}
				}
			}

			if (state.has(property)) {
				return state.with((IProperty<T>) property, (T) value);
			}

			return state;
		}

	}

}
