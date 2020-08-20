package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

public class RandomMonster {

    public static final HashMap<EntityType<?>, MobNBTProcessor> NBT_PROCESSORS = new HashMap<>();

    public static WeightedRandomEntity[] COMMON, RARE;

    private static final String[] VILLAGER_TYPES = {"desert", "jungle", "plains", "savanna", "snow", "swamp", "taiga"};
    private static final String[] VILLAGER_PROFESSIONS = {"armorer", "butcher", "cartographer", "cleric", "farmer", "fisherman",
            "fletcher", "leatherworker", "librarian", "mason", "nitwit", "shepherd", "toolsmith", "weaponsmith"};

    static {
        NBT_PROCESSORS.put(EntityType.WITHER_SKELETON, (nbt, rand, stage) -> {
            nbt.putString("DeathLootTable", Loot.WITHER_SKELETON.toString());
            nbt.putLong("DeathLootTableSeed", rand.nextInt());
        });
        NBT_PROCESSORS.put(EntityType.ZOMBIE_VILLAGER, (nbt, rand, stage) -> {
            CompoundNBT offers = new CompoundNBT();
            offers.put("Recipes", new ListNBT());
            nbt.put("Offers", offers);
            nbt.putInt("Xp", 250);
            nbt.putBoolean("Willing", false);
            CompoundNBT villagerData = new CompoundNBT();
            villagerData.putString("type", "minecraft:" + VILLAGER_TYPES[rand.nextInt(VILLAGER_TYPES.length)]);
            villagerData.putString("profession", "minecraft:" + VILLAGER_PROFESSIONS[rand.nextInt(VILLAGER_PROFESSIONS.length)]);
            villagerData.putInt("level", 6);
            nbt.put("VillagerData", villagerData);
        });
    }

    /**
     * Loads all monster entity files.
     *
     * @param resourceManager
     */
    public static void loadJson(IResourceManager resourceManager) {
        COMMON = new WeightedRandomEntity[5];
        RARE = new WeightedRandomEntity[5];

        JsonParser parser = new JsonParser();
        try {
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_1.json"), parser, 0);
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_2.json"), parser, 1);
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_3.json"), parser, 2);
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_4.json"), parser, 3);
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_5.json"), parser, 4);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load the monster entity files.");
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to load a single entity file.
     *
     * @param resourceManager
     * @param file
     * @param parser
     * @param stage
     */
    private static void loadEntityFile(IResourceManager resourceManager, ResourceLocation file, JsonParser parser, int stage) throws IOException {
        if (resourceManager.hasResource(file)) {
            DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
            JsonObject object = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()))).getAsJsonObject();

            if (object.has("common")) {
                COMMON[stage] = WeightedRandomEntity.fromJson(object.getAsJsonArray("common"));
            } else {
                DungeonCrawl.LOGGER.warn("Missing entry 'common' in {}", file.toString());
                COMMON[stage] = WeightedRandomEntity.EMPTY;
            }

            if (object.has("rare")) {
                RARE[stage] = WeightedRandomEntity.fromJson(object.getAsJsonArray("rare"));
            } else {
                DungeonCrawl.LOGGER.warn("Missing entry 'rare' in {}", file.toString());
                RARE[stage] = WeightedRandomEntity.EMPTY;
            }
        } else {
            throw new FileNotFoundException("Missing file: " + file.toString());
        }
    }

    public static EntityType<?> randomMonster(Random rand, int stage) {
        if (stage > 4)
            stage = 4;
        if (rand.nextFloat() < 0.1) {
            EntityType<?> monster = RARE[stage].roll(rand);
            if (monster != null) {
                return monster;
            } else {
                return COMMON[stage].roll(rand);
            }
        }
        return COMMON[stage].roll(rand);
    }

    @FunctionalInterface
    public interface MobNBTProcessor {

        void process(CompoundNBT nbt, Random rand, int stage);

    }

}
