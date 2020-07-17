package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import xiroc.dungeoncrawl.DungeonCrawl;

@Mod.EventBusSubscriber(modid = DungeonCrawl.MODID, bus = Bus.MOD)
public class EventManager {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBiomeRegistry(RegistryEvent.Register<Biome> event) {

    }

    @SubscribeEvent
    public void onServerStart(FMLServerAboutToStartEvent event) {
        DungeonCrawl.LOGGER.debug(">>>>>>>>> SERVER ABOUT TO START <<<<<<<<<");
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
