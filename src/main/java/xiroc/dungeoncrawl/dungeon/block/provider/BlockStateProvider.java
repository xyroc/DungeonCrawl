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

package xiroc.dungeoncrawl.dungeon.block.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.dungeon.block.provider.pattern.CheckerboardPattern;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Random;

public interface BlockStateProvider {
    Gson GSON = gsonAdapters(new GsonBuilder()).create();

    static GsonBuilder gsonAdapters(GsonBuilder builder) {
        return builder.registerTypeAdapter(BlockStateProvider.class, new Deserializer())
                .registerTypeAdapter(SingleBlock.class, new SingleBlock.Serializer())
                .registerTypeAdapter(RandomBlock.class, new RandomBlock.Serializer())
                .registerTypeAdapter(CheckerboardPattern.class, new CheckerboardPattern.Serializer());
    }

    BlockState get(BlockPos pos, Random random);

    static BlockStateProvider deserialize(JsonElement json) throws DatapackLoadException {
        return GSON.fromJson(json, BlockStateProvider.class);
    }

    class Deserializer implements JsonDeserializer<BlockStateProvider> {
        @Override
        public BlockStateProvider deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return GSON.fromJson(json, SingleBlock.class);
            }
            if (json.isJsonArray()) {
                return GSON.fromJson(json, RandomBlock.class);
            }
            JsonObject object = json.getAsJsonObject();
            if (!object.has(SharedSerializationConstants.KEY_PROVIDER_TYPE)) {
                throw new JsonParseException("Missing block state provider type specification");
            }
            String type = object.get(SharedSerializationConstants.KEY_PROVIDER_TYPE).getAsString().toLowerCase(Locale.ROOT);
            switch (type) {
                case SharedSerializationConstants.TYPE_SINGLE_BLOCK -> {
                    return GSON.fromJson(json, SingleBlock.class);
                }
                case SharedSerializationConstants.TYPE_RANDOM_BLOCK -> {
                    return GSON.fromJson(json, RandomBlock.class);
                }
                case SharedSerializationConstants.TYPE_CHECKERBOARD_PATTERN -> {
                    return GSON.fromJson(json, CheckerboardPattern.class);
                }
                default -> throw new JsonParseException("Unknown block state provider type: " + type);
            }
        }
    }

    interface SharedSerializationConstants {
        String KEY_PROVIDER_TYPE = "type";

        String TYPE_SINGLE_BLOCK = "block";
        String TYPE_RANDOM_BLOCK = "random_block";
        String TYPE_CHECKERBOARD_PATTERN = "checkerboard";
    }
}
