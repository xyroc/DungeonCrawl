package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.common.collect.ImmutableSet;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonPlacementCheckEvent;
import xiroc.dungeoncrawl.config.Config;

import java.util.Set;


public class Dungeon extends Structure<NoFeatureConfig> {

    public static final Set<Biome.Category> ALLOWED_CATEGORIES = ImmutableSet.<Biome.Category>builder()
            .add(Biome.Category.BEACH).add(Biome.Category.DESERT).add(Biome.Category.EXTREME_HILLS)
            .add(Biome.Category.FOREST).add(Biome.Category.ICY).add(Biome.Category.JUNGLE).add(Biome.Category.MESA)
            .add(Biome.Category.PLAINS).add(Biome.Category.RIVER).add(Biome.Category.SAVANNA).add(Biome.Category.SWAMP)
            .add(Biome.Category.TAIGA).add(Biome.Category.RIVER).build();

    public static final String NAME = DungeonCrawl.MODID + ":dungeon";

    public static final Dungeon DUNGEON = new Dungeon();
    public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> FEATURE = DUNGEON.func_236391_a_(NoFeatureConfig.field_236559_b_);

    public static final int SIZE = 15;

    public Dungeon() {
        super(NoFeatureConfig.field_236558_a_);
    }

    @Override
    public GenerationStage.Decoration func_236396_f_() {
        return GenerationStage.Decoration.UNDERGROUND_DECORATION;
    }

    @Override
    protected boolean func_230365_b_() {
        return false;
    }

    private static ChunkPos calculateChunkPos(int chunkX, int chunkZ, SharedSeedRandom rand) {
        int x = chunkX - (Math.abs(chunkX % 12));
        int z = chunkZ - (Math.abs(chunkZ % 12));
        return new ChunkPos( x + x % 8 , z + z % 8);
        //return new ChunkPos(chunkX - (chunkX % 16) + 4 + (chunkZ % 8), chunkZ - (chunkZ % 16) + 4 + (chunkX % 8));
    }

    //DimensionStructuresSettings

    @Override
    protected boolean func_230363_a_(ChunkGenerator chunkGen, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom rand, int chunkX, int chunkZ, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_) {
        if (p_230363_8_.func_242440_e().func_242493_a(this)) {
            //ChunkPos pos = getStartPositionForPosition(rand, chunkX, chunkZ, p_230363_3_);
            //DungeonCrawl.LOGGER.debug("Calculated: {} {} Original: {} {}", pos.x, pos.z, chunkX, chunkZ);
            ChunkPos pos = calculateChunkPos(chunkX, chunkZ, rand);
            if (chunkX == pos.x && chunkZ == pos.z) {
                for (Biome biome : p_230363_2_.getBiomes(chunkX * 16 - SIZE / 2 * 9, chunkGen.getGroundHeight(),
                        chunkZ * 16 - SIZE / 2 * 9, 64)) {
                    if (!Config.IGNORE_OVERWORLD_BLACKLIST.get() && !biome.func_242440_e().func_242493_a(this)) {
                        return false;
                    }
                }
                if (DungeonCrawl.EVENT_BUS
                        .post(new DungeonPlacementCheckEvent(chunkGen, chunkGen.getBiomeProvider().getNoiseBiome(chunkX * 16, chunkGen.getGroundHeight(), chunkZ * 16), chunkX, chunkZ))) {
                    return false;
                }
                //rand.setLargeFeatureSeed(p_230363_3_, chunkX, chunkZ);
                double r = rand.nextDouble();
                //DungeonCrawl.LOGGER.info("Random: {}", r);
                return r < Config.DUNGEON_PROBABLILITY.get();
            }
        }
        return false;
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Dungeon.Start::new;
    }

    @Override
    public String getStructureName() {
        return NAME;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {

        public Start(Structure<NoFeatureConfig> p_i51341_1_, int chunkX, int chunkZ, MutableBoundingBox boundsIn,
                     int referenceIn, long seed) {
            super(p_i51341_1_, chunkX, chunkZ, boundsIn, referenceIn, seed);
        }

        @Override
        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator chunkGenerator, TemplateManager p_230364_3_, int chunkX, int chunkZ, Biome p_230364_6_, NoFeatureConfig p_230364_7_) {
            ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
            long now = System.currentTimeMillis();
            DungeonBuilder builder = new DungeonBuilder(chunkGenerator, chunkpos, rand);
            this.components.addAll(builder.build());
            this.recalculateStructureSize();
            DungeonCrawl.LOGGER.info("Created the dungeon layout for [{}, {}] ({} ms) ({} pieces).", chunkX, chunkZ,
                    (System.currentTimeMillis() - now), this.components.size());
        }

    }

}