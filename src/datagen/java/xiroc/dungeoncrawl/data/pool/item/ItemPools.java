package xiroc.dungeoncrawl.data.pool.item;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface ItemPools {
    static void generate(BootstrapContext<IRandom<Item>> context) {
        context.register(ItemPoolKeys.MAIN_HAND_LEVEL_0, IRandom.<Item>builder()
                .add(Items.WOODEN_AXE)
                .build());

        context.register(ItemPoolKeys.MAIN_HAND_LEVEL_1, IRandom.<Item>builder()
                .add(Items.STONE_AXE)
                .build());
    }
}
