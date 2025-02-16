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
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.generator.DungeonGenerator;
import xiroc.dungeoncrawl.dungeon.generator.RoguelikeDungeonGenerator;
import xiroc.dungeoncrawl.dungeon.generator.StaircaseBuilder;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.DungeonTypes;

import java.util.Random;

public class DungeonBuilder {
    /**
     * The amount of block layers at the bottom of the world that should not be considered usable for dungeon generation.
     * Used to avoid cutting into the bedrock layer.
     */
    public static final int WORLD_BOTTOM_CUTOFF = 5;

    public final RegistryAccess registryAccess;
    public final ChunkGenerator chunkGenerator;
    public final ChunkPos chunkPos;
    public final LevelHeightAccessor heightAccessor;
    public final StructurePiecesBuilder structurePiecesBuilder;
    public final int startHeight;
    public final BlockPos groundPos;
    public final Random random;
    public final ResourceLocation biomeKey;
    public final BoundingBox maximumBounds;
    public final Delegate<DungeonType> dungeonType;

    public DungeonBuilder(RegistryAccess registryAccess,
                          ChunkGenerator chunkGenerator,
                          LevelHeightAccessor heightAccessor,
                          StructurePiecesBuilder structurePiecesBuilder,
                          int startHeight,
                          BlockPos groundPos,
                          ChunkPos pos,
                          Random random) {
        this.registryAccess = registryAccess;
        this.chunkGenerator = chunkGenerator;
        this.heightAccessor = heightAccessor;
        this.structurePiecesBuilder = structurePiecesBuilder;
        this.startHeight = startHeight;
        this.groundPos = groundPos;
        this.chunkPos = pos;
        this.random = random;


        final Biome biome = chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(this.groundPos.getX()),
                QuartPos.fromBlock(this.groundPos.getY()),
                QuartPos.fromBlock(this.groundPos.getZ()),
                chunkGenerator.climateSampler()).value();

        this.biomeKey = registryAccess.registry(Registry.BIOME_REGISTRY).orElseThrow().getKey(biome);
        this.dungeonType = DungeonTypes.biomeMapping().roll(this.biomeKey, random);

        // Find the bounding box all pieces need to be inside to avoid exceeding the maximum size.
        final int range = StructureFeature.MAX_STRUCTURE_RANGE;
        final ChunkPos lowestChunk = new ChunkPos(chunkPos.x - range, chunkPos.z - range);
        final ChunkPos highestChunk = new ChunkPos(chunkPos.x + range, chunkPos.z + range);
        this.maximumBounds = BoundingBox.fromCorners(
                lowestChunk.getBlockAt(0, heightAccessor.getMinBuildHeight() + WORLD_BOTTOM_CUTOFF, 0),
                highestChunk.getBlockAt(15, heightAccessor.getMaxBuildHeight(), 15)
        );
    }

    public void build() {
        DungeonGenerator dungeonGenerator = new RoguelikeDungeonGenerator();
        StaircaseBuilder staircaseBuilder = new StaircaseBuilder(groundPos.getX(), groundPos.getZ());
        staircaseBuilder.top(BlockPos.ZERO, groundPos.getY() + 1);
        dungeonGenerator.generateDungeon(this, startHeight, staircaseBuilder, random);
    }
}
