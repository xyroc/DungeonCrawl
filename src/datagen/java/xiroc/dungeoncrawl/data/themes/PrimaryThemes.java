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

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.block.provider.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.decoration.VineDecoration;
import xiroc.dungeoncrawl.theme.Theme;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class PrimaryThemes implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public PrimaryThemes(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "theming/primary_themes");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput directoryCache) {
        HashMap<ResourceLocation, Theme> themes = new HashMap<>();
        collectThemes(((resourceLocation, theme) -> {
            if (themes.containsKey(resourceLocation)) {
                throw new IllegalStateException("Duplicate primary theme " + resourceLocation.toString());
            }
            themes.put(resourceLocation, theme);
        }));

        return CompletableFuture.allOf(themes.entrySet().stream().map((entry) -> {
            Path path = pathProvider.json(entry.getKey());
            return DataProvider.saveStable(directoryCache, entry.getValue().serialize(), path);
        }).toArray(CompletableFuture[]::new));

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
                        .add(Blocks.GRAVEL)
                        .add(Blocks.COBBLESTONE, 2)
                        .add(Blocks.CRACKED_STONE_BRICKS, 2)
                        .add(Blocks.MOSSY_STONE_BRICKS, 4)
                        .add(Blocks.MOSSY_COBBLESTONE, 2)
                        .build())
                .generic(WeightedRandomBlock.builder()
                        .add(Blocks.GRAVEL)
                        .add(Blocks.COBBLESTONE, 2)
                        .add(Blocks.CRACKED_STONE_BRICKS, 3)
                        .add(Blocks.MOSSY_STONE_BRICKS, 3)
                        .add(Blocks.MOSSY_COBBLESTONE, 2)
                        .build())
                .pillar(new SingleBlock(Blocks.BASALT))
                .fencing(new SingleBlock(Blocks.IRON_BARS))
                .floor(WeightedRandomBlock.builder()
                        .add(Blocks.GRAVEL)
                        .add(Blocks.BASALT)
                        .add(Blocks.POLISHED_BASALT, 1)
                        .add(Blocks.MOSSY_COBBLESTONE, 2)
                        .add(Blocks.COBBLESTONE, 2)
                        .build())
                .material(WeightedRandomBlock.builder()
                        .add(Blocks.GRAVEL)
                        .add(Blocks.BASALT, 1)
                        .add(Blocks.CRACKED_STONE_BRICKS, 3)
                        .add(Blocks.MOSSY_STONE_BRICKS, 4)
                        .add(Blocks.MOSSY_COBBLESTONE, 2)
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

        collector.accept(hell("roguelike"), Theme.builder()
                .solid(SharedThemeConstants.HELL_MATERIAL)
                .generic(SharedThemeConstants.HELL_MATERIAL)
                .pillar(new SingleBlock(Blocks.OBSIDIAN))
                .fencing(new SingleBlock(Blocks.IRON_BARS))
                .material(SharedThemeConstants.HELL_MATERIAL)
                .floor(WeightedRandomBlock.builder()
                        .add(Blocks.NETHER_BRICKS, 200)
                        .add(Blocks.NETHERRACK, 20)
                        .add(Blocks.SOUL_SAND, 15)
                        .add(Blocks.NETHER_WART_BLOCK, 10)
                        .add(Blocks.COAL_BLOCK, 5)
                        .add(Blocks.RED_NETHER_BRICKS, 50)
                        .add(Blocks.REDSTONE_BLOCK, 5).build())
                .fluid(new SingleBlock(Blocks.LAVA))
                .stairs(new SingleBlock(Blocks.NETHER_BRICK_STAIRS))
                .solidStairs(new SingleBlock(Blocks.NETHER_BRICK_STAIRS))
                .slab(new SingleBlock(Blocks.NETHER_BRICK_SLAB))
                .solidSlab(new SingleBlock(Blocks.NETHER_BRICK_SLAB))
                .wall(new SingleBlock(Blocks.NETHER_BRICK_WALL)).build());
    }

    private static ResourceLocation catacombs(String name) {
        return catacombs(DungeonCrawl.MOD_ID, name);
    }

    private static ResourceLocation catacombs(String namespace, String name) {
        return ResourceLocation.fromNamespaceAndPath(namespace, "catacombs/" + name);
    }

    private static ResourceLocation hell(String name) {
        return hell(DungeonCrawl.MOD_ID, name);
    }

    private static ResourceLocation hell(String namespace, String name) {
        return ResourceLocation.fromNamespaceAndPath(namespace, "hell/" + name);
    }

}
