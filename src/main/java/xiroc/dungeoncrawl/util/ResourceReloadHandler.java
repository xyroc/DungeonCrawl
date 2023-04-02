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

package xiroc.dungeoncrawl.util;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprints;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.dungeon.monster.RandomMonster;
import xiroc.dungeoncrawl.dungeon.monster.RandomPotionEffect;
import xiroc.dungeoncrawl.dungeon.monster.SpawnRates;
import xiroc.dungeoncrawl.dungeon.theme.Themes;
import xiroc.dungeoncrawl.dungeon.treasure.RandomItems;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceReloadHandler implements PreparableReloadListener {

    /**
     * A list of all objects that need to get updated after the data pack files have been loaded.
     */
    public static final ArrayList<Updatable> PENDING_UPDATES = new ArrayList<>();

    public void reload(ResourceManager resourceManager) {
        DungeonCrawl.LOGGER.info("Loading data...");
        PENDING_UPDATES.clear();

        Blueprints.load(resourceManager);
        Themes.load(resourceManager);
        SpawnRates.loadJson(resourceManager);
        RandomItems.loadJson(resourceManager);
        RandomMonster.loadJson(resourceManager);
        RandomEquipment.loadJson(resourceManager);
        RandomPotionEffect.loadJson(resourceManager);

        DungeonCrawl.LOGGER.debug("Completing...");
        PENDING_UPDATES.forEach(Updatable::update);
        PENDING_UPDATES.clear();

        DungeonCrawl.LOGGER.info("Done.");
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
