package xiroc.dungeoncrawl.dungeon.block.provider;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.JSONUtils;

import java.lang.reflect.Type;
import java.util.Random;

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
    public BlockState get(BlockPos pos, Random random) {
        return state;
    }

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, Random random, Rotation rotation) {
        return state;
    }

    public static class Serializer implements JsonSerializer<SingleBlock>, JsonDeserializer<SingleBlock> {
        @Override
        public SingleBlock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                ResourceLocation blockName = new ResourceLocation(json.getAsString());
                Block block = ForgeRegistries.BLOCKS.getValue(blockName);
                if (block == null) {
                    throw new DatapackLoadException("Unknown block: " + blockName);
                }
                return new SingleBlock(block);
            } else {
                ResourceLocation blockName = new ResourceLocation(json.getAsJsonObject().get("block").getAsString());
                JsonObject jsonBlock = json.getAsJsonObject();

                Block block = ForgeRegistries.BLOCKS.getValue(blockName);
                if (block == null) {
                    throw new DatapackLoadException("Unknown block: " + blockName);
                }
                return new SingleBlock(JSONUtils.deserializeBlockStateProperties(block, jsonBlock));
            }
        }

        @Override
        public JsonElement serialize(SingleBlock src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(SharedSerializationConstants.KEY_PROVIDER_TYPE, SharedSerializationConstants.TYPE_SINGLE_BLOCK);
            return JSONUtils.serializeBlockState(object, src.state);
        }
    }
}