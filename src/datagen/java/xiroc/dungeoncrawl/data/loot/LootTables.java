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
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTables extends LootTableProvider {

    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders = ImmutableList
            .of(Pair.of(ChestLootTables::new, LootContextParamSets.CHEST));

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;

    public LootTables(DataGenerator p_i50789_1_) {
        super(p_i50789_1_);
        this.generator = p_i50789_1_;
    }

    @Override
    public void run(HashCache p_200398_1_) {
        Path path = this.generator.getOutputFolder();
        Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        this.getTables().forEach((p_218438_1_) -> {
            p_218438_1_.getFirst().get().accept((p_218437_2_, p_218437_3_) -> {
                if (map.put(p_218437_2_, p_218437_3_.setParamSet(p_218438_1_.getSecond()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + p_218437_2_);
                }
            });
        });

        map.forEach((p_229441_2_, p_229441_3_) -> {
            Path path1 = createPath(path, p_229441_2_);

            try {
                DataProvider.save(GSON, p_200398_1_, net.minecraft.world.level.storage.loot.LootTables.serialize(p_229441_3_), path1);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't save loot table {}", path1, ioexception);
            }

        });
    }

    private static Path createPath(Path p_218439_0_, ResourceLocation p_218439_1_) {
        return p_218439_0_.resolve("data/" + p_218439_1_.getNamespace() + "/loot_tables/" + p_218439_1_.getPath() + ".json");
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return subProviders;
    }

}
