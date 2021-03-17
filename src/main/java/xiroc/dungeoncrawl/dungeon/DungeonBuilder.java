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
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonBuilderStartEvent;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.generator.DefaultDungeonGenerator;
import xiroc.dungeoncrawl.dungeon.generator.DungeonGenerator;
import xiroc.dungeoncrawl.dungeon.generator.DungeonGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.model.ModelCategory;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DungeonBuilder {

    public static final DungeonGenerator DEFAULT_GENERATOR = new DefaultDungeonGenerator(DungeonGeneratorSettings.DEFAULT);

    public Random rand;
    public Position2D start;

    public DungeonLayer[] layers;
    public DungeonLayerMap[] maps;

    public DungeonStatTracker statTracker;

    public BlockPos startPos;

    public ChunkGenerator<?> chunkGen;
    public Biome startBiome;

    public Theme entranceTheme, theme, lowerTheme, bottomTheme;
    public Theme.SubTheme entranceSubTheme, subTheme, lowerSubTheme, bottomSubTheme;

    public DungeonBuilder(ChunkGenerator<?> world, ChunkPos pos, Random rand) {
        this.chunkGen = world;

        this.rand = rand;
        this.startPos = new BlockPos(pos.x * 16 - Dungeon.SIZE / 2 * 9, world.getSeaLevel() - 15,
                pos.z * 16 - Dungeon.SIZE / 2 * 9);

        int layerCount = DEFAULT_GENERATOR.layerCount(rand, startPos.getY());

        this.layers = new DungeonLayer[layerCount];
        this.maps = new DungeonLayerMap[layerCount];

        this.statTracker = new DungeonStatTracker(layerCount);

        DEFAULT_GENERATOR.initializeDungeon(this, pos, rand);

        DungeonBuilderStartEvent startEvent = new DungeonBuilderStartEvent(world, startPos, statTracker, layers.length);

        DungeonCrawl.EVENT_BUS.post(startEvent);

        DungeonCrawl.LOGGER.debug("Creating the layout for a dungeon at (" + startPos.getX() + " | " + startPos.getY() + " | "
                + startPos.getZ() + ") with " + layerCount + " layers. Theme: {} Sub-Theme: {}", theme, subTheme);
    }

    public DungeonBuilder(ServerWorld world, ChunkPos pos) {
        this.rand = new Random();
        this.start = new Position2D(7, 7);
        this.startPos = new BlockPos(pos.x * 16 - Dungeon.SIZE / 2 * 9, 100 - 16,
                pos.z * 16 - Dungeon.SIZE / 2 * 9);

        int layerCount = DEFAULT_GENERATOR.layerCount(rand, startPos.getY());

        this.chunkGen = world.getChunkProvider().generator;

        this.layers = new DungeonLayer[layerCount];
        this.maps = new DungeonLayerMap[layerCount];

        this.statTracker = new DungeonStatTracker(layerCount);

        DEFAULT_GENERATOR.initializeDungeon(this, pos, rand);
    }

    public static boolean isWorldEligible(ServerWorld world, BlockPos pos) {
        return world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) > 32;
    }

    public static boolean isWorldEligible(ChunkGenerator<?> chunkGenerator) {
        return chunkGenerator.getSeaLevel() > 32;
    }

    public List<DungeonPiece> build() {
        List<DungeonPiece> pieces = Lists.newArrayList();

        int startCoordinate = DEFAULT_GENERATOR.settings.gridSize.apply(0) / 2;

        this.start = new Position2D(startCoordinate, startCoordinate);

        for (int i = 0; i < layers.length; i++) {
            int size = DEFAULT_GENERATOR.settings.gridSize.apply(i);
            this.maps[i] = new DungeonLayerMap(size, size);
            this.layers[i] = new DungeonLayer(size, size);
            this.layers[i].map = maps[i];
        }

        for (int i = 0; i < layers.length; i++) {
            DEFAULT_GENERATOR.initializeLayer(this, rand, i);
            DEFAULT_GENERATOR.generateLayer(this, layers[i], i, rand, (i == 0) ? this.start : layers[i - 1].end);
        }

        for (int i = 0; i < layers.length; i++) {
            processCorridors(layers[i], i);
        }

        DungeonPiece entrance = new DungeonEntrance();
        entrance.setWorldPosition(startPos.getX() + layers[0].start.x * 9, startPos.getY() + 9,
                startPos.getZ() + layers[0].start.z * 9);
        entrance.stage = 0;
        entrance.setupModel(this, null, pieces, rand);
        entrance.setupBoundingBox();

        this.startBiome = chunkGen.getBiomeProvider().getNoiseBiome(entrance.x >> 2, chunkGen.getSeaLevel() >> 2, entrance.z >> 2);

        ResourceLocation registryName = startBiome.getRegistryName();

        if (registryName != null) {
            this.entranceTheme = Theme.randomTheme(registryName.toString(), rand);
            this.theme = Theme.randomTheme(registryName.toString(), rand);
        } else {
            this.entranceTheme = Theme.getDefaultTheme();
            this.theme = Theme.getDefaultTheme();
        }

        if (theme.subTheme != null) {
            this.subTheme = theme.subTheme.roll(rand);
        } else {
            this.subTheme = registryName != null ? Theme.randomSubTheme(registryName.toString(), rand) : Theme.getDefaultSubTheme();
        }

        if (entranceTheme.subTheme != null) {
            this.entranceSubTheme = entranceTheme.subTheme.roll(rand);
        } else {
            this.entranceSubTheme = registryName != null ? Theme.randomSubTheme(registryName.toString(), rand) : Theme.getDefaultSubTheme();
        }

        this.lowerTheme = Theme.getTheme("catacombs/default");

        if (lowerTheme.subTheme != null) {
            this.lowerSubTheme = lowerTheme.subTheme.roll(rand);
        } else {
            this.lowerSubTheme = this.subTheme;
        }

        this.bottomTheme = Config.NO_NETHER_STUFF.get() ? Theme.getTheme("mossy_obsidian") : Theme.getTheme("hell");

        if (bottomTheme.subTheme != null) {
            this.bottomSubTheme = bottomTheme.subTheme.roll(rand);
        } else {
            this.bottomSubTheme = this.subTheme;
        }

        entrance.theme = theme;
        entrance.subTheme = subTheme;

        pieces.add(entrance);

        postProcessDungeon(pieces, rand);

        return pieces;
    }

    public void processCorridors(DungeonLayer layer, int lyr) {
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

    public void postProcessDungeon(List<DungeonPiece> pieces, Random rand) {
        boolean catacombs = layers.length > 3;

        for (int i = 0; i < layers.length; i++) {
            DungeonLayer layer = layers[i];
            ModelCategory layerCategory = DEFAULT_GENERATOR.getCategoryForLayer(i);
            for (int x = 0; x < layer.width; x++)
                for (int z = 0; z < layer.length; z++) {
                    if (layer.grid[x][z] != null && !layer.grid[x][z].hasFlag(PlaceHolder.Flag.PLACEHOLDER)) {

                        if (i == layers.length - 1) {
                            layer.grid[x][z].reference.theme = bottomTheme;
                            layer.grid[x][z].reference.subTheme = bottomSubTheme;
                        } else if (catacombs && layers.length - i < 4) {
                            layer.grid[x][z].reference.theme = lowerTheme;
                            layer.grid[x][z].reference.subTheme = lowerSubTheme;
                        } else {
                            layer.grid[x][z].reference.theme = theme;
                            layer.grid[x][z].reference.subTheme = subTheme;
                        }

                        if (!layer.grid[x][z].hasFlag(PlaceHolder.Flag.FIXED_MODEL)) {
                            layer.grid[x][z].reference.setupModel(this, layerCategory, pieces, rand);
                        }

                        if (!layer.grid[x][z].hasFlag(PlaceHolder.Flag.FIXED_POSITION)) {
                            layer.grid[x][z].reference.setWorldPosition(startPos.getX() + x * 9,
                                    startPos.getY() - i * 9, startPos.getZ() + z * 9);
                        }

                        layer.grid[x][z].reference.setupBoundingBox();

                        if (layer.grid[x][z].reference.hasChildPieces()) {
                            layer.grid[x][z].reference.addChildPieces(pieces, this, layerCategory, i, rand);
                        }

                        if (layer.grid[x][z].reference.getType() == 10) {
                            layer.rotateNode(layer.grid[x][z], rand);
                        }

                        layer.grid[x][z].reference.customSetup(rand);

                        pieces.add(layer.grid[x][z].reference);
                    }
                }
        }
    }

    /**
     * Checks if a piece can be placed at the given position.
     *
     * @return true if the piece can be placed, false if not
     */
    public static boolean canPlacePiece(DungeonLayer layer, int x, int z, int width, int length,
                                        boolean ignoreStartPosition) {
        if (x + width > Dungeon.SIZE || z + length > Dungeon.SIZE || x < 0 || z < 0)
            return false;

        for (int x0 = 0; x0 < width; x0++) {
            for (int z0 = 0; z0 < length; z0++) {
                if (!(ignoreStartPosition && x0 == 0 && z0 == 0)
                        && (layer.grid[x + x0][z + z0] != null || !layer.map.isPositionFree(x + x0, z + z0))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isBlockProtected(IWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlockHardness(world, pos) < 0 || BlockTags.PORTALS.contains(state.getBlock());
    }

    /**
     * Checks if a piece can be placed at the given position and layer. This does
     * also check if there are pieces on other layers (height variable) to avoid
     * collisions. For example, a piece that goes 1 to 9 blocks below the height of
     * its layer would have a height value of -1 (minus, because it goes down; 1
     * layer = 9 blocks).
     *
     * @return true if the piece can be placed, false if not
     */
    public boolean canPlacePieceWithHeight(int layer, int x, int z, int width,
                                           int length, int layerHeight, boolean ignoreStartPosition) {
        /*
         * x + width - 1 > Dungeon.SIZE -1 <=> x + width > Dungeon.SIZE
         * (same for z of course)
         */
        if (x + width > Dungeon.SIZE || z + length > Dungeon.SIZE || x < 0 || z < 0)
            return false;

        int layers = this.layers.length, lh = layer - layerHeight;
        if (layer > layers - 1 || layer < 0 || lh > layers || lh < 0)
            return false;

        boolean up = layerHeight > 0;
        int c = up ? -1 : 1, k = lh + c;

        for (int lyr = layer; up ? lyr > k : lyr < k; lyr += c) {
            if (layers - lyr == 0)
                continue;
            else if (layers - lyr < 0)
                return false;

            for (int x0 = 0; x0 < width; x0++) {
                for (int z0 = 0; z0 < length; z0++) {
                    if (!(ignoreStartPosition && lyr == layer && x0 == 0 && z0 == 0)
                            && (this.layers[lyr].grid[x + x0][z + z0] != null
                            || !this.maps[lyr].isPositionFree(x + x0, z + z0))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Marks the given area of the dungeon as occupied to prevent collision between
     * multiple dungeon features. The given coordinates and size values are assumed
     * to be correct. All parameters are the same as in #canPlacePiece.
     */
    public static void mark(DungeonLayer layer, int x, int z, int width, int length) {
        for (int x0 = 0; x0 < width; x0++) {
            for (int z0 = 0; z0 < length; z0++) {
                layer.map.map[x][z] = true;
            }
        }
    }

    /**
     * Marks the given area of the dungeon as occupied to prevent collision between
     * multiple dungeon features. The given coordinates and size values are assumed
     * to be correct. All parameters are the same as in #canPlacePieceWithHeight.
     */
    public void mark(int layer, int x, int z, int width, int length, int layerHeight) {
        int layers = this.layers.length;
        boolean up = layerHeight > 0;
        int c = up ? -1 : 1, k = layer - layerHeight + c;

        for (int lyr = layer; up ? lyr > k : lyr < k; lyr += c) {
            if (layers - lyr == 0)
                continue;
            for (int x0 = 0; x0 < width; x0++)
                for (int z0 = 0; z0 < length; z0++)
                    this.maps[lyr].markPositionAsOccupied(new Position2D(x + x0, z + z0));
        }
    }

}
