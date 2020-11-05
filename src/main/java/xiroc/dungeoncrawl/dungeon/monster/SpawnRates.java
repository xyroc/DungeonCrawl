package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.DungeonCrawl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SpawnRates {

    private static RandomValueRange[] DELAY, AMOUNT;

    public static RandomValueRange getAmount(int level) {
        if (level < 0) {
            return AMOUNT[0];
        }
        if (level > 4) {
            return AMOUNT[4];
        }
        return AMOUNT[level];
    }

    public static RandomValueRange getDelay(int level) {
        if (level < 0) {
            return DELAY[0];
        }
        if (level > 4) {
            return DELAY[4];
        }
        return DELAY[level];
    }

    public static void loadJson(IResourceManager resourceManager) {
        DELAY = new RandomValueRange[5];
        AMOUNT = new RandomValueRange[5];
        ResourceLocation resource = DungeonCrawl.locate("monster/spawn_rates.json");
        DungeonCrawl.LOGGER.debug("Loading {}", resource.toString());
        try {
            if (resourceManager.hasResource(resource)) {
                JsonObject data = new JsonParser().parse(new InputStreamReader(resourceManager.getResource(resource).getInputStream())).getAsJsonObject();
                loadLevel(data, resource, 0);
                loadLevel(data, resource, 1);
                loadLevel(data, resource, 2);
                loadLevel(data, resource, 3);
                loadLevel(data, resource, 4);
            } else {
                throw new FileNotFoundException("Missing file " + resource.toString());
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

            DELAY[level] = new RandomValueRange(delay.get("min").getAsInt(), delay.get("max").getAsInt());
            AMOUNT[level] = new RandomValueRange(amount.get("min").getAsInt(), amount.get("max").getAsInt());
        } else {
            DungeonCrawl.LOGGER.warn("Missing entry {} in {}", entry, resource.toString());
            // Fallback to default values
            DELAY[level] = new RandomValueRange(200, 180);
            AMOUNT[level] = new RandomValueRange(1, 3);
        }
    }

}
