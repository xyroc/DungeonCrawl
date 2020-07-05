package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonBuilderStartEvent;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.config.JsonConfig;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.BossEntry;
import xiroc.dungeoncrawl.util.IRandom;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DungeonBuilder {

    public static final HashMap<Integer, EntranceProcessor> ENTRANCE_PROCESSORS;

    private static final DungeonModel[] ENTRANCES = new DungeonModel[]{DungeonModels.ENTRANCE};

    public static final EntranceProcessor DEFAULT_PROCESSOR = (world, pos, theme, piece) -> {
        ;
    };

    public static final IRandom<DungeonModel> ENTRANCE = (rand) -> {
        return ENTRANCES[rand.nextInt(ENTRANCES.length)];
    };

    public Random rand;
    public Position2D start;

    public DungeonLayer[] layers;
    public DungeonLayerMap[] maps;

    public DungeonStatTracker statTracker;

    public BlockPos startPos;

    public ChunkGenerator<?> chunkGen;
    public Biome startBiome;

    public int theme, subTheme;

    static {
//		ENTRANCE_OFFSET_DATA.put(21, new Tuple<Integer, Integer>(-2 , -2));

        ENTRANCE_PROCESSORS = new HashMap<Integer, EntranceProcessor>();
//		ENTRANCE_PROCESSORS.put(33, (world, pos, theme, piece) -> {
//			int x = pos.getX(), y = pos.getY(), z = pos.getZ();
//
//			buildWallPillar(world, theme, new BlockPos(x + 4, y, z + 2), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 5, y, z + 2), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 6, y, z + 2), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 7, y, z + 2), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 8, y, z + 2), piece);
//
//			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 4), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 5), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 6), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 7), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 8), piece);
//
//			buildWallPillar(world, theme, new BlockPos(x + 4, y, z + 10), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 5, y, z + 10), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 6, y, z + 10), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 7, y, z + 10), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 8, y, z + 10), piece);
//
//			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 4), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 5), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 6), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 7), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 8), piece);
//
//			buildWallPillar(world, theme, new BlockPos(x + 5, y, z + 1), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 7, y, z + 1), piece);
//
//			buildWallPillar(world, theme, new BlockPos(x + 1, y, z + 5), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 1, y, z + 7), piece);
//
//			buildWallPillar(world, theme, new BlockPos(x + 5, y, z + 11), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 7, y, z + 11), piece);
//
//			buildWallPillar(world, theme, new BlockPos(x + 11, y, z + 5), piece);
//			buildWallPillar(world, theme, new BlockPos(x + 11, y, z + 7), piece);
//		});
    }

    public DungeonBuilder(ChunkGenerator<?> world, ChunkPos pos, Random rand) {
        this.chunkGen = world;

        this.rand = rand;
        this.start = new Position2D(7, 7);
        this.startPos = new BlockPos(pos.x * 16 - Dungeon.SIZE / 2 * 9, world.getGroundHeight() - 16,
                pos.z * 16 - Dungeon.SIZE / 2 * 9);

        this.layers = new DungeonLayer[Math.min(5, startPos.getY() / 9)];
        this.maps = new DungeonLayerMap[layers.length];

        this.statTracker = new DungeonStatTracker(layers.length);

        DungeonBuilderStartEvent startEvent = new DungeonBuilderStartEvent(world, startPos, statTracker, layers.length);

        DungeonCrawl.EVENT_BUS.post(startEvent);

        DungeonCrawl.LOGGER.info("DungeonBuilder starts at (" + startPos.getX() + " / " + startPos.getY() + " / "
                + startPos.getZ() + "), " + +this.layers.length + " layers, Theme: {}, {}", theme, subTheme);
    }

    public List<DungeonPiece> build() {
        List<DungeonPiece> list = Lists.newArrayList();

        for (int i = 0; i < layers.length; i++) {
            this.maps[i] = new DungeonLayerMap(Dungeon.SIZE, Dungeon.SIZE);
            this.layers[i] = new DungeonLayer(Dungeon.SIZE, Dungeon.SIZE);
            this.layers[i].map = maps[i];
        }

        for (int i = 0; i < layers.length; i++) {
            this.layers[i].buildMap(this, list, rand, (i == 0) ? this.start : layers[i - 1].end, i,
                    i == layers.length - 1);
        }

        for (int i = 0; i < layers.length; i++) {
            this.layers[i].extend(this, maps[i], rand, i);
            processLayer(layers[i], i, startPos);
        }

        DungeonPiece entrance = new DungeonEntrance(null, DungeonPiece.DEFAULT_NBT);
        entrance.setRealPosition(startPos.getX() + layers[0].start.x * 9, startPos.getY() + 9,
                startPos.getZ() + layers[0].start.z * 9);
        entrance.stage = 0;
        entrance.modelID = entrance.determineModel(this, rand);
        entrance.setupBoundingBox();

        this.startBiome = chunkGen.getBiomeProvider()
                .getBiome(new BlockPos(entrance.x + 4, entrance.y, entrance.z + 4));
        String biome = startBiome.getRegistryName().toString();

        this.theme = Theme.getTheme(biome, rand);
        this.subTheme = Theme.getSubTheme(biome, rand);

        DungeonCrawl.LOGGER.debug("Entrance Biome: {} SubTheme: {}", biome, subTheme);

        entrance.theme = theme;
        entrance.subTheme = subTheme;

        list.add(entrance);

        postProcessDungeon(list, rand);

        return list;
    }

    public void processLayer(DungeonLayer layer, int lyr, BlockPos startPos) {
        int stage = lyr > 4 ? 4 : lyr;
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
        int lyrs = layers.length;

        for (int i = 0; i < layers.length; i++) {
            DungeonLayer layer = layers[i];
            for (int x = 0; x < layer.width; x++)
                for (int z = 0; z < layer.length; z++) {
                    if (layer.segments[x][z] != null && !layer.segments[x][z].hasFlag(PlaceHolder.Flag.PLACEHOLDER)) {
                        if (i == lyrs - 1) {
                            if (Config.NO_NETHER_STUFF.get()) {
                                layer.segments[x][z].reference.theme = 81;
                                layer.segments[x][z].reference.subTheme = 9;
                            } else {
                                layer.segments[x][z].reference.theme = 1;
                                layer.segments[x][z].reference.subTheme = 8;
                            }
                        } else if (mossArea && lyrs - i < 4) {
                            layer.segments[x][z].reference.theme = 80;
                            layer.segments[x][z].reference.subTheme = subTheme;
                        } else {
                            layer.segments[x][z].reference.theme = theme;
                            layer.segments[x][z].reference.subTheme = subTheme;
                        }

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

    public static BossEntry getRandomBoss(Random rand) {
        if (JsonConfig.DUNGEON_BOSSES.length < 1)
            return null;
        return JsonConfig.DUNGEON_BOSSES[rand.nextInt(JsonConfig.DUNGEON_BOSSES.length)];
    }

    @FunctionalInterface
    public static interface EntranceProcessor {

        void process(IWorld world, BlockPos pos, int theme, DungeonPiece piece);

    }

}
