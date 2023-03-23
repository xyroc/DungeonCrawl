package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.random.value.RandomValue;
import xiroc.dungeoncrawl.util.random.value.Range;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SpawnRates {

    private static Range[] DELAY;
    private static RandomValue[] AMOUNT;

    public static RandomValue getAmount(int level) {
        if (level < 0) {
            return AMOUNT[0];
        }
        if (level > 4) {
            return AMOUNT[4];
        }
        return AMOUNT[level];
    }

    public static Range getDelay(int level) {
        if (level < 0) {
            return DELAY[0];
        }
        if (level > 4) {
            return DELAY[4];
        }
        return DELAY[level];
    }

    public static void loadJson(ResourceManager resourceManager) {
        DELAY = new Range[5];
        AMOUNT = new Range[5];
        ResourceLocation resource = DungeonCrawl.locate("monster/spawn_rates.json");
        DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
        try {
            if (resourceManager.hasResource(resource)) {
                JsonObject data = JsonParser.parseReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream())).getAsJsonObject();
                loadLevel(data, resource, 0);
                loadLevel(data, resource, 1);
                loadLevel(data, resource, 2);
                loadLevel(data, resource, 3);
                loadLevel(data, resource, 4);
            } else {
                throw new FileNotFoundException("Missing file " + resource);
            }
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("An error occurred whilst trying to load {}", resource.toString());
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to load a single level instance from a spawn rate file.
     */
    private static void loadLevel(JsonObject object, ResourceLocation resource, int level) {
        String entry = "level_" + (level + 1);
        if (object.has(entry)) {
            JsonObject data = object.getAsJsonObject(entry);
            JsonObject delay = data.getAsJsonObject("delay");
            JsonObject amount = data.getAsJsonObject("amount");

            DELAY[level] = new Range(delay.get("min").getAsInt(), delay.get("max").getAsInt());
            AMOUNT[level] = RandomValue.deserialize(amount);
        } else {
            throw new DatapackLoadException("Missing entry " + entry + " in " + resource);
        }
    }

}
