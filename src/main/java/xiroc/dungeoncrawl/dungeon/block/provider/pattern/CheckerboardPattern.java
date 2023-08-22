package xiroc.dungeoncrawl.dungeon.block.provider.pattern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;

import java.lang.reflect.Type;
import java.util.Random;

public record CheckerboardPattern(BlockStateProvider block1, BlockStateProvider block2) implements BlockStateProvider {
    @Override
    public BlockState get(BlockPos pos, Random random) {
        if (((pos.getX() & 1) ^ (pos.getZ() & 1)) == 1) { // X is odd XOR Z is odd
            return block1.get(pos, random);
        } else {
            return block2.get(pos, random);
        }
    }

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, Random random, Rotation rotation) {
        if (((pos.getX() & 1) ^ (pos.getZ() & 1)) == 1) { // X is odd XOR Z is odd
            return block1.get(world, pos, random, rotation);
        } else {
            return block2.get(world, pos, random, rotation);
        }
    }

    public static class Serializer implements JsonSerializer<CheckerboardPattern>, JsonDeserializer<CheckerboardPattern> {

        private static final String KEY_BLOCK_1 = "block_1";
        private static final String KEY_BLOCK_2 = "block_2";

        @Override
        public CheckerboardPattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            BlockStateProvider block1 = BlockStateProvider.deserialize(object.get(KEY_BLOCK_1));
            BlockStateProvider block2 = BlockStateProvider.deserialize(object.get(KEY_BLOCK_2));
            return new CheckerboardPattern(block1, block2);
        }

        @Override
        public JsonElement serialize(CheckerboardPattern src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(SharedSerializationConstants.KEY_PROVIDER_TYPE, SharedSerializationConstants.TYPE_PATTERN);
            object.addProperty(SharedSerializationConstants.KEY_PATTERN_TYPE, SharedSerializationConstants.PATTERN_TYPE_CHECKERBOARD);

            object.add(KEY_BLOCK_1, context.serialize(src.block1));
            object.add(KEY_BLOCK_2, context.serialize(src.block2));
            return object;
        }

    }
}