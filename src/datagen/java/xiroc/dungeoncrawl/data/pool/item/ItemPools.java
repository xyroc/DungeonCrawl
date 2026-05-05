package xiroc.dungeoncrawl.data.pool.item;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface ItemPools {
    static void generate(BootstrapContext<IRandom<Item>> context) {
        context.register(ItemPoolKeys.MELEE_WEAPON_LEVEL_0, IRandom.<Item>builder()
                .add(Items.WOODEN_AXE)
                .build());

        context.register(ItemPoolKeys.MELEE_WEAPON_LEVEL_1, IRandom.<Item>builder()
                .add(Items.STONE_AXE)
                .build());

        context.register(ItemPoolKeys.HELMET_LEVEL_0, IRandom.<Item>builder()
                .add(Items.LEATHER_HELMET)
                .add(Items.CHAINMAIL_HELMET)
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
    }
}
