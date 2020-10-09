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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonBuilderStartEvent;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.generator.DefaultGenerator;
import xiroc.dungeoncrawl.dungeon.generator.DungeonGenerator;
import xiroc.dungeoncrawl.dungeon.generator.DungeonGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DungeonBuilder {

    public static final DungeonGenerator DEFAULT_GENERATOR = new DefaultGenerator(DungeonGeneratorSettings.DEFAULT);

    public Random rand;
    public Position2D start;

    public DungeonLayer[] layers;
    public DungeonLayerMap[] maps;

    public DungeonStatTracker statTracker;

    public BlockPos startPos;

    public ChunkGenerator chunkGen;
    public Biome startBiome;

    public int theme, subTheme, lowerTheme, lowerSubTheme, bottomTheme, bottomSubTheme;

    private final DynamicRegistries dynamicRegistries;

    public DungeonBuilder(DynamicRegistries dynamicRegistries, ChunkGenerator world, ChunkPos pos, Random rand) {
        this.dynamicRegistries = dynamicRegistries;
        this.rand = rand;
        this.start = new Position2D(7, 7);
        this.startPos = new BlockPos(pos.x * 16 - Dungeon.SIZE / 2 * 9, world.getGroundHeight() - 16,
                pos.z * 16 - Dungeon.SIZE / 2 * 9);

        int layerCount = DEFAULT_GENERATOR.calculateLayerCount(rand, startPos.getY());

        this.layers = new DungeonLayer[layerCount];
        this.maps = new DungeonLayerMap[layerCount];

        this.statTracker = new DungeonStatTracker(layerCount);

        DEFAULT_GENERATOR.initialize(this, pos, rand);

        DungeonBuilderStartEvent startEvent = new DungeonBuilderStartEvent(world, startPos, statTracker, layers.length);

        DungeonCrawl.EVENT_BUS.post(startEvent);

        DungeonCrawl.LOGGER.info("Building a Dungeon at (" + startPos.getX() + " / " + startPos.getY() + " / "
                + startPos.getZ() + "), " + layerCount + " layers, Theme: {}, {}", theme, subTheme);
    }

    public DungeonBuilder(ServerWorld world, ChunkPos pos) {
        this.dynamicRegistries = world.func_241828_r();
        this.rand = new Random();
        this.start = new Position2D(7, 7);
        this.startPos = new BlockPos(pos.x * 16 - Dungeon.SIZE / 2 * 9, 100 - 16,
                pos.z * 16 - Dungeon.SIZE / 2 * 9);

        int layerCount = DEFAULT_GENERATOR.calculateLayerCount(rand, startPos.getY());

        this.chunkGen = world.getChunkProvider().generator;

        this.layers = new DungeonLayer[layerCount];
        this.maps = new DungeonLayerMap[layerCount];

        this.statTracker = new DungeonStatTracker(layerCount);

        DEFAULT_GENERATOR.initialize(this, pos, rand);
    }

    public static boolean isWorldEligible(ServerWorld world, BlockPos pos) {
        return world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) > 32;
    }

    public static boolean isWorldEligible(ChunkGenerator chunkGenerator) {
        return chunkGenerator.func_230356_f_() > 32;
    }

    public List<DungeonPiece> build() {
        List<DungeonPiece> list = Lists.newArrayList();

        for (int i = 0; i < layers.length; i++) {
            this.maps[i] = new DungeonLayerMap(Dungeon.SIZE, Dungeon.SIZE);
            this.layers[i] = new DungeonLayer(Dungeon.SIZE, Dungeon.SIZE);
            this.layers[i].map = maps[i];
        }

        for (int i = 0; i < layers.length; i++) {
            DEFAULT_GENERATOR.generateLayer(this, layers[i], i, rand, (i == 0) ? this.start : layers[i - 1].end);
        }

        for (int i = 0; i < layers.length; i++) {
            processCorridors(layers[i], i);
        }

        DungeonPiece entrance = new DungeonEntrance();
        entrance.setRealPosition(startPos.getX() + layers[0].start.x * 9, startPos.getY() + 9,
                startPos.getZ() + layers[0].start.z * 9);
        entrance.stage = 0;
        entrance.modelID = entrance.determineModel(this, rand);
        entrance.setupBoundingBox();

        this.startBiome = chunkGen.getBiomeProvider().getNoiseBiome(entrance.x >> 2, chunkGen.func_230356_f_() >> 2, entrance.z >> 2);

        ResourceLocation biomeName = dynamicRegistries.getRegistry(Registry.BIOME_KEY).getKey(startBiome);
        String biome;

        if (biomeName != null) {
            biome = biomeName.toString();
        } else {
            DungeonCrawl.LOGGER.warn("Couldn't find the registry name for biome {} - Proceeding with default \"minecraft:plains\".", startBiome.toString());
            biome = "minecraft:plains";
        }

        this.theme = Theme.getTheme(biome, rand);

        if (Theme.get(theme).subTheme != null) {
            this.subTheme = Theme.randomizeSubTheme(Theme.get(theme).subTheme, rand);
        } else {
            this.subTheme = Theme.getSubTheme(biome, rand);
        }

        this.lowerTheme = Theme.randomizeTheme(80, rand);

        if (Theme.get(lowerTheme).subTheme != null) {
            this.lowerSubTheme = Theme.randomizeSubTheme(Theme.get(lowerTheme).subTheme, rand);
        } else {
            this.lowerSubTheme = this.subTheme;
        }

        this.bottomTheme = Config.NO_NETHER_STUFF.get() ? Theme.randomizeTheme(81, rand) : Theme.randomizeTheme(1, rand);

        if (Theme.get(bottomTheme).subTheme != null) {
            this.bottomSubTheme = Theme.randomizeSubTheme(Theme.get(bottomTheme).subTheme, rand);
        } else {
            this.bottomSubTheme = this.subTheme;
        }

        entrance.theme = theme;
        entrance.subTheme = subTheme;

        list.add(entrance);

        postProcessDungeon(list, rand);

        return list;
    }

    public List<DungeonPiece> build(int theme, int subTheme) {
        List<DungeonPiece> list = Lists.newArrayList();

        for (int i = 0; i < layers.length; i++) {
            this.maps[i] = new DungeonLayerMap(Dungeon.SIZE, Dungeon.SIZE);
            this.layers[i] = new DungeonLayer(Dungeon.SIZE, Dungeon.SIZE);
            this.layers[i].map = maps[i];
        }

        for (int i = 0; i < layers.length; i++) {
            DEFAULT_GENERATOR.generateLayer(this, layers[i], i, rand, (i == 0) ? this.start : layers[i - 1].end);
        }

        for (int i = 0; i < layers.length; i++) {
            processCorridors(layers[i], i);
        }

        DungeonPiece entrance = new DungeonEntrance();
        entrance.setRealPosition(startPos.getX() + layers[0].start.x * 9, startPos.getY() + 9,
                startPos.getZ() + layers[0].start.z * 9);
        entrance.stage = 0;
        entrance.modelID = entrance.determineModel(this, rand);
        entrance.setupBoundingBox();

        this.startBiome = chunkGen.getBiomeProvider().getNoiseBiome(entrance.x >> 2, chunkGen.func_230356_f_() >> 2, entrance.z >> 2);

        //String biome = startBiome.getRegistryName().toString();

        this.theme = theme;

        if (Theme.get(theme).subTheme != null) {
            this.subTheme = Theme.randomizeSubTheme(Theme.get(theme).subTheme, rand);
        } else {
            this.subTheme = subTheme;
        }

        this.lowerTheme = Theme.randomizeTheme(80, rand);

        if (Theme.get(lowerTheme).subTheme != null) {
            this.lowerSubTheme = Theme.randomizeSubTheme(Theme.get(lowerTheme).subTheme, rand);
        } else {
            this.lowerSubTheme = this.subTheme;
        }

        this.bottomTheme = Config.NO_NETHER_STUFF.get() ? Theme.randomizeTheme(81, rand) : Theme.randomizeTheme(1, rand);

        if (Theme.get(bottomTheme).subTheme != null) {
            this.bottomSubTheme = Theme.randomizeSubTheme(Theme.get(bottomTheme).subTheme, rand);
        } else {
            this.bottomSubTheme = this.subTheme;
        }

        entrance.theme = theme;
        entrance.subTheme = subTheme;

        list.add(entrance);

        postProcessDungeon(list, rand);

        return list;
    }

    public void processCorridors(DungeonLayer layer, int lyr) {
        int stage = Math.min(lyr, 4);
        for (int x = 0; x < layer.width; x++) {
            for (int z = 0; z < layer.length; z++) {
                if (layer.segments[x][z] != null) {
                    if (!layer.segments[x][z].hasFlag(PlaceHolder.Flag.PLACEHOLDER)) {
                        layer.segments[x][z].reference.stage = stage;
                        if (layer.segments[x][z].reference.getType() == 0)
                            DungeonFeatures.processCorridor(this, layer, x, z, rand, lyr, stage, startPos);
                    }
                }
            }
        }
    }

    public void postProcessDungeon(List<DungeonPiece> list, Random rand) {
        boolean mossArea = layers.length > 3;

        for (int i = 0; i < layers.length; i++) {
            DungeonLayer layer = layers[i];
            for (int x = 0; x < layer.width; x++)
                for (int z = 0; z < layer.length; z++) {
                    if (layer.segments[x][z] != null && !layer.segments[x][z].hasFlag(PlaceHolder.Flag.PLACEHOLDER)) {
                        if (i == layers.length - 1) {
                            layer.segments[x][z].reference.theme = bottomTheme;
                            layer.segments[x][z].reference.subTheme = bottomSubTheme;
                        } else if (mossArea && layers.length - i < 4) {
                            layer.segments[x][z].reference.theme = lowerTheme;
                            layer.segments[x][z].reference.subTheme = lowerSubTheme;
                        } else {
                            layer.segments[x][z].reference.theme = theme;
                            layer.segments[x][z].reference.subTheme = subTheme;
                        }
//                        if (layer.segments[x][z].reference.getType() == 0) {
//                            DungeonFeatures.processCorridor(this, layer, x, z, rand, i, i, startPos);
//                        }

                        if (!layer.segments[x][z].hasFlag(PlaceHolder.Flag.FIXED_MODEL)) {
                            layer.segments[x][z].reference.modelID = layer.segments[x][z].reference.determineModel(this,
                                    rand);
                        }

                        if (!layer.segments[x][z].hasFlag(PlaceHolder.Flag.FIXED_POSITION)) {
                            layer.segments[x][z].reference.setRealPosition(startPos.getX() + x * 9,
                                    startPos.getY() - i * 9, startPos.getZ() + z * 9);
                        }

                        layer.segments[x][z].reference.setupBoundingBox();

                        if (layer.segments[x][z].reference.hasChildPieces()) {
                            layer.segments[x][z].reference.addChildPieces(list, this, i, rand);
                        }

                        if (layer.segments[x][z].reference.getType() == 10) {
                            layer.rotateNode(layer.segments[x][z]);
                        }

                        layer.segments[x][z].reference.customSetup(rand);

                        list.add(layer.segments[x][z].reference);
                    }
                }
        }

    }

    /**
     * Builds a 1x1 pillar to the ground
     */
    public static void buildPillar(IWorld world, Theme theme, int x, int y, int z, MutableBoundingBox bounds) {
        int height = DungeonPiece.getGroundHeightFrom(world, x, z, y - 1);
        for (int y0 = y - 1; y0 > height; y0--) {
            DungeonPiece.setBlockState(world, theme.solid.get(), x, y0, z, bounds, true);
        }
    }

}
