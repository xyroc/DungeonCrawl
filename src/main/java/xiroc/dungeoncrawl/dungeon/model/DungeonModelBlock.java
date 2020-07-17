package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;

import java.util.*;

public class DungeonModelBlock {

    private static final Tuple<BlockState, Boolean> CAVE_AIR = tuple(DungeonBlocks.CAVE_AIR, false);
    private static final Tuple<BlockState, Boolean> SPAWNER = tuple(DungeonBlocks.SPAWNER, false);
    private static final Tuple<BlockState, Boolean> LAVA = tuple(Blocks.LAVA.getDefaultState(), false);
    private static final Tuple<BlockState, Boolean> WATER = tuple(Blocks.LAVA.getDefaultState(), false);

    public static HashMap<DungeonModelBlockType, DungeonModelBlockStateProvider> PROVIDERS = new HashMap<DungeonModelBlockType, DungeonModelBlockStateProvider>();

    public DungeonModelBlockType type;

    public PropertyHolder[] properties;

    public Integer variation;

    public ResourceLocation resource;

    public DungeonModelBlock(DungeonModelBlockType type) {
        this.type = type;
    }

    /**
     * Creates a NBT representation of the DungeonModelBlock for the
     * new model type.
     *
     * @return The CompoundNBT
     */
    public CompoundNBT getAsNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("type", type.toString());

        if (resource != null)
            tag.putString("resourceName", resource.toString());

        if (this.properties != null) {
            ListNBT properties = new ListNBT();
            for (PropertyHolder holder : this.properties) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putString("property", holder.property.getName());

                String value = holder.value.toString().toLowerCase(Locale.ROOT);
                nbt.putString("value", value);
                properties.add(nbt);
            }
            if (properties.size() > 0)
                tag.put("properties", properties);
        }

        if (this.variation != null) {
            tag.putInt("variation", variation);
        }

        return tag;
    }

    public static DungeonModelBlock fromNBT(CompoundNBT nbt) {
        if (!nbt.contains("type"))
            return null;
        String type = nbt.getString("type");
        DungeonModelBlock block = new DungeonModelBlock(DungeonModelBlockType.valueOf(type));

        if (nbt.contains("resourceName"))
            block.resource = new ResourceLocation(nbt.getString("resourceName"));

        if (nbt.contains("properties")) {
            ListNBT properties = nbt.getList("properties", 10);

            block.properties = new PropertyHolder[properties.size()];

            for (int i = 0; i < properties.size(); i++) {
                CompoundNBT data = (CompoundNBT) properties.get(i);
                block.properties[i] = new PropertyHolder(data.getString("property"), data.getString("value"));
            }
        }

        if (nbt.contains("variation")) {
            block.variation = nbt.getInt("variation");
        }

        return block;
    }

    /**
     * Loads all existing properties from the given BlockState.
     */
    public DungeonModelBlock loadDataFromState(BlockState state) {
        List<PropertyHolder> properties = Lists.newArrayList();
        for (IProperty<?> property : state.getProperties()) {
            properties.add(new PropertyHolder(property, state.get(property)));
        }
        this.properties = properties.toArray(new PropertyHolder[0]);
        if (type == DungeonModelBlockType.CARPET) {
            for (int i = 0; i < DungeonBlocks.CARPET.length; i++) {
                if (state.getBlock() == DungeonBlocks.CARPET[i])
                    this.variation = i;
            }
            this.resource = state.getBlock().getRegistryName();
        } else if (type == DungeonModelBlockType.OTHER) {
            this.resource = state.getBlock().getRegistryName();
        }
        return this;
    }

    /**
     * Applies all existing properties to the given BlockState.
     */
    public Tuple<BlockState, Boolean> create(BlockState state, IWorld world, BlockPos pos, Rotation rotation) {
        boolean postProcessing = false;
        if (properties != null) {
            for (PropertyHolder holder : properties) {
                state = holder.apply(state);
                postProcessing = true;
            }
        }
        return tuple(state.rotate(rotation), postProcessing);
    }

    public Tuple<BlockState, Boolean> create(BlockState state, IWorld world, BlockPos pos) {
        boolean postProcessing = false;
        if (properties != null) {
            for (PropertyHolder holder : properties) {
                state = holder.apply(state);
                postProcessing = true;
            }
        }

        return tuple(state, postProcessing);
    }

    /**
     * Creates all BlockState providers.
     */
    public static void createProviders() {
        PROVIDERS.put(DungeonModelBlockType.NONE, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> null);
        PROVIDERS.put(DungeonModelBlockType.CARPET, (block, rotation, world, pos, theme, subTheme, rand, variation, stage)
                -> {
            //DungeonCrawl.LOGGER.info("Variation: {}" , Arrays.toString(variation));
            Block b = block.variation != null  && variation != null ? DungeonBlocks.CARPET[(block.variation + variation[block.variation]) % DungeonBlocks.CARPET.length]
                    : ForgeRegistries.BLOCKS.getValue(block.resource);
            if (b == null) {
                b = DungeonBlocks.CARPET[rand.nextInt(DungeonBlocks.CARPET.length)];
            }
            return block.create(b.getDefaultState(), world, pos);
        });
        PROVIDERS.put(DungeonModelBlockType.BARREL, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block
                .create(Blocks.BARREL.getDefaultState(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.MATERIAL, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                       stage) -> block.create(subTheme.material.get(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.SOLID,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(theme.solid.get(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.WALL,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(theme.normal.get(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.STAIRS, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block
                .create(theme.stairs.get(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.SOLID_STAIRS, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                           stage) -> block.create(theme.solidStairs.get(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.MATERIAL_STAIRS, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                              stage) -> block.create(subTheme.stairs.get(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.CHEST, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block
                .create(DungeonBlocks.CHEST, world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.RARE_CHEST,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> rand.nextFloat() < 0.15
                        ? block.create(DungeonBlocks.CHEST, world, pos, rotation)
                        : CAVE_AIR);
        PROVIDERS.put(DungeonModelBlockType.CHEST_50,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> rand.nextFloat() < 0.5
                        ? block.create(DungeonBlocks.CHEST, world, pos, rotation)
                        : CAVE_AIR);
        PROVIDERS.put(DungeonModelBlockType.DISPENSER, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                        stage) -> block.create(Blocks.DISPENSER.getDefaultState(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.FLOOR,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(theme.floor.get(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.SPAWNER,
                Config.NO_SPAWNERS.get()
                        ? (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> CAVE_AIR
                        : (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    return tuple(DungeonBlocks.SPAWNER, false);
                });
        PROVIDERS.put(DungeonModelBlockType.RARE_SPAWNER,
                Config.NO_SPAWNERS.get()
                        ? (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(theme.solid.get(), world, pos, rotation)
                        : (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> rand.nextFloat() < 0.15
                        ? SPAWNER
                        : CAVE_AIR);
        PROVIDERS.put(DungeonModelBlockType.RAND_FLOOR_CHEST_SPAWNER,
                Config.NO_SPAWNERS.get() ? (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    if (rand.nextInt(10) == 5)
                        return block.create(Blocks.CHEST.getDefaultState(), world, pos, rotation);
                    return block.create(theme.floor.get(), world, pos, rotation);
                } : (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    int i = rand.nextInt(20);
                    if (i < 1 + stage)
                        return SPAWNER;
                    if (i == 5)
                        return block.create(DungeonBlocks.CHEST, world, pos, rotation);
                    return block.create(theme.floor.get(), world, pos, rotation);
                });
        PROVIDERS.put(DungeonModelBlockType.RAND_FLOOR_LAVA,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    switch (rand.nextInt(2)) {
                        case 0:
                            return block.create(theme.floor.get(), world, pos, rotation);
                        default:
                            return LAVA;
                    }
                });
        PROVIDERS.put(DungeonModelBlockType.RAND_FLOOR_WATER,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    if (stage > 1)
                        return block.create(theme.floor.get(), world, pos, rotation);
                    switch (rand.nextInt(2)) {
                        case 0:
                            return block.create(theme.floor.get(), world, pos, rotation);
                        default:
                            return WATER;
                    }
                });
        PROVIDERS.put(DungeonModelBlockType.RAND_WALL_AIR,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    if (rand.nextFloat() < 0.75)
                        return block.create(theme.solid.get(), world, pos, rotation);
                    return CAVE_AIR;
                });
        PROVIDERS.put(DungeonModelBlockType.RAND_WALL_SPAWNER,
                Config.NO_SPAWNERS.get() ? (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    return block.create(theme.solid.get(), world, pos, rotation);
                } : (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    if (rand.nextInt(4 + (3 - stage)) == 0)
                        return SPAWNER;
                    return block.create(theme.solid.get(), world, pos, rotation);
                });
        PROVIDERS.put(DungeonModelBlockType.RAND_COBWEB_AIR,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    if (rand.nextInt(5) == 0)
                        return CAVE_AIR;
                    return tuple(Blocks.COBWEB.getDefaultState(), false);
                });
        PROVIDERS.put(DungeonModelBlockType.RAND_BOOKSHELF_COBWEB,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
                    int roll = rand.nextInt(10);
                    if (roll > 2)
                        return tuple(Blocks.BOOKSHELF.getDefaultState(), false);
                    return tuple(Blocks.COBWEB.getDefaultState(), false);
                });
        PROVIDERS.put(DungeonModelBlockType.TRAPDOOR, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                       stage) -> block.create(subTheme.trapDoor.get(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.PILLAR, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
            return block.create(subTheme.wallLog.get(), world, pos, rotation);
//			if (state.has(BlockStateProperties.SLAB_TYPE)) {
//				state = state.with(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE);
//			}
        });
        PROVIDERS.put(DungeonModelBlockType.DOOR, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block
                .create(subTheme.door.get(), world, pos, rotation));
        PROVIDERS.put(DungeonModelBlockType.OTHER, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
            Block b = ForgeRegistries.BLOCKS.getValue(block.resource);
            if (b != null) {
                return block.create(b.getDefaultState(),world, pos, rotation);
            } else {
                DungeonCrawl.LOGGER.warn("Unknown block {}", block.resource.toString());
                return CAVE_AIR;
            }
        });
    }

//    public void loadResource() {
//        this.resource = new ResourceLocation(resourceName);
//    }

    /**
     * Creates a BlockState from a DungeonModelBlock using a
     * DungeonBlockStateProvider from the provider-map.
     */
    public static Tuple<BlockState, Boolean> getBlockState(DungeonModelBlock block, Rotation rotation, IWorld world, BlockPos pos,
                                                           Theme theme, SubTheme subTheme, Random rand, byte[] variation, int stage) {
        DungeonModelBlockStateProvider provider = PROVIDERS.get(block.type);
        if (provider == null)
            return tuple(Blocks.CAVE_AIR.getDefaultState(), false);
        return provider.get(block, rotation, world, pos, theme, subTheme, rand, variation, stage);
    }

    /**
     * A functional interface used to generate a BlockState from a
     * DungeonModelBlock.
     */
    @FunctionalInterface
    public interface DungeonModelBlockStateProvider {

        Tuple<BlockState, Boolean> get(DungeonModelBlock block, Rotation rotation, IWorld world, BlockPos pos, Theme theme,
                                       SubTheme subTheme, Random rand, byte[] variation, int stage);

    }

    private static Tuple<BlockState, Boolean> tuple(BlockState state, boolean postProcessing) {
        return new Tuple<>(state, postProcessing);
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
