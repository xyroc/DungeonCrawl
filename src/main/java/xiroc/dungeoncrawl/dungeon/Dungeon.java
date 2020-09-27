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

import com.google.common.collect.ImmutableSet;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;

import java.util.Set;


public class Dungeon extends Structure<NoFeatureConfig> {

    public static final Set<Biome.Category> ALLOWED_CATEGORIES = ImmutableSet.<Biome.Category>builder()
            .add(Biome.Category.BEACH).add(Biome.Category.DESERT).add(Biome.Category.EXTREME_HILLS)
            .add(Biome.Category.FOREST).add(Biome.Category.ICY).add(Biome.Category.JUNGLE).add(Biome.Category.MESA)
            .add(Biome.Category.PLAINS).add(Biome.Category.RIVER).add(Biome.Category.SAVANNA).add(Biome.Category.SWAMP)
            .add(Biome.Category.TAIGA).add(Biome.Category.RIVER).build();

    public static final String NAME = DungeonCrawl.MODID + ":dungeon";

    public static final Structure<NoFeatureConfig> DUNGEON = new Dungeon();
    public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> FEATURE =
            WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, "dungeoncrawl:dungeon", DUNGEON.func_236391_a_(NoFeatureConfig.field_236559_b_));

    public static final StructureSeparationSettings SEPARATION_SETTINGS = new StructureSeparationSettings(15, 5, 10387313);

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

//    @Override
//    protected boolean func_230363_a_(ChunkGenerator chunkGen, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom rand, int chunkX, int chunkZ, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_) {
//        DungeonCrawl.LOGGER.info("func_230363_a {} {} {}", chunkGen.getBiomeProvider().hasStructure(this), chunkX, chunkZ);
//        ChunkPos pos = func_236392_a_(SEPARATION_SETTINGS, p_230363_3_, rand, chunkX, chunkZ);
//        if (pos.x == chunkX && pos.z == chunkZ) {
//            int x = chunkX * 16, z = chunkZ * 16;
//            for (Biome biome : p_230363_2_.getBiomes(x, chunkGen.getHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG), z, 64)) {
//                if (!Config.IGNORE_OVERWORLD_BLACKLIST.get() && !biome.func_242440_e().func_242493_a(this)) {
//                    return false;
//                }
//            }
//            if (DungeonCrawl.EVENT_BUS
//                    .post(new DungeonPlacementCheckEvent(chunkGen, chunkGen.getBiomeProvider().getNoiseBiome(chunkX * 16, chunkGen.getGroundHeight(), chunkZ * 16), chunkX, chunkZ))) {
//                return false;
//            }
//            return rand.nextDouble() < Config.DUNGEON_PROBABLILITY.get();
//        }
//        return false;
//    }


    @Override
    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_) {
        return super.func_230363_a_(p_230363_1_, p_230363_2_, p_230363_3_, p_230363_5_, p_230363_6_, p_230363_7_, p_230363_8_, p_230363_9_, p_230363_10_) && p_230363_5_.nextDouble() < Config.DUNGEON_PROBABLILITY.get();
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
        public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager p_230364_3_, int chunkX, int chunkZ, Biome p_230364_6_, NoFeatureConfig p_230364_7_) {
            ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
            long now = System.currentTimeMillis();
            DungeonBuilder builder = new DungeonBuilder(dynamicRegistries, chunkGenerator, chunkpos, rand);
            this.components.addAll(builder.build());
            this.recalculateStructureSize();
            DungeonCrawl.LOGGER.info("Created the dungeon layout for [{}, {}] ({} ms) ({} pieces).", chunkX, chunkZ,
                    (System.currentTimeMillis() - now), this.components.size());
        }

    }

}