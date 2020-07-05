package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.RandomSpecialItem;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.function.Predicate;

public class DataReloadListener implements ISelectiveResourceReloadListener {

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        DungeonCrawl.LOGGER.debug("Reloading Models...");
        DungeonModels.load(resourceManager);
        Theme.loadJson(resourceManager);
        RandomSpecialItem.loadJson(resourceManager);
        ChildPieceHandler.load();
    }

}
