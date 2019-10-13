package xiroc.dungeoncrawl.util;

import net.minecraft.item.ItemStack;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

@FunctionalInterface
public interface ItemProcessor<W, F, S, T> {

	ItemStack generate(W w, F f, S s, T t);

}
