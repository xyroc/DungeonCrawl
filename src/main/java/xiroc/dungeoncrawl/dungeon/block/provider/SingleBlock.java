package xiroc.dungeoncrawl.dungeon.block.provider;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;

import java.lang.reflect.Type;

public class SingleBlock implements BlockStateProvider {
    public static final SingleBlock AIR = new SingleBlock(Blocks.CAVE_AIR.defaultBlockState());

    private final BlockState state;

    public SingleBlock(Block block) {
        this(block.defaultBlockState());
    }

    public SingleBlock(BlockState state) {
        this.state = state;
    }

    @Override
    public BlockState get(BlockPos pos, RandomSource random) {
        return state;
    }

    public static class Serializer implements JsonSerializer<SingleBlock>, JsonDeserializer<SingleBlock> {
        private static final String KEY_BLOCK = "block";

        @Override
        public SingleBlock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                final BlockState state = JSONUtils.parse(json, GlobalCodecs.BLOCK_STATE);
                return new SingleBlock(state);
            } else {
                final BlockState state = JSONUtils.parse(json.getAsJsonObject().get(KEY_BLOCK), GlobalCodecs.BLOCK_STATE);
                return new SingleBlock(state);
            }
        }

        @Override
        public JsonElement serialize(SingleBlock src, Type typeOfSrc, JsonSerializationContext context) {
            return JSONUtils.encode(src.state, GlobalCodecs.BLOCK_STATE).getAsJsonPrimitive();
        }
    }
}