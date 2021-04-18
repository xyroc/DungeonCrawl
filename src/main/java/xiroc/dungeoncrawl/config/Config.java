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

package xiroc.dungeoncrawl.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import java.nio.file.Path;

public class Config {

    public static final String GENERAL = "general";
    public static final String DUNGEON = "dungeon";
    public static final String WORLD_GENERATION = "world generation";
    public static final String ADVANCED_WORLD_GENERATION = "advanced world generation";

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec CONFIG;

    public static final IntValue SPAWNER_ENTITIES, SPAWNER_RANGE, SPACING, SEPARATION;

    public static final DoubleValue SHIELD_PROBABILITY, MOB_SPAWN_RATE;

    public static final BooleanValue IGNORE_OVERWORLD_BLACKLIST, IGNORE_DIMENSION, VANILLA_SPAWNERS, NO_SPAWNERS,
            NO_NETHER_STUFF, ENABLE_TOOLS, ENABLE_DUMMY_PIECES, SOLID, NATURAL_DESPAWN, EXTENDED_DEBUG, TICK_FALLING_BLOCKS,
            OVERWRITE_ENTITY_LOOT_TABLES;

    static {
        BUILDER.comment("General Settings").push(GENERAL);

        ENABLE_TOOLS = BUILDER.comment("Enables the dungeon crawl developer tools.").define("enable_tools", false);
        ENABLE_DUMMY_PIECES = BUILDER.comment("This option will make pre-2.0.0 worlds playable with version 2.0.0 and later.").define("enable_dummy_pieces", false);
        EXTENDED_DEBUG = BUILDER.comment("Enables extended debug logging to help detecting potential errors. This is enabled by default.").define("extended_debug", true);

        BUILDER.pop();

        BUILDER.comment("World Generation Settings").push(WORLD_GENERATION);
        IGNORE_DIMENSION = BUILDER.comment(
                "If this is set to false, no dungeons can be generated outside the overworld.")
                .define("ignore_dimension", false);
        SOLID = BUILDER.comment("Makes the entire dungeon solid, preventing caves, ravines, etc... from interfering with the dungeon.").define("solid", false);
        TICK_FALLING_BLOCKS = BUILDER.comment("Whether falling blocks like sand or gravel should drop down after being placed during dungeon generation.")
                .define("tick_falling_blocks", true);
        BUILDER.pop();

        BUILDER.comment("Advanced World Generation Settings").push(ADVANCED_WORLD_GENERATION);
        SPACING = BUILDER.comment("The average distance between the dungeons. This has to be higher than the separation!")
                .defineInRange("spacing", 20, 8, 8192);
        SEPARATION = BUILDER.comment("The minimum distance between the dungeons. This has to be lower than the spacing!")
                .defineInRange("separation", 10, 8, 8192);
        BUILDER.pop();

        BUILDER.comment("Dungeon Settings").push(DUNGEON);
        NO_SPAWNERS = BUILDER.comment(
                "If you dont like the fact that the dungeons contain lots of mob spawners, set this to true! Mobs will get spawned manually during the dungeon generation then. Note that this is a lot more performance demanding than enabling spawners. (Which also depends on the mob spawn rate)")
                .define("no_spawners", false);
        OVERWRITE_ENTITY_LOOT_TABLES = BUILDER.comment("Wheter loot tables of certain spawner entities should be overwritten." +
                " Having this enabled will prevent wither skeletons in dungeons from dropping skulls. Changing this will not affect already generated dungeons.")
                .define("overwrite_entity_loot_tables", true);
        NO_NETHER_STUFF = BUILDER.comment(
                "Set this to true if you want to prevent that the last layer of each dungeon will contain nether content.")
                .define("no_nether_stuff", false);
        MOB_SPAWN_RATE = BUILDER.comment(
                "This value defines how many mobs do get spawned manually during the generation. (if no_spawners = true, there is no effect otherwise)")
                .defineInRange("mob_spawn_rate", 0.05, 0.0, 1.0);
        SPAWNER_RANGE = BUILDER.comment("The activation range for the spawners in the dungeons").defineInRange("spawner_activation_range", 12, 1, 64);
        SPAWNER_ENTITIES = BUILDER.comment(
                "The number of different entities per spawner. Increasing the number increases the diversity of the monster equipment.")
                .defineInRange("spawner_entities", 6, 1, 128);
        IGNORE_OVERWORLD_BLACKLIST = BUILDER.comment(
                "If set to true, the dungeon generation will ignore the biome blacklist and generate dungeons in any overworld biome.")
                .define("ignore_overworld_blacklist", false);
        SHIELD_PROBABILITY = BUILDER.comment("The Probability of a spawner entity having a shield in the offhand.")
                .defineInRange("shield_probability", 0.25, 0.01, 1.0);
        VANILLA_SPAWNERS = BUILDER.comment(
                "Determines if vanilla spawners or modified spawners with armor, weapons etc... should be used.")
                .define("use_vanilla_spawners", false);
        NATURAL_DESPAWN = BUILDER.comment("Whether mobs from spawners should despawn naturally or not.").define("natural_despawn", true);

        BUILDER.pop();

        CONFIG = BUILDER.build();
    }

    public static void load(Path path) {
        final CommentedFileConfig config = CommentedFileConfig.builder(path).sync().autosave()
                .writingMode(WritingMode.REPLACE).build();
        config.load();
        CONFIG.setConfig(config);
    }

}
