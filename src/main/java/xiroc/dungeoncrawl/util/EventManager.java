package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Dungeon;

@EventBusSubscriber(modid = DungeonCrawl.MODID, bus = Bus.MOD)
public class EventManager {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onBiomeRegistry(RegistryEvent.Register<Biome> event) {
		DungeonCrawl.LOGGER.info("Adding features and structures");
		for (Biome biome : ForgeRegistries.BIOMES) {
			if (!JsonConfig.BIOME_BLACKLIST.contains(biome.getRegistryName().toString())
					&& createAssumption(biome.getRegistryName().toString())) {
				DungeonCrawl.LOGGER.debug("Biome >> " + biome.getRegistryName());
				biome.addFeature(Decoration.UNDERGROUND_STRUCTURES,
						Biome.createDecoratedFeature(Dungeon.DUNGEON_FEATURE, NoFeatureConfig.NO_FEATURE_CONFIG,
								Placement.NOPE, NoPlacementConfig.NO_PLACEMENT_CONFIG));
				if (!JsonConfig.BIOME_OVERWORLD_BLACKLIST.contains(biome.getRegistryName().toString())) {
					DungeonCrawl.LOGGER.debug("Generation Biome >> " + biome.getRegistryName());
					biome.addStructure(Dungeon.DUNGEON_FEATURE, NoFeatureConfig.NO_FEATURE_CONFIG);
				}
			}
		}
	}

	public static boolean createAssumption(String name) {
		for (String part : JsonConfig.ASSUMPTION_SEARCHLIST) {
			if (name.contains(part)) {
				DungeonCrawl.LOGGER.info(
						"The biome {} contains the sequence \"{}\". It will be blacklisted, assuming that it belogs to a different dimension than the overworld. If you are a modpack creator or thinking that this is wrong, consider configurating the biome-blacklists inside the DungeonCrawl/config.json file in the confg directory and clear the ASSUMPTION_SEARCHLIST.",
						name, part);
				return false;
			}
		}
		return true;
	}

}
