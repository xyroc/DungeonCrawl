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

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.init.ModStructureTypes;

import java.util.Optional;

public class Dungeon extends Structure {

    public static final GenerationStep.Decoration GENERATION_STEP = GenerationStep.Decoration.UNDERGROUND_STRUCTURES;

    public static final Codec<Structure> CODEC = simpleCodec(Dungeon::new);

    private static final int BIOME_CHECK_RADIUS = 1;

    public Dungeon(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int centerX = context.chunkPos().getBlockX(7);
        int centerZ = context.chunkPos().getBlockZ(7);
        int centerHeight = context.chunkGenerator().getFirstOccupiedHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

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

        BlockPos position = new BlockPos(centerX, centerHeight, centerZ);

        return Optional.of(new Structure.GenerationStub(position, (structurePiecesBuilder) -> this.generatePieces(structurePiecesBuilder, context, position, startHeight)));
    }

    private void generatePieces(StructurePiecesBuilder structurePiecesBuilder, GenerationContext context, BlockPos position, int startHeight) {
        DungeonBuilder builder = new DungeonBuilder(context, startHeight, position);
        builder.build().forEach(structurePiecesBuilder::addPiece);
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.DUNGEON.get();
    }

    private static boolean isInvalidSpot(GenerationContext context, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                ChunkPos pos = new ChunkPos(context.chunkPos().x + x, context.chunkPos().z + z);
                int centerX = QuartPos.fromBlock(pos.getBlockX(7));
                int centerZ = QuartPos.fromBlock(pos.getBlockZ(7));
                Holder<Biome> centerBiome = context.chunkGenerator().getBiomeSource().getNoiseBiome(centerX, context.chunkGenerator()
                        .getFirstOccupiedHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()), centerZ, context.randomState().sampler());
                if (!context.validBiome().test(centerBiome)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int minHeight(GenerationContext context, int centerX, int centerZ, int centerHeight) {
        int minHeight = centerHeight;

        for (int i = 1; i < 9; i++) {
            int size = i * 5;
            minHeight = Math.min(minHeight, lowestCornerHeight(context, centerX - size, size << 1, centerZ - size, size << 1));
        }

        return minHeight;
    }

    private static int lowestCornerHeight(GenerationContext context, int x, int sizeX, int z, int sizeZ) {
        int height = context.chunkGenerator().getFirstFreeHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        height = Math.min(height, context.chunkGenerator().getFirstFreeHeight(x + sizeX, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState()));
        height = Math.min(height, context.chunkGenerator().getFirstFreeHeight(x, z + sizeZ, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState()));
        height = Math.min(height, context.chunkGenerator().getFirstFreeHeight(x + sizeX, z + sizeZ, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState()));
        return height;
    }

    @Override
    public GenerationStep.Decoration step() {
        return GENERATION_STEP;
    }

}