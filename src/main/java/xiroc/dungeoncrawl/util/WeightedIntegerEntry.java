package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.util.Tuple;

public class WeightedIntegerEntry extends Tuple<Integer, Integer> {

	public WeightedIntegerEntry(Integer aIn, Integer bIn) {
		super(aIn, bIn);
	}
	
	public static class Deserializer implements JsonDeserializer<WeightedIntegerEntry>{

		@Override
		public WeightedIntegerEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			
			JsonObject object = json.getAsJsonObject();
			
			return new WeightedIntegerEntry(object.get("weight").getAsInt(), object.get("value").getAsInt());
		}
		
	}

}
