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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.Range;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class RandomPotionEffect {

    private static float[] CHANCES;
    private static Range[] ROLLS;
    private static WeightedRandomPotionEffect[] EFFECTS;
    private static PotionEffect[][] GUARANTEED_EFFECTS;

    /**
     * Loads all potion effect files.
     */
    public static void loadJson(ResourceManager resourceManager) {
        CHANCES = new float[5];
        ROLLS = new Range[5];
        EFFECTS = new WeightedRandomPotionEffect[5];
        GUARANTEED_EFFECTS = new PotionEffect[5][];

        try {
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_1.json"), 0);
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_2.json"), 1);
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_3.json"), 2);
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_4.json"), 3);
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_5.json"), 4);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load the monster potion effect files.");
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to load a single potion effect file.
     */
    private static void loadFile(ResourceManager resourceManager, ResourceLocation file, int stage) throws IOException {
        Resource resource = resourceManager.getResource(file).orElseThrow(() -> new DatapackLoadException("Missing file: " + file));
        try {
            DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
            JsonObject object = JsonParser.parseReader(new JsonReader(new InputStreamReader(resource.open()))).getAsJsonObject();

            if (object.has("chance")) {
                CHANCES[stage] = object.get("chance").getAsFloat();
            } else {
                throw new DatapackLoadException("Missing entry 'chance' in " + file);
            }

            if (object.has("rolls")) {
                JsonObject amount = object.getAsJsonObject("rolls");
                ROLLS[stage] = new Range(amount.get("min").getAsInt(), amount.get("max").getAsInt());
            } else {
                throw new DatapackLoadException("Missing entry 'rolls' in " + file);
            }

            if (object.has("effects")) {
                EFFECTS[stage] = WeightedRandomPotionEffect.fromJson(object.getAsJsonArray("effects"));
            } else {
                throw new DatapackLoadException("Missing entry 'effects' in " + file);
            }

            if (object.has("guaranteed")) {
                JsonArray array = object.getAsJsonArray("guaranteed");
                GUARANTEED_EFFECTS[stage] = new PotionEffect[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    JsonObject effect = array.get(i).getAsJsonObject();
                    Range amplifier = effect.has("amplifier") ?
                            new Range(effect.getAsJsonObject("amplifier").get("min").getAsInt(),
                                    effect.getAsJsonObject("amplifier").get("max").getAsInt())
                            : new Range(0, 0);
                    GUARANTEED_EFFECTS[stage][i] = new PotionEffect(
                            BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(effect.get("effect").getAsString())).orElseThrow(),
                            effect.get("duration").getAsInt(), amplifier);
                }
            }
        } catch (Exception e) {
            DungeonCrawl.LOGGER.error("Failed to load {} ", file);
            e.printStackTrace();
        }

    }

    /**
     * @return An NBT list of potion effects or null.
     */
    @Nullable
    public static ListTag createPotionEffects(RandomSource rand, int stage) {
        if (stage > 4)
            stage = 4;
        boolean chance = rand.nextFloat() < CHANCES[stage];
        boolean guaranteed = GUARANTEED_EFFECTS[stage] != null;
        if (chance || guaranteed) {
            ListTag list = new ListTag();
            if (chance) {
                int rolls = ROLLS[stage].nextInt(rand);
                if (rolls > 0) {
                    Set<Holder<MobEffect>> effects = new HashSet<>();
                    loop:
                    for (int i = 0; i < rolls; i++) {
                        WeightedRandomPotionEffect.WeightedEntry effect = EFFECTS[stage].roll(rand);
                        if (effect != null) {
                            if (effects.add(effect.effect())) {
                                list.add(toNBT(effect.effect(), effect.duration(), effect.amplifier().nextInt(rand)));
                            }
                        }
                    }
                }
            }
            if (guaranteed) {
                for (PotionEffect effect : GUARANTEED_EFFECTS[stage]) {
                    list.add(toNBT(effect.effect, effect.duration, effect.amplifier.nextInt(rand)));
                }
            }
            return list;
        }

        return null;
    }

    /**
     * Creates an NBT-representation of the given effect.
     */
    private static Tag toNBT(Holder<MobEffect> effect, int duration, int amplifier) {
        return new MobEffectInstance(effect, duration, amplifier).save();
    }

    private record PotionEffect(Holder<MobEffect> effect, int duration,
                                Range amplifier) {
    }

}
