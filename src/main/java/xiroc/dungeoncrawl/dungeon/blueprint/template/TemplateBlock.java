package xiroc.dungeoncrawl.dungeon.blueprint.template;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import javax.json.JsonString;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.function.Function;

public record TemplateBlock(PlacementProperties placementProperties, Vec3i position, Block block, Function<BlockState, BlockState> properties) {
    private static final ImmutableMap<TemplateBlockType, Block> BLOCK_MAP = ImmutableMap.<TemplateBlockType, Block>builder()
            .put(TemplateBlockType.AIR, Blocks.AIR)
            .put(TemplateBlockType.CHEST, Blocks.CHEST)

            .put(TemplateBlockType.SOLID_STAIRS, Blocks.STONE_BRICK_STAIRS)
            .put(TemplateBlockType.GENERIC, Blocks.COBBLESTONE)
            .put(TemplateBlockType.SOLID, Blocks.STONE_BRICKS)
            .put(TemplateBlockType.WALL, Blocks.STONE_BRICK_WALL)
            .put(TemplateBlockType.SOLID_PILLAR, Blocks.PURPUR_PILLAR)
            .put(TemplateBlockType.FENCING, Blocks.IRON_BARS)
            .put(TemplateBlockType.FLUID, Blocks.WATER)

            .put(TemplateBlockType.PILLAR, Blocks.OAK_LOG)
            .put(TemplateBlockType.MATERIAL, Blocks.OAK_PLANKS)
            .put(TemplateBlockType.DOOR, Blocks.OAK_DOOR)
            .put(TemplateBlockType.MATERIAL_STAIRS, Blocks.OAK_STAIRS)
            .put(TemplateBlockType.MATERIAL_SLAB, Blocks.OAK_SLAB)
            .put(TemplateBlockType.MATERIAL_BUTTON, Blocks.OAK_BUTTON)
            .put(TemplateBlockType.FENCE, Blocks.OAK_FENCE)
            .put(TemplateBlockType.FENCE_GATE, Blocks.OAK_FENCE_GATE)
            .put(TemplateBlockType.MATERIAL_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE)
            .put(TemplateBlockType.TRAPDOOR, Blocks.OAK_TRAPDOOR)

            .put(TemplateBlockType.FLOOR, Blocks.GRAVEL)
            .put(TemplateBlockType.SOLID_FLOOR, Blocks.CRACKED_STONE_BRICKS)
            .put(TemplateBlockType.STAIRS, Blocks.COBBLESTONE_STAIRS)
            .put(TemplateBlockType.SLAB, Blocks.COBBLESTONE_SLAB)

            .put(TemplateBlockType.SKULL, Blocks.SKELETON_SKULL)
            .build();

    public static Function<BlockState, BlockState> properties(BlockState state) {
        return (blockState) -> {
            for (Property<?> property : state.getProperties()) {
                blockState = applyProperty(blockState, property, state.getValue(property));
            }
            return blockState;
        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, Object value) {
        if (state.hasProperty(property)) {
            return state.setValue(property, (T) value);
        }
        return state;
    }

    public record PlacementProperties(TemplateBlockType blockType, boolean isSolid) {
        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            PlacementProperties that = (PlacementProperties) other;

            if (isSolid != that.isSolid) return false;
            return blockType == that.blockType;
        }

        public static class Serializer implements JsonSerializer<PlacementProperties>, JsonDeserializer<PlacementProperties> {
            private static final String KEY_BLOCK_TYPE = "type";
            private static final String KEY_IS_SOLID = "solid";

            private static TemplateBlockType getBlockType(String name) {
                try {
                    return TemplateBlockType.valueOf(name.toUpperCase(Locale.ROOT));
                } catch(IllegalArgumentException e) {
                    throw new DatapackLoadException("Invalid blueprint block type: " + name);
                }
            }

            @Override
            public PlacementProperties deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                if (jsonElement instanceof JsonString) {
                    TemplateBlockType blockType = getBlockType(jsonElement.getAsString());
                    return new PlacementProperties(blockType, blockType.isSolid);
                }
                JsonObject object = jsonElement.getAsJsonObject();
                TemplateBlockType blockType = getBlockType(object.get(KEY_BLOCK_TYPE).getAsString());
                boolean isSolid = object.has(KEY_IS_SOLID) ? object.get(KEY_IS_SOLID).getAsBoolean() : blockType.isSolid;
                return new PlacementProperties(blockType, isSolid);
            }

            @Override
            public JsonElement serialize(PlacementProperties placementProperties, Type type, JsonSerializationContext jsonSerializationContext) {
                if (placementProperties.isSolid == placementProperties.blockType.isSolid) {
                    return new JsonPrimitive(placementProperties.blockType.name());
                }
                JsonObject object = new JsonObject();
                object.addProperty(KEY_BLOCK_TYPE, placementProperties.blockType.name().toLowerCase(Locale.ROOT));
                object.addProperty(KEY_IS_SOLID, placementProperties.isSolid);
                return object;
            }
        }
    }
}