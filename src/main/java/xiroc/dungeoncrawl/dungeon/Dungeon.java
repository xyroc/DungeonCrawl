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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
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

public class Dungeon extends StructureFeature<NoneFeatureConfiguration> {

    private static final int BIOME_CHECK_RADIUS = 1;

    public Dungeon() {
        super(NoneFeatureConfiguration.CODEC, Dungeon::pieceGeneratorSupplier);
    }

    private static Optional<PieceGenerator<NoneFeatureConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context) {
        int centerX = context.chunkPos().getBlockX(7);
        int centerZ = context.chunkPos().getBlockZ(7);
        int centerHeight = context.chunkGenerator().getFirstOccupiedHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());

        if (isInvalidSpot(context, BIOME_CHECK_RADIUS)) {
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
                    context.heightAccessor(),
                    startHeight,
                    new BlockPos(centerX, centerHeight, centerZ),
                    generatorContext.chunkPos(),
                    generatorContext.random());
            builder.build().forEach((structurePiecesBuilder::addPiece));
        }));
    }

    private static boolean isInvalidSpot(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                ChunkPos pos = new ChunkPos(context.chunkPos().x + x, context.chunkPos().z + z);
                int centerX = QuartPos.fromBlock(pos.getBlockX(7));
                int centerZ = QuartPos.fromBlock(pos.getBlockZ(7));
                Holder<Biome> centerBiome = context.chunkGenerator().getNoiseBiome(centerX, context.chunkGenerator()
                        .getFirstOccupiedHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor()), centerZ);
                if (!context.validBiome().test(centerBiome)) {
                    DungeonCrawl.LOGGER.debug("Invalid biome {} at [{},{}]", centerBiome.value().getRegistryName(), pos.x, pos.z);
                    return true;
                }
            }
        }
        return false;
    }

    private static int minHeight(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context, int centerX, int centerZ, int centerHeight) {
        int minHeight = centerHeight;

        for (int i = 1; i < 9; i++) {
            int size = i * 5;
            minHeight = Math.min(minHeight, lowestCornerHeight(context, centerX - size, size << 1, centerZ - size, size << 1));
        }

        return minHeight;
    }

    private static int lowestCornerHeight(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context, int x, int sizeX, int z, int sizeZ) {
        int height = context.chunkGenerator().getFirstFreeHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor());
        height = Math.min(height, context.chunkGenerator().getFirstFreeHeight(x + sizeX, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor()));
        height = Math.min(height, context.chunkGenerator().getFirstFreeHeight(x, z + sizeZ, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor()));
        height = Math.min(height, context.chunkGenerator().getFirstFreeHeight(x + sizeX, z + sizeZ, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor()));
        return height;
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

}