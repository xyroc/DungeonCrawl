package xiroc.dungeoncrawl.util;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
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
		event.getRegistry()
				.register(new Dungeon(NoFeatureConfig::deserialize).setRegistryName(DungeonCrawl.locate("dungeon")));
	}

	@SubscribeEvent
	public static void onBiomeRegistry(RegistryEvent.Register<Biome> event) {
		DungeonCrawl.LOGGER.info("Adding features");
		for (Biome biome : ForgeRegistries.BIOMES) {
			DungeonCrawl.LOGGER.info("BIOME >> " + biome.getRegistryName());
			biome.addFeature(Decoration.UNDERGROUND_STRUCTURES,
					Biome.createDecoratedFeature(new Dungeon(NoFeatureConfig::deserialize),
							NoFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, NoPlacementConfig.NO_PLACEMENT_CONFIG));
			biome.addStructure(new Dungeon(NoFeatureConfig::deserialize), NoFeatureConfig.NO_FEATURE_CONFIG);
		}
	}

}
