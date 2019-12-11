package xiroc.dungeoncrawl.module;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import biomesoplenty.api.biome.BOPBiomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.NoFeatureConfig;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.module.ModuleManager.Module;

public class BOPCompatModule extends Module {
	
	public static final Logger LOGGER = LogManager.getLogger("BOP Compat");

	public BOPCompatModule() {
		super(DungeonCrawl.locate("biomesoplenty_compat"));
	}

	@Override
	public boolean load() {
		addFeatureToBiome(BOPBiomes.alps);
		addFeatureToBiome(BOPBiomes.alps_foothills);
		addFeatureToBiome(BOPBiomes.bayou);
		addFeatureToBiome(BOPBiomes.bog);
		addFeatureToBiome(BOPBiomes.boreal_forest);
		addFeatureToBiome(BOPBiomes.brushland);
		addFeatureToBiome(BOPBiomes.chaparral);
		addFeatureToBiome(BOPBiomes.cherry_blossom_grove);
		addFeatureToBiome(BOPBiomes.cold_desert);
		addFeatureToBiome(BOPBiomes.coniferous_forest);
		addFeatureToBiome(BOPBiomes.dead_forest);
		addFeatureToBiome(BOPBiomes.fir_clearing);
		addFeatureToBiome(BOPBiomes.floodplain);
		addFeatureToBiome(BOPBiomes.flower_meadow);
		addFeatureToBiome(BOPBiomes.grassland);
		addFeatureToBiome(BOPBiomes.gravel_beach);
		addFeatureToBiome(BOPBiomes.grove);
		addFeatureToBiome(BOPBiomes.highland);
		addFeatureToBiome(BOPBiomes.highland_moor);
		addFeatureToBiome(BOPBiomes.lavender_field);
		addFeatureToBiome(BOPBiomes.lush_grassland);
		addFeatureToBiome(BOPBiomes.lush_swamp);
		addFeatureToBiome(BOPBiomes.mangrove);
		addFeatureToBiome(BOPBiomes.maple_woods);
		addFeatureToBiome(BOPBiomes.marsh);
		addFeatureToBiome(BOPBiomes.meadow);
		addFeatureToBiome(BOPBiomes.mire);
		addFeatureToBiome(BOPBiomes.mystic_grove);
		addFeatureToBiome(BOPBiomes.oasis);
		addFeatureToBiome(BOPBiomes.ominous_woods);
		addFeatureToBiome(BOPBiomes.orchard);
		addFeatureToBiome(BOPBiomes.origin_beach);
		addFeatureToBiome(BOPBiomes.origin_hills);
		addFeatureToBiome(BOPBiomes.outback);
		addFeatureToBiome(BOPBiomes.overgrown_cliffs);
		addFeatureToBiome(BOPBiomes.pasture);
		addFeatureToBiome(BOPBiomes.prairie);
		addFeatureToBiome(BOPBiomes.pumpkin_patch);
		addFeatureToBiome(BOPBiomes.rainforest);
		addFeatureToBiome(BOPBiomes.redwood_forest);
		addFeatureToBiome(BOPBiomes.redwood_forest_edge);
		addFeatureToBiome(BOPBiomes.scrubland);
		addFeatureToBiome(BOPBiomes.seasonal_forest);
		addFeatureToBiome(BOPBiomes.shrubland);
		addFeatureToBiome(BOPBiomes.silkglade);
		addFeatureToBiome(BOPBiomes.snowy_coniferous_forest);
		addFeatureToBiome(BOPBiomes.snowy_fir_clearing);
		addFeatureToBiome(BOPBiomes.snowy_forest);
		addFeatureToBiome(BOPBiomes.steppe);
		addFeatureToBiome(BOPBiomes.temperate_rainforest);
		addFeatureToBiome(BOPBiomes.temperate_rainforest_hills);
		addFeatureToBiome(BOPBiomes.tropical_rainforest);
		addFeatureToBiome(BOPBiomes.tropics);
		addFeatureToBiome(BOPBiomes.tundra);
		addFeatureToBiome(BOPBiomes.wasteland);
		addFeatureToBiome(BOPBiomes.wetland);
		addFeatureToBiome(BOPBiomes.woodland);
		addFeatureToBiome(BOPBiomes.xeric_shrubland);
		return true;
	}

	private static void addFeatureToBiome(Optional<Biome> biome) {
		if (biome.isPresent()) {
//			biome.get().addFeature(Decoration.UNDERGROUND_STRUCTURES, Biome.createDecoratedFeature(Dungeon.DUNGEON,
//					NoFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, NoPlacementConfig.NO_PLACEMENT_CONFIG));
			biome.get().addStructure(Dungeon.DUNGEON, NoFeatureConfig.NO_FEATURE_CONFIG);
			LOGGER.info("Added Generation to BOP Biome {}.", biome.get().getRegistryName().toString());
		} else {
			LOGGER.error("Failed to add a BOP biome. Biome was not present.");
		}
	}

}
