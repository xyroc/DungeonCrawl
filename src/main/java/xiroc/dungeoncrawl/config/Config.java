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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Dungeon;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;

public class Config {

    private static final ImmutableList<Biome.Category> DEFAULT_BIOME_CATEGORIES = ImmutableList.<Biome.Category>builder()
            .add(Biome.Category.DESERT)
            .add(Biome.Category.EXTREME_HILLS)
            .add(Biome.Category.FOREST)
            .add(Biome.Category.ICY)
            .add(Biome.Category.JUNGLE)
            .add(Biome.Category.MESA)
            .add(Biome.Category.PLAINS)
            .add(Biome.Category.SAVANNA)
            .add(Biome.Category.SWAMP)
            .add(Biome.Category.TAIGA).build();

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

    private static final String SEPARATOR_LINE = "----------------------------------------------------------------------------------------------------+\n";

    static {
        BUILDER.push("Miscellaneous Settings");
        ENABLE_TOOLS = BUILDER
                .comment(SEPARATOR_LINE +
                        " Enables the dungeon crawl developer tools. Do not use this for normal gameplay.\n")
                .define("enable_tools", false);
        EXTENDED_DEBUG = BUILDER
                .comment(SEPARATOR_LINE +
                        " Enables extended debug logging to help detecting errors. Enabled by default.\n")
                .define("extended_debug", true);
        PRINT_BIOME_CATEGORIES = BUILDER
                .comment(SEPARATOR_LINE +
                        " Prints all biome categories and their respective biomes to the console when entering a world.\n"
                        + " Might be useful to modpack creators. Ignore this for normal gameplay.\n")
                .define("Print Biome Categories", false);
        BUILDER.pop();

        BUILDER.push("World Generation");
        SOLID = BUILDER
                .comment(SEPARATOR_LINE +
                        " Makes the entire dungeon solid, preventing caves, ravines, etc... from interfering with the dungeon.\n")
                .define("solid", false);
        TICK_FALLING_BLOCKS = BUILDER
                .comment(SEPARATOR_LINE +
                        " Whether falling blocks like sand or gravel should drop down after being placed during dungeon generation.\n")
                .define("tick_falling_blocks", true);
        BUILDER.pop();

        BUILDER.push("Dungeon Placement");
        SPACING = BUILDER
                .comment(SEPARATOR_LINE +
                        " The cell size of the grid used to generate the dungeons in chunks. Each cell of this grid can only contain one dungeon.\n" +
                        " You can also see this as the average distance between two adjacent dungeons in chunks.\n" +
                        " !! Has to be higher than the separation! !! \n" +
                        " Reduce this value to make the dungeons more common, increase it to make them more rare.\n" +
                        " Halving it will quadruple the amount of dungeons, doubling it would have the opposite effect.\n")
                .defineInRange("spacing", 32, 9, 4096);
        SEPARATION = BUILDER
                .comment(SEPARATOR_LINE +
                        " The minimum distance between two adjacent dungeons in chunks. Has to be lower than the spacing!\n" +
                        " The closer the separation is to the spacing, the more grid-aligned and predictable the dungeon placement will be.\n" +
                        " Generally, bigger values allow for less, and smaller values for more randomness.\n" +
                        " !! Has to be lower than the spacing! !!\n")
                .defineInRange("separation", 12, 8, 4095);

        BUILDER.push("Biomes");
        BIOME_WHITELIST = BUILDER
                .comment(SEPARATOR_LINE +
                        " Biomes the dungeons should spawn in.\n" +
                        " Entries need to use the full biome name ( eg. minecraft:plains ) and have to be comma-separated.\n" +
                        " You can use this in combination with the Biome Categories option.\n" +
                        " This is empty by default since all biomes the dungeons should spawn in are whitelisted via the Biome Categories option below.\n")
                .define("Biome Whitelist", "");
        BIOME_BLACKLIST = BUILDER
                .comment(SEPARATOR_LINE +
                        " Biomes that should never contain dungeons.\n" +
                        " Entries need to use the full biome name ( eg. minecraft:plains ) and have to be comma-separated.\n")
                .define("Biome Blacklist", "");
        BIOME_CATEGORIES = BUILDER
                .comment(SEPARATOR_LINE +
                        " List of biome categories the dungeons should spawn in.\n" +
                        " Entries have to be comma-separated.\n" +
                        " Biome Categories are groupings of biomes of specific types. Using these allows Dungeon Crawl to\n" +
                        "  automatically generate in suitable biomes and to ignore unsuitable ones like ocean biomes.\n" +
                        " You can use this in combination with the Biome Whitelist and you can exclude specific biomes with the Biome Blacklist.\n" +
                        " ALL CATEGORIES: " + allBiomeCategories() + "\n" +
                        " You may also enable the 'Print Biome Categories' option to have a list of all categories and their respective biomes\n" +
                        "  (including biomes of mods you have installed) printed to the logs.\n")
                .define("Biome Categories", defaultBiomeCategories());
        BUILDER.pop();

        BUILDER.push("Dimensions");
        DIMENSION_WHITELIST = BUILDER
                .comment(SEPARATOR_LINE +
                        " Dimensions the dungeons should spawn in.\n" +
                        " Entries need to use the full dimension name ( eg. minecraft:overworld ) and have to be comma-separated.\n")
                .define("Dimension Whitelist", "minecraft:overworld");
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Dungeon Settings");
        SECRET_ROOMS = BUILDER
                .comment(SEPARATOR_LINE +
                        " Whether the dungeons should have secret rooms or not.\n")
                .define("secret_rooms", true);
        OVERWRITE_ENTITY_LOOT_TABLES = BUILDER.
                comment(SEPARATOR_LINE +
                        " Whether loot tables of certain spawner entities should be overwritten.\n" +
                        " For example, wither skeletons from dungeon spawners will never drop skulls if this is enabled.\n")
                .define("overwrite_entity_loot_tables", true);
        NO_NETHER_STUFF = BUILDER
                .comment(SEPARATOR_LINE +
                        " Whether the hell stage should be built with blocks from the overworld instead from the nether.\n")
                .define("no_nether_blocks", false);
        SPAWNER_RANGE = BUILDER
                .comment(SEPARATOR_LINE +
                        " The activation range for the spawners in the dungeons.\n")
                .defineInRange("spawner_activation_range", 12, 1, 64);
        SPAWNER_ENTITIES = BUILDER
                .comment(SEPARATOR_LINE +
                        " The number of different entities per spawner. Increasing the number increases the diversity of the monster equipment.\n")
                .defineInRange("spawner_entities", 6, 1, 128);
        CUSTOM_SPAWNERS = BUILDER
                .comment(SEPARATOR_LINE +
                        " Whether custom mob spawners with equipment, etc.. should be used.\n")
                .define("custom_spawners", true);
        NATURAL_DESPAWN = BUILDER
                .comment(SEPARATOR_LINE +
                        " Whether mobs from spawners should despawn naturally or not.\n")
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
            Biome.Category category = Biome.Category.byName(s.toLowerCase(Locale.ROOT));
            if (category == null) {
                DungeonCrawl.LOGGER.warn("Unknown biome category '{}' in the config.", s);
                continue;
            }
            builder.add(category);
        }
        Dungeon.biomeCategories = builder.build();
    }

    private static String allBiomeCategories() {
        Iterator<Biome.Category> iterator = Arrays.stream(Biome.Category.values()).sorted(Comparator.comparing(Biome.Category::getName)).iterator();
        return commaSeparated(iterator, Biome.Category::getName);
    }

    private static String defaultBiomeCategories() {
        return commaSeparated(DEFAULT_BIOME_CATEGORIES.iterator(), Biome.Category::getName);
    }

    private static <T> String commaSeparated(Iterator<T> elements, Function<T, String> toString) {
        StringBuilder builder = new StringBuilder();
        while (elements.hasNext()) {
            builder.append(toString.apply(elements.next()));
            if (elements.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

}
