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
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
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

    public ChunkGenerator<?> chunkGenerator;

    public Biome biome;

    public Theme theme, catacombsTheme, lowerCatacombsTheme, bottomTheme;
    public Theme.SecondaryTheme secondaryTheme, catacombsSecondaryTheme, lowerCatacombsSecondaryTheme, bottomSecondaryTheme;

    /**
     * Instantiates a Dungeon Builder for usage during world gen.
     */
    public DungeonBuilder(ChunkGenerator<?> chunkGenerator, ChunkPos pos, Random rand) {
        this.chunkGenerator = chunkGenerator;
        this.rand = rand;

        this.chunkPos = pos;
        this.startPos = new BlockPos(pos.x * 16 - Dungeon.SIZE / 2 * 9, chunkGenerator.getSeaLevel() - 15,
                pos.z * 16 - Dungeon.SIZE / 2 * 9);


        DungeonCrawl.LOGGER.debug("Creating a dungeon at (" + startPos.getX() + " | " + startPos.getY() + " | "
                + startPos.getZ() + ").");
    }

    /**
     * Instantiates a Dungeon Builder for post world gen usage like a manual dungeon spawn by command.
     */
    public DungeonBuilder(ServerWorld world, BlockPos pos, Random rand) {
        this.chunkGenerator = world.getChunkProvider().getChunkGenerator();
        this.rand = rand;

        this.chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        this.startPos = new BlockPos(pos.getX() - Dungeon.SIZE / 2 * 9, world.getSeaLevel() - 15,
                pos.getZ() - Dungeon.SIZE / 2 * 9);

        DungeonCrawl.LOGGER.debug("Creating a dungeon at (" + startPos.getX() + " | " + startPos.getY() + " | "
                + startPos.getZ() + ").");
    }

    public static boolean isWorldEligible(ChunkGenerator<?> chunkGenerator) {
        return chunkGenerator.getSeaLevel() > 32;
    }

    public List<DungeonPiece> build() {
        this.biome = chunkGenerator.getBiomeProvider().getNoiseBiome(chunkPos.x << 2, chunkGenerator.getSeaLevel() >> 4, chunkPos.z << 2);
        DungeonType type = DungeonType.randomType(this.biome.getRegistryName(), this.rand);
        generateLayout(type, DEFAULT_GENERATOR);

        List<DungeonPiece> pieces = Lists.newArrayList();
        DungeonPiece entrance = new DungeonEntrance();
        entrance.setWorldPosition(startPos.getX() + layers[0].start.x * 9, startPos.getY() + 9,
                startPos.getZ() + layers[0].start.z * 9);
        entrance.stage = 0;
        entrance.model = type.entrances.roll(rand);
        entrance.setupBoundingBox();

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
            generator.initializeLayer(type.getLayer(layer).settings, this, rand, layer, layer == layerCount - 1);
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
                    if (!layer.grid[x][z].hasFlag(PlaceHolder.Flag.PLACEHOLDER)) {
                        layer.grid[x][z].reference.stage = stage;
                        if (layer.grid[x][z].reference.getType() == 0)
                            DungeonFeatures.processCorridor(this, layer, x, z, rand, lyr, stage, startPos);
                    }
                }
            }
        }
    }

    private void determineThemes() {
        ResourceLocation registryName = biome.getRegistryName();

        if (registryName != null) {
            if (this.theme == null) this.theme = Theme.randomTheme(registryName.toString(), rand);
        } else {
            if (this.theme == null) this.theme = Theme.getDefaultTheme();
        }

        if (secondaryTheme == null) {
            if (theme.subTheme != null) {
                this.secondaryTheme = theme.subTheme.roll(rand);
            } else {
                this.secondaryTheme = registryName != null ? Theme.randomSecondaryTheme(registryName.toString(), rand) : Theme.getDefaultSecondaryTheme();
            }
        }

        if (this.catacombsTheme == null) {
            this.catacombsTheme = Theme.randomCatacombsTheme(rand);
        }

        if (catacombsSecondaryTheme == null) {
            if (catacombsTheme.subTheme != null) {
                this.catacombsSecondaryTheme = catacombsTheme.subTheme.roll(rand);
            } else {
                this.catacombsSecondaryTheme = Theme.randomCatacombsSecondaryTheme(rand);
            }
        }

        if (this.lowerCatacombsTheme == null) {
            this.lowerCatacombsTheme = Theme.randomLowerCatacombsTheme(rand);
        }

        if (lowerCatacombsSecondaryTheme == null) {
            if (lowerCatacombsTheme.subTheme != null) {
                this.lowerCatacombsSecondaryTheme = lowerCatacombsTheme.subTheme.roll(rand);
            } else {
                this.lowerCatacombsSecondaryTheme = Theme.randomLowerCatacombsSecondaryTheme(rand);
            }
        }

        if (this.bottomTheme == null) {
            this.bottomTheme = Config.NO_NETHER_STUFF.get() ? Theme.getTheme(Theme.PRIMARY_HELL_MOSSY) : Theme.randomHellTheme(rand);
        }

        if (bottomTheme.subTheme != null && this.bottomSecondaryTheme == null) {
            this.bottomSecondaryTheme = bottomTheme.subTheme.roll(rand);
        } else {
            this.bottomSecondaryTheme = Theme.randomHellSecondaryTheme(rand);
        }
    }

    private void postProcessDungeon(List<DungeonPiece> pieces, DungeonType type, Random rand) {
        boolean catacombs = layers.length > 3;

        for (int i = 0; i < layers.length; i++) {
            DungeonLayer layer = layers[i];
            ModelSelector modelSelector = type.getLayer(i).modelSelector;
            for (int x = 0; x < layer.width; x++)
                for (int z = 0; z < layer.length; z++) {
                    PlaceHolder placeHolder = layer.grid[x][z];
                    if (placeHolder != null && !placeHolder.hasFlag(PlaceHolder.Flag.PLACEHOLDER)) {
                        switch (i) {
                            case 2: {
                                placeHolder.reference.theme = catacombsTheme;
                                placeHolder.reference.secondaryTheme = catacombsSecondaryTheme;
                                break;
                            }
                            case 3: {
                                placeHolder.reference.theme = lowerCatacombsTheme;
                                placeHolder.reference.secondaryTheme = lowerCatacombsSecondaryTheme;
                                break;
                            }
                            default: {
                                if (i >= 4) {
                                    placeHolder.reference.theme = bottomTheme;
                                    placeHolder.reference.secondaryTheme = bottomSecondaryTheme;
                                } else {
                                    placeHolder.reference.theme = theme;
                                    placeHolder.reference.secondaryTheme = secondaryTheme;
                                }
                            }
                        }

                        if (!placeHolder.hasFlag(PlaceHolder.Flag.FIXED_MODEL)) {
                            placeHolder.reference.setupModel(this, modelSelector, pieces, rand);
                        }

                        if (!placeHolder.hasFlag(PlaceHolder.Flag.FIXED_POSITION)) {
                            placeHolder.reference.setWorldPosition(startPos.getX() + x * 9,
                                    startPos.getY() - i * 9, startPos.getZ() + z * 9);
                        }

                        placeHolder.reference.setupBoundingBox();

                        if (placeHolder.reference.getType() == 10) {
                            layer.rotateNode(placeHolder, rand);
                        }

                        if (placeHolder.reference.hasChildPieces()) {
                            placeHolder.reference.addChildPieces(pieces, this, modelSelector, i, rand);
                        }

                        placeHolder.reference.customSetup(rand);
                        pieces.add(placeHolder.reference);
                    }
                }
        }
    }

    public static boolean isBlockProtected(IWorld world, BlockPos pos, PlacementContext context) {
        BlockState state = world.getBlockState(pos);
        return state.getBlockHardness(world, pos) < 0 || context.protectedBlocks.contains(pos) || BlockTags.PORTALS.contains(state.getBlock());
    }

}
