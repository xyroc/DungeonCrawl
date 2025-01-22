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
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.dungeon.generator.DungeonGenerator;
import xiroc.dungeoncrawl.dungeon.generator.RoguelikeDungeonGenerator;
import xiroc.dungeoncrawl.dungeon.generator.StaircaseBuilder;

import java.util.List;
import java.util.Random;

public class DungeonBuilder {
    public final RegistryAccess registryAccess;
    public final ChunkGenerator chunkGenerator;
    public final ChunkPos chunkPos;
    public final LevelHeightAccessor heightAccessor;
    public final int startHeight;
    public final BlockPos groundPos;
    public final Random random;
    public final Biome biome;
    public final BoundingBox maximumBounds;

    public DungeonBuilder(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, LevelHeightAccessor heightAccessor, int startHeight, BlockPos groundPos, ChunkPos pos, Random random) {
        this.registryAccess = registryAccess;
        this.chunkGenerator = chunkGenerator;
        this.heightAccessor = heightAccessor;
        this.startHeight = startHeight;
        this.groundPos = groundPos;
        this.chunkPos = pos;
        this.random = random;
        this.biome = chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(this.groundPos.getX()),
                QuartPos.fromBlock(this.groundPos.getY()),
                QuartPos.fromBlock(this.groundPos.getZ()),
                chunkGenerator.climateSampler()).value();

        // Find the bounding box all pieces need to be inside to avoid exceeding the maximum size.
        final int range = StructureFeature.MAX_STRUCTURE_RANGE;
        final ChunkPos lowestChunk = new ChunkPos(chunkPos.x - range, chunkPos.z - range);
        final ChunkPos highestChunk = new ChunkPos(chunkPos.x + range, chunkPos.z + range);
        this.maximumBounds = BoundingBox.fromCorners(
                // The lowest two layers of blocks are usually bedrock and should not be considered usable.
                lowestChunk.getBlockAt(0, heightAccessor.getMinBuildHeight() + 2, 0),
                highestChunk.getBlockAt(15, heightAccessor.getMaxBuildHeight(), 15)
        );
    }

    public List<? extends StructurePiece> build() {
        DungeonGenerator dungeonGenerator = new RoguelikeDungeonGenerator();
        StaircaseBuilder staircaseBuilder = new StaircaseBuilder(groundPos.getX(), groundPos.getZ());
        staircaseBuilder.top(BlockPos.ZERO, groundPos.getY() + 1);
        return dungeonGenerator.generateDungeon(this, startHeight, staircaseBuilder, random);
    }
}
