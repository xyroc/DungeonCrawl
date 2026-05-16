package xiroc.dungeoncrawl.data.pool.item;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface ItemPools {
    static void generate(BootstrapContext<IRandom<Item>> context) {
        context.register(ItemPoolKeys.MELEE_WEAPON_LEVEL_0, IRandom.<Item>builder()
                .add(Items.WOODEN_AXE)
                .add(Items.WOODEN_HOE, 2)
                .add(Items.WOODEN_PICKAXE, 2)
                .add(Items.WOODEN_SHOVEL, 2)
                .add(Items.WOODEN_SWORD, 2)
                .add(Items.STONE_HOE)
                .add(Items.STONE_PICKAXE)
                .add(Items.STONE_SHOVEL, 2)
                .add(Items.STONE_SWORD, 2)
                .add(Items.GOLDEN_HOE)
                .add(Items.GOLDEN_PICKAXE)
                .add(Items.GOLDEN_SHOVEL)
                .add(Items.GOLDEN_SWORD)
                .add(Items.IRON_HOE)
                .add(Items.IRON_SHOVEL)
                .build());

        context.register(ItemPoolKeys.MELEE_WEAPON_LEVEL_1, IRandom.<Item>builder()
                .add(Items.WOODEN_AXE)
                .add(Items.WOODEN_HOE)
                .add(Items.WOODEN_PICKAXE)
                .add(Items.WOODEN_SHOVEL)
                .add(Items.WOODEN_SWORD)
                .add(Items.STONE_AXE)
                .add(Items.STONE_HOE, 2)
                .add(Items.STONE_PICKAXE, 2)
                .add(Items.STONE_SHOVEL, 2)
                .add(Items.STONE_SWORD, 2)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_HOE)
                .add(Items.GOLDEN_PICKAXE)
                .add(Items.GOLDEN_SHOVEL)
                .add(Items.GOLDEN_SWORD, 2)
                .add(Items.IRON_HOE)
                .add(Items.IRON_PICKAXE)
                .add(Items.IRON_SHOVEL, 2)
                .add(Items.IRON_SWORD, 2)
                .build());

        context.register(ItemPoolKeys.MELEE_WEAPON_LEVEL_2, IRandom.<Item>builder()
                .add(Items.WOODEN_AXE)
                .add(Items.WOODEN_HOE)
                .add(Items.WOODEN_SHOVEL)
                .add(Items.WOODEN_SWORD)
                .add(Items.WOODEN_PICKAXE)
                .add(Items.STONE_AXE)
                .add(Items.STONE_HOE)
                .add(Items.STONE_PICKAXE, 2)
                .add(Items.STONE_SHOVEL, 2)
                .add(Items.STONE_SWORD, 2)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_HOE)
                .add(Items.GOLDEN_PICKAXE)
                .add(Items.GOLDEN_SHOVEL)
                .add(Items.GOLDEN_SWORD, 2)
                .add(Items.IRON_AXE)
                .add(Items.IRON_HOE, 2)
                .add(Items.IRON_PICKAXE, 3)
                .add(Items.IRON_SHOVEL, 3)
                .add(Items.IRON_SWORD, 6)
                .add(Items.DIAMOND_HOE)
                .add(Items.DIAMOND_PICKAXE)
                .add(Items.DIAMOND_SHOVEL)
                .build());

        context.register(ItemPoolKeys.MELEE_WEAPON_LEVEL_3, IRandom.<Item>builder()
                .add(Items.STONE_AXE)
                .add(Items.STONE_HOE)
                .add(Items.STONE_SHOVEL)
                .add(Items.STONE_SWORD)
                .add(Items.STONE_PICKAXE)
                .add(Items.IRON_AXE, 3)
                .add(Items.IRON_HOE, 3)
                .add(Items.IRON_SHOVEL, 3)
                .add(Items.IRON_PICKAXE, 3)
                .add(Items.IRON_SWORD, 8)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_PICKAXE)
                .add(Items.GOLDEN_SWORD, 2)
                .add(Items.DIAMOND_AXE)
                .add(Items.DIAMOND_HOE, 2)
                .add(Items.DIAMOND_SHOVEL, 2)
                .add(Items.DIAMOND_SWORD, 2)
                .add(Items.DIAMOND_PICKAXE, 2)
                .build());

        context.register(ItemPoolKeys.MELEE_WEAPON_LEVEL_4, IRandom.<Item>builder()
                .add(Items.STONE_AXE, 2)
                .add(Items.STONE_HOE)
                .add(Items.STONE_PICKAXE)
                .add(Items.STONE_SHOVEL, 2)
                .add(Items.STONE_SWORD)
                .add(Items.IRON_AXE, 4)
                .add(Items.IRON_HOE)
                .add(Items.IRON_PICKAXE, 3)
                .add(Items.IRON_SHOVEL)
                .add(Items.IRON_SWORD, 8)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_SWORD, 2)
                .add(Items.DIAMOND_AXE, 2)
                .add(Items.DIAMOND_HOE, 2)
                .add(Items.DIAMOND_PICKAXE, 3)
                .add(Items.DIAMOND_SHOVEL, 2)
                .add(Items.DIAMOND_SWORD, 6)
                .add(Items.NETHERITE_SWORD)
                .build());

        context.register(ItemPoolKeys.BOW_LEVEL_0, IRandom.<Item>builder()
                .add(Items.BOW)
                .build());

        context.register(ItemPoolKeys.BOW_LEVEL_1, IRandom.<Item>builder()
                .add(Items.BOW)
                .build());

        context.register(ItemPoolKeys.BOW_LEVEL_2, IRandom.<Item>builder()
                .add(Items.BOW)
                .build());

        context.register(ItemPoolKeys.BOW_LEVEL_3, IRandom.<Item>builder()
                .add(Items.BOW)
                .build());

        context.register(ItemPoolKeys.BOW_LEVEL_4, IRandom.<Item>builder()
                .add(Items.BOW)
                .build());

        context.register(ItemPoolKeys.SHIELD_LEVEL_0, IRandom.<Item>builder()
                .add(Items.SHIELD)
                .build());

        context.register(ItemPoolKeys.SHIELD_LEVEL_1, IRandom.<Item>builder()
                .add(Items.SHIELD)
                .build());

        context.register(ItemPoolKeys.SHIELD_LEVEL_2, IRandom.<Item>builder()
                .add(Items.SHIELD)
                .build());

        context.register(ItemPoolKeys.SHIELD_LEVEL_3, IRandom.<Item>builder()
                .add(Items.SHIELD)
                .build());

        context.register(ItemPoolKeys.SHIELD_LEVEL_4, IRandom.<Item>builder()
                .add(Items.SHIELD)
                .build());

        context.register(ItemPoolKeys.BOOTS_LEVEL_0, IRandom.<Item>builder()
                .add(Items.LEATHER_BOOTS, 2)
                .add(Items.CHAINMAIL_BOOTS, 2)
                .add(Items.GOLDEN_BOOTS)
                .add(Items.IRON_BOOTS)
                .build());

        context.register(ItemPoolKeys.BOOTS_LEVEL_1, IRandom.<Item>builder()
                .add(Items.LEATHER_BOOTS)
                .add(Items.CHAINMAIL_BOOTS, 2)
                .add(Items.GOLDEN_BOOTS)
                .add(Items.IRON_BOOTS, 2)
                .build());

        context.register(ItemPoolKeys.BOOTS_LEVEL_2, IRandom.<Item>builder()
                .add(Items.LEATHER_BOOTS)
                .add(Items.CHAINMAIL_BOOTS, 2)
                .add(Items.GOLDEN_BOOTS, 2)
                .add(Items.IRON_BOOTS, 4)
                .add(Items.DIAMOND_BOOTS)
                .build());

        context.register(ItemPoolKeys.BOOTS_LEVEL_3, IRandom.<Item>builder()
                .add(Items.LEATHER_BOOTS)
                .add(Items.CHAINMAIL_BOOTS, 2)
                .add(Items.GOLDEN_BOOTS, 2)
                .add(Items.IRON_BOOTS, 4)
                .add(Items.DIAMOND_BOOTS, 2)
                .build());

        context.register(ItemPoolKeys.BOOTS_LEVEL_4, IRandom.<Item>builder()
                .add(Items.LEATHER_BOOTS, 2)
                .add(Items.CHAINMAIL_BOOTS, 3)
                .add(Items.GOLDEN_BOOTS, 3)
                .add(Items.IRON_BOOTS, 5)
                .add(Items.DIAMOND_BOOTS, 5)
                .add(Items.NETHERITE_BOOTS)
                .build());

        context.register(ItemPoolKeys.CHESTPLATE_LEVEL_0, IRandom.<Item>builder()
                .add(Items.LEATHER_CHESTPLATE, 2)
                .add(Items.CHAINMAIL_CHESTPLATE, 2)
                .add(Items.GOLDEN_CHESTPLATE)
                .add(Items.IRON_CHESTPLATE)
                .build());

        context.register(ItemPoolKeys.CHESTPLATE_LEVEL_1, IRandom.<Item>builder()
                .add(Items.LEATHER_CHESTPLATE)
                .add(Items.CHAINMAIL_CHESTPLATE, 2)
                .add(Items.GOLDEN_CHESTPLATE)
                .add(Items.IRON_CHESTPLATE)
                .build());

        context.register(ItemPoolKeys.CHESTPLATE_LEVEL_2, IRandom.<Item>builder()
                .add(Items.LEATHER_CHESTPLATE, 2)
                .add(Items.CHAINMAIL_CHESTPLATE, 3)
                .add(Items.GOLDEN_CHESTPLATE, 3)
                .add(Items.IRON_CHESTPLATE, 4)
                .add(Items.DIAMOND_CHESTPLATE)
                .build());

        context.register(ItemPoolKeys.CHESTPLATE_LEVEL_3, IRandom.<Item>builder()
                .add(Items.LEATHER_CHESTPLATE)
                .add(Items.CHAINMAIL_CHESTPLATE, 2)
                .add(Items.GOLDEN_CHESTPLATE, 2)
                .add(Items.IRON_CHESTPLATE, 5)
                .add(Items.DIAMOND_CHESTPLATE, 2)
                .build());

        context.register(ItemPoolKeys.CHESTPLATE_LEVEL_4, IRandom.<Item>builder()
                .add(Items.LEATHER_CHESTPLATE, 2)
                .add(Items.CHAINMAIL_CHESTPLATE, 3)
                .add(Items.GOLDEN_CHESTPLATE, 3)
                .add(Items.IRON_CHESTPLATE, 5)
                .add(Items.DIAMOND_CHESTPLATE, 5)
                .add(Items.NETHERITE_CHESTPLATE)
                .build());

        context.register(ItemPoolKeys.HELMET_LEVEL_0, IRandom.<Item>builder()
                .add(Items.LEATHER_HELMET, 2)
                .add(Items.CHAINMAIL_HELMET, 2)
                .add(Items.GOLDEN_HELMET)
                .add(Items.IRON_HELMET)
                .build());

        context.register(ItemPoolKeys.HELMET_LEVEL_1, IRandom.<Item>builder()
                .add(Items.LEATHER_HELMET)
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.GOLDEN_HELMET, 2)
                .add(Items.IRON_HELMET, 2)
                .build());

        context.register(ItemPoolKeys.HELMET_LEVEL_2, IRandom.<Item>builder()
                .add(Items.LEATHER_HELMET)
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.GOLDEN_HELMET, 2)
                .add(Items.IRON_HELMET, 3)
                .add(Items.DIAMOND_HELMET)
                .build());

        context.register(ItemPoolKeys.HELMET_LEVEL_3, IRandom.<Item>builder()
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.GOLDEN_HELMET)
                .add(Items.IRON_HELMET, 3)
                .add(Items.DIAMOND_HELMET, 2)
                .build());

        context.register(ItemPoolKeys.HELMET_LEVEL_4, IRandom.<Item>builder()
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.GOLDEN_HELMET)
                .add(Items.IRON_HELMET, 2)
                .add(Items.DIAMOND_HELMET, 3)
                .add(Items.NETHERITE_HELMET)
                .build());

        context.register(ItemPoolKeys.LEGGINGS_LEVEL_0, IRandom.<Item>builder()
                .add(Items.LEATHER_LEGGINGS, 2)
                .add(Items.CHAINMAIL_LEGGINGS, 2)
                .add(Items.GOLDEN_LEGGINGS)
                .add(Items.IRON_LEGGINGS)
                .build());

        context.register(ItemPoolKeys.LEGGINGS_LEVEL_1, IRandom.<Item>builder()
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.CHAINMAIL_LEGGINGS, 2)
                .add(Items.GOLDEN_LEGGINGS)
                .add(Items.IRON_LEGGINGS)
                .build());

        context.register(ItemPoolKeys.LEGGINGS_LEVEL_2, IRandom.<Item>builder()
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.CHAINMAIL_LEGGINGS, 2)
                .add(Items.GOLDEN_LEGGINGS, 2)
                .add(Items.IRON_LEGGINGS, 3)
                .add(Items.DIAMOND_LEGGINGS)
                .build());

        context.register(ItemPoolKeys.LEGGINGS_LEVEL_3, IRandom.<Item>builder()
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.CHAINMAIL_LEGGINGS, 2)
                .add(Items.GOLDEN_LEGGINGS, 2)
                .add(Items.IRON_LEGGINGS, 4)
                .add(Items.DIAMOND_LEGGINGS, 2)
                .build());

        context.register(ItemPoolKeys.LEGGINGS_LEVEL_4, IRandom.<Item>builder()
                .add(Items.LEATHER_LEGGINGS, 2)
                .add(Items.CHAINMAIL_LEGGINGS, 3)
                .add(Items.GOLDEN_LEGGINGS, 3)
                .add(Items.IRON_LEGGINGS, 5)
                .add(Items.DIAMOND_LEGGINGS, 5)
                .add(Items.NETHERITE_LEGGINGS)
                .build());
    }
}
