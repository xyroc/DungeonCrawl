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
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Function;

public class Config {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec CONFIG;

    public static final IntValue SPAWNER_ENTITIES, SPAWNER_RANGE, SPACING, SEPARATION;

    public static final BooleanValue
            CUSTOM_SPAWNERS,
            NO_NETHER_STUFF,
            ENABLE_TOOLS,
            SOLID,
            BURY,
            NATURAL_DESPAWN,
            EXTENDED_DEBUG,
            TICK_FALLING_BLOCKS,
            OVERWRITE_ENTITY_LOOT_TABLES,
            SECRET_ROOMS,
            FIXED_GENERATION_HEIGHT;

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
        BUILDER.pop();

        BUILDER.push("World Generation");
        BURY = BUILDER.comment(SEPARATOR_LINE +
                " Experimental. Prevents caves, ravines, etc.. from interfering with a dungeon.\n").define("bury", false);
        SOLID = BUILDER
                .comment(SEPARATOR_LINE +
                        " When enabled, the dungeons will ignore caves instead of trying to adjust to them (by not generating specific blocks).\n")
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
        BUILDER.pop();

        BUILDER.push("Dungeon Settings");
        SECRET_ROOMS = BUILDER
                .comment(SEPARATOR_LINE +
                        " Whether the dungeons should have secret rooms or not.\n")
                .define("secret_rooms", true);
        FIXED_GENERATION_HEIGHT = BUILDER.comment(SEPARATOR_LINE +
                        "\nWhether the dungeons should generate at a fixed height or not. Enable this if the dungeons are generating too high.")
                .define("fixed_generation_height", false);
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
