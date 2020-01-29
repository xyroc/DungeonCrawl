package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ItemProcessor<W, F, S, T> {

	ItemStack generate(W w, F f, S s, T t);

}
