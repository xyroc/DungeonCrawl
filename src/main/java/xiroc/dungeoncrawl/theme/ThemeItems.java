package xiroc.dungeoncrawl.theme;

import java.util.Random;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.util.ResourceLocation;

public class ThemeItems {

	private static final Random RANDOM = new Random();

	public static ResourceLocation getMaterial(int theme, int subTheme) {
		return RANDOM.nextBoolean() ? Theme.get(theme).material.get().getBlock().getRegistryName()
				: Theme.getSub(theme).material.get().getBlock().getRegistryName();
	}

}
