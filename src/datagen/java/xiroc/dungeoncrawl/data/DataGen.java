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

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.blueprint.Blueprints;
import xiroc.dungeoncrawl.data.loot.NonValidatingLootTableProvider;
import xiroc.dungeoncrawl.data.loot.chest.ChestLootTables;
import xiroc.dungeoncrawl.data.loot.chest.contents.*;
import xiroc.dungeoncrawl.data.mappings.DungeonTypeMappings;
import xiroc.dungeoncrawl.data.mappings.PrimaryThemeMappings;
import xiroc.dungeoncrawl.data.mappings.SecondaryThemeMappings;
import xiroc.dungeoncrawl.data.pool.BlueprintPools;
import xiroc.dungeoncrawl.data.spawner.EntityProperties;
import xiroc.dungeoncrawl.data.spawner.SpawnerEntityTypes;
import xiroc.dungeoncrawl.data.spawner.SpawnerTypes;
import xiroc.dungeoncrawl.data.structure.ModStructureSets;
import xiroc.dungeoncrawl.data.structure.ModStructures;
import xiroc.dungeoncrawl.data.tags.BlueprintPoolTags;
import xiroc.dungeoncrawl.data.tags.worldgen.ModBiomeTags;
import xiroc.dungeoncrawl.data.themes.PrimaryThemes;
import xiroc.dungeoncrawl.data.themes.SecondaryThemes;
import xiroc.dungeoncrawl.data.type.DungeonTypes;
import xiroc.dungeoncrawl.data.type.LevelTypes;
import xiroc.dungeoncrawl.datapack.DatapackNamespaces;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = DungeonCrawl.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        boolean includeServer = event.includeServer();

        final DatapackBuiltinEntriesProvider builtinEntriesProvider = new DatapackBuiltinEntriesProvider(
                event.getGenerator().getPackOutput(),
                event.getLookupProvider(),
                new RegistrySetBuilder()
                        .add(Registries.STRUCTURE, ModStructures::generate)
                        .add(Registries.STRUCTURE_SET, ModStructureSets::generate)
                        .add(DatapackRegistries.SPAWNER_ENTITY_PROPERTIES, EntityProperties::generate)
                        .add(DatapackRegistries.SPAWNER_ENTITY_TYPE, SpawnerEntityTypes::generate)
                        .add(DatapackRegistries.SPAWNER_TYPE, SpawnerTypes::generate)
                        .add(DatapackRegistries.PRIMARY_THEME, PrimaryThemes::generate)
                        .add(DatapackRegistries.SECONDARY_THEME, SecondaryThemes::generate)
                        .add(DatapackRegistries.PRIMARY_THEME_POOLS, bootstrap -> {})
                        .add(DatapackRegistries.SECONDARY_THEME_POOLS, bootstrap -> {})
                        .add(DatapackRegistries.PRIMARY_THEME_MAPPINGS, PrimaryThemeMappings::generate)
                        .add(DatapackRegistries.SECONDARY_THEME_MAPPINGS, SecondaryThemeMappings::generate)
                        .add(DatapackRegistries.LEVEL_TYPE, LevelTypes::generate)
                        .add(DatapackRegistries.DUNGEON_TYPE, DungeonTypes::generate)
                        .add(DatapackRegistries.DUNGEON_TYPE_POOLS, bootstrap -> {})
                        .add(DatapackRegistries.DUNGEON_TYPE_MAPPINGS, DungeonTypeMappings::generate)
                        .add(DatapackRegistries.BLUEPRINT, Blueprints::generate)
                        .add(DatapackRegistries.BLUEPRINT_POOLS, BlueprintPools::generate),
                Set.of(DatapackNamespaces.DEFAULT)
        );

        generator.addProvider(includeServer, builtinEntriesProvider);

        generator.addProvider(includeServer, new NonValidatingLootTableProvider(event.getGenerator().getPackOutput(),
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ChestLootTables::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(BlockLootTables::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(EquipmentLootTables::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(FoodLootTables::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(PotionLootTables::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(ScrapLootTables::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(SpecialityLootTables::new, LootContextParamSets.CHEST),
                        new LootTableProvider.SubProviderEntry(ValuablesLootTables::new, LootContextParamSets.CHEST)),
                event.getLookupProvider()));

        generator.addProvider(includeServer, new ModBiomeTags(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        generator.addProvider(includeServer, new BlueprintPoolTags(generator.getPackOutput(), builtinEntriesProvider.getRegistryProvider(), event.getExistingFileHelper()));
    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(DatapackNamespaces.DEFAULT, path);
    }

    public static ResourceLocation resource(ResourceLocation directory, String name) {
        return ResourceLocation.fromNamespaceAndPath(directory.getNamespace(), directory.getPath() + "/" + name);
    }

    public static ResourceLocation resourceLevel(ResourceLocation directory, int level) {
        return ResourceLocation.fromNamespaceAndPath(directory.getNamespace(), directory.getPath() + "/level_" + level);
    }
}
