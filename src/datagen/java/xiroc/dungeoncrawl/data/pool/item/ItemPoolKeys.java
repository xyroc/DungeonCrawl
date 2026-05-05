package xiroc.dungeoncrawl.data.pool.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface ItemPoolKeys {
    ResourceLocation __EQUIPMENT__ = DataGen.resource("monster_equipment");

    ResourceLocation __WEAPON__ = DataGen.resource(__EQUIPMENT__, "weapon");

    ResourceLocation __MELEE_WEAPON__ = DataGen.resource(__WEAPON__, "melee");
    ResourceKey<IRandom<Item>> MELEE_WEAPON_LEVEL_0 = key(DataGen.resourceLevel(__MELEE_WEAPON__, 0));
    ResourceKey<IRandom<Item>> MELEE_WEAPON_LEVEL_1 = key(DataGen.resourceLevel(__MELEE_WEAPON__, 1));

    ResourceLocation __ARMOR__ = DataGen.resource(__EQUIPMENT__, "armor");

    ResourceLocation __HELMET__ = DataGen.resource(__ARMOR__, "helmet");
    ResourceKey<IRandom<Item>> HELMET_LEVEL_0 = key(DataGen.resourceLevel(__HELMET__, 0));
    ResourceKey<IRandom<Item>> HELMET_LEVEL_1 = key(DataGen.resourceLevel(__HELMET__, 1));
    ResourceKey<IRandom<Item>> HELMET_LEVEL_2 = key(DataGen.resourceLevel(__HELMET__, 2));
    ResourceKey<IRandom<Item>> HELMET_LEVEL_3 = key(DataGen.resourceLevel(__HELMET__, 3));
    ResourceKey<IRandom<Item>> HELMET_LEVEL_4 = key(DataGen.resourceLevel(__HELMET__, 4));

    private static ResourceKey<IRandom<Item>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.ITEM_POOLS, identifier);
    }
}
