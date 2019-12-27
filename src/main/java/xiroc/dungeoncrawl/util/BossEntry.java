package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import xiroc.dungeoncrawl.DungeonCrawl;



public class BossEntry {

	public String entityName, nbt;

	public BossEntry(String entityName, String nbt) {
		this.entityName = entityName;
		this.nbt = nbt;
	}

	public CompoundNBT createTag() {
		if (nbt == null)
			return null;
		try {
			return new JsonToNBT(new StringReader(nbt)).readStruct();
		} catch (CommandSyntaxException e) {
			DungeonCrawl.LOGGER.error("Failed to read a boss nbt tag.");
			e.printStackTrace();
			return null;
		}
	}

}
