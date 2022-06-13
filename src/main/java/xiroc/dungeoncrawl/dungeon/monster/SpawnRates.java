package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.Range;

import java.io.IOException;
import java.io.InputStreamReader;

public class SpawnRates {

    private static Range[] DELAY, AMOUNT;

    public static Range getAmount(int level) {
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
        ResourceLocation file = DungeonCrawl.locate("monster/spawn_rates.json");
        DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
        Resource resource = resourceManager.getResource(file).orElseThrow(() -> new DatapackLoadException("Missing file: " + file));
        try {
            JsonObject data = JsonParser.parseReader(new InputStreamReader(resource.open())).getAsJsonObject();
            loadLevel(data, file, 0);
            loadLevel(data, file, 1);
            loadLevel(data, file, 2);
            loadLevel(data, file, 3);
            loadLevel(data, file, 4);
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
            AMOUNT[level] = new Range(amount.get("min").getAsInt(), amount.get("max").getAsInt());
        } else {
            throw new DatapackLoadException("Missing entry " + entry + " in " + resource);
        }
    }

}
