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
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.block.provider.WeightedRandomBlock;
import xiroc.dungeoncrawl.theme.SecondaryTheme;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class SecondaryThemes implements IDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public SecondaryThemes(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(DirectoryCache directoryCache) {
        Path path = this.generator.getOutputFolder();

        HashMap<ResourceLocation, SecondaryTheme> themes = new HashMap<>();
        collectThemes(((resourceLocation, theme) -> {
            if (themes.containsKey(resourceLocation)) {
                throw new IllegalStateException("Duplicate primary theme " + resourceLocation.toString());
            }
            themes.put(resourceLocation, theme);
        }));

        themes.forEach(((resourceLocation, theme) -> {
            Path filePath = createPath(path, resourceLocation);
            try {
                IDataProvider.save(GSON, directoryCache, theme.serialize(), filePath);
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

    public void collectThemes(BiConsumer<ResourceLocation, SecondaryTheme> collector) {
        collector.accept(catacombs("crumbled"), SecondaryTheme.builder()
                .pillar(new SingleBlock(Blocks.BASALT))
                .trapdoor(SingleBlock.AIR)
                .door(new SingleBlock(Blocks.IRON_DOOR))
                .material(WeightedRandomBlock.builder()
                        .add(Blocks.GRAVEL)
                        .add(Blocks.BASALT)
                        .add(Blocks.CRACKED_STONE_BRICKS, 3)
                        .add(Blocks.MOSSY_STONE_BRICKS, 3)
                        .add(Blocks.MOSSY_COBBLESTONE, 2)
                        .add(Blocks.COBBLESTONE, 2)
                        .build())
                .stairs(WeightedRandomBlock.builder()
                        .add(Blocks.COBBLESTONE_STAIRS, 2)
                        .add(Blocks.MOSSY_COBBLESTONE_STAIRS, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_STAIRS)
                        .build())
                .slab(WeightedRandomBlock.builder()
                        .add(Blocks.COBBLESTONE_SLAB, 2)
                        .add(Blocks.MOSSY_COBBLESTONE_SLAB, 2)
                        .add(Blocks.MOSSY_STONE_BRICK_SLAB)
                        .build())
                .fence(SingleBlock.AIR)
                .fenceGate(SingleBlock.AIR)
                .button(new SingleBlock(Blocks.STONE_BUTTON))
                .pressurePlate(new SingleBlock(Blocks.STONE_PRESSURE_PLATE)).build());

        collector.accept(hell("roguelike"), SecondaryTheme.builder()
                .pillar(new SingleBlock(Blocks.MAGMA_BLOCK))
                .trapdoor(new SingleBlock(Blocks.IRON_TRAPDOOR))
                .door(new SingleBlock(Blocks.IRON_DOOR))
                .material(SharedThemeConstants.HELL_MATERIAL)
                .stairs(new SingleBlock(Blocks.NETHER_BRICK_STAIRS))
                .slab(new SingleBlock(Blocks.NETHER_BRICK_SLAB))
                .fence(new SingleBlock(Blocks.NETHER_BRICK_FENCE))
                .fenceGate(SingleBlock.AIR)
                .button(new SingleBlock(Blocks.CRIMSON_BUTTON))
                .pressurePlate(new SingleBlock(Blocks.CRIMSON_PRESSURE_PLATE))
                .build());
    }

    private static ResourceLocation catacombs(String name) {
        return catacombs(DungeonCrawl.MOD_ID, name);
    }

    private static ResourceLocation catacombs(String namespace, String name) {
        return new ResourceLocation(namespace, "catacombs/" + name);
    }

    private static ResourceLocation hell(String name) {
        return hell(DungeonCrawl.MOD_ID, name);
    }

    private static ResourceLocation hell(String namespace, String name) {
        return new ResourceLocation(namespace, "hell/" + name);
    }

}
