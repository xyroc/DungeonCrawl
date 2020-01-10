package xiroc.dungeoncrawl.theme;

import java.lang.reflect.Type;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.part.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

/*
 * A class used to load and store custom themes from/as json.
 */
public class JsonTheme {
	
	public JsonBaseTheme theme;
	public JsonSubTheme subTheme;

	private static final Logger LOGGER = LogManager.getLogger("DungeonCrawl/JsonThemeDeserializer");

	public static class JsonBaseTheme {

		public IBlockStateProvider wall, floor, stairs, material, vanillaWall, column;

		public static class Deserializer implements JsonDeserializer<JsonBaseTheme> {

			@Override
			public JsonBaseTheme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {

				JsonObject object = json.getAsJsonObject();

				JsonBaseTheme theme = new JsonBaseTheme();

				theme.wall = JsonTheme.deserialize(object, "wall");
				theme.floor = JsonTheme.deserialize(object, "floor");
				theme.stairs = JsonTheme.deserialize(object, "stairs");
				theme.material = JsonTheme.deserialize(object, "material");
				theme.vanillaWall = JsonTheme.deserialize(object, "vanillaWall");
				theme.column = JsonTheme.deserialize(object, "column");

				return theme;
			}

		}

		public Theme toTheme() {
			return new Theme(null, wall, floor, stairs, material, vanillaWall, column);
		}

	}

	public static class JsonSubTheme {

		public IBlockStateProvider wallLog, trapDoor, torchDark, door, material;

		public static class Deserializer implements JsonDeserializer<JsonSubTheme> {

			@Override
			public JsonSubTheme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {

				JsonObject object = json.getAsJsonObject();

				JsonSubTheme theme = new JsonSubTheme();

				theme.wallLog = JsonTheme.deserialize(object, "wallLog");
				theme.trapDoor = JsonTheme.deserialize(object, "trapDoor");
				theme.torchDark = JsonTheme.deserialize(object, "torch");
				theme.door = JsonTheme.deserialize(object, "door");
				theme.material = JsonTheme.deserialize(object, "material");

				return theme;
			}

		}

		public SubTheme toSubTheme() {
			return new SubTheme(wallLog, trapDoor, torchDark, door, material);
		}

	}

	public static IBlockStateProvider deserialize(JsonObject base, String name) {
		if (!base.has(name)) {
			LOGGER.error("Missing BlockState Provider \"{}\"", name);
			return null;
		}
		JsonObject object = (JsonObject) base.get(name);
		if (object.has("type")) {
			String type = object.get("type").getAsString();
			if (type.equalsIgnoreCase("WeightedRandomBlock")) {
				JsonArray blockObjects = object.get("blocks").getAsJsonArray();
				TupleIntBlock[] blocks = new TupleIntBlock[blockObjects.size()];

				int i = 0;
				Iterator<JsonElement> iterator = blockObjects.iterator();
				while (iterator.hasNext()) {
					JsonObject element = (JsonObject) iterator.next();
					blocks[i++] = new TupleIntBlock(element.get("weight").getAsInt(),
							ForgeRegistries.BLOCKS.getValue(new ResourceLocation(element.get("block").getAsString())));
				}

				return WeightedRandomBlock.of(blocks);
			} else if (type.equalsIgnoreCase("Block")) {
				BlockState state = ForgeRegistries.BLOCKS
						.getValue(new ResourceLocation(object.get("block").getAsString())).getDefaultState();
				return () -> state;
			} else {
				LOGGER.error("Failed to load BlockState Provider {}: Unknown type {}.", object, type);
				return null;
			}
		} else {
			LOGGER.error("Failed to load a BlockState Provider: Type not specified.");
			return null;
		}
	}

	private static final class TupleIntBlock extends Tuple<Integer, Block> {

		public TupleIntBlock(Integer aIn, Block bIn) {
			super(aIn, bIn);
		}

	}

}
