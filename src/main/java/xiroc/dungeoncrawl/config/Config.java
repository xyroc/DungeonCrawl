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
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Dungeon;

import java.nio.file.Path;

public class Config {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec CONFIG;

    public static final IntValue SPAWNER_ENTITIES, SPAWNER_RANGE, SPACING, SEPARATION;

    public static final ForgeConfigSpec.ConfigValue<String> DIMENSION_WHITELIST,
            BIOME_WHITELIST,
            BIOME_BLACKLIST,
            BIOME_CATEGORIES;

    public static final BooleanValue
            CUSTOM_SPAWNERS,
            NO_NETHER_STUFF,
            ENABLE_TOOLS,
            SOLID,
            NATURAL_DESPAWN,
            EXTENDED_DEBUG,
            TICK_FALLING_BLOCKS,
            OVERWRITE_ENTITY_LOOT_TABLES,
            SECRET_ROOMS,
            PRINT_BIOME_CATEGORIES;

    static {
        BUILDER.push("Miscellaneous Settings");
        ENABLE_TOOLS = BUILDER
                .comment("Enables the dungeon crawl developer tools. Do not use this for normal gameplay.")
                .define("enable_tools", false);
        EXTENDED_DEBUG = BUILDER
                .comment("Enables extended debug logging to help detecting errors. Enabled by default.")
                .define("extended_debug", true);
        PRINT_BIOME_CATEGORIES = BUILDER
                .comment("Prints all biome categories and their biomes to the console when entering a world.\n"
                        + "Might be useful for modpack creators. Ignore this for normal gameplay.")
                .define("Print Biome Categories", false);
        BUILDER.pop();

        BUILDER.push("World Generation");
        SOLID = BUILDER
                .comment("Makes the entire dungeon solid, preventing caves, ravines, etc... from interfering with the dungeon.")
                .define("solid", false);
        TICK_FALLING_BLOCKS = BUILDER
                .comment("Whether falling blocks like sand or gravel should drop down after being placed during dungeon generation.")
                .define("tick_falling_blocks", true);
        BUILDER.pop();

        BUILDER.push("Dungeon Placement");
        SPACING = BUILDER
                .comment("The cell size of the grid used to generate the dungeons in chunks. Each cell of this grid can only contain one dungeon.\n" +
                        "You can also interpret this as the average distance between two adjacent dungeons in chunks.\n" +
                        "Has to be higher than the separation!")
                .defineInRange("spacing", 24, 9, 8192);
        SEPARATION = BUILDER
                .comment("The minimum distance between the dungeons in chunks. Has to be lower than the spacing!\n" +
                        "The closer the separation is to the spacing, the more grid-aligned and predictable the dungeon placement will be.\n" +
                        "Generally, bigger values allow for less, and smaller values for more randomness.")
                .defineInRange("separation", 12, 8, 8191);

        BUILDER.push("Biomes");
        BIOME_WHITELIST = BUILDER
                .comment("List of biomes the dungeons should spawn in.\n" +
                        "Entries have to be comma-separated.\n" +
                        "You can use this together with the Biome Categories.")
                .define("Biome Whitelist", "");
        BIOME_BLACKLIST = BUILDER
                .comment("List of biomes that should never contain dungeons.\n" +
                        "Entries must be comma-separated.")
                .define("Biome Blacklist", "");
        BIOME_CATEGORIES = BUILDER
                .comment("List of biome categories the dungeons should spawn in.\n" +
                        "Entries have to be comma-separated.\n" +
                        "Biome Categories are groupings of biomes of specific types. Using these allows Dungeon Crawl to automatically generate in suitable mod biomes\n" +
                        " and to ignore unsuitable ones like ocean biomes.\n" +
                        "You can use this together with the Biome Whitelist\n" +
                        " and you can blacklist specific biomes with the Biome Blacklist.\n" +
                        "All categories: beach, desert, extreme_hills, forest, icy, jungle, mesa, mushroom, nether, none, ocean, plains, river, savanna, swamp, taiga, the_end\n" +
                        "To receive a list of all categories and their respective biomes (including biomes of mods you have installed), enable the 'Print Biome Categories' option.'")
                .define("Biome Categories", "desert, extreme_hills, forest, icy, jungle, mesa, plains, savanna, swamp, taiga");
        BUILDER.pop();

        BUILDER.push("Dimensions");
        DIMENSION_WHITELIST = BUILDER
                .comment("List of dimensions the dungeons should spawn in.\n" +
                        "Entries have to be comma-separated.")
                .define("Dimension Whitelist", "minecraft:overworld");
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Dungeon Settings");
        SECRET_ROOMS = BUILDER
                .comment("Whether the dungeons should have secret rooms or not.")
                .define("secret_rooms", true);
        OVERWRITE_ENTITY_LOOT_TABLES = BUILDER.
                comment("Whether loot tables of certain spawner entities should be overwritten.\n" +
                        "For example, wither skeletons from dungeon spawners will never drop skulls if this is enabled.")
                .define("overwrite_entity_loot_tables", true);
        NO_NETHER_STUFF = BUILDER
                .comment("Whether the hell stage should be built with blocks from the overworld instead from the nether.")
                .define("no_nether_stuff", false);
        SPAWNER_RANGE = BUILDER
                .comment("The activation range for the spawners in the dungeons.")
                .defineInRange("spawner_activation_range", 12, 1, 64);
        SPAWNER_ENTITIES = BUILDER
                .comment("The number of different entities per spawner. Increasing the number increases the diversity of the monster equipment.")
                .defineInRange("spawner_entities", 6, 1, 128);
        CUSTOM_SPAWNERS = BUILDER
                .comment("Whether custom mob spawners with equipment, etc.. should be used.")
                .define("custom_spawners", true);
        NATURAL_DESPAWN = BUILDER
                .comment("Whether mobs from spawners should despawn naturally or not.")
                .define("natural_despawn", true);
        BUILDER.pop();

        CONFIG = BUILDER.build();
    }

    public static void load(Path path) {
        final CommentedFileConfig config = CommentedFileConfig.builder(path).sync().autosave()
                .writingMode(WritingMode.REPLACE).build();
        config.load();
        CONFIG.setConfig(config);

        Dungeon.whitelistedDimensions = ImmutableSet.copyOf(DIMENSION_WHITELIST.get().split(",\\s*"));
        DungeonCrawl.LOGGER.info("Whitelisted Dimensions:");
        Dungeon.whitelistedDimensions.forEach(DungeonCrawl.LOGGER::info);

        Dungeon.whitelistedBiomes = ImmutableSet.copyOf(BIOME_WHITELIST.get().split(",\\s*"));
        Dungeon.blacklistedBiomes = ImmutableSet.copyOf(BIOME_BLACKLIST.get().split(",\\s*"));

        ImmutableSet.Builder<Biome.Category> builder = new ImmutableSet.Builder<>();
        for (String s : BIOME_CATEGORIES.get().split(",\\s*")) {
            Biome.Category category = Biome.Category.byName(s);
            if (category == null) {
                DungeonCrawl.LOGGER.warn("Unknown biome category '{}' in the config.", s);
                continue;
            }
            builder.add(category);
        }
        Dungeon.biomeCategories = builder.build();
    }

}
