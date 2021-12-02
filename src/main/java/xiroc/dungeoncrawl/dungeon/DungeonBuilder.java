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

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.generator.DefaultDungeonGenerator;
import xiroc.dungeoncrawl.dungeon.generator.DungeonGenerator;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DungeonBuilder {

    public static final DungeonGenerator DEFAULT_GENERATOR = new DefaultDungeonGenerator();

    public Random rand;
    public Position2D start;

    public DungeonLayer[] layers;

    public ChunkPos chunkPos;
    public BlockPos startPos;

    public ChunkGenerator chunkGenerator;
    public Biome biome;

    private final RegistryAccess registryAccess;

    public Theme theme, catacombsTheme, lowerCatacombsTheme, bottomTheme;
    public Theme.SecondaryTheme secondaryTheme, catacombsSecondaryTheme, lowerCatacombsSecondaryTheme, bottomSecondaryTheme;

    private final int groundHeight;

    /**
     * Instantiates a Dungeon Builder for usage during world gen.
     */
    public DungeonBuilder(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, int startHeight, BlockPos groundPos, ChunkPos pos, Random rand) {
        this.registryAccess = registryAccess;
        this.rand = rand;
        this.chunkGenerator = chunkGenerator;
        this.groundHeight = groundPos.getY();

        this.chunkPos = pos;
        this.startPos = new BlockPos(groundPos.getX() - Dungeon.SIZE / 2 * 9, startHeight,
                groundPos.getZ() - Dungeon.SIZE / 2 * 9);
        DungeonCrawl.LOGGER.debug("Creating a dungeon at (" + startPos.getX() + " | " + startPos.getY() + " | "
                + startPos.getZ() + ").");
    }

    /**
     * Instantiates a Dungeon Builder for post world gen usage like a manual dungeon spawn by command.
     */
    public DungeonBuilder(ServerLevel world, BlockPos pos, Random rand) {
        this.registryAccess = world.registryAccess();
        this.chunkGenerator = world.getChunkSource().getGenerator();
        this.rand = rand;
        this.groundHeight = world.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());

        this.chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        this.startPos = new BlockPos(pos.getX() - Dungeon.SIZE / 2 * 9, chunkGenerator.getSpawnHeight(world) - 15,
                pos.getZ() - Dungeon.SIZE / 2 * 9);

        DungeonCrawl.LOGGER.debug("Creating a dungeon at (" + startPos.getX() + " | " + startPos.getY() + " | "
                + startPos.getZ() + ").");
    }

    public List<DungeonPiece> build() {
        if (startPos.getY() < 16) {
            return Lists.newArrayList();
        }
        this.biome = chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(startPos.getX()), QuartPos.fromBlock(groundHeight), QuartPos.fromBlock(startPos.getZ()), chunkGenerator.climateSampler());
        DungeonType type = DungeonType.randomType(this.biome.getRegistryName(), this.rand);
        generateLayout(type, DEFAULT_GENERATOR);

        List<DungeonPiece> pieces = Lists.newArrayList();
        DungeonPiece entrance = new DungeonEntrance();
        entrance.setWorldPosition(startPos.getX() + layers[0].start.x * 9, startPos.getY() + 9,
                startPos.getZ() + layers[0].start.z * 9);
        entrance.stage = 0;

        entrance.model = type.entrances().roll(rand);
        entrance.createBoundingBox();

        determineThemes();

        entrance.theme = this.theme;
        entrance.secondaryTheme = this.secondaryTheme;

        pieces.add(entrance);

        postProcessDungeon(pieces, type, rand);
        return pieces;
    }

    private void generateLayout(DungeonType type, DungeonGenerator generator) {
        generator.initializeDungeon(type, this, this.chunkPos, this.rand);

        int gridSize = 17;
        int startCoordinate = gridSize / 2;

        this.start = new Position2D(startCoordinate, startCoordinate);

        int layerCount = generator.layerCount(rand, startPos.getY());

        this.layers = new DungeonLayer[layerCount];

        for (int layer = 0; layer < layers.length; layer++) {
            this.layers[layer] = new DungeonLayer(gridSize);
        }

        for (int layer = 0; layer < layers.length; layer++) {

            if (layer > 0 && layers[layer - 1].end == null) {
                // This is an abnormal state and means that the previous layer is unfinished.
                // Layer generation cannot continue at this point.
                break;
            }
            generator.initializeLayer(type.getLayer(layer).settings(), this, rand, layer, layer == layerCount - 1);
            generator.generateLayer(this, layers[layer], layer, rand, (layer == 0) ? this.start : layers[layer - 1].end);
        }

        for (int layer = 0; layer < layers.length; layer++) {
            processCorridors(layers[layer], layer);
        }
    }

    private void processCorridors(DungeonLayer layer, int lyr) {
        int stage = Math.min(lyr, 4);
        for (int x = 0; x < layer.width; x++) {
            for (int z = 0; z < layer.length; z++) {
                if (layer.grid[x][z] != null) {
                    if (!layer.grid[x][z].hasFlag(Tile.Flag.PLACEHOLDER)) {
                        layer.grid[x][z].piece.stage = stage;
                        if (layer.grid[x][z].piece.getDungeonPieceType() == DungeonPiece.CORRIDOR)
                            DungeonFeatures.processCorridor(this, layer, x, z, rand, lyr, stage, startPos);
                    }
                }
            }
        }
    }

    private void determineThemes() {
        ResourceLocation registryName = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);

        if (registryName != null) {
            if (this.theme == null) this.theme = Theme.randomTheme(registryName.toString(), rand);
        } else {
            if (this.theme == null) this.theme = Theme.getBuiltinDefaultTheme();
        }

        if (secondaryTheme == null) {
            if (theme.secondaryTheme != null) {
                this.secondaryTheme = theme.secondaryTheme.roll(rand);
            } else {
                this.secondaryTheme = registryName != null ? Theme.randomSecondaryTheme(registryName.toString(), rand) : Theme.getBuiltinDefaultSecondaryTheme();
            }
        }

        if (this.catacombsTheme == null) {
            this.catacombsTheme = Theme.randomCatacombsTheme(rand);
        }

        if (catacombsSecondaryTheme == null) {
            if (catacombsTheme.secondaryTheme != null) {
                this.catacombsSecondaryTheme = catacombsTheme.secondaryTheme.roll(rand);
            } else {
                this.catacombsSecondaryTheme = Theme.randomCatacombsSecondaryTheme(rand);
            }
        }

        if (this.lowerCatacombsTheme == null) {
            this.lowerCatacombsTheme = Theme.randomLowerCatacombsTheme(rand);
        }

        if (lowerCatacombsSecondaryTheme == null) {
            if (lowerCatacombsTheme.secondaryTheme != null) {
                this.lowerCatacombsSecondaryTheme = lowerCatacombsTheme.secondaryTheme.roll(rand);
            } else {
                this.lowerCatacombsSecondaryTheme = Theme.randomLowerCatacombsSecondaryTheme(rand);
            }
        }

        if (this.bottomTheme == null) {
            this.bottomTheme = Config.NO_NETHER_STUFF.get() ? Theme.getTheme(Theme.PRIMARY_HELL_MOSSY) : Theme.randomHellTheme(rand);
        }

        if (bottomTheme.secondaryTheme != null && this.bottomSecondaryTheme == null) {
            this.bottomSecondaryTheme = bottomTheme.secondaryTheme.roll(rand);
        } else {
            this.bottomSecondaryTheme = Theme.randomHellSecondaryTheme(rand);
        }
    }

    private void postProcessDungeon(List<DungeonPiece> pieces, DungeonType type, Random rand) {
        for (int i = 0; i < layers.length; i++) {
            DungeonLayer layer = layers[i];
            ModelSelector modelSelector = type.getLayer(i).modelSelector();
            for (int x = 0; x < layer.width; x++)
                for (int z = 0; z < layer.length; z++) {
                    Tile tile = layer.grid[x][z];
                    if (tile != null && !tile.hasFlag(Tile.Flag.PLACEHOLDER)) {
                        switch (i) {
                            case 2 -> {
                                tile.piece.theme = catacombsTheme;
                                tile.piece.secondaryTheme = catacombsSecondaryTheme;
                            }
                            case 3 -> {
                                tile.piece.theme = lowerCatacombsTheme;
                                tile.piece.secondaryTheme = lowerCatacombsSecondaryTheme;
                            }
                            default -> {
                                if (i >= 4) {
                                    tile.piece.theme = bottomTheme;
                                    tile.piece.secondaryTheme = bottomSecondaryTheme;
                                } else {
                                    tile.piece.theme = theme;
                                    tile.piece.secondaryTheme = secondaryTheme;
                                }
                            }
                        }

                        if (!tile.hasFlag(Tile.Flag.FIXED_MODEL)) {
                            tile.piece.setupModel(this, modelSelector, pieces, rand);
                        }

                        if (!tile.hasFlag(Tile.Flag.FIXED_POSITION)) {
                            tile.piece.setWorldPosition(startPos.getX() + x * 9,
                                    startPos.getY() - i * 9, startPos.getZ() + z * 9);
                        }

                        tile.piece.createBoundingBox();

                        if (tile.piece.getDungeonPieceType() == DungeonPiece.NODE_ROOM) {
                            layer.rotateNode(tile, rand);
                        }

                        if (tile.piece.hasChildPieces()) {
                            tile.piece.addChildPieces(pieces, this, type, modelSelector, i, rand);
                        }

                        tile.piece.customSetup(rand);
                        pieces.add(tile.piece);
                    }
                }
        }
    }

    public static boolean isBlockProtected(LevelAccessor world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getDestroySpeed(world, pos) < 0 || BlockTags.PORTALS.contains(state.getBlock());
    }

}
