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
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonPlacementCheckEvent;
import xiroc.dungeoncrawl.config.Config;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class Dungeon extends Structure<NoFeatureConfig> {

    public static final Set<Biome.Category> ALLOWED_CATEGORIES = ImmutableSet.<Biome.Category>builder()
            .add(Biome.Category.BEACH).add(Biome.Category.DESERT).add(Biome.Category.EXTREME_HILLS)
            .add(Biome.Category.FOREST).add(Biome.Category.ICY).add(Biome.Category.JUNGLE).add(Biome.Category.MESA)
            .add(Biome.Category.PLAINS).add(Biome.Category.RIVER).add(Biome.Category.SAVANNA).add(Biome.Category.SWAMP)
            .add(Biome.Category.TAIGA).add(Biome.Category.RIVER).build();

    public static final Set<Biome.Category> OVERWORLD_CATEGORIES = ImmutableSet.<Biome.Category>builder()
            .addAll(ALLOWED_CATEGORIES).add(Biome.Category.MUSHROOM).add(Biome.Category.OCEAN).build();

    public static final String NAME = DungeonCrawl.MOD_ID + ":dungeon";
    public static final Dungeon DUNGEON = new Dungeon(NoFeatureConfig::deserialize);

    public static final int SIZE = 15;
    public static int spacing, separation;

    public Dungeon(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51427_1_) {
        super(p_i51427_1_);
    }

    public ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z) {
        int i = spacing;
        int j = separation;
        int i1 = x < 0 ? x - i + 1 : x;
        int j1 = z < 0 ? z - i + 1 : z;
        int k1 = i1 / i;
        int l1 = j1 / i;
        ((SharedSeedRandom) random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), k1, l1, 10387319);
        k1 = k1 * i;
        l1 = l1 * i;
        k1 = k1 + (random.nextInt(i - j) + random.nextInt(i - j)) / 2;
        l1 = l1 + (random.nextInt(i - j) + random.nextInt(i - j)) / 2;
        return new ChunkPos(k1, l1);
    }

    @Override
    public boolean canBeGenerated(BiomeManager p_225558_1_, ChunkGenerator<?> chunkGen, Random rand, int chunkX, int chunkZ, Biome p_225558_6_) {
        ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkX, chunkZ);
        if (chunkX == chunkpos.x && chunkZ == chunkpos.z && p_225558_6_.hasStructure(Dungeon.DUNGEON)) {
            for (Biome biome : chunkGen.getBiomeProvider().getBiomes((chunkX << 4) + 9, chunkGen.getSeaLevel(), (chunkZ << 4) + 9, 64)) {
                if (!biome.hasStructure(this) && !Config.IGNORE_OVERWORLD_BLACKLIST.get()) {
                    return false;
                }
            }
            return !DungeonCrawl.EVENT_BUS.post(new DungeonPlacementCheckEvent(chunkGen,
                    chunkGen.getBiomeProvider().getNoiseBiome(chunkX << 4, chunkGen.getSeaLevel(), chunkZ << 4), chunkX, chunkZ));
        } else {
            return false;
        }
    }

    @Override
    public IStartFactory getStartFactory() {
        return Dungeon.Start::new;
    }

    @Override
    public String getStructureName() {
        return NAME;
    }

    @Override
    public int getSize() {
        return 0;
    }

    public static class Start extends StructureStart {

        public Start(Structure<?> p_i51341_1_, int chunkX, int chunkZ, MutableBoundingBox boundsIn,
                     int referenceIn, long seed) {
            super(p_i51341_1_, chunkX, chunkZ, boundsIn, referenceIn, seed);
        }

        @Override
        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            if (DungeonBuilder.isWorldEligible(generator)) {
                ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
                DungeonBuilder builder = new DungeonBuilder(generator, chunkpos, rand);
                this.components.addAll(builder.build());
                this.recalculateStructureSize();
                DungeonCrawl.LOGGER.debug("Created the dungeon layout for [{}, {}] with a total of {} pieces.", chunkX, chunkZ, this.components.size());
            } else {
                DungeonCrawl.LOGGER.warn("The current world seems to have biomes of overworld-like categories, but is not eligible for dungeon generation.");
            }
        }

        @Override
        public void generateStructure(IWorld worldIn, ChunkGenerator<?> chunkGen, Random rand, MutableBoundingBox structurebb, ChunkPos pos) {
            if (!Config.IGNORE_DIMENSION.get() && !(worldIn.getDimension().getType() == DimensionType.OVERWORLD)) {
                DungeonCrawl.LOGGER.info(
                        "Cancelling the generation of an existing Dungeon because it is not in the overworld. To avoid this, set \"ignore_dimension\" in the config to true. Dimension: {}",
                        worldIn.getDimension().getType());
                return;
            }

            if (Config.EXTENDED_DEBUG.get()) {
                DungeonCrawl.LOGGER.debug("Starting dungeon generation in chunk [{},{}]", pos.x, pos.z);
            }

            super.generateStructure(worldIn, chunkGen, rand, structurebb, pos);

            if (Config.EXTENDED_DEBUG.get()) {
                DungeonCrawl.LOGGER.debug("Finished dungeon generation in chunk [{},{}]", pos.x, pos.z);
            }
        }

    }

}