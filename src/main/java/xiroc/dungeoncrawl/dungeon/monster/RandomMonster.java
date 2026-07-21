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
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.storage.ValueOutput;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class RandomMonster {

    public static final Hashtable<EntityType<?>, MobNBTPatcher> NBT_PATCHERS = new Hashtable<>();

    public static WeightedRandomEntity[] COMMON, RARE;


    static {
        NBT_PATCHERS.put(EntityTypes.WITHER_SKELETON, (nbt, rand, stage) -> {
            nbt.putString("DeathLootTable", Loot.WITHER_SKELETON.identifier().toString());
            nbt.putLong("DeathLootTableSeed", rand.nextInt());
        });
    }

    /**
     * Loads all monster entity files.
     */
    public static void loadJson(ResourceManager resourceManager) {
        COMMON = new WeightedRandomEntity[5];
        RARE = new WeightedRandomEntity[5];

        try {
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_1.json"), 0);
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_2.json"), 1);
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_3.json"), 2);
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_4.json"), 3);
            loadEntityFile(resourceManager, DungeonCrawl.locate("monster/entities/stage_5.json"), 4);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load the monster entity files.");
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to load a single entity file.
     */
    private static void loadEntityFile(ResourceManager resourceManager, Identifier file, int stage) throws IOException {
        DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
        Resource resource = resourceManager.getResource(file).orElseThrow(() -> new DatapackLoadException("Missing file: " + file));
        JsonObject object = JsonParser.parseReader(new JsonReader(new InputStreamReader(resource.open()))).getAsJsonObject();

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
    }

    public static EntityType<?> randomMonster(RandomSource rand, int stage) {
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

        void patch(ValueOutput nbt, RandomSource rand, int stage);

    }

}
