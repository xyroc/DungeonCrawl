package xiroc.dungeoncrawl.module;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

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
	
	public static final Logger LOGGER = LogManager.getLogger("Dungeon Crawl/BOP Compat");

	public BOPCompatModule() {
		super(DungeonCrawl.locate("biomesoplenty_compat"), "biomesoplenty");
	}

	@Override
	public boolean load() {
		addStructureToBiome(BOPBiomes.alps);
		addStructureToBiome(BOPBiomes.alps_foothills);
		addStructureToBiome(BOPBiomes.bayou);
		addStructureToBiome(BOPBiomes.bog);
		addStructureToBiome(BOPBiomes.boreal_forest);
		addStructureToBiome(BOPBiomes.brushland);
		addStructureToBiome(BOPBiomes.chaparral);
		addStructureToBiome(BOPBiomes.cherry_blossom_grove);
		addStructureToBiome(BOPBiomes.cold_desert);
		addStructureToBiome(BOPBiomes.coniferous_forest);
		addStructureToBiome(BOPBiomes.dead_forest);
		addStructureToBiome(BOPBiomes.fir_clearing);
		addStructureToBiome(BOPBiomes.floodplain);
		addStructureToBiome(BOPBiomes.flower_meadow);
		addStructureToBiome(BOPBiomes.grassland);
		addStructureToBiome(BOPBiomes.gravel_beach);
		addStructureToBiome(BOPBiomes.grove);
		addStructureToBiome(BOPBiomes.highland);
		addStructureToBiome(BOPBiomes.highland_moor);
		addStructureToBiome(BOPBiomes.lavender_field);
		addStructureToBiome(BOPBiomes.lush_grassland);
		addStructureToBiome(BOPBiomes.lush_swamp);
		addStructureToBiome(BOPBiomes.mangrove);
		addStructureToBiome(BOPBiomes.maple_woods);
		addStructureToBiome(BOPBiomes.marsh);
		addStructureToBiome(BOPBiomes.meadow);
		addStructureToBiome(BOPBiomes.mire);
		addStructureToBiome(BOPBiomes.mystic_grove);
		addStructureToBiome(BOPBiomes.oasis);
		addStructureToBiome(BOPBiomes.ominous_woods);
		addStructureToBiome(BOPBiomes.orchard);
		addStructureToBiome(BOPBiomes.origin_beach);
		addStructureToBiome(BOPBiomes.origin_hills);
		addStructureToBiome(BOPBiomes.outback);
		addStructureToBiome(BOPBiomes.overgrown_cliffs);
		addStructureToBiome(BOPBiomes.pasture);
		addStructureToBiome(BOPBiomes.prairie);
		addStructureToBiome(BOPBiomes.pumpkin_patch);
		addStructureToBiome(BOPBiomes.rainforest);
		addStructureToBiome(BOPBiomes.redwood_forest);
		addStructureToBiome(BOPBiomes.redwood_forest_edge);
		addStructureToBiome(BOPBiomes.scrubland);
		addStructureToBiome(BOPBiomes.seasonal_forest);
		addStructureToBiome(BOPBiomes.shield);
		addStructureToBiome(BOPBiomes.shrubland);
		addStructureToBiome(BOPBiomes.silkglade);
		addStructureToBiome(BOPBiomes.snowy_coniferous_forest);
		addStructureToBiome(BOPBiomes.snowy_fir_clearing);
		addStructureToBiome(BOPBiomes.snowy_forest);
		addStructureToBiome(BOPBiomes.steppe);
		addStructureToBiome(BOPBiomes.temperate_rainforest);
		addStructureToBiome(BOPBiomes.temperate_rainforest_hills);
		addStructureToBiome(BOPBiomes.tropical_rainforest);
		addStructureToBiome(BOPBiomes.tropics);
		addStructureToBiome(BOPBiomes.tundra);
		addStructureToBiome(BOPBiomes.wasteland);
		addStructureToBiome(BOPBiomes.wetland);
		addStructureToBiome(BOPBiomes.woodland);
		addStructureToBiome(BOPBiomes.xeric_shrubland);
		return true;
	}

	private static void addStructureToBiome(Optional<Biome> biome) {
		if (biome.isPresent()) {
			biome.get().addStructure(Dungeon.DUNGEON, NoFeatureConfig.NO_FEATURE_CONFIG);
			LOGGER.info("Added Generation to BOP Biome {}", biome.get().getRegistryName().toString());
		} else {
			LOGGER.error("Failed to add a BOP biome: Biome was not present. ({})", biome);
		}
	}

}
