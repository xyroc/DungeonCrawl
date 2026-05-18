package xiroc.dungeoncrawl.data.loot.chest;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.data.DataGen;

public interface ChestLootTableKeys {
    ResourceLocation __CHEST__ = DataGen.resource("chests");

    ResourceKey<LootTable> LEVEL_0 = key(DataGen.resourceLevel(__CHEST__, 0));
    ResourceKey<LootTable> LEVEL_1 = key(DataGen.resourceLevel(__CHEST__, 1));
    ResourceKey<LootTable> LEVEL_2 = key(DataGen.resourceLevel(__CHEST__, 2));
    ResourceKey<LootTable> LEVEL_3 = key(DataGen.resourceLevel(__CHEST__, 3));
    ResourceKey<LootTable> LEVEL_4 = key(DataGen.resourceLevel(__CHEST__, 4));
    ResourceKey<LootTable> SECRET_ROOM = key(DataGen.resource(__CHEST__, "secret_room"));
    ResourceKey<LootTable> SUPPLY = key(DataGen.resource(__CHEST__, "supply"));
    ResourceKey<LootTable> TREASURE = key(DataGen.resource(__CHEST__, "treasure"));

    ResourceLocation __CONTENTS__ = DataGen.resource(__CHEST__, "contents");

    ResourceLocation __FOOD__ = DataGen.resource(__CONTENTS__, "food");
    ResourceKey<LootTable> FOOD_LEVEL_0 = key(DataGen.resourceLevel(__FOOD__, 0));
    ResourceKey<LootTable> FOOD_LEVEL_1 = key(DataGen.resourceLevel(__FOOD__, 1));
    ResourceKey<LootTable> FOOD_LEVEL_2 = key(DataGen.resourceLevel(__FOOD__, 2));
    ResourceKey<LootTable> FOOD_LEVEL_3 = key(DataGen.resourceLevel(__FOOD__, 3));
    ResourceKey<LootTable> FOOD_LEVEL_4 = key(DataGen.resourceLevel(__FOOD__, 4));

    ResourceLocation __BLOCKS__ = DataGen.resource(__CONTENTS__, "blocks");
    ResourceKey<LootTable> BLOCKS_LEVEL_0 = key(DataGen.resourceLevel(__BLOCKS__, 0));
    ResourceKey<LootTable> BLOCKS_LEVEL_1 = key(DataGen.resourceLevel(__BLOCKS__, 1));
    ResourceKey<LootTable> BLOCKS_LEVEL_2 = key(DataGen.resourceLevel(__BLOCKS__, 2));
    ResourceKey<LootTable> BLOCKS_LEVEL_3 = key(DataGen.resourceLevel(__BLOCKS__, 3));
    ResourceKey<LootTable> BLOCKS_LEVEL_4 = key(DataGen.resourceLevel(__BLOCKS__, 4));

    ResourceLocation __LUCK_BONUS__ = DataGen.resource(__CONTENTS__, "luck_bonus");
    ResourceKey<LootTable> LUCK_BONUS_LEVEL_0 = key(DataGen.resourceLevel(__LUCK_BONUS__, 0));
    ResourceKey<LootTable> LUCK_BONUS_LEVEL_1 = key(DataGen.resourceLevel(__LUCK_BONUS__, 1));
    ResourceKey<LootTable> LUCK_BONUS_LEVEL_2 = key(DataGen.resourceLevel(__LUCK_BONUS__, 2));
    ResourceKey<LootTable> LUCK_BONUS_LEVEL_3 = key(DataGen.resourceLevel(__LUCK_BONUS__, 3));
    ResourceKey<LootTable> LUCK_BONUS_LEVEL_4 = key(DataGen.resourceLevel(__LUCK_BONUS__, 4));

    ResourceLocation __SCRAP__ = DataGen.resource(__CONTENTS__, "scrap");
    ResourceKey<LootTable> SCRAP_LEVEL_0 = key(DataGen.resourceLevel(__SCRAP__, 0));
    ResourceKey<LootTable> SCRAP_LEVEL_1 = key(DataGen.resourceLevel(__SCRAP__, 1));
    ResourceKey<LootTable> SCRAP_LEVEL_2 = key(DataGen.resourceLevel(__SCRAP__, 2));
    ResourceKey<LootTable> SCRAP_LEVEL_3 = key(DataGen.resourceLevel(__SCRAP__, 3));
    ResourceKey<LootTable> SCRAP_LEVEL_4 = key(DataGen.resourceLevel(__SCRAP__, 4));

    ResourceLocation __VALUABLES__ = DataGen.resource(__CONTENTS__, "valuables");
    ResourceKey<LootTable> VALUABLES_LEVEL_0 = key(DataGen.resourceLevel(__VALUABLES__, 0));
    ResourceKey<LootTable> VALUABLES_LEVEL_1 = key(DataGen.resourceLevel(__VALUABLES__, 1));
    ResourceKey<LootTable> VALUABLES_LEVEL_2 = key(DataGen.resourceLevel(__VALUABLES__, 2));
    ResourceKey<LootTable> VALUABLES_LEVEL_3 = key(DataGen.resourceLevel(__VALUABLES__, 3));
    ResourceKey<LootTable> VALUABLES_LEVEL_4 = key(DataGen.resourceLevel(__VALUABLES__, 4));

    ResourceLocation __EQUIPMENT__ = DataGen.resource(__CONTENTS__, "equipment");
    ResourceKey<LootTable> EQUIPMENT_LEVEL_0 = key(DataGen.resourceLevel(__EQUIPMENT__, 0));
    ResourceKey<LootTable> EQUIPMENT_LEVEL_1 = key(DataGen.resourceLevel(__EQUIPMENT__, 1));
    ResourceKey<LootTable> EQUIPMENT_LEVEL_2 = key(DataGen.resourceLevel(__EQUIPMENT__, 2));
    ResourceKey<LootTable> EQUIPMENT_LEVEL_3 = key(DataGen.resourceLevel(__EQUIPMENT__, 3));
    ResourceKey<LootTable> EQUIPMENT_LEVEL_4 = key(DataGen.resourceLevel(__EQUIPMENT__, 4));

    interface Potion {
        ResourceLocation __POTIONS__ = DataGen.resource(__CONTENTS__, "potions");
        ResourceKey<LootTable> LEVEL_0 = key(DataGen.resourceLevel(__POTIONS__, 0));
        ResourceKey<LootTable> LEVEL_1 = key(DataGen.resourceLevel(__POTIONS__, 1));
        ResourceKey<LootTable> LEVEL_2 = key(DataGen.resourceLevel(__POTIONS__, 2));
        ResourceKey<LootTable> LEVEL_3 = key(DataGen.resourceLevel(__POTIONS__, 3));
        ResourceKey<LootTable> LEVEL_4 = key(DataGen.resourceLevel(__POTIONS__, 4));

        ResourceLocation __ITEM__ = DataGen.resource(__POTIONS__, "item");

        // Special Potions from Roguelike Dungeons.
        ResourceKey<LootTable> ABSINTHE = key(DataGen.resource(__ITEM__, "absinthe"));
        ResourceKey<LootTable> ANIMUS = key(DataGen.resource(__ITEM__, "animus"));
        ResourceKey<LootTable> COFFEE = key(DataGen.resource(__ITEM__, "coffee"));
        ResourceKey<LootTable> LAUDANUM = key(DataGen.resource(__ITEM__, "laudanum"));
        ResourceKey<LootTable> LUMA = key(DataGen.resource(__ITEM__, "luma"));
        ResourceKey<LootTable> MOONSHINE = key(DataGen.resource(__ITEM__, "moonshine"));
        ResourceKey<LootTable> NECTAR = key(DataGen.resource(__ITEM__, "nectar"));
        ResourceKey<LootTable> STOUT = key(DataGen.resource(__ITEM__, "stout"));
        ResourceKey<LootTable> TEQUILA = key(DataGen.resource(__ITEM__, "tequila"));
        ResourceKey<LootTable> VILE_MIXTURE = key(DataGen.resource(__ITEM__, "vile_mixture"));
        ResourceKey<LootTable> VITAE = key(DataGen.resource(__ITEM__, "vitae"));
    }

    interface Speciality {
        ResourceLocation __SPECIALITY__ = DataGen.resource(__CONTENTS__, "speciality");
        ResourceKey<LootTable> LEVEL_0 = key(DataGen.resourceLevel(__SPECIALITY__, 0));
        ResourceKey<LootTable> LEVEL_1 = key(DataGen.resourceLevel(__SPECIALITY__, 1));
        ResourceKey<LootTable> LEVEL_2 = key(DataGen.resourceLevel(__SPECIALITY__, 2));
        ResourceKey<LootTable> LEVEL_3 = key(DataGen.resourceLevel(__SPECIALITY__, 3));
        ResourceKey<LootTable> LEVEL_4 = key(DataGen.resourceLevel(__SPECIALITY__, 4));

        ResourceLocation __ITEM = DataGen.resource(__SPECIALITY__, "item");

        ResourceKey<LootTable> TEMPERED_BLADE = key(DataGen.resource(__ITEM, "tempered_blade"));

        ResourceKey<LootTable> CASE_HARDENED_PICK = key(DataGen.resource(__ITEM, "case_hardened_pick"));
        ResourceKey<LootTable> CRYSTAL_PICK = key(DataGen.resource(__ITEM, "crystal_pick"));

        ResourceKey<LootTable> WOODLAND_HATCHET = key(DataGen.resource(__ITEM, "woodland_hatchet"));
        ResourceKey<LootTable> CRYSTAL_AXE = key(DataGen.resource(__ITEM, "crystal_axe"));

        ResourceKey<LootTable> GRAVE_SPADE = key(DataGen.resource(__ITEM, "grave_spade"));
        ResourceKey<LootTable> SOUL_SPADE = key(DataGen.resource(__ITEM, "soul_spade"));
    }

    private static ResourceKey<LootTable> key(ResourceLocation location) {
        return ResourceKey.create(Registries.LOOT_TABLE, location);
    }
}
