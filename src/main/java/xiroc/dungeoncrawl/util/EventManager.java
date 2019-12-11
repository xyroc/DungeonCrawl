package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import xiroc.dungeoncrawl.DungeonCrawl;

@EventBusSubscriber(modid = DungeonCrawl.MODID, bus = Bus.MOD)
public class EventManager {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onBiomeRegistry(RegistryEvent.Register<Biome> event) {
		
	}

//	public static boolean createAssumption(String name) {
//		for (String part : JsonConfig.ASSUMPTION_SEARCHLIST) {
//			if (name.contains(part)) {
//				DungeonCrawl.LOGGER.info(
//						"The biome {} contains the sequence \"{}\". It will be blacklisted, assuming that it belogs to a different dimension than the overworld. If you are a modpack creator or thinking that this is wrong, consider configurating the biome-blacklists inside the DungeonCrawl/config.json file in the confg directory and clear the ASSUMPTION_SEARCHLIST.",
//						name, part);
//				return false;
//			}
//		}
//		return true;
//	}

}
