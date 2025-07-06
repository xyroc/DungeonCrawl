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

package xiroc.dungeoncrawl.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.blueprint.TemplateConfigurations;
import xiroc.dungeoncrawl.data.loot.ChestLootTables;
import xiroc.dungeoncrawl.data.mappings.PrimaryThemeMappings;
import xiroc.dungeoncrawl.data.mappings.SecondaryThemeMappings;
import xiroc.dungeoncrawl.data.spawner.SpawnerEntityTypes;
import xiroc.dungeoncrawl.data.spawner.SpawnerTypes;
import xiroc.dungeoncrawl.data.themes.PrimaryThemes;
import xiroc.dungeoncrawl.data.themes.SecondaryThemes;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = DungeonCrawl.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        boolean includeServer = event.includeServer();
        generator.addProvider(includeServer, new LootTableProvider(event.getGenerator().getPackOutput(""), Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ChestLootTables::new, LootContextParamSets.CHEST)), event.getLookupProvider()));
        generator.addProvider(includeServer, new PrimaryThemes(generator.getPackOutput()));
        generator.addProvider(includeServer, new SecondaryThemes(generator.getPackOutput()));
        generator.addProvider(includeServer, new PrimaryThemeMappings(generator.getPackOutput()));
        generator.addProvider(includeServer, new SecondaryThemeMappings(generator.getPackOutput()));
        generator.addProvider(includeServer, new SpawnerEntityTypes(generator.getPackOutput()));
        generator.addProvider(includeServer, new SpawnerTypes(generator.getPackOutput()));
        generator.addProvider(includeServer, new TemplateConfigurations(generator.getPackOutput()));

    }

}
