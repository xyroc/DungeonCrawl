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

    public static final int SIZE = 15;

    public Dungeon() {
        super(NoneFeatureConfiguration.CODEC, Dungeon::pieceGeneratorSupplier);
    }

    private static Optional<PieceGenerator<NoneFeatureConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context) {
        int centerX = context.chunkPos().getBlockX(7);
        int centerZ = context.chunkPos().getBlockZ(7);
        int centerHeight = context.chunkGenerator().getBaseHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
        if (!context.validBiome().test(context.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(centerX), QuartPos.fromBlock(centerHeight), QuartPos.fromBlock(centerZ)))) {
            DungeonCrawl.LOGGER.debug("Found invalid biome {}",
                    context.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(centerX), QuartPos.fromBlock(centerHeight), QuartPos.fromBlock(centerZ)).getRegistryName());
            return Optional.empty();
        }
        int[] innerCornerHeights = context.getCornerHeights(centerX - 5, 10, centerZ - 5, 10);
        int[] outerCornerHeights = context.getCornerHeights(centerX - 20, 20, centerZ - 20, 20);

        int averageInnerGroundHeight = (centerHeight + innerCornerHeights[0] + innerCornerHeights[1] + innerCornerHeights[2] + innerCornerHeights[3]) / 5;
        int averageOuterGroundHeight = (outerCornerHeights[0] + outerCornerHeights[1] + outerCornerHeights[3] + outerCornerHeights[3]) / 4;

        int averageGroundHeight = Math.min(averageInnerGroundHeight, averageOuterGroundHeight);

        DungeonCrawl.LOGGER.info("Average height: {} Center height: {} ({},{}) around center {} {}", averageGroundHeight, averageInnerGroundHeight, averageOuterGroundHeight, centerHeight, centerX, centerZ);
        if (averageGroundHeight < 45) {
            return Optional.empty();
        }
        return Optional.of(((structurePiecesBuilder, generatorContext) -> {
            DungeonBuilder builder = new DungeonBuilder(context.registryAccess(),
                    generatorContext.chunkGenerator(),
                    averageGroundHeight - 16,
                    new BlockPos(centerX, centerHeight, centerZ),
                    generatorContext.chunkPos(),
                    generatorContext.random());
            builder.build().forEach((structurePiecesBuilder::addPiece));
        }));
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

    @Override
    protected boolean linearSeparation() {
        return false;
    }

    @Override
    public String getFeatureName() {
        return NAME;
    }

}