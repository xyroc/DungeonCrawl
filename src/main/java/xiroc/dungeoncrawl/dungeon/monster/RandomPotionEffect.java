package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.DungeonCrawl;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class RandomPotionEffect {

    public static float[] CHANCES;
    public static RandomValueRange[] AMOUNTS;
    public static WeightedRandomPotionEffect[] EFFECTS;

    /**
     * Loads all potion effect files.
     *
     * @param resourceManager
     */
    public static void loadJson(IResourceManager resourceManager) {
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

                if (object.has("count")) {
                    JsonObject amount = object.getAsJsonObject("count");
                    AMOUNTS[stage] = new RandomValueRange(amount.get("min").getAsInt(), amount.get("max").getAsInt());
                } else {
                    DungeonCrawl.LOGGER.warn("Missing entry 'count' in {}", file.toString());
                    AMOUNTS[stage] = new RandomValueRange(0);
                }

                if (object.has("effects")) {
                    EFFECTS[stage] = WeightedRandomPotionEffect.fromJson(object.getAsJsonArray("effects"));
                } else {
                    DungeonCrawl.LOGGER.warn("Missing entry 'effects' in {}", file.toString());
                    EFFECTS[stage] = WeightedRandomPotionEffect.EMPTY;
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
            int count = AMOUNTS[stage].generateInt(rand);
            if (count > 0) {
                Effect[] effects = new Effect[count - 1];
                loop:
                for (int i = 0; i < count; i++) {
                    WeightedRandomPotionEffect.WeightedEntry effect = EFFECTS[stage].roll(rand);
                    if (effect != null) {
                        for (Effect value : effects) { // Skip duplicates
                            if (value == effect.effect)
                                continue loop;
                        }
                        if (i < count - 1)
                            effects[i] = effect.effect;
                        entity.addPotionEffect(new EffectInstance(effect.effect, effect.duration, effect.level.generateInt(rand)));
                    }
                }
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
        if (rand.nextFloat() < CHANCES[stage]) {
            int count = AMOUNTS[stage].generateInt(rand);
            if (count > 0) {
                ListNBT list = new ListNBT();
                Effect[] effects = new Effect[count - 1];
                loop:
                for (int i = 0; i < count; i++) {
                    WeightedRandomPotionEffect.WeightedEntry effect = EFFECTS[stage].roll(rand);
                    if (effect != null) {
                        for (Effect value : effects) { // Skip duplicates
                            if (value == effect.effect)
                                continue loop;
                        }
                        if (i < count - 1)
                            effects[i] = effect.effect;
                        list.add(toNBT(effect.effect, effect.duration, effect.level.generateInt(rand)));
                    }
                }
                return list;
            }
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

}
