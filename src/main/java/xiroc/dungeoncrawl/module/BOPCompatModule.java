package xiroc.dungeoncrawl.module;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.api.block.BOPBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.NoFeatureConfig;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.module.ModuleManager.Module;
import xiroc.dungeoncrawl.part.block.BlockRegistry.TupleIntBlock;
import xiroc.dungeoncrawl.part.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.theme.Theme;

public class BOPCompatModule extends Module {
	
	// Dungeon Crawl - Biomes O' Plenty Support v 1.0.0

	public static final Logger LOGGER = LogManager.getLogger("Dungeon Crawl/BOP Compat");

	public BOPCompatModule() {
		super(DungeonCrawl.locate("biomesoplenty_compat"), "biomesoplenty");
	}

	@Override
	public boolean load() {

		WeightedRandomBlock mudFloor = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, BOPBlocks.mud.getDefaultState()),
						new TupleIntBlock(1, BOPBlocks.mud_bricks.getDefaultState()),
						new TupleIntBlock(1, Blocks.COARSE_DIRT.getDefaultState()) });

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
//		addStructureToBiome(BOPBiomes.mangrove);
		addStructureToBiome(BOPBiomes.maple_woods);
//		addStructureToBiome(BOPBiomes.marsh);
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
//		addStructureToBiome(BOPBiomes.redwood_forest);
//		addStructureToBiome(BOPBiomes.redwood_forest_edge);
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
		
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:xeric_shrubland", 3);

		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:grove", 5);
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:land_of_lakes", 5);
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:maple_woods", 5);
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:shield", 5);
		
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:bayou", 32);
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:wetland", 32);

		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:lavender_field", 33);

		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:brushland", 34);
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:dead_forest", 34);
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:dead_swamp", 34);
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:wasteland", 34);

		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:cherry_blossom_grove", 35);

		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:coniferous_forest", 36);
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:snowy_coniferous_forest", 36);
		
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:omnious_woods", 37);
		
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:overgrown_cliffs", 38);
		Theme.BIOME_TO_SUBTHEME_MAP.put("biomesoplenty:tropical_rainforest", 38);


		Theme.ID_TO_SUBTHEME_MAP.put(32,
				new Theme.SubTheme(BOPBlocks.willow_log.getDefaultState(), BOPBlocks.willow_trapdoor.getDefaultState(),
						Blocks.REDSTONE_WALL_TORCH.getDefaultState(), BOPBlocks.willow_door.getDefaultState(),
						BOPBlocks.willow_planks.getDefaultState()));

		Theme.ID_TO_SUBTHEME_MAP.put(33,
				new Theme.SubTheme(BOPBlocks.jacaranda_log.getDefaultState(),
						BOPBlocks.jacaranda_trapdoor.getDefaultState(), Blocks.REDSTONE_WALL_TORCH.getDefaultState(),
						BOPBlocks.jacaranda_door.getDefaultState(), BOPBlocks.jacaranda_planks.getDefaultState()));

		Theme.ID_TO_SUBTHEME_MAP.put(34,
				new Theme.SubTheme(BOPBlocks.dead_log.getDefaultState(), BOPBlocks.dead_trapdoor.getDefaultState(),
						Blocks.REDSTONE_WALL_TORCH.getDefaultState(), BOPBlocks.dead_door.getDefaultState(),
						BOPBlocks.dead_planks.getDefaultState()));

		Theme.ID_TO_SUBTHEME_MAP.put(35,
				new Theme.SubTheme(BOPBlocks.cherry_log.getDefaultState(), BOPBlocks.cherry_trapdoor.getDefaultState(),
						Blocks.REDSTONE_WALL_TORCH.getDefaultState(), BOPBlocks.cherry_door.getDefaultState(),
						BOPBlocks.cherry_planks.getDefaultState()));

		Theme.ID_TO_SUBTHEME_MAP.put(36,
				new Theme.SubTheme(BOPBlocks.fir_log.getDefaultState(), BOPBlocks.fir_trapdoor.getDefaultState(),
						Blocks.REDSTONE_WALL_TORCH.getDefaultState(), BOPBlocks.fir_door.getDefaultState(),
						BOPBlocks.fir_planks.getDefaultState()));
		
		Theme.ID_TO_SUBTHEME_MAP.put(37,
				new Theme.SubTheme(BOPBlocks.umbran_log.getDefaultState(), BOPBlocks.umbran_trapdoor.getDefaultState(),
						Blocks.REDSTONE_WALL_TORCH.getDefaultState(), BOPBlocks.umbran_door.getDefaultState(),
						BOPBlocks.umbran_planks.getDefaultState()));
		
		Theme.ID_TO_SUBTHEME_MAP.put(38,
				new Theme.SubTheme(BOPBlocks.mahogany_log.getDefaultState(), BOPBlocks.mahogany_trapdoor.getDefaultState(),
						Blocks.REDSTONE_WALL_TORCH.getDefaultState(), BOPBlocks.mahogany_door.getDefaultState(),
						BOPBlocks.mahogany_planks.getDefaultState()));

		Theme.BIOME_TO_THEME_MAP.put("biomesoplenty:xeric_shrubland", 16);
		
		Theme.BIOME_TO_THEME_MAP.put("biomesoplenty:brushland", 64);
		Theme.BIOME_TO_THEME_MAP.put("biomesoplenty:quagmire", 64);

		Theme.ID_TO_THEME_MAP.put(64,
				new Theme(() -> BOPBlocks.mud_bricks.getDefaultState(),
						() -> BOPBlocks.mud_bricks.getDefaultState(), mudFloor,
						() -> BOPBlocks.mud_brick_stairs.getDefaultState(), () -> BOPBlocks.mud.getDefaultState(),
						() -> BOPBlocks.mud_brick_wall.getDefaultState(), mudFloor));
		
		Theme.RANDOMIZERS.put(64, Theme.createRandomizer(64, 0, 49));

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
