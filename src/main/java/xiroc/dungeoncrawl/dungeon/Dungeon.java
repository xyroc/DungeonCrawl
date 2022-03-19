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

package xiroc.dungeoncrawl.dungeon;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;

import java.util.Optional;
import java.util.Set;

public class Dungeon extends StructureFeature<NoneFeatureConfiguration> {

    public static Set<Biome.BiomeCategory> biomeCategories = ImmutableSet.<Biome.BiomeCategory>builder()
            .add(Biome.BiomeCategory.DESERT).add(Biome.BiomeCategory.EXTREME_HILLS).add(Biome.BiomeCategory.FOREST)
            .add(Biome.BiomeCategory.ICY).add(Biome.BiomeCategory.JUNGLE).add(Biome.BiomeCategory.MESA)
            .add(Biome.BiomeCategory.PLAINS).add(Biome.BiomeCategory.SAVANNA)
            .add(Biome.BiomeCategory.SWAMP).add(Biome.BiomeCategory.TAIGA).build();

    public static ImmutableSet<String> whitelistedDimensions = ImmutableSet.of("minecraft:overworld");
    public static ImmutableSet<String> whitelistedBiomes = ImmutableSet.of();
    public static ImmutableSet<String> blacklistedBiomes = ImmutableSet.of();

    public static final String NAME = DungeonCrawl.MOD_ID + ":dungeon";

    public Dungeon() {
        super(NoneFeatureConfiguration.CODEC, Dungeon::pieceGeneratorSupplier);
    }

    private static Optional<PieceGenerator<NoneFeatureConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context) {
        int centerX = context.chunkPos().getBlockX(7);
        int centerZ = context.chunkPos().getBlockZ(7);
        int centerHeight = context.chunkGenerator().getFirstOccupiedHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());

        if (!context.validBiome().test(context.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(centerX), QuartPos.fromBlock(centerHeight), QuartPos.fromBlock(centerZ)))) {
            DungeonCrawl.LOGGER.debug("Found invalid biome {}",
                    context.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(centerX), QuartPos.fromBlock(centerHeight), QuartPos.fromBlock(centerZ)).value().getRegistryName());
            return Optional.empty();
        }

        int minGroundHeight = minHeight(context, centerX, centerZ, centerHeight);
        if (minGroundHeight < 45) {
            return Optional.empty();
        }

        int startHeight = Config.FIXED_GENERATION_HEIGHT.get()
                ? context.chunkGenerator().getSpawnHeight(context.heightAccessor()) - 20
                : (minGroundHeight > 80 ? (80 + ((minGroundHeight - 80) / 3)) : minGroundHeight) - 20;

        return Optional.of(((structurePiecesBuilder, generatorContext) -> {
            DungeonBuilder builder = new DungeonBuilder(context.registryAccess(),
                    generatorContext.chunkGenerator(),
                    startHeight,
                    new BlockPos(centerX, centerHeight, centerZ),
                    generatorContext.chunkPos(),
                    generatorContext.random());
            builder.build().forEach((structurePiecesBuilder::addPiece));
        }));
    }

    private static int minHeight(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context, int centerX, int centerZ, int centerHeight) {
        int minHeight = centerHeight;

        for (int i = 1; i < 9; i++) {
            int size = i * 5;
            int[] heights = context.getCornerHeights(centerX - size, size * 2, centerZ - size, size * 2);
            int lowestHeight = Math.min(Math.min(heights[0], heights[1]), Math.min(heights[2], heights[3]));
            minHeight = Math.min(minHeight, lowestHeight);
        }

        return minHeight;
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

}