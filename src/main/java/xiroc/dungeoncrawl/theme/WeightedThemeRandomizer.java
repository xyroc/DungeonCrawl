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

package xiroc.dungeoncrawl.theme;

import com.google.gson.*;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.theme.Theme.ThemeRandomizer;
import xiroc.dungeoncrawl.util.WeightedIntegerEntry;
import xiroc.dungeoncrawl.util.WeightedRandomInteger;

import java.lang.reflect.Type;
import java.util.Random;

public class WeightedThemeRandomizer implements ThemeRandomizer {

    public int base;
    public WeightedRandomInteger themes;

    public WeightedThemeRandomizer(WeightedIntegerEntry[] entries, int base) {
        this.themes = new WeightedRandomInteger(entries);
        this.base = base;
    }

    @Override
    public int randomize(Random rand, int base) {
        return themes.roll(rand);
    }

    public static class Deserializer implements JsonDeserializer<WeightedThemeRandomizer> {

        @Override
        public WeightedThemeRandomizer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject object = json.getAsJsonObject();

            int base = object.get("base").getAsInt();
            WeightedIntegerEntry[] entries = DungeonCrawl.GSON.fromJson(object.get("entries"), WeightedIntegerEntry[].class);

            return new WeightedThemeRandomizer(entries, base);
        }

    }

}
