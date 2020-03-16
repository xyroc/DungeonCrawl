package xiroc.dungeoncrawl.config;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import xiroc.dungeoncrawl.dungeon.Dungeon;

public class Config {

	public static final String CONFIG_GENERAL = "general";
	public static final String CONFIG_DUNGEON = "dungeon";
	public static final String CONFIG_WORLDGEN = "world_generation";

	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec CONFIG;

	public static final IntValue SPAWNER_ENTITIES, LAYER_ADDITIONS_MIN, LAYER_ADDITIONS_EXTRA, SIZE;

	public static final DoubleValue DUNGEON_PROBABLILITY, SHIELD_PROBABILITY, MOB_SPAWN_RATE;

	public static final BooleanValue BUILD_BRIDGES, IGNORE_OVERWORLD_BLACKLIST, IGNORE_DIMENSION, VANILLA_SPAWNERS,
			NO_SPAWNERS;
//			ENABLE_DUNGEON_BOSS;

	static {
		BUILDER.comment("General Settings").push(CONFIG_GENERAL);
		BUILDER.pop();

		BUILDER.comment("Dungeon Settings").push(CONFIG_DUNGEON);
		NO_SPAWNERS = BUILDER.comment(
				"If you dont like the fact that the dungeons contain lots of mob spawners, set this to true! Mobs will get spawned manually during the dungeon generation then. Note that this is a lot more performance demanding than enabling spawners. (Which also depends on the mob spawn rate)")
				.define("no_spawners", false);
		MOB_SPAWN_RATE = BUILDER.comment(
				"This value defines how many mobs do get spawned manually during the generation. (if no_spawners = true, there is no effect otherwise)")
				.defineInRange("mob_spawn_rate", 0.05, 0.001, 1.0);
		SPAWNER_ENTITIES = BUILDER.comment(
				"The number of different entities per spawner. Increasing the number increases the diversity of the monster equipment.")
				.defineInRange("spawner_entities", 8, 1, 128);
		LAYER_ADDITIONS_MIN = BUILDER.comment("The minumum amount of rooms for each dungeon layer.")
				.defineInRange("layer_min_additions", 5, 0, 24);
		LAYER_ADDITIONS_EXTRA = BUILDER.comment(
				"The amount of extra rooms per dungeon layer. A random number in the range [0 ~ layer_extra_additions-1] will be used.")
				.defineInRange("layer_extra_additions", 6, 1, 24);
		BUILD_BRIDGES = BUILDER.comment(
				"Defines if bridges should be used in dungeons. (Bridges are built if there is a certain amout of air under a corridor segment.")
				.define("build_bridges", true);
		IGNORE_OVERWORLD_BLACKLIST = BUILDER.comment(
				"If set to true, the dungeon generation will ignore the biome blacklist and generate dungeons in any overworld biome.")
				.define("ignore_overworld_blacklist", false);
		SHIELD_PROBABILITY = BUILDER.comment("The Probability of a spawner entity having a shield in the offhand.")
				.defineInRange("shield_probability", 0.25, 0.01, 1.0);
		VANILLA_SPAWNERS = BUILDER.comment(
				"Determines if vanilla spawners or modified spawners with armor, weapons etc... should be used.")
				.define("use_vanilla_spawners", false);
		SIZE = BUILDER.comment("The size of the dungeons. (1 unit = 8 blocks)").defineInRange("size", 16, 4, 16);
//		ENABLE_DUNGEON_BOSS = BUILDER.comment(
//				"If enabled, there will be a final room with a boss at the lowers layer. Please note that Dungeon Crawl DOES NOT bring its own bosses. The boss entities can be configured in the config.json file.")
//				.define("enable_dungeon_boss", false);

		BUILDER.comment("More configuration options will come in future updates.");
		BUILDER.pop();

		BUILDER.comment("World Generation Settings").push(CONFIG_WORLDGEN);
		DUNGEON_PROBABLILITY = BUILDER.comment("The probability of a dungeon getting generated on each fitting chunk.")
				.defineInRange("dungeon_probability", 0.26, 0.0001, 1.0);
		IGNORE_DIMENSION = BUILDER.comment(
				"If set to true, the dungeon generation will ignore the dimension and use only the biome blacklists to determine generation eligibility.")
				.define("ignore_dimension", false);
		BUILDER.pop();

		BUILDER.comment("There are a lot more other config options in config/DungeonCrawl.").push("Information");

		CONFIG = BUILDER.build();
	}

	public static void load(Path path) {
		final CommentedFileConfig config = CommentedFileConfig.builder(path).sync().autosave()
				.writingMode(WritingMode.REPLACE).build();
		config.load();
		CONFIG.setConfig(config);

		Dungeon.SIZE = SIZE.get();
	}

}
