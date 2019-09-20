package xiroc.dungeoncrawl.dungeon.monster;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.util.ResourceLocation;
import xiroc.dungeoncrawl.config.JsonConfig;

public class ArmorSet {

	public ResourceLocation[] items;
	public String[] resourceNames;

	public ArmorSet() {
		resourceNames = new String[4];
	}

	public ArmorSet(String... armor) {
		this();
		for (int i = 0; i < 4; i++)
			resourceNames[i] = armor[i];
	}

	public void build() {
		items = JsonConfig.toResourceLocationArray(resourceNames);
	}

	public static void buildAll(ArmorSet[] armorSets) {
		for (ArmorSet armorSet : armorSets)
			armorSet.build();
	}

}
