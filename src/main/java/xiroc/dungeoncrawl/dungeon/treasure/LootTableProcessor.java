package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import xiroc.dungeoncrawl.DungeonCrawl;

@EventBusSubscriber(modid = DungeonCrawl.MODID, bus = Bus.FORGE)
public class LootTableProcessor {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLootTableLoad(LootTableLoadEvent event) {
		DungeonCrawl.LOGGER.debug("LootTable: {}", event.getName().toString());
//		LootTable table = event.getLootTableManager().getLootTableFromLocation(event.getName());
//		if (table != null) {
//			LootPool pool = table.getPool("main");
//			if (pool != null) {
//				LootPool p = LootPool.builder().addEntry(ItemLootEntry.builder(Items.ACACIA_BOAT)).name("").build();
//			}
//		}
	}

}
