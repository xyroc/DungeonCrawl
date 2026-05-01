package xiroc.dungeoncrawl.data.pool.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface ItemPoolKeys {
    ResourceLocation __EQUIPMENT = DataGen.resource("monster_equipment");

    ResourceLocation __MAIN_HAND = DataGen.resource(__EQUIPMENT, "main_hand");
    ResourceKey<IRandom<Item>> MAIN_HAND_LEVEL_0 = key(DataGen.resourceLevel(__MAIN_HAND, 0));
    ResourceKey<IRandom<Item>> MAIN_HAND_LEVEL_1 = key(DataGen.resourceLevel(__MAIN_HAND, 1));

    ResourceLocation __HELMET = DataGen.resource(__EQUIPMENT, "helmet");
    ResourceKey<IRandom<Item>> HELMET_LEVEL_0 = key(DataGen.resourceLevel(__HELMET, 0));
    ResourceKey<IRandom<Item>> HELMET_LEVEL_1 = key(DataGen.resourceLevel(__HELMET, 1));
    ResourceKey<IRandom<Item>> HELMET_LEVEL_2 = key(DataGen.resourceLevel(__HELMET, 2));
    ResourceKey<IRandom<Item>> HELMET_LEVEL_3 = key(DataGen.resourceLevel(__HELMET, 3));
    ResourceKey<IRandom<Item>> HELMET_LEVEL_4 = key(DataGen.resourceLevel(__HELMET, 4));

    private static ResourceKey<IRandom<Item>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.ITEM_POOLS, identifier);
    }
}
