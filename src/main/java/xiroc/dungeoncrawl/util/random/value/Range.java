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

package xiroc.dungeoncrawl.util.random.value;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.util.Mth;

import java.lang.reflect.Type;
import java.util.Random;

public record Range(int min, int max) implements RandomValue {
    @Override
    public int nextInt(Random random) {
        return Mth.nextInt(random, min, max);
    }

    @Override
    public boolean isAlwaysWithin(int lowerBound, int upperBound) {
        return min >= lowerBound && max <= upperBound;
    }

    public static class Serializer implements JsonSerializer<Range>, JsonDeserializer<Range> {
        private static final String KEY_MIN_VALUE = "min";
        private static final String KEY_MAX_VALUE = "max";

        @Override
        public Range deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            return new Range(object.get(KEY_MIN_VALUE).getAsInt(), object.get(KEY_MAX_VALUE).getAsInt());
        }

        @Override
        public JsonElement serialize(Range range, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_MIN_VALUE, range.min);
            object.addProperty(KEY_MAX_VALUE, range.max);
            return object;
        }
    }
}
