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

package xiroc.dungeoncrawl.dungeon.treasure;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.JsonConfig;
import xiroc.dungeoncrawl.util.IJsonConfigurable;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

/*
 * CURRENTLY UNUSED
 */
public class EnchantedBook implements IJsonConfigurable {

    public static final String KEY_ENCHANTMENTS_COMMON = "common_enchantments";
    public static final String KEY_ENCHANTMENTS_NORMAL = "normal_enchantments";
    public static final String KEY_ENCHANTMENTS_RARE = "rare_enchantments";

    public static final String[] KEYS = new String[]{KEY_ENCHANTMENTS_COMMON, KEY_ENCHANTMENTS_NORMAL,
            KEY_ENCHANTMENTS_RARE};

    public static final HashMap<String, Object> DEFAULTS;

    public static ResourceLocation[] ENCHANTMENTS_COMMON;
    public static ResourceLocation[] ENCHANTMENTS_NORMAL;
    public static ResourceLocation[] ENCHANTMENTS_RARE;

    static {
        DEFAULTS = new HashMap<String, Object>();
        DEFAULTS.put(KEY_ENCHANTMENTS_COMMON,
                new String[]{Enchantments.AQUA_AFFINITY.getRegistryName().toString(),
                        Enchantments.BANE_OF_ARTHROPODS.getRegistryName().toString(),
                        Enchantments.BLAST_PROTECTION.getRegistryName().toString(),
                        Enchantments.KNOCKBACK.getRegistryName().toString(),
                        Enchantments.PROJECTILE_PROTECTION.getRegistryName().toString(),
                        Enchantments.FIRE_PROTECTION.getRegistryName().toString(),
                        Enchantments.PROTECTION.getRegistryName().toString(),
                        Enchantments.UNBREAKING.getRegistryName().toString(),
                        Enchantments.SMITE.getRegistryName().toString()});
        DEFAULTS.put(KEY_ENCHANTMENTS_NORMAL, new String[]{Enchantments.DEPTH_STRIDER.getRegistryName().toString(),
                Enchantments.FIRE_ASPECT.getRegistryName().toString(),
                Enchantments.IMPALING.getRegistryName().toString(), Enchantments.THORNS.getRegistryName().toString(),
                Enchantments.MULTISHOT.getRegistryName().toString(),
                Enchantments.SHARPNESS.getRegistryName().toString(),
                Enchantments.EFFICIENCY.getRegistryName().toString(), Enchantments.LURE.getRegistryName().toString(),
                Enchantments.POWER.getRegistryName().toString(), Enchantments.PUNCH.getRegistryName().toString(),
                Enchantments.RESPIRATION.getRegistryName().toString()});
        DEFAULTS.put(KEY_ENCHANTMENTS_RARE, new String[]{Enchantments.FIRE_ASPECT.getRegistryName().toString(),
                Enchantments.FLAME.getRegistryName().toString(), Enchantments.FORTUNE.getRegistryName().toString(),
                Enchantments.CHANNELING.getRegistryName().toString(),
                Enchantments.INFINITY.getRegistryName().toString(), Enchantments.LOOTING.getRegistryName().toString(),
                Enchantments.LOYALTY.getRegistryName().toString(), Enchantments.RIPTIDE.getRegistryName().toString(),
                Enchantments.LUCK_OF_THE_SEA.getRegistryName().toString(),
                Enchantments.PIERCING.getRegistryName().toString(),
                Enchantments.QUICK_CHARGE.getRegistryName().toString(),
                Enchantments.SILK_TOUCH.getRegistryName().toString(),
                Enchantments.SWEEPING.getRegistryName().toString()});
    }

    @Override
    public File getFile() {
        return FMLPaths.CONFIGDIR.get().resolve("DungeonCrawl/loot/enchanted_book.json").toFile();
    }

    @Override
    public void load(JsonObject object, File file) {
        ENCHANTMENTS_COMMON = JsonConfig.toResourceLocationArray(DungeonCrawl.GSON
                .fromJson(JsonConfig.getOrRewrite(object, KEY_ENCHANTMENTS_COMMON, this), String[].class));
        ENCHANTMENTS_NORMAL = JsonConfig.toResourceLocationArray(DungeonCrawl.GSON
                .fromJson(JsonConfig.getOrRewrite(object, KEY_ENCHANTMENTS_NORMAL, this), String[].class));
        ENCHANTMENTS_RARE = JsonConfig.toResourceLocationArray(DungeonCrawl.GSON
                .fromJson(JsonConfig.getOrRewrite(object, KEY_ENCHANTMENTS_RARE, this), String[].class));
    }

    @Override
    public JsonObject create(JsonObject object) {
        object.add(KEY_ENCHANTMENTS_COMMON, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_ENCHANTMENTS_COMMON)));
        object.add(KEY_ENCHANTMENTS_NORMAL, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_ENCHANTMENTS_NORMAL)));
        object.add(KEY_ENCHANTMENTS_RARE, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_ENCHANTMENTS_RARE)));
        return object;
    }

    @Override
    public HashMap<String, Object> getDefaults() {
        return DEFAULTS;
    }

    @Override
    public String[] getKeys() {
        return KEYS;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    public static Enchantment getRandomEnchantment(Random rand, int lootLevel) {
        if (rand.nextDouble() < getChance(2, lootLevel))
            return ForgeRegistries.ENCHANTMENTS.getValue(ENCHANTMENTS_RARE[rand.nextInt(ENCHANTMENTS_RARE.length)]);
        if (rand.nextDouble() < getChance(1, lootLevel))
            return ForgeRegistries.ENCHANTMENTS.getValue(ENCHANTMENTS_NORMAL[rand.nextInt(ENCHANTMENTS_NORMAL.length)]);
        return ForgeRegistries.ENCHANTMENTS.getValue(ENCHANTMENTS_COMMON[rand.nextInt(ENCHANTMENTS_COMMON.length)]);
    }

    public static double getChance(int rarity, int lootLevel) {
        return (1D + lootLevel - rarity) / (1D + lootLevel);
    }

}
