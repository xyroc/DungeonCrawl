package xiroc.dungeoncrawl.data.loot.chest.contents;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.data.loot.chest.ChestLootTableKeys;

import java.util.function.BiConsumer;

public record BlockLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> collector) {
        collector.accept(ChestLootTableKeys.BLOCKS_LEVEL_0, LootTable.lootTable());
        collector.accept(ChestLootTableKeys.BLOCKS_LEVEL_1, LootTable.lootTable());
        collector.accept(ChestLootTableKeys.BLOCKS_LEVEL_2, LootTable.lootTable());
        collector.accept(ChestLootTableKeys.BLOCKS_LEVEL_3, LootTable.lootTable());
        collector.accept(ChestLootTableKeys.BLOCKS_LEVEL_4, LootTable.lootTable());
    }
}
