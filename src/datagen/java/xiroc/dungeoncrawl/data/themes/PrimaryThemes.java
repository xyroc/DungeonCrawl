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
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.themes.decoration.Decoration;
import xiroc.dungeoncrawl.data.themes.decoration.DecorationType;
import xiroc.dungeoncrawl.data.themes.provider.BlockProvider;
import xiroc.dungeoncrawl.data.themes.provider.WeightedBlockProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class PrimaryThemes implements IDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public PrimaryThemes(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(DirectoryCache directoryCache) {
        Path path = this.generator.getOutputFolder();

        HashMap<ResourceLocation, ProviderTheme.Primary> themes = new HashMap<>();
        collectThemes(((resourceLocation, theme) -> {
            if (themes.containsKey(resourceLocation)) {
                throw new IllegalStateException("Duplicate primary theme " + resourceLocation.toString());
            }
            themes.put(resourceLocation, theme);
        }));

        themes.forEach(((resourceLocation, theme) -> {
            Path filePath = createPath(path, resourceLocation);
            try {
                IDataProvider.save(GSON, directoryCache, theme.get(), filePath);
            } catch (IOException exception) {
                LOGGER.error("Failed to save {}", resourceLocation);
            }
        }));

    }

    private static Path createPath(Path p_218439_0_, ResourceLocation p_218439_1_) {
        return p_218439_0_.resolve("data/" + p_218439_1_.getNamespace() + "/theming/primary_themes/" + p_218439_1_.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Dungeon Crawl Primary Themes";
    }

    public void collectThemes(BiConsumer<ResourceLocation, ProviderTheme.Primary> collector) {
        collector.accept(catacombs("crumbled"), new ProviderTheme.Primary()
                .addDecoration(new Decoration(DecorationType.VINES)
                        .chance(0.5F))
                .solid(new WeightedBlockProvider()
                        .add(new BlockProvider(Blocks.GRAVEL), 2)
                        .add(new BlockProvider(Blocks.COBBLESTONE), 2)
                        .add(new BlockProvider(Blocks.CRACKED_STONE_BRICKS), 1)
                        .add(new BlockProvider(Blocks.MOSSY_STONE_BRICKS), 3)
                        .add(new BlockProvider(Blocks.MOSSY_COBBLESTONE)))
                .generic(new WeightedBlockProvider()
                        .add(new BlockProvider(Blocks.GRAVEL), 2)
                        .add(new BlockProvider(Blocks.COBBLESTONE), 2)
                        .add(new BlockProvider(Blocks.CRACKED_STONE_BRICKS), 1)
                        .add(new BlockProvider(Blocks.MOSSY_STONE_BRICKS), 3)
                        .add(new BlockProvider(Blocks.MOSSY_COBBLESTONE)))
                .pillar(new BlockProvider(Blocks.BASALT))
                .fencing(new BlockProvider(Blocks.CAVE_AIR))
                .floor(new WeightedBlockProvider()
                        .add(new BlockProvider(Blocks.BASALT), 2)
                        .add(new BlockProvider(Blocks.POLISHED_BASALT))
                        .add(new BlockProvider(Blocks.GRAVEL), 3)
                        .add(new BlockProvider(Blocks.MOSSY_COBBLESTONE), 2)
                        .add(new BlockProvider(Blocks.COBBLESTONE), 2))
                .fluid(new BlockProvider(Blocks.WATER))
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
                .solid_stairs(new WeightedBlockProvider()
                        .add(new BlockProvider(Blocks.COBBLESTONE_STAIRS), 2)
                        .add(new BlockProvider(Blocks.MOSSY_COBBLESTONE_STAIRS), 2)
                        .add(new BlockProvider(Blocks.MOSSY_STONE_BRICK_STAIRS)))
                .slab(new WeightedBlockProvider()
                        .add(new BlockProvider(Blocks.COBBLESTONE_SLAB), 2)
                        .add(new BlockProvider(Blocks.MOSSY_COBBLESTONE_SLAB), 2)
                        .add(new BlockProvider(Blocks.MOSSY_STONE_BRICK_SLAB)))
                .solid_slab(new WeightedBlockProvider()
                        .add(new BlockProvider(Blocks.COBBLESTONE_SLAB), 2)
                        .add(new BlockProvider(Blocks.MOSSY_COBBLESTONE_SLAB), 2)
                        .add(new BlockProvider(Blocks.MOSSY_STONE_BRICK_SLAB)))
                .wall(new BlockProvider(Blocks.MOSSY_STONE_BRICK_WALL)));
    }

    private static ResourceLocation catacombs(String name) {
        return catacombs(DungeonCrawl.MOD_ID, name);
    }

    private static ResourceLocation catacombs(String namespace, String name) {
        return new ResourceLocation(namespace, "catacombs/" + name);
    }

}
