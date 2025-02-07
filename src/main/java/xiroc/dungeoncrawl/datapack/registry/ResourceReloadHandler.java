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

package xiroc.dungeoncrawl.datapack.registry;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.type.DungeonTypes;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ResourceReloadHandler implements PreparableReloadListener {
    private static final ImmutableList<DatapackRegistry<?>> REGISTRIES = ImmutableList.<DatapackRegistry<?>>builder()
            .add(DatapackRegistries.PRIMARY_THEME)
            .add(DatapackRegistries.SECONDARY_THEME)
            .add(DatapackRegistries.PRIMARY_THEME_MAPPINGS)
            .add(DatapackRegistries.SECONDARY_THEME_MAPPINGS)
            .add(DatapackRegistries.SPAWNER_ENTITY_TYPE)
            .add(DatapackRegistries.SPAWNER_TYPE)
            .add(DatapackRegistries.BLUEPRINT)
            .add(DatapackRegistries.LEVEL_TYPE)
            .add(DatapackRegistries.DUNGEON_TYPE)
            .build();

    private static void reload(ResourceManager resourceManager) {
        REGISTRIES.forEach(DatapackRegistry::unload);
        REGISTRIES.forEach(registry -> registry.reload(resourceManager));
        DungeonTypes.load(resourceManager);

        final var statistics = REGISTRIES.stream().collect(Collectors.summarizingInt(DatapackRegistry::entryCount));
        DungeonCrawl.LOGGER.info("Loaded {} registries with a total of {} data entries.", statistics.getCount(), statistics.getSum());
    }

    public static void onTagsUpdated(RegistryAccess registryAccess) {
        final var biomeMappings = List.of(DatapackRegistries.PRIMARY_THEME_MAPPINGS, DatapackRegistries.SECONDARY_THEME_MAPPINGS);
        final var biomeRegistry = registryAccess.registry(Registry.BIOME_REGISTRY).orElseThrow();
        biomeMappings.forEach(mappings ->
                mappings.getValues().forEach((ignored, mapping) ->
                        mapping.resolveTagReferences(biomeRegistry)));
        DungeonTypes.biomeMapping().resolveTagReferences(biomeRegistry);
        DungeonCrawl.LOGGER.info("Updated biome mappings.");
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return stage.wait(Unit.INSTANCE).thenRunAsync(() -> {
            reloadProfiler.startTick();
            reloadProfiler.push("listener");
            ResourceReloadHandler.reload(resourceManager);
            reloadProfiler.pop();
            reloadProfiler.endTick();
        }, gameExecutor);
    }
}
