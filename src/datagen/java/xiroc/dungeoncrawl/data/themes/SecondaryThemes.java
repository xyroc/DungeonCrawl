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

package xiroc.dungeoncrawl.data.themes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.themes.provider.BlockProvider;
import xiroc.dungeoncrawl.data.themes.provider.WeightedBlockProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class SecondaryThemes implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public SecondaryThemes(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(HashCache directoryCache) {
        Path path = this.generator.getOutputFolder();

        HashMap<ResourceLocation, ProviderTheme.Secondary> themes = new HashMap<>();
        collectThemes(((resourceLocation, theme) -> {
            if (themes.containsKey(resourceLocation)) {
                throw new IllegalStateException("Duplicate primary theme " + resourceLocation.toString());
            }
            themes.put(resourceLocation, theme);
        }));

        themes.forEach(((resourceLocation, theme) -> {
            Path filePath = createPath(path, resourceLocation);
            try {
                DataProvider.save(GSON, directoryCache, theme.get(), filePath);
            } catch (IOException exception) {
                LOGGER.error("Failed to save {}", resourceLocation);
            }
        }));

    }

    private static Path createPath(Path p_218439_0_, ResourceLocation p_218439_1_) {
        return p_218439_0_.resolve("data/" + p_218439_1_.getNamespace() + "/theming/secondary_themes/" + p_218439_1_.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Dungeon Crawl Secondary Themes";
    }

    public void collectThemes(BiConsumer<ResourceLocation, ProviderTheme.Secondary> collector) {
        collector.accept(catacombs("crumbled"), new ProviderTheme.Secondary()
                .pillar(new BlockProvider(Blocks.BASALT))
                .trapdoor(new BlockProvider(Blocks.CAVE_AIR))
                .door(new BlockProvider(Blocks.IRON_DOOR))
                .material(new WeightedBlockProvider()
                        .add(new BlockProvider(Blocks.GRAVEL), 2)
                        .add(new BlockProvider(Blocks.BASALT))
                        .add(new BlockProvider(Blocks.CRACKED_STONE_BRICKS), 1)
                        .add(new BlockProvider(Blocks.MOSSY_STONE_BRICKS), 3)
                        .add(new BlockProvider(Blocks.MOSSY_COBBLESTONE))
                        .add(new BlockProvider(Blocks.COBBLESTONE), 2))
                .stairs(new WeightedBlockProvider()
                        .add(new BlockProvider(Blocks.COBBLESTONE_STAIRS), 2)
                        .add(new BlockProvider(Blocks.MOSSY_COBBLESTONE_STAIRS), 2)
                        .add(new BlockProvider(Blocks.MOSSY_STONE_BRICK_STAIRS)))
                .slab(new WeightedBlockProvider()
                        .add(new BlockProvider(Blocks.COBBLESTONE_SLAB), 2)
                        .add(new BlockProvider(Blocks.MOSSY_COBBLESTONE_SLAB), 2)
                        .add(new BlockProvider(Blocks.MOSSY_STONE_BRICK_SLAB)))
                .fence(new BlockProvider(Blocks.CAVE_AIR))
                .fence_gate(new BlockProvider(Blocks.CAVE_AIR))
                .button(new BlockProvider(Blocks.STONE_BUTTON))
                .pressure_plate(new BlockProvider(Blocks.STONE_PRESSURE_PLATE)));
    }

    private static ResourceLocation catacombs(String name) {
        return catacombs(DungeonCrawl.MOD_ID, name);
    }

    private static ResourceLocation catacombs(String namespace, String name) {
        return new ResourceLocation(namespace, "catacombs/" + name);
    }

}
