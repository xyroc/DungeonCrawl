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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;

import java.util.Locale;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.gen.feature.structure.Structure.IStartFactory;

public class Dungeon extends Structure<NoFeatureConfig> {

    public static final Set<Biome.Category> ALLOWED_CATEGORIES = ImmutableSet.<Biome.Category>builder()
            .add(Biome.Category.BEACH).add(Biome.Category.DESERT).add(Biome.Category.EXTREME_HILLS)
            .add(Biome.Category.FOREST).add(Biome.Category.ICY).add(Biome.Category.JUNGLE).add(Biome.Category.MESA)
            .add(Biome.Category.PLAINS).add(Biome.Category.RIVER).add(Biome.Category.SAVANNA).add(Biome.Category.SWAMP)
            .add(Biome.Category.TAIGA).add(Biome.Category.RIVER).build();

    public static final String NAME = DungeonCrawl.MOD_ID + ":dungeon";

    public static final Structure<NoFeatureConfig> DUNGEON = new Dungeon();
    public static final StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> CONFIGURED_DUNGEON = DUNGEON.configured(NoFeatureConfig.NONE);


    public static final int SIZE = 15;

    public Dungeon() {
        super(NoFeatureConfig.CODEC);
    }

    public static void register() {
        ResourceLocation registryName = new ResourceLocation(Dungeon.NAME.toLowerCase(Locale.ROOT));

        DUNGEON.setRegistryName(registryName);
        Structure.STRUCTURES_REGISTRY.put(registryName.toString().toLowerCase(Locale.ROOT), DUNGEON);

        ForgeRegistries.STRUCTURE_FEATURES.register(DUNGEON);

        StructureSeparationSettings separationSettings;

        if (Config.SPACING.get() > Config.SEPARATION.get() && Config.SEPARATION.get() >= 0) {
            separationSettings = new StructureSeparationSettings(Config.SPACING.get(), Config.SEPARATION.get(), 10387313);
        } else {
            throw new IllegalArgumentException("Invalid dungeon spacing/separation settings in the config.");
        }

        DimensionStructuresSettings.DEFAULTS =
                ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                        .putAll(DimensionStructuresSettings.DEFAULTS)
                        .put(DUNGEON, separationSettings)
                        .build();

        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, registryName, CONFIGURED_DUNGEON);

        FlatGenerationSettings.STRUCTURE_FEATURES.put(DUNGEON, CONFIGURED_DUNGEON);
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.UNDERGROUND_STRUCTURES;
    }

    @Override
    protected boolean linearSeparation() {
        return false;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_) {
        for (Biome biome : p_230363_2_.getBiomesWithin(p_230363_6_ * 16, p_230363_1_.getSpawnHeight(), p_230363_7_ * 16, 64)) {
            if (!biome.getGenerationSettings().isValidStart(this) && !Config.IGNORE_OVERWORLD_BLACKLIST.get())
                return false;
        }
        return true;
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Dungeon.Start::new;
    }

    @Override
    public String getFeatureName() {
        return NAME;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {

        public Start(Structure<NoFeatureConfig> p_i51341_1_, int chunkX, int chunkZ, MutableBoundingBox boundsIn,
                     int referenceIn, long seed) {
            super(p_i51341_1_, chunkX, chunkZ, boundsIn, referenceIn, seed);
        }

        @Override
        public void generatePieces(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager p_230364_3_, int chunkX, int chunkZ, Biome p_230364_6_, NoFeatureConfig p_230364_7_) {
            ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
            DungeonBuilder builder = new DungeonBuilder(dynamicRegistries, chunkGenerator, chunkpos, random);
            this.pieces.addAll(builder.build());
            this.calculateBoundingBox();
            DungeonCrawl.LOGGER.debug("Created the dungeon layout for [{}, {}] with a total of {} pieces.", chunkX, chunkZ, this.pieces.size());
        }

        @Override
        public void placeInChunk(ISeedReader p_230366_1_, StructureManager p_230366_2_, ChunkGenerator p_230366_3_, Random p_230366_4_, MutableBoundingBox p_230366_5_, ChunkPos p_230366_6_) {
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