/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.util;

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
