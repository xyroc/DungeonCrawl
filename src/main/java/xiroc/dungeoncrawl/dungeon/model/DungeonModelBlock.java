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

package xiroc.dungeoncrawl.dungeon.model;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;

import java.util.*;

public class DungeonModelBlock {

    private static final Tuple<BlockState, Boolean> CAVE_AIR = tuple(DungeonBlocks.CAVE_AIR, false);

    public static final Hashtable<DungeonModelBlockType, DungeonModelBlockStateProvider> PROVIDERS = new Hashtable<>();

    public final DungeonModelBlockType type;

    public Vec3i position;

    private PropertyHolder[] properties;

    public Integer variation;

    public ResourceLocation resource;

    public DungeonModelBlock(DungeonModelBlockType type, Vec3i position) {
        this.type = type;
        this.position = position;
    }

    /**
     * Creates a NBT representation of the DungeonModelBlock for the
     * new model type.
     *
     * @return The CompoundNBT
     */
    public CompoundNBT toNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("type", type.toString());

        CompoundNBT position = new CompoundNBT();
        position.putInt("x", this.position.getX());
        position.putInt("y", this.position.getY());
        position.putInt("z", this.position.getZ());
        tag.put("position", position);

        if (resource != null)
            tag.putString("resourceName", resource.toString());

        if (this.properties != null) {
            ListNBT properties = new ListNBT();
            for (PropertyHolder holder : this.properties) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putString("property", holder.propertyName);
                nbt.putString("value", holder.valueName);
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
        if (!nbt.contains("type")) { // backwards compatibility
            return new DungeonModelBlock(DungeonModelBlockType.AIR, null);
        }

        String type = nbt.getString("type");
        if (!DungeonModelBlockType.NAME_TO_TYPE.containsKey(type)) {
            if (type.equals("SPAWNER")) {
                DungeonModelBlock spawner = new DungeonModelBlock(DungeonModelBlockType.OTHER, null);
                spawner.resource = new ResourceLocation("spawner");
                return spawner;
            }
            DungeonCrawl.LOGGER.warn("Unknown model block type: {}", type);
            return null;
        }

        CompoundNBT pos = nbt.getCompound("position");
        Vec3i position = new Vec3i(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));

        DungeonModelBlock block = new DungeonModelBlock(DungeonModelBlockType.NAME_TO_TYPE.get(type), position);

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
                if (state.getBlock() == DungeonBlocks.CARPET[i]) {
                    this.variation = i;
                    break;
                }
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
    public Tuple<BlockState, Boolean> create(BlockState state, Rotation rotation) {
        if (properties != null) {
            for (PropertyHolder holder : properties) {
                state = holder.apply(state);
            }
            return tuple(state.rotate(rotation), true);
        }
        return tuple(state.rotate(rotation), false);
    }

    public Tuple<BlockState, Boolean> create(BlockState state) {
        if (properties != null) {
            for (PropertyHolder holder : properties) {
                state = holder.apply(state);
            }
            return tuple(state, true);
        }
        return tuple(state, false);
    }

    /**
     * Creates all BlockState providers.
     */
    public static void init() {
        PROVIDERS.put(DungeonModelBlockType.AIR, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> tuple(DungeonBlocks.CAVE_AIR, false));
        PROVIDERS.put(DungeonModelBlockType.CHEST, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                       stage) -> block.create(Blocks.CHEST.getDefaultState(), rotation));
        PROVIDERS.put(DungeonModelBlockType.CARPET, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
            Block b = block.variation != null && variation != null ?
                    DungeonBlocks.CARPET[(block.variation + variation[block.variation]) % DungeonBlocks.CARPET.length]
                    : ForgeRegistries.BLOCKS.getValue(block.resource);
            if (b == null) {
                b = DungeonBlocks.CARPET[rand.nextInt(DungeonBlocks.CARPET.length)];
            }
            return block.create(b.getDefaultState());
        });
        PROVIDERS.put(DungeonModelBlockType.MATERIAL, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                       stage) -> block.create(subTheme.material.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.SOLID,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(theme.solid.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.GENERIC,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(theme.generic.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.GENERIC_SECONDARY,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(theme.generic2.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.STAIRS, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block
                .create(theme.stairs.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.SOLID_STAIRS, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                           stage) -> block.create(theme.solidStairs.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.MATERIAL_STAIRS, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                              stage) -> block.create(subTheme.stairs.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.SLAB, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                   stage) -> block.create(theme.slab.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.SOLID_SLAB, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                         stage) -> block.create(theme.solidSlab.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.WOODEN_SLAB, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                          stage) -> block.create(subTheme.slab.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.WOODEN_PRESSURE_PLATE, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                                    stage) -> block.create(subTheme.pressurePlate.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.FENCE, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                    stage) -> block.create(subTheme.fence.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.FENCE_GATE, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                         stage) -> block.create(subTheme.fenceGate.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.WOODEN_BUTTON, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                            stage) -> block.create(subTheme.button.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.SKULL, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                    stage) -> {
            Tuple<BlockState, Boolean> skull = block.create(Blocks.SKELETON_SKULL.getDefaultState(), rotation);
            BlockState state = skull.getA();
            if (state.has(BlockStateProperties.ROTATION_0_15)) {
                int r = state.get(BlockStateProperties.ROTATION_0_15);
                int add = rand.nextInt(3);
                if (rand.nextBoolean()) {
                    r -= add;
                    if (r < 0)
                        r += 16;
                } else {
                    r = (r + add) % 16;
                }
                state = state.with(BlockStateProperties.ROTATION_0_15, r);
                return new Tuple<>(state, skull.getB());
            }
            return skull;
        });
        PROVIDERS.put(DungeonModelBlockType.FLOOR,
                (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block.create(theme.floor.get(pos), rotation));

        PROVIDERS.put(DungeonModelBlockType.TRAPDOOR, (block, rotation, world, pos, theme, subTheme, rand, variation,
                                                       stage) -> block.create(subTheme.trapDoor.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.PILLAR, (block, rotation, world, pos, theme, subTheme, rand, variation, stage)
                -> block.create(subTheme.wallLog.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.DOOR, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> block
                .create(subTheme.door.get(pos), rotation));
        PROVIDERS.put(DungeonModelBlockType.OTHER, (block, rotation, world, pos, theme, subTheme, rand, variation, stage) -> {
            Block b = ForgeRegistries.BLOCKS.getValue(block.resource);
            if (b != null) {
                return block.create(b.getDefaultState(), rotation);
            } else {
                DungeonCrawl.LOGGER.warn("Unknown block {}", block.resource.toString());
                return CAVE_AIR;
            }
        });
    }

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
            this.propertyName = property.getName();
            this.value = value;
            this.valueName = value.toString().toLowerCase(Locale.ROOT);
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
                            DungeonCrawl.LOGGER.error("Couldn't parse property {} with value {}", p.getName(), valueName);
                        }
                        break;
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
