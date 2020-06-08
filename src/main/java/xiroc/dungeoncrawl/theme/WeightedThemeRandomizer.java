package xiroc.dungeoncrawl.theme;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.lang.reflect.Type;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.theme.Theme.ThemeRandomizer;
import xiroc.dungeoncrawl.util.WeightedIntegerEntry;
import xiroc.dungeoncrawl.util.WeightedRandomInteger;

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
