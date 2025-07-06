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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.generator.DungeonGenerator;
import xiroc.dungeoncrawl.dungeon.generator.RoguelikeDungeonGenerator;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.DungeonTypes;

public class DungeonBuilder {
    /**
     * The number of block layers at the bottom of the world that should not be considered usable for dungeon generation.
     * Used to avoid cutting into the bedrock layer.
     */
    public static final int WORLD_BOTTOM_CUTOFF = 5;

    public final Structure.GenerationContext context;
    public final RandomSource random;
    public final Delegate<DungeonType> dungeonType;
    public final ResourceLocation biome;
    public final int startHeight;
    public final BlockPos groundPos;
    public final BoundingBox maximumBounds;
    public final StructurePiecesBuilder structurePiecesBuilder;

    public DungeonBuilder(Structure.GenerationContext generationContext,
                          StructurePiecesBuilder structurePiecesBuilder,
                          int startHeight,
                          BlockPos groundPos) {
        this.context = generationContext;

        this.structurePiecesBuilder = structurePiecesBuilder;
        this.startHeight = startHeight;
        this.groundPos = groundPos;
        this.random = context.random();

        final Biome biome = context.biomeSource().getNoiseBiome(QuartPos.fromBlock(this.groundPos.getX()),
                QuartPos.fromBlock(this.groundPos.getY()),
                QuartPos.fromBlock(this.groundPos.getZ()),
                context.randomState().sampler()).value();

        this.biome = context.registryAccess().registry(Registries.BIOME).orElseThrow().getKey(biome);
        this.dungeonType = DungeonTypes.biomeMapping().roll(this.biome, this.random);

        // Find the bounding box all pieces need to be inside to avoid exceeding the maximum size.
        final int range = 8;
        final ChunkPos chunkPos = context.chunkPos();
        final ChunkPos lowestChunk = new ChunkPos(chunkPos.x - range, chunkPos.z - range);
        final ChunkPos highestChunk = new ChunkPos(chunkPos.x + range, chunkPos.z + range);
        this.maximumBounds = BoundingBox.fromCorners(
                lowestChunk.getBlockAt(0, context.heightAccessor().getMinBuildHeight() + WORLD_BOTTOM_CUTOFF, 0),
                highestChunk.getBlockAt(15, context.heightAccessor().getMaxBuildHeight(), 15)
        );
    }

    public void generateDungeon() {
        DungeonGenerator dungeonGenerator = new RoguelikeDungeonGenerator();
        dungeonGenerator.generateDungeon(this);
    }
}
