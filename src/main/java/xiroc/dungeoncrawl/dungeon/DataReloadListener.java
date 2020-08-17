package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Unit;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.dungeon.treasure.RandomSpecialItem;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DataReloadListener implements IFutureReloadListener {

    public void reload(IResourceManager resourceManager) {
        DungeonCrawl.LOGGER.debug("Reloading Models...");
        DungeonModels.load(resourceManager);
        Theme.loadJson(resourceManager);
        RandomSpecialItem.loadJson(resourceManager);
        RandomEquipment.loadJson(resourceManager);
        ChildPieceHandler.load();
    }

    @Override
    public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return stage.markCompleteAwaitingOthers(Unit.INSTANCE).thenRunAsync(() -> {
            this.reload(resourceManager);
        }, gameExecutor);
    }

}
