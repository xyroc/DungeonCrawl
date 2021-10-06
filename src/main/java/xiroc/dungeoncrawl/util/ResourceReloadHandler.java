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

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Unit;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonType;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelBlockDefinition;
import xiroc.dungeoncrawl.dungeon.model.ModelPools;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.dungeon.monster.RandomMonster;
import xiroc.dungeoncrawl.dungeon.monster.RandomPotionEffect;
import xiroc.dungeoncrawl.dungeon.monster.SpawnRates;
import xiroc.dungeoncrawl.dungeon.treasure.RandomItems;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceReloadHandler implements IFutureReloadListener {

    /**
     * A list of all objects that need to get updated after the data pack files have been loaded.
     */
    public static final ArrayList<Updatable> PENDING_UPDATES = new ArrayList<>();

    public void reload(IResourceManager resourceManager) {
        DungeonCrawl.LOGGER.info("Loading data...");
        PENDING_UPDATES.clear();

        DungeonModels.load(resourceManager);
        ModelPools.load(resourceManager);
        DungeonType.load(resourceManager);

        Theme.loadJson(resourceManager);
        SpawnRates.loadJson(resourceManager);
        RandomItems.loadJson(resourceManager);
        RandomMonster.loadJson(resourceManager);
        RandomEquipment.loadJson(resourceManager);
        RandomPotionEffect.loadJson(resourceManager);

        if (Config.ENABLE_TOOLS.get()) {
            ModelBlockDefinition.loadJson(resourceManager);
        }

        DungeonCrawl.LOGGER.debug("Completing...");
        PENDING_UPDATES.forEach(Updatable::update);
        PENDING_UPDATES.clear();

        DungeonCrawl.LOGGER.info("Done.");
    }

    @Override
    public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return stage.wait(Unit.INSTANCE).thenRunAsync(() -> {
            reloadProfiler.startTick();
            reloadProfiler.push("listener");
            this.reload(resourceManager);
            reloadProfiler.pop();
            reloadProfiler.endTick();
        }, gameExecutor);
    }

}
