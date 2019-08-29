package xiroc.dungeoncrawl.dungeon.monster;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.util.ResourceLocation;

public class ArmorSet {

	public ResourceLocation[] items;

	public ArmorSet() {
		items = new ResourceLocation[4];
	}

	public ArmorSet(String... armor) {
		this();
		for (int i = 0; i < 4; i++)
			items[i] = new ResourceLocation(armor[i]);
	}

}
