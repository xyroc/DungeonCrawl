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
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class RandomPotionEffect {

    public static float[] CHANCES;
    public static RandomValueRange[] ROLLS;
    public static WeightedRandomPotionEffect[] EFFECTS;
    public static PotionEffect[][] GUARANTEED_EFFECTS;

    /**
     * Loads all potion effect files.
     *
     * @param resourceManager
     */
    public static void loadJson(IResourceManager resourceManager) {
        CHANCES = new float[5];
        ROLLS = new RandomValueRange[5];
        EFFECTS = new WeightedRandomPotionEffect[5];
        GUARANTEED_EFFECTS = new PotionEffect[5][];

        JsonParser parser = new JsonParser();
        try {
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_1.json"), parser, 0);
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_2.json"), parser, 1);
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_3.json"), parser, 2);
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_4.json"), parser, 3);
            loadFile(resourceManager, DungeonCrawl.locate("monster/potion_effects/stage_5.json"), parser, 4);
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load the monster potion effect files.");
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to load a single potion effect file.
     *
     * @param resourceManager
     * @param file
     * @param parser
     * @param stage
     */
    private static void loadFile(IResourceManager resourceManager, ResourceLocation file, JsonParser parser, int stage) throws IOException {
        if (resourceManager.hasResource(file)) {
            try {
                DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
                JsonObject object = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()))).getAsJsonObject();

                if (object.has("chance")) {
                    CHANCES[stage] = object.get("chance").getAsFloat();
                } else {
                    DungeonCrawl.LOGGER.warn("Missing entry 'chance' in {}", file.toString());
                    CHANCES[stage] = 0F;
                }

                if (object.has("rolls")) {
                    JsonObject amount = object.getAsJsonObject("rolls");
                    ROLLS[stage] = new RandomValueRange(amount.get("min").getAsInt(), amount.get("max").getAsInt());
                } else {
                    DungeonCrawl.LOGGER.warn("Missing entry 'rolls' in {}", file.toString());
                    ROLLS[stage] = new RandomValueRange(0);
                }

                if (object.has("effects")) {
                    EFFECTS[stage] = WeightedRandomPotionEffect.fromJson(object.getAsJsonArray("effects"));
                } else {
                    DungeonCrawl.LOGGER.warn("Missing entry 'effects' in {}", file.toString());
                    EFFECTS[stage] = WeightedRandomPotionEffect.EMPTY;
                }

                if (object.has("guaranteed")) {
                    JsonArray array = object.getAsJsonArray("guaranteed");
                    GUARANTEED_EFFECTS[stage] = new PotionEffect[array.size()];
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject effect = array.get(i).getAsJsonObject();
                        RandomValueRange amplifier = effect.has("amplifier") ?
                                new RandomValueRange(effect.getAsJsonObject("amplifier").get("min").getAsInt(),
                                        effect.getAsJsonObject("amplifier").get("max").getAsInt())
                                : new RandomValueRange(0);
                        GUARANTEED_EFFECTS[stage][i] = new PotionEffect(ForgeRegistries.POTIONS.getValue(new ResourceLocation(effect.get("effect").getAsString())),
                                effect.get("duration").getAsInt(), amplifier);
                    }
                }

            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}" + file.toString());
                e.printStackTrace();
            }
        } else {
            throw new FileNotFoundException("Missing file " + file.toString());
        }
    }

    /**
     * Applies random potion effects to the given monster entity.
     *
     * @param entity
     * @param rand
     * @param stage
     */
    public static void applyPotionEffects(MonsterEntity entity, Random rand, int stage) {
        if (stage > 4)
            stage = 4;
        if (rand.nextFloat() < CHANCES[stage]) {
            int rolls = ROLLS[stage].generateInt(rand);
            if (rolls > 0) {
                Effect[] effects = new Effect[rolls - 1];
                loop:
                for (int i = 0; i < rolls; i++) {
                    WeightedRandomPotionEffect.WeightedEntry effect = EFFECTS[stage].roll(rand);
                    if (effect != null) {
                        for (Effect value : effects) { // Skip duplicates
                            if (value == effect.effect)
                                continue loop;
                        }
                        if (i < rolls - 1)
                            effects[i] = effect.effect;
                        entity.addPotionEffect(new EffectInstance(effect.effect, effect.duration, effect.amplifier.generateInt(rand)));
                    }
                }
            }
        }
        if (GUARANTEED_EFFECTS[stage] != null) {
            for (PotionEffect effect : GUARANTEED_EFFECTS[stage]) {
                entity.addPotionEffect(new EffectInstance(effect.effect, effect.duration, effect.amplifier.generateInt(rand)));
            }
        }
    }

    /**
     * @param rand
     * @param stage
     * @return An NBT list of potion effects or null.
     */
    @Nullable
    public static ListNBT createPotionEffects(Random rand, int stage) {
        if (stage > 4)
            stage = 4;
        boolean chance = rand.nextFloat() < CHANCES[stage];
        boolean guaranteed = GUARANTEED_EFFECTS[stage] != null;
        if (chance || guaranteed) {
            ListNBT list = new ListNBT();
            if (chance) {
                int rolls = ROLLS[stage].generateInt(rand);
                if (rolls > 0) {
                    Effect[] effects = new Effect[rolls - 1];
                    loop:
                    for (int i = 0; i < rolls; i++) {
                        WeightedRandomPotionEffect.WeightedEntry effect = EFFECTS[stage].roll(rand);
                        if (effect != null) {
                            for (Effect value : effects) { // Skip duplicates
                                if (value == effect.effect)
                                    continue loop;
                            }
                            if (i < rolls - 1)
                                effects[i] = effect.effect;
                            list.add(toNBT(effect.effect, effect.duration, effect.amplifier.generateInt(rand)));
                        }
                    }
                }
            }
            if (guaranteed) {
                for (PotionEffect effect : GUARANTEED_EFFECTS[stage]) {
                    list.add(toNBT(effect.effect, effect.duration, effect.amplifier.generateInt(rand)));
                }
            }
            return list;
        }

        return null;
    }

    /**
     * Creates an NBT-representation of the given effect.
     *
     * @param effect
     * @param duration
     * @param amplifier
     */
    private static CompoundNBT toNBT(Effect effect, int duration, int amplifier) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("Id", Effect.getId(effect));
        nbt.putInt("Duration", duration);
        nbt.putInt("Amplifier", amplifier);
        return nbt;
    }

    private static class PotionEffect {

        public final Effect effect;
        public final int duration;
        public final RandomValueRange amplifier;

        public PotionEffect(Effect effect, int duration, RandomValueRange amplifier) {
            this.effect = effect;
            this.duration = duration;
            this.amplifier = amplifier;
        }
    }

}
