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
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.block.provider.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.decoration.VineDecoration;
import xiroc.dungeoncrawl.theme.Theme;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class PrimaryThemes implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public PrimaryThemes(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(HashCache directoryCache) {
        Path path = this.generator.getOutputFolder();

        HashMap<ResourceLocation, Theme> themes = new HashMap<>();
        collectThemes(((resourceLocation, theme) -> {
            if (themes.containsKey(resourceLocation)) {
                throw new IllegalStateException("Duplicate primary theme " + resourceLocation.toString());
            }
            themes.put(resourceLocation, theme);
        }));

        themes.forEach(((resourceLocation, theme) -> {
            Path filePath = createPath(path, resourceLocation);
            try {
                DataProvider.save(GSON, directoryCache, theme.serialize(), filePath);
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

    public void collectThemes(BiConsumer<ResourceLocation, Theme> collector) {
        collector.accept(catacombs("crumbled"), Theme.builder()
                .addDecoration(new VineDecoration(0.5F))
                .solid(WeightedRandomBlock.builder()
                        .add(Blocks.GRAVEL, 2)
                        .add(Blocks.COBBLESTONE, 2)
                        .add(Blocks.CRACKED_STONE_BRICKS, 1)
                        .add(Blocks.MOSSY_STONE_BRICKS, 3)
                        .add(Blocks.MOSSY_COBBLESTONE, 1)
                        .build())
                .generic(WeightedRandomBlock.builder()
                        .add(Blocks.GRAVEL, 2)
                        .add(Blocks.COBBLESTONE, 2)
                        .add(Blocks.CRACKED_STONE_BRICKS, 1)
                        .add(Blocks.MOSSY_STONE_BRICKS, 3)
                        .add(Blocks.MOSSY_COBBLESTONE, 1)
                        .build())
                .pillar(new SingleBlock(Blocks.BASALT))
                .fencing(SingleBlock.AIR)
                .floor(WeightedRandomBlock.builder()
                        .add(Blocks.BASALT, 2)
                        .add(Blocks.POLISHED_BASALT, 1)
                        .add(Blocks.GRAVEL, 3)
                        .add(Blocks.MOSSY_COBBLESTONE, 2)
                        .add(Blocks.COBBLESTONE, 2)
                        .build())
                .material(WeightedRandomBlock.builder()
                        .add(Blocks.GRAVEL, 2)
                        .add(Blocks.BASALT, 1)
                        .add(Blocks.CRACKED_STONE_BRICKS, 1)
                        .add(Blocks.MOSSY_STONE_BRICKS, 3)
                        .add(Blocks.MOSSY_COBBLESTONE, 1)
                        .add(Blocks.COBBLESTONE, 2)
                        .build())
                .stairs(WeightedRandomBlock.builder()
                        .add(Blocks.COBBLESTONE_STAIRS, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_STAIRS, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_STAIRS, 1)
                        .build())
                .solidStairs(WeightedRandomBlock.builder()
                        .add(Blocks.COBBLESTONE_STAIRS, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_STAIRS, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_STAIRS, 1)
                        .build())
                .slab(WeightedRandomBlock.builder()
                        .add(Blocks.COBBLESTONE_SLAB, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_SLAB, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_SLAB, 1)
                        .build())
                .solidSlab(WeightedRandomBlock.builder()
                        .add(Blocks.COBBLESTONE_SLAB, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_SLAB, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_SLAB, 1)
                        .build())
                .wall(new SingleBlock(Blocks.MOSSY_STONE_BRICK_WALL))
                .build());
    }

    private static ResourceLocation catacombs(String name) {
        return catacombs(DungeonCrawl.MOD_ID, name);
    }

    private static ResourceLocation catacombs(String namespace, String name) {
        return new ResourceLocation(namespace, "catacombs/" + name);
    }

}
