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

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.TemplateLoader;
import xiroc.dungeoncrawl.dungeon.type.DungeonTypes;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public record ResourceReloadHandler(RegistryAccess registryAccess) implements PreparableReloadListener {
    private void reload(ResourceManager resourceManager) {
        final var blueprints = registryAccess.registryOrThrow(DatapackRegistries.BLUEPRINT);
        int blueprintCounter = 0;
        for (var blueprint : blueprints) {
            try {
                TemplateLoader.loadTemplateForBlueprint(resourceManager, blueprint);
                ++blueprintCounter;
            } catch (Exception e) {
                throw new DatapackLoadException("Failed to load blueprint " + blueprints.getKey(blueprint), e);
            }
        }
        DungeonCrawl.LOGGER.info("Updated {} blueprints.", blueprintCounter);
        DungeonTypes.buildMapping(registryAccess);
    }

    public static void onTagsUpdated(RegistryAccess registryAccess) {
        final var mappingRegistries = List.of(DatapackRegistries.PRIMARY_THEME_MAPPINGS, DatapackRegistries.SECONDARY_THEME_MAPPINGS);
        mappingRegistries.forEach(mappingRegistry -> registryAccess.registryOrThrow(mappingRegistry).forEach(RandomMapping::compile));
        DungeonTypes.updateMapping(registryAccess);
        DungeonCrawl.LOGGER.info("Updated biome mappings.");
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return stage.wait(Unit.INSTANCE).thenRunAsync(() -> {
            reloadProfiler.startTick();
            reloadProfiler.push("Dungeon Crawl resource reload listener");
            reload(resourceManager);
            reloadProfiler.pop();
            reloadProfiler.endTick();
        }, gameExecutor);
    }
}
