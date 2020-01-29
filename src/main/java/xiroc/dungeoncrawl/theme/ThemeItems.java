package xiroc.dungeoncrawl.theme;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;
import net.minecraft.util.ResourceLocation;

public class ThemeItems {

	private static final Random RANDOM = new Random();

	public static ResourceLocation getMaterial(int theme, int subTheme) {
		return RANDOM.nextBoolean() ? Theme.get(theme).material.get().getBlock().getRegistryName()
				: Theme.getSub(subTheme).material.get().getBlock().getRegistryName();
	}

}
