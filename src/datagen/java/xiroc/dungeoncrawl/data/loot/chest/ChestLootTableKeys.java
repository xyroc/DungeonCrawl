package xiroc.dungeoncrawl.data.loot.chest;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.data.DataGen;

public interface ChestLootTableKeys {
    ResourceLocation __CHEST = DataGen.resource("chest");

    ResourceKey<LootTable> LEVEL_0 = key(DataGen.resourceLevel(__CHEST, 0));
    ResourceKey<LootTable> LEVEL_1 = key(DataGen.resourceLevel(__CHEST, 1));
    ResourceKey<LootTable> LEVEL_2 = key(DataGen.resourceLevel(__CHEST, 2));
    ResourceKey<LootTable> LEVEL_3 = key(DataGen.resourceLevel(__CHEST, 3));
    ResourceKey<LootTable> LEVEL_4 = key(DataGen.resourceLevel(__CHEST, 4));

    ResourceLocation __CONTENTS = DataGen.resource(__CHEST, "contents");

    ResourceLocation __FOOD = DataGen.resource(__CONTENTS, "food");
    ResourceKey<LootTable> FOOD_LEVEL_0 = key(DataGen.resourceLevel(__FOOD, 0));
    ResourceKey<LootTable> FOOD_LEVEL_1 = key(DataGen.resourceLevel(__FOOD, 1));
    ResourceKey<LootTable> FOOD_LEVEL_2 = key(DataGen.resourceLevel(__FOOD, 2));
    ResourceKey<LootTable> FOOD_LEVEL_3 = key(DataGen.resourceLevel(__FOOD, 3));
    ResourceKey<LootTable> FOOD_LEVEL_4 = key(DataGen.resourceLevel(__FOOD, 4));

    ResourceLocation __BLOCKS = DataGen.resource(__CONTENTS, "blocks");
    ResourceKey<LootTable> BLOCKS_LEVEL_0 = key(DataGen.resourceLevel(__BLOCKS, 0));
    ResourceKey<LootTable> BLOCKS_LEVEL_1 = key(DataGen.resourceLevel(__BLOCKS, 1));
    ResourceKey<LootTable> BLOCKS_LEVEL_2 = key(DataGen.resourceLevel(__BLOCKS, 2));
    ResourceKey<LootTable> BLOCKS_LEVEL_3 = key(DataGen.resourceLevel(__BLOCKS, 3));
    ResourceKey<LootTable> BLOCKS_LEVEL_4 = key(DataGen.resourceLevel(__BLOCKS, 4));

    ResourceLocation __SCRAP = DataGen.resource(__CONTENTS, "scrap");
    ResourceKey<LootTable> SCRAP_LEVEL_0 = key(DataGen.resourceLevel(__SCRAP, 0));
    ResourceKey<LootTable> SCRAP_LEVEL_1 = key(DataGen.resourceLevel(__SCRAP, 1));
    ResourceKey<LootTable> SCRAP_LEVEL_2 = key(DataGen.resourceLevel(__SCRAP, 2));
    ResourceKey<LootTable> SCRAP_LEVEL_3 = key(DataGen.resourceLevel(__SCRAP, 3));
    ResourceKey<LootTable> SCRAP_LEVEL_4 = key(DataGen.resourceLevel(__SCRAP, 4));

    ResourceLocation __VALUABLES = DataGen.resource(__CONTENTS, "valuables");
    ResourceKey<LootTable> VALUABLES_LEVEL_0 = key(DataGen.resourceLevel(__VALUABLES, 0));
    ResourceKey<LootTable> VALUABLES_LEVEL_1 = key(DataGen.resourceLevel(__VALUABLES, 1));
    ResourceKey<LootTable> VALUABLES_LEVEL_2 = key(DataGen.resourceLevel(__VALUABLES, 2));
    ResourceKey<LootTable> VALUABLES_LEVEL_3 = key(DataGen.resourceLevel(__VALUABLES, 3));
    ResourceKey<LootTable> VALUABLES_LEVEL_4 = key(DataGen.resourceLevel(__VALUABLES, 4));

    ResourceLocation __EQUIPMENT = DataGen.resource(__CONTENTS, "equipment");
    ResourceKey<LootTable> EQUIPMENT_LEVEL_0 = key(DataGen.resourceLevel(__EQUIPMENT, 0));
    ResourceKey<LootTable> EQUIPMENT_LEVEL_1 = key(DataGen.resourceLevel(__EQUIPMENT, 1));
    ResourceKey<LootTable> EQUIPMENT_LEVEL_2 = key(DataGen.resourceLevel(__EQUIPMENT, 2));
    ResourceKey<LootTable> EQUIPMENT_LEVEL_3 = key(DataGen.resourceLevel(__EQUIPMENT, 3));
    ResourceKey<LootTable> EQUIPMENT_LEVEL_4 = key(DataGen.resourceLevel(__EQUIPMENT, 4));

    interface Speciality {
        ResourceLocation __SPECIALITY = DataGen.resource(__CONTENTS, "speciality");
        ResourceLocation __RAW = DataGen.resource(__SPECIALITY, "raw");

        ResourceKey<LootTable> TEMPERED_BLADE = key(DataGen.resource(__RAW, "tempered_blade"));

        ResourceKey<LootTable> CASE_HARDENED_PICK = key(DataGen.resource(__RAW, "case_hardened_pick"));
        ResourceKey<LootTable> CRYSTAL_PICK = key(DataGen.resource(__RAW, "crystal_pick"));

        ResourceKey<LootTable> WOODLAND_HATCHET = key(DataGen.resource(__RAW, "woodland_hatchet"));
        ResourceKey<LootTable> CRYSTAL_AXE = key(DataGen.resource(__RAW, "crystal_axe"));

        ResourceKey<LootTable> GRAVE_SPADE = key(DataGen.resource(__RAW, "grave_spade"));
        ResourceKey<LootTable> SOUL_SPADE = key(DataGen.resource(__RAW, "soul_spade"));
    }
    
    private static ResourceKey<LootTable> key(ResourceLocation location) {
        return ResourceKey.create(Registries.LOOT_TABLE, location);
    }
}
