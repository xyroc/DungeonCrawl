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

package xiroc.dungeoncrawl.util;

import com.google.gson.*;
import net.minecraft.util.Tuple;

import java.lang.reflect.Type;

public class WeightedIntegerEntry extends Tuple<Integer, Integer> {

    public WeightedIntegerEntry(Integer aIn, Integer bIn) {
        super(aIn, bIn);
    }

    public static class Deserializer implements JsonDeserializer<WeightedIntegerEntry> {

        @Override
        public WeightedIntegerEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject object = json.getAsJsonObject();

            return new WeightedIntegerEntry(object.get("weight").getAsInt(), object.get("value").getAsInt());
        }

    }

}
