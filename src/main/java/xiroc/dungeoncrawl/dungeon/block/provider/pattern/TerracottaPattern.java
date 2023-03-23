package xiroc.dungeoncrawl.dungeon.block.provider.pattern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.util.Orientation;

import java.lang.reflect.Type;
import java.util.Random;

public record TerracottaPattern(BlockStateProvider block) implements BlockStateProvider {
    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, Random random, Rotation rotation) {
        BlockState state = block.get(world, pos, random, rotation);
        if ((pos.getX() & 1) == 0) {
            if ((pos.getZ() & 1) == 0) {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).rotate(world, pos, Orientation.getOppositeRotation(rotation));
            } else {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).rotate(world, pos, Orientation.getOppositeRotation(rotation));
            }
        } else {
            if ((pos.getZ() & 1) == 0) {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).rotate(world, pos, Orientation.getOppositeRotation(rotation));
            } else {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).rotate(world, pos, Orientation.getOppositeRotation(rotation));
            }
        }
    }

    public static class Serializer implements JsonSerializer<TerracottaPattern>, JsonDeserializer<TerracottaPattern> {
        private static final String KEY_BLOCK = "block";

        @Override
        public TerracottaPattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            BlockStateProvider block = BlockStateProvider.deserialize(object.get(KEY_BLOCK));
            return new TerracottaPattern(block);
        }

        @Override
        public JsonElement serialize(TerracottaPattern src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(SharedSerializationConstants.KEY_PROVIDER_TYPE, SharedSerializationConstants.TYPE_PATTERN);
            object.addProperty(SharedSerializationConstants.KEY_PATTERN_TYPE, SharedSerializationConstants.PATTERN_TYPE_CHECKERBOARD);
            object.add(KEY_BLOCK, context.serialize(src.block));
            return object;
        }
    }
}
