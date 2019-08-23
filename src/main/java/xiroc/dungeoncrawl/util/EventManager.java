package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.Feature;
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

    @SubscribeEvent
    public static void onFeatureRegistry(RegistryEvent.Register<Feature<?>> event) {
	DungeonCrawl.LOGGER.info("Registering features");
//		event.getRegistry().register(Dungeon.DUNGEON);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBiomeRegistry(RegistryEvent.Register<Biome> event) {
	DungeonCrawl.LOGGER.info("Adding features and structures");
	for (Biome biome : ForgeRegistries.BIOMES) {
	    if (!DungeonCrawl.BIOME_BLACKLIST.contains(biome.getRegistryName().toString())) {
		DungeonCrawl.LOGGER.debug("Biome >> " + biome.getRegistryName());
		biome.addFeature(Decoration.UNDERGROUND_STRUCTURES,
			Biome.createDecoratedFeature(Dungeon.DUNGEON_FEATURE, NoFeatureConfig.NO_FEATURE_CONFIG,
				Placement.NOPE, NoPlacementConfig.NO_PLACEMENT_CONFIG));
		if (!DungeonCrawl.BIOME_OVERWORLD_BLACKLIST.contains(biome.getRegistryName().toString())) {
		    DungeonCrawl.LOGGER.debug("Generation Biome >> " + biome.getRegistryName());
		    biome.addStructure(Dungeon.DUNGEON_FEATURE, NoFeatureConfig.NO_FEATURE_CONFIG);
		}
	    }
	}
    }

//	public static void onSpawn(EntityEvent.EntityConstructing event) {
//		if ()
//	}

}
