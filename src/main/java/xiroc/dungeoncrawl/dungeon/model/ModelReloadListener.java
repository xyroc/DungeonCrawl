package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.function.Predicate;

import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.ChildPieceHandler;

public class ModelReloadListener implements ISelectiveResourceReloadListener {

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		DungeonCrawl.LOGGER.debug("Reloading Models...");
		DungeonModels.load(resourceManager);
		ChildPieceHandler.load();
	}

}
