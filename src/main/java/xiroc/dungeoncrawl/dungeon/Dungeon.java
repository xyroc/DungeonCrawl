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
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;

import java.util.Random;
import java.util.Set;

public class Dungeon extends StructureFeature<NoneFeatureConfiguration> {

    public static Set<Biome.BiomeCategory> biomeCategories = ImmutableSet.<Biome.BiomeCategory>builder()
            .add(Biome.BiomeCategory.DESERT).add(Biome.BiomeCategory.EXTREME_HILLS).add(Biome.BiomeCategory.FOREST)
            .add(Biome.BiomeCategory.ICY).add(Biome.BiomeCategory.JUNGLE).add(Biome.BiomeCategory.MESA)
            .add(Biome.BiomeCategory.PLAINS).add(Biome.BiomeCategory.SAVANNA)
            .add(Biome.BiomeCategory.SWAMP).add(Biome.BiomeCategory.TAIGA).build();

    public static ImmutableSet<String> whitelistedDimensions = ImmutableSet.of("minecraft:overworld");
    public static ImmutableSet<String> whitelistedBiomes = ImmutableSet.of();
    public static ImmutableSet<String> blacklistedBiomes = ImmutableSet.of();

    public static final String NAME = DungeonCrawl.MOD_ID + ":dungeon";

    public Dungeon() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

    @Override
    protected boolean linearSeparation() {
        return false;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator p_160455_, BiomeSource p_160456_, long p_160457_, WorldgenRandom p_160458_, ChunkPos p_160459_, Biome p_160460_, ChunkPos p_160461_, NoneFeatureConfiguration p_160462_, LevelHeightAccessor p_160463_) {
        return true;
    }

    @Override
    public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return Dungeon.Start::new;
    }

    @Override
    public String getFeatureName() {
        return NAME;
    }

    public static class Start extends StructureStart<NoneFeatureConfiguration> {

        public Start(StructureFeature<NoneFeatureConfiguration> p_163595_, ChunkPos p_163596_, int p_163597_, long p_163598_) {
            super(p_163595_, p_163596_, p_163597_, p_163598_);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager p_163617_, ChunkPos chunkPos, Biome p_163619_, NoneFeatureConfiguration p_163620_, LevelHeightAccessor heightAccessor) {
            DungeonBuilder builder = new DungeonBuilder(registryAccess, chunkGenerator, heightAccessor, chunkPos, random);
            this.pieces.addAll(builder.build());
            DungeonCrawl.LOGGER.debug("Created the dungeon layout for [{}, {}] with a total of {} pieces.", chunkPos.x, chunkPos.z, this.pieces.size());
        }

        @Override
        public void placeInChunk(WorldGenLevel p_230366_1_, StructureFeatureManager p_230366_2_, ChunkGenerator p_230366_3_, Random p_230366_4_, BoundingBox p_230366_5_, ChunkPos p_230366_6_) {
            if (Config.EXTENDED_DEBUG.get()) {
                DungeonCrawl.LOGGER.debug("Starting dungeon generation in chunk [{},{}]", p_230366_6_.x, p_230366_6_.z);
            }

            super.placeInChunk(p_230366_1_, p_230366_2_, p_230366_3_, p_230366_4_, p_230366_5_, p_230366_6_);

            if (Config.EXTENDED_DEBUG.get()) {
                DungeonCrawl.LOGGER.debug("Finished dungeon generation in chunk [{},{}]", p_230366_6_.x, p_230366_6_.z);
            }
        }
    }

}