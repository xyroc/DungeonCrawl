package xiroc.dungeoncrawl.dungeon.monster;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.WeightedRandomItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class RandomEquipment {

    public static final int HIGHEST_STAGE = 4;

    public static final int[] ARMOR_COLORS = new int[]{11546150, 16701501, 3949738, 6192150, 16351261, 16383998,
            15961002, 1908001, 8439583, 4673362, 1481884, 8991416, 3847130};

    public static WeightedRandomItem[] HELMET, CHESTPLATE, LEGGINGS, BOOTS, MELEE_WEAPON, RANGED_WEAPON;

//    public static final IRandom<ItemStack> BOW = (rand) -> {
//        ItemStack item = new ItemStack(getItem(JsonConfig.BOWS[rand.nextInt(JsonConfig.BOWS.length)]));
//        if (item.getItem() == null)
//            return ItemStack.EMPTY;
//        applyDamage(item, rand);
//        return item;
//    };
//
//    public static final IRandom<ItemStack> SWORD = (rand) -> {
//        ItemStack item = new ItemStack(
//                getItem(rand.nextFloat() < 0.05 ? JsonConfig.SWORDS_RARE[rand.nextInt(JsonConfig.SWORDS_RARE.length)]
//                        : JsonConfig.SWORDS[rand.nextInt(JsonConfig.SWORDS.length)]));
//        if (item.getItem() == null)
//            return ItemStack.EMPTY;
//        applyDamage(item, rand);
//        return item;
//    };
//
//    public static final IRandom<ItemStack> PICKAXE = (rand) -> {
//        ItemStack item = new ItemStack(getItem(JsonConfig.PICKAXES[rand.nextInt(JsonConfig.PICKAXES.length)]));
//        if (item.getItem() == null)
//            return ItemStack.EMPTY;
//        applyDamage(item, rand);
//        return item;
//    };
//
//    public static final IRandom<ItemStack> AXE = (rand) -> {
//        ItemStack item = new ItemStack(getItem(JsonConfig.AXES[rand.nextInt(JsonConfig.AXES.length)]));
//        if (item.getItem() == null)
//            return ItemStack.EMPTY;
//        applyDamage(item, rand);
//        return item;
//    };
//
//    public static final IRandom<ItemStack[]> ARMOR_1 = (rand) -> {
//        ItemStack[] items = new ItemStack[4];
//        ArmorSet armor = JsonConfig.ARMOR_SETS_1[rand.nextInt(JsonConfig.ARMOR_SETS_1.length)];
//        for (int i = 0; i < 4; i++) {
//            if (rand.nextFloat() < 0.5) {
//                ItemStack item = new ItemStack(getItem(armor.items[i]));
//                if (item.getItem() != null) {
//                    enchantArmor(item, rand, 0.25);
//                    applyDamage(item, rand);
//                    if (JsonConfig.COLORED_ARMOR.contains(armor.items[i].toString()))
//                        setArmorColor(item, getRandomColor(rand));
//                    items[i] = item;
//                } else
//                    items[i] = ItemStack.EMPTY;
//            } else
//                items[i] = ItemStack.EMPTY;
//        }
//        return items;
//    };
//
//    public static final IRandom<ItemStack[]> ARMOR_2 = (rand) -> {
//        ItemStack[] items = new ItemStack[4];
//        ArmorSet armor = JsonConfig.ARMOR_SETS_2[rand.nextInt(JsonConfig.ARMOR_SETS_2.length)];
//        for (int i = 0; i < 4; i++) {
//            if (rand.nextFloat() < 0.5) {
//                ItemStack item = new ItemStack(getItem(armor.items[i]));
//                if (item.getItem() != null) {
//                    enchantArmor(item, rand, 0.5);
//                    applyDamage(item, rand);
//                    if (JsonConfig.COLORED_ARMOR.contains(armor.items[i].toString()))
//                        setArmorColor(item, getRandomColor(rand));
//                    items[i] = item;
//                } else
//                    items[i] = ItemStack.EMPTY;
//            } else
//                items[i] = ItemStack.EMPTY;
//        }
//        return items;
//    };
//
//    public static final IRandom<ItemStack[]> ARMOR_3 = (rand) -> {
//        ItemStack[] items = new ItemStack[4];
//        ArmorSet armor = rand.nextFloat() < 0.05
//                ? JsonConfig.ARMOR_SETS_RARE[rand.nextInt(JsonConfig.ARMOR_SETS_RARE.length)]
//                : JsonConfig.ARMOR_SETS_3[rand.nextInt(JsonConfig.ARMOR_SETS_3.length)];
//        for (int i = 0; i < 4; i++) {
//            if (rand.nextFloat() < 0.5) {
//                ItemStack item = new ItemStack(getItem(armor.items[i]));
//                if (item.getItem() != null) {
//                    enchantArmor(item, rand, 1);
//                    applyDamage(item, rand);
//                    if (JsonConfig.COLORED_ARMOR.contains(armor.items[i].toString()))
//                        setArmorColor(item, getRandomColor(rand));
//                    items[i] = item;
//                } else
//                    items[i] = ItemStack.EMPTY;
//            } else
//                items[i] = ItemStack.EMPTY;
//        }
//        return items;
//    };

    /**
     * Initializes all WeightedRandomItems from the datapack.
     */
    public static void loadJson(IResourceManager resourceManager) {
        HELMET = new WeightedRandomItem[5];
        CHESTPLATE = new WeightedRandomItem[5];
        LEGGINGS = new WeightedRandomItem[5];
        BOOTS = new WeightedRandomItem[5];
        MELEE_WEAPON = new WeightedRandomItem[5];
        RANGED_WEAPON = new WeightedRandomItem[5];

        JsonParser parser = new JsonParser();

        try {
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_1.json"), parser, 0);
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_2.json"), parser, 1);
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_3.json"), parser, 2);
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_4.json"), parser, 3);
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_5.json"), parser, 4);

            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_1.json"), parser, 0);
            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_2.json"), parser, 1);
            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_3.json"), parser, 2);
            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_4.json"), parser, 3);
            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_5.json"), parser, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to load an armor file from json.
     *
     * @param resourceManager
     * @param file
     * @param parser
     * @param stage
     */
    private static void loadArmorFromJson(IResourceManager resourceManager, ResourceLocation file, JsonParser parser, int stage) throws IOException {
        if (resourceManager.hasResource(file)) {
            try {
                DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
                JsonObject object = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()))).getAsJsonObject();
                if (object.has("helmet")) {
                    HELMET[stage] = WeightedRandomItem.fromJson(object.getAsJsonArray("helmet"));
                } else {
                    DungeonCrawl.LOGGER.error("Missing entry 'helmet' in {}", file.toString());
                }

                if (object.has("chestplate")) {
                    CHESTPLATE[stage] = WeightedRandomItem.fromJson(object.getAsJsonArray("chestplate"));
                } else {
                    DungeonCrawl.LOGGER.error("Missing entry 'chestplate' in {}", file.toString());
                }

                if (object.has("leggings")) {
                    LEGGINGS[stage] = WeightedRandomItem.fromJson(object.getAsJsonArray("leggings"));
                } else {
                    DungeonCrawl.LOGGER.error("Missing entry 'leggings' in {}", file.toString());
                }

                if (object.has("boots")) {
                    BOOTS[stage] = WeightedRandomItem.fromJson(object.getAsJsonArray("boots"));
                } else {
                    DungeonCrawl.LOGGER.error("Missing entry 'boots' in {}", file.toString());
                }
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}" + file.toString());
                e.printStackTrace();
            }
        } else {
            throw new FileNotFoundException("Missing file: " + file.toString());
        }
    }

    /**
     * Convenience method to load a weapon file from json.
     *
     * @param resourceManager
     * @param file
     * @param parser
     * @param stage
     */
    private static void loadWeaponsFromJson(IResourceManager resourceManager, ResourceLocation file, JsonParser parser, int stage) throws IOException {
        if (resourceManager.hasResource(file)) {
            DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
            JsonObject object = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()))).getAsJsonObject();

            if (object.has("melee")) {
                MELEE_WEAPON[stage] = WeightedRandomItem.fromJson(object.getAsJsonArray("melee"));
            } else {
                DungeonCrawl.LOGGER.error("Missing entry 'melee' in {}", file.toString());
            }

            if (object.has("ranged")) {
                RANGED_WEAPON[stage] = WeightedRandomItem.fromJson(object.getAsJsonArray("ranged"));
            } else {
                DungeonCrawl.LOGGER.error("Missing entry 'ranged' in {}", file.toString());
            }
        } else {
            throw new FileNotFoundException("Missing file: " + file.toString());
        }
    }


    public static ItemStack[] createArmor(Random rand, int stage) {
        if (stage > 4)
            stage = 4;

        ItemStack[] items = new ItemStack[4];
        float chance = 0.4F + 0.15F * stage;

        if (HELMET[stage] != null) {
            if (rand.nextFloat() < chance) {
                Item item = HELMET[stage].roll(rand);
                items[3] = createItemStack(rand, item, stage);
                if (item instanceof DyeableArmorItem) {
                    setArmorColor(items[3], getRandomColor(rand));
                }
            } else {
                items[3] = ItemStack.EMPTY;
            }
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            items[3] = ItemStack.EMPTY;
        }

        if (CHESTPLATE[stage] != null) {
            if (rand.nextFloat() < chance) {
                Item item = CHESTPLATE[stage].roll(rand);
                items[2] = createItemStack(rand, item, stage);
                if (item instanceof DyeableArmorItem) {
                    setArmorColor(items[2], getRandomColor(rand));
                }
            } else {
                items[2] = ItemStack.EMPTY;
            }
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            items[2] = ItemStack.EMPTY;
        }

        if (LEGGINGS[stage] != null) {
            if (rand.nextFloat() < chance) {
                Item item = LEGGINGS[stage].roll(rand);
                items[1] = createItemStack(rand, item, stage);
                if (item instanceof DyeableArmorItem) {
                    setArmorColor(items[1], getRandomColor(rand));
                }
            } else {
                items[1] = ItemStack.EMPTY;
            }
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            items[1] = ItemStack.EMPTY;
        }

        if (BOOTS[stage] != null) {
            if (rand.nextFloat() < chance) {
                Item item = BOOTS[stage].roll(rand);
                items[0] = createItemStack(rand, item, stage);
                if (item instanceof DyeableArmorItem) {
                    setArmorColor(items[0], getRandomColor(rand));
                }
            } else {
                items[0] = ItemStack.EMPTY;
            }
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            items[0] = ItemStack.EMPTY;
        }

        //DungeonCrawl.LOGGER.info("ARMOR ({}) : {}", stage, Arrays.toString(items));

        return items;
    }

    public static ItemStack createItemStack(Random rand, Item item, int stage) {
        ItemStack itemStack = EnchantmentHelper.addRandomEnchantment(rand, new ItemStack(item), 10 + 3 * stage, false);
        applyDamage(itemStack, rand);
        return itemStack;
    }

    public static void applyDamage(ItemStack item, Random rand) {
        if (item.isDamageable())
            item.setDamage(rand.nextInt(item.getMaxDamage()));
    }

    public static void enchantItem(ItemStack item, Enchantment enchantment, double multiplier) {
        int level = (int) ((double) enchantment.getMaxLevel() * multiplier);
        if (level < 1)
            level = 1;
        item.addEnchantment(enchantment, level);
    }

//    public static ItemStack enchantItem(ItemStack item, Random rand, Enchantment enchantment, double multiplier) {
//        int minLevel = enchantment.getMinLevel();
//        int maxLevel = (int) ((double) enchantment.getMaxLevel() * multiplier);
//        item.addEnchantment(enchantment, minLevel < maxLevel ? minLevel + rand.nextInt(maxLevel - minLevel) : minLevel);
//        return item;
//    }
//
//    public static ItemStack enchantBow(ItemStack item, Random rand, double multiplier) {
//        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
//                .getValue(JsonConfig.BOW_ENCHANTMENTS[rand.nextInt(JsonConfig.BOW_ENCHANTMENTS.length)]);
//        enchantItem(item, rand, enchantment, multiplier);
//        return item;
//    }
//
//    public static ItemStack enchantArmor(ItemStack item, Random rand, double multiplier) {
//        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
//                .getValue(JsonConfig.ARMOR_ENCHANTMENTS[rand.nextInt(JsonConfig.ARMOR_ENCHANTMENTS.length)]);
//        enchantItem(item, rand, enchantment, multiplier);
//        return item;
//    }
//
//    public static ItemStack enchantSword(ItemStack item, Random rand, double multiplier) {
//        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
//                .getValue(JsonConfig.SWORD_ENCHANTMENTS[rand.nextInt(JsonConfig.SWORD_ENCHANTMENTS.length)]);
//        enchantItem(item, rand, enchantment, multiplier);
//        return item;
//    }
//
//    public static ItemStack enchantPickaxe(ItemStack item, Random rand, double multiplier) {
//        enchantSword(item, rand, multiplier);
//        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
//                .getValue(JsonConfig.PICKAXE_ENCHANTMENTS[rand.nextInt(JsonConfig.PICKAXE_ENCHANTMENTS.length)]);
//        enchantItem(item, rand, enchantment, multiplier);
//        return item;
//    }
//
//    public static ItemStack enchantAxe(ItemStack item, Random rand, double multiplier) {
//        enchantSword(item, rand, multiplier);
//        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
//                .getValue(JsonConfig.AXE_ENCHANTMENTS[rand.nextInt(JsonConfig.AXE_ENCHANTMENTS.length)]);
//        enchantItem(item, rand, enchantment, multiplier);
//        return item;
//    }

    public static ItemStack setArmorColor(ItemStack item, int color) {
        CompoundNBT tag = item.getTag();
        if (tag == null)
            tag = new CompoundNBT();
        INBT displayNBT = tag.get("display");
        CompoundNBT display;
        if (displayNBT == null)
            display = new CompoundNBT();
        else
            display = (CompoundNBT) displayNBT;
        display.putInt("color", color);
        tag.put("display", display);
        item.setTag(tag);
        return item;
    }

    public static ItemStack getMeleeWeapon(Random rand, int stage) {
        if (stage > 4)
            stage = 4;
        if (MELEE_WEAPON[stage] != null) {
            return createItemStack(rand, MELEE_WEAPON[stage].roll(rand), stage);
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack getRangedWeapon(Random rand, int stage) {
        if (stage > 4)
            stage = 4;
        if (RANGED_WEAPON[stage] != null) {
            return createItemStack(rand, RANGED_WEAPON[stage].roll(rand), stage);
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            return ItemStack.EMPTY;
        }
    }

    public static double getStageMultiplier(int stage) {
        if (stage > 2)
            return 1.0D;
        return 1D * Math.pow(0.5, HIGHEST_STAGE - stage);
    }

    public static int getRandomColor(Random rand) {
        return ARMOR_COLORS[rand.nextInt(ARMOR_COLORS.length)];
    }

    public static Item getItem(ResourceLocation resourceLocation) {
        if (ForgeRegistries.ITEMS.containsKey(resourceLocation))
            return ForgeRegistries.ITEMS.getValue(resourceLocation);
        DungeonCrawl.LOGGER.warn("Failed to get {} from the item registry.", resourceLocation.toString());
        return null;
    }

}
