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
import net.minecraft.state.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DungeonModelBlock {

    public final DungeonModelBlockType type;

    public Vector3i position;

    @Nullable
    private final PropertyHolder[] properties;
    public final boolean hasProperties;
    @Nullable

    public final Integer variation;
    private final Block block;
    @Nullable
    public final ResourceLocation blockName;
    // A custom loot table, can be defined in model metadata.
    @Nullable
    public ResourceLocation lootTable;


    private DungeonModelBlock(DungeonModelBlockType type, Vector3i position) {
        this(type, position, null, null, Blocks.CAVE_AIR, null);
    }

    private DungeonModelBlock(DungeonModelBlockType type,
                              Vector3i position,
                              @Nullable PropertyHolder[] properties,
                              @Nullable Integer variation,
                              Block block,
                              @Nullable ResourceLocation blockName) {
        this.type = type;
        this.position = position;
        this.properties = properties;
        this.hasProperties = properties != null;
        this.variation = variation;
        this.block = block;
        this.blockName = blockName;
    }

    public static DungeonModelBlock fromBlockState(BlockState state, DungeonModelBlockType type, Vector3i position) {
        List<PropertyHolder> properties = Lists.newArrayList();
        for (Property<?> property : state.getProperties()) {
            properties.add(new PropertyHolder(property, state.getValue(property)));
        }
        PropertyHolder[] blockProperties = properties.isEmpty() ? null : properties.toArray(new PropertyHolder[0]);
        Integer variation = null;
        Block block;
        ResourceLocation blockName = null;
        if (type == DungeonModelBlockType.CARPET) {
            Collection<Block> carpets = BlockTags.CARPETS.getValues();
            int index = 0;
            for (Block carpet : carpets) {
                if (state.getBlock() == carpet) {
                    variation = index;
                    break;
                }
                index++;
            }
            block = state.getBlock();
            blockName = state.getBlock().getRegistryName();
        } else if (type == DungeonModelBlockType.OTHER) {
            block = state.getBlock();
            blockName = state.getBlock().getRegistryName();
        } else {
            block = Blocks.CAVE_AIR;
        }
        return new DungeonModelBlock(type, position, blockProperties, variation, block, blockName);
    }

    /**
     * <<<<<<< HEAD
     * Creates an NBT representation of the DungeonModelBlock.
     * =======
     * Creates a NBT representation of the DungeonModelBlock.
     * >>>>>>> origin/1.15
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

        if (blockName != null)
            tag.putString("resourceName", blockName.toString());

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

    /**
     * Loads a model block with a fallback position, used to load from legacy models.
     */
    public static DungeonModelBlock fromNBT(CompoundNBT nbt, Vector3i position) {
        if (!nbt.contains("type")) { // backwards compatibility
//            return new DungeonModelBlock(DungeonModelBlockType.AIR, null);
            DungeonCrawl.LOGGER.info("Model block does not have a type parameter");
            return null;
        }

        if (nbt.contains("position")) {
            CompoundNBT pos = nbt.getCompound("position");
            position = new Vector3i(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
        }

        String type = nbt.getString("type");
        if (!DungeonModelBlockType.NAME_TO_TYPE.containsKey(type)) {
            DungeonCrawl.LOGGER.warn("Unknown model block type: {}", type);
            return new DungeonModelBlock(DungeonModelBlockType.AIR, position);
        }

        DungeonModelBlockType blockType = DungeonModelBlockType.NAME_TO_TYPE.get(type);
        Block block;
        ResourceLocation blockName = null;

        if (nbt.contains("resourceName")) {
            blockName = new ResourceLocation(nbt.getString("resourceName"));
            if (ForgeRegistries.BLOCKS.containsKey(blockName)) {
                block = ForgeRegistries.BLOCKS.getValue(blockName);
            } else {
                DungeonCrawl.LOGGER.warn("Unknown block: {}", blockName);
                block = Blocks.CAVE_AIR;
            }
        } else {
            block = Blocks.CAVE_AIR;
        }

        PropertyHolder[] properties = null;

        if (nbt.contains("properties")) {
            ListNBT nbtProperties = nbt.getList("properties", 10);

            properties = new PropertyHolder[nbtProperties.size()];

            for (int i = 0; i < nbtProperties.size(); i++) {
                CompoundNBT data = (CompoundNBT) nbtProperties.get(i);
                properties[i] = new PropertyHolder(data.getString("property"), data.getString("value"));
            }
        }

        Integer variation = null;

        if (nbt.contains("variation")) {
            variation = nbt.getInt("variation");
        }

        return new DungeonModelBlock(blockType, position, properties, variation, block, blockName);
    }

    /**
     * Loads a model block from a version 1 model.
     */

    public static DungeonModelBlock fromNBT(CompoundNBT nbt) {
        return fromNBT(nbt, null);
    }

    /**
     * Applies all existing properties to the given BlockState.
     */
    public BlockState create(BlockState state, IWorld world, BlockPos pos, Rotation rotation) {

        if (properties != null) {
            for (PropertyHolder holder : properties) {
                state = holder.apply(state);
            }
        }
        return state.rotate(world, pos, rotation);
    }

    public BlockState create(BlockState state) {
        if (properties != null) {
            for (PropertyHolder holder : properties) {
                state = holder.apply(state);
            }
        }
        return state;
    }

    /**
     * @return the custom block, or cave air if not specified
     */
    public Block getBlock() {
        return block;
    }

    private static class PropertyHolder {

        public String propertyName, valueName;

        public Property<?> property;
        public Object value;

        public PropertyHolder(String propertyName, String valueName) {
            this.propertyName = propertyName;
            this.valueName = valueName;
        }

        public PropertyHolder(Property<?> property, Object value) {
            this.property = property;
            this.propertyName = property.getName();
            this.value = value;
            this.valueName = value.toString().toLowerCase(Locale.ROOT);
        }

        @SuppressWarnings("unchecked")
        public <T extends Comparable<T>> BlockState apply(BlockState state) {
            if (property == null) {
                for (Property<?> p : state.getProperties()) {
                    if (p.getName().equals(propertyName)) {
                        this.property = p;
                        Optional<?> optional = p.getValue(valueName);
                        if (optional.isPresent()) {
                            this.value = optional.get();
                        } else {
                            DungeonCrawl.LOGGER.error("Couldn't parse property {} with value {}", p.getName(), valueName);
                        }
                        break;
                    }
                }
            }

            if (state.hasProperty(property)) {
                return state.setValue((Property<T>) property, (T) value);
            }

            return state;
        }

    }

}
