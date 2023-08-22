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

import java.lang.reflect.Type;
import java.util.Random;

public record TerracottaPattern(BlockStateProvider block) implements BlockStateProvider {
    private static final Direction[] TERRACOTTA_FACINGS = {Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST};

    @Override
    public BlockState get(BlockPos pos, Random random) {
        Direction facing = TERRACOTTA_FACINGS[((pos.getX() & 1) << 1) + pos.getZ() & 1];
        return DungeonBlocks.applyProperty(block.get(pos, random), BlockStateProperties.HORIZONTAL_FACING, facing);
    }

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, Random random, Rotation rotation) {
        return get(pos, random).rotate(world, pos, rotation);
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
