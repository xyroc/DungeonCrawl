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

package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
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
import java.util.UUID;

public class RandomMonster {

    public static final HashMap<EntityType<?>, MobNBTPatcher> NBT_PATCHERS = new HashMap<>();

    public static WeightedRandomEntity[] COMMON, RARE;

//    private static final String[] VILLAGER_TYPES = {"desert", "jungle", "plains", "savanna", "snow", "swamp", "taiga"};
//    private static final String[] VILLAGER_PROFESSIONS = {"armorer", "butcher", "cartographer", "cleric", "farmer", "fisherman",
//            "fletcher", "leatherworker", "librarian", "mason", "nitwit", "shepherd", "toolsmith", "weaponsmith"};

    private static final CompoundNBT VILLAGER_OFFERS;
    private static final ListNBT VILLAGER_GOSSIPS;

    static {
        VILLAGER_OFFERS = new CompoundNBT();
        ListNBT recipes = new ListNBT();
        CompoundNBT offer = new CompoundNBT();
        offer.putBoolean("rewardExp", false);
        offer.putInt("maxUses", 1);
        offer.putInt("uses", 1);
        offer.putInt("xp", 0);
        offer.putInt("specialPrice", 0);
        offer.putInt("demand", 0);
        offer.putFloat("priceMultiplier", 0F);
        CompoundNBT grassBlock = new ItemStack(Blocks.GRASS_BLOCK).write(new CompoundNBT());
        offer.put("buy", grassBlock);
        offer.put("sell", grassBlock);
        recipes.add(offer);
        VILLAGER_OFFERS.put("Recipes", recipes);

        VILLAGER_GOSSIPS = new ListNBT();
        CompoundNBT gossip = new CompoundNBT();
        gossip.putString("Type", "trading");
        gossip.putInt("Value", 1);
        gossip.putUniqueId("Target", UUID.fromString("00000000-0000-0000-0000-000000000000"));
        VILLAGER_GOSSIPS.add(gossip);

        NBT_PATCHERS.put(EntityType.WITHER_SKELETON, (nbt, rand, stage) -> {
            nbt.putString("DeathLootTable", Loot.WITHER_SKELETON.toString());
            nbt.putLong("DeathLootTableSeed", rand.nextInt());
        });

        NBT_PATCHERS.put(EntityType.ZOMBIE_VILLAGER, (nbt, rand, stage) -> {
            nbt.put("Offers", VILLAGER_OFFERS.copy());
            nbt.putInt("Xp", 1);
            nbt.putBoolean("Willing", false);
//            CompoundNBT villagerData = new CompoundNBT();
//            villagerData.putString("type", "minecraft:" + VILLAGER_TYPES[rand.nextInt(VILLAGER_TYPES.length)]);
//            villagerData.putString("profession", "minecraft:" + VILLAGER_PROFESSIONS[rand.nextInt(VILLAGER_PROFESSIONS.length)]);
//            villagerData.putInt("level", 1);
//            nbt.put("Gossips", VILLAGER_GOSSIPS.copy());
//            nbt.put("VillagerData", villagerData);
        });
    }

    /**
     * Loads all monster entity files.
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
    public interface MobNBTPatcher {

        void patch(CompoundNBT nbt, Random rand, int stage);

    }

}
