package xiroc.dungeoncrawl.theme;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.util.ResourceLocation;

public class ThemeItems {

	public static ResourceLocation getMaterial(int theme) {
		return Theme.get(theme).material.get().getBlock().getRegistryName();
	}

}
