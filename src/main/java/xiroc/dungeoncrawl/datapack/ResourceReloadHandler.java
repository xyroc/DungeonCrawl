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

package xiroc.dungeoncrawl.datapack;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.theme.Themes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ResourceReloadHandler implements PreparableReloadListener {
    private static final ImmutableList<DatapackRegistry<?>> REGISTRIES = ImmutableList.<DatapackRegistry<?>>builder()
            .add(DatapackRegistries.PRIMARY_THEME)
            .add(DatapackRegistries.SECONDARY_THEME)
            .add(DatapackRegistries.SPAWNER_ENTITY_TYPE)
            .add(DatapackRegistries.SPAWNER_TYPE)
            .add(DatapackRegistries.BLUEPRINT)
            .build();

    public void reload(ResourceManager resourceManager) {
        REGISTRIES.forEach(DatapackRegistry::unload);
        REGISTRIES.forEach(registry -> registry.reload(resourceManager));

        Themes.load(resourceManager);

        final var statistics = REGISTRIES.stream().collect(Collectors.summarizingInt(DatapackRegistry::entryCount));
        DungeonCrawl.LOGGER.info("Loaded {} registries with a total of {} data entries.", statistics.getCount(), statistics.getSum());
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return stage.wait(Unit.INSTANCE).thenRunAsync(() -> {
            reloadProfiler.startTick();
            reloadProfiler.push("listener");
            this.reload(resourceManager);
            reloadProfiler.pop();
            reloadProfiler.endTick();
        }, gameExecutor);
    }
}
