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

package xiroc.dungeoncrawl.data.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTables extends LootTableProvider {

    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders = ImmutableList
            .of(Pair.of(ChestLootTables::new, LootContextParamSets.CHEST));


    public LootTables(DataGenerator p_i50789_1_) {
        super(p_i50789_1_);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        for (ResourceLocation resourcelocation : Sets.difference(Loot.ALL_LOOT_TABLES, map.keySet())) {
            validationtracker.reportProblem("Missing built-in table: " + resourcelocation);
        }

        map.forEach((p_218436_2_, p_218436_3_) -> {
            net.minecraft.world.level.storage.loot.LootTables.validate(validationtracker, p_218436_2_, p_218436_3_);
        });
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return subProviders;
    }

    @Override
    public String getName() {
        return "Dungeon Crawl Loot Tables";
    }

}
