package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.util.ResourceLocation;
import xiroc.dungeoncrawl.theme.Theme;

public class MaterialBlocks {

	public static ResourceLocation getMaterial(int theme) {
		return Theme.ID_TO_THEME_MAP.get(theme).material.get().getBlock().getRegistryName();
	}

}
