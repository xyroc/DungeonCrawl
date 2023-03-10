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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DungeonModelBlock {

    public final DungeonModelBlockType type;

    public Vec3i position;

    @Nullable
    private final PropertyHolder[] properties;
    public final boolean hasProperties;
    @Nullable

    public final Integer variation;
    public final Block block;
    @Nullable
    public final ResourceLocation blockName;
    // A custom loot table, can be defined in model metadata.
    @Nullable
    public ResourceLocation lootTable;

    private DungeonModelBlock(DungeonModelBlockType type, Vec3i position) {
        this(type, position, null, null, Blocks.CAVE_AIR, null);
    }

    private DungeonModelBlock(DungeonModelBlockType type,
                              Vec3i position,
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

    public static DungeonModelBlock fromBlockState(BlockState state, DungeonModelBlockType type, Vec3i position) {
        List<PropertyHolder> properties = Lists.newArrayList();
        for (Property<?> property : state.getProperties()) {
            properties.add(new PropertyHolder(property, state.getValue(property)));
        }
        PropertyHolder[] blockProperties = properties.isEmpty() ? null : properties.toArray(new PropertyHolder[0]);
        Integer variation = null;
        Block block;
        ResourceLocation blockName = null;
        if (type == DungeonModelBlockType.CARPET) {
//            Iterator<Holder<Block>> carpets = Registry.BLOCK.getTagOrEmpty(BlockTags.CARPETS).iterator();
//            int index = 0;
//            while (carpets.hasNext()) {
//                Block carpet = carpets.next().value();
//                if (state.getBlock() == carpet) {
//                    variation = index;
//                    break;
//                }
//                index++;
//            }
            variation = 0;
            block = state.getBlock();
            blockName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        } else if (type == DungeonModelBlockType.OTHER) {
            block = state.getBlock();
            blockName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        } else {
            block = Blocks.CAVE_AIR;
        }
        return new DungeonModelBlock(type, position, blockProperties, variation, block, blockName);
    }

    /**
     * Creates a NBT representation of the DungeonModelBlock.
     *
     * @return The CompoundNBT
     */
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", type.toString());

        CompoundTag position = new CompoundTag();
        position.putInt("x", this.position.getX());
        position.putInt("y", this.position.getY());
        position.putInt("z", this.position.getZ());
        tag.put("position", position);

        if (blockName != null)
            tag.putString("resourceName", blockName.toString());

        if (this.properties != null) {
            ListTag properties = new ListTag();
            for (PropertyHolder holder : this.properties) {
                CompoundTag nbt = new CompoundTag();
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
    public static DungeonModelBlock fromNBT(CompoundTag nbt, Vec3i position) {
        if (!nbt.contains("type")) { // backwards compatibility
            DungeonCrawl.LOGGER.info("Model block does not have a type parameter");
            return new DungeonModelBlock(DungeonModelBlockType.AIR, position);
        }

        if (nbt.contains("position")) {
            CompoundTag pos = nbt.getCompound("position");
            position = new Vec3i(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
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
            ListTag nbtProperties = nbt.getList("properties", 10);

            properties = new PropertyHolder[nbtProperties.size()];

            for (int i = 0; i < nbtProperties.size(); i++) {
                CompoundTag data = (CompoundTag) nbtProperties.get(i);
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

    public static DungeonModelBlock fromNBT(CompoundTag nbt) {
        return fromNBT(nbt, null);
    }

    public BlockPos worldPos(DungeonModel model, Rotation rotation, BlockPos offset) {
        return switch (rotation) {
            case CLOCKWISE_90 -> new BlockPos(
                    offset.getX() + model.length - position.getZ() - 1,
                    offset.getY() + position.getY(),
                    offset.getZ() + position.getX());
            case COUNTERCLOCKWISE_90 -> new BlockPos(
                    offset.getX() + position.getZ(),
                    offset.getY() + position.getY(),
                    offset.getZ() + model.width - position.getX() - 1);
            case CLOCKWISE_180 -> new BlockPos(
                    offset.getX() + model.width - position.getX() - 1,
                    offset.getY() + position.getY(),
                    offset.getZ() + model.length - position.getZ() - 1);
            default -> offset.offset(position);
        };
    }

    /**
     * Applies all existing properties to the given BlockState.
     */
    public BlockState create(BlockState state, LevelAccessor world, BlockPos pos, Rotation rotation) {

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
