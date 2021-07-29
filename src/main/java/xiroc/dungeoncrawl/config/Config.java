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

public class Config {

    public static final String GENERAL = "general";
    public static final String DUNGEON = "dungeon";
    public static final String WORLD_GENERATION = "world generation";
    public static final String DUNGEON_PLACEMENT = "dungeon placement";

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec CONFIG;

    public static final IntValue SPAWNER_ENTITIES, SPAWNER_RANGE, SPACING, SEPARATION;

    public static final BooleanValue
            CUSTOM_SPAWNERS,
            NO_NETHER_STUFF,
            ENABLE_TOOLS,
            SOLID,
            NATURAL_DESPAWN,
            EXTENDED_DEBUG,
            TICK_FALLING_BLOCKS,
            OVERWRITE_ENTITY_LOOT_TABLES,
            SECRET_ROOMS;

    static {
        BUILDER.comment("General Settings").push(GENERAL);

        ENABLE_TOOLS = BUILDER.comment("Enables the dungeon crawl developer tools.").define("enable_tools", false);
        EXTENDED_DEBUG = BUILDER.comment("Enables extended debug logging to help detecting potential errors. This is enabled by default.").define("extended_debug", true);

        BUILDER.pop();

        BUILDER.comment("World Generation Settings").push(WORLD_GENERATION);
        SOLID = BUILDER.comment("Makes the entire dungeon solid, preventing caves, ravines, etc... from interfering with the dungeon.").define("solid", false);
        TICK_FALLING_BLOCKS = BUILDER.comment("Whether falling blocks like sand or gravel should drop down after being placed during dungeon generation.")
                .define("tick_falling_blocks", true);
        BUILDER.pop();

        BUILDER.comment("Dungeon Placement Settings").push(DUNGEON_PLACEMENT);
        SPACING = BUILDER.comment("The average distance between the dungeons in chunks. This has to be higher than the separation!")
                .defineInRange("spacing", 24, 8, 8192);
        SEPARATION = BUILDER.comment("The minimum distance between the dungeons in chunks. This has to be lower than the spacing!")
                .defineInRange("separation", 12, 8, 8192);
        BUILDER.pop();

        BUILDER.comment("Dungeon Settings").push(DUNGEON);
        SECRET_ROOMS = BUILDER.comment("Whether the dungeons should have secret rooms or not.").define("secret_rooms", true);
        OVERWRITE_ENTITY_LOOT_TABLES = BUILDER.comment("Whether loot tables of certain spawner entities should be overwritten." +
                " If this enabled, wither skeletons from dungeon spawners will never drop skulls. Changing this will not affect already generated dungeons.")
                .define("overwrite_entity_loot_tables", true);
        NO_NETHER_STUFF = BUILDER.comment(
                "When enabled, the hell stage will be built with blocks from the overworld, not from the nether.")
                .define("no_nether_stuff", false);
        SPAWNER_RANGE = BUILDER.comment("The activation range for the spawners in the dungeons").defineInRange("spawner_activation_range", 12, 1, 64);
        SPAWNER_ENTITIES = BUILDER.comment(
                "The number of different entities per spawner. Increasing the number increases the diversity of the monster equipment.")
                .defineInRange("spawner_entities", 6, 1, 128);
        CUSTOM_SPAWNERS = BUILDER.comment(
                "Whether custom mob spawners with equipment, etc.. should be used.")
                .define("custom_spawners", true);
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
