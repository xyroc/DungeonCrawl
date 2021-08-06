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

package xiroc.dungeoncrawl.init;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.Dungeon;

import java.util.HashMap;
import java.util.Map;

public class ModStructures {

    public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DungeonCrawl.MOD_ID);

    public static final RegistryObject<Structure<NoFeatureConfig>> DUNGEON = STRUCTURES.register("dungeon", Dungeon::new);

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        STRUCTURES.register(modEventBus);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(ModStructures::onWorldLoad);
        forgeEventBus.addListener(EventPriority.HIGH, ModStructures::onBiomeLoad);
    }

    public static void register() {
        StructureSeparationSettings dungeonSeparationSettings;

        if (Config.SPACING.get() > Config.SEPARATION.get() && Config.SEPARATION.get() >= 0) {
            dungeonSeparationSettings = new StructureSeparationSettings(Config.SPACING.get(), Config.SEPARATION.get(), 10387313);
        } else {
            throw new RuntimeException("Invalid dungeon spacing/separation settings in the config.");
        }

        registerStructure(DUNGEON, ModStructureFeatures.CONFIGURED_DUNGEON, dungeonSeparationSettings);
    }

    private static <FC extends IFeatureConfig> void registerStructure(RegistryObject<Structure<FC>> structure,
                                                                      StructureFeature<FC, ? extends Structure<FC>> configuredFeature,
                                                                      StructureSeparationSettings separationSettings) {
        ResourceLocation registryName = structure.getId();

        Structure.STRUCTURES_REGISTRY.put(registryName.toString(), structure.get());

        DimensionStructuresSettings.DEFAULTS =
                ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                        .putAll(DimensionStructuresSettings.DEFAULTS)
                        .put(structure.get(), separationSettings)
                        .build();

        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, registryName, configuredFeature);

        FlatGenerationSettings.STRUCTURE_FEATURES.put(structure.get(), configuredFeature);

        WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
            Map<Structure<?>, StructureSeparationSettings> structureConfig = settings.getValue().structureSettings().structureConfig();

            if (structureConfig instanceof ImmutableMap) {
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureConfig);
                tempMap.put(structure.get(), separationSettings);
                settings.getValue().structureSettings().structureConfig = tempMap;
            } else {
                structureConfig.put(structure.get(), separationSettings);
            }
        });
    }

    private static void onBiomeLoad(final BiomeLoadingEvent event) {
        if ((Dungeon.biomeCategories.contains(event.getCategory()) || event.getName() == null || Dungeon.whitelistedBiomes.contains(event.getName().toString()))
                && (event.getName() == null || !Dungeon.blacklistedBiomes.contains(event.getName().toString()))) {
            DungeonCrawl.LOGGER.debug("Generating in biome {}", event.getName());
            event.getGeneration().addStructureStart(ModStructureFeatures.CONFIGURED_DUNGEON);
        } else {
            DungeonCrawl.LOGGER.debug("Ignoring biome {} with category {}", event.getName(), event.getCategory().getName());
        }
    }

    private static void onWorldLoad(final WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) event.getWorld();
            if (serverWorld.getChunkSource().getGenerator() instanceof FlatChunkGenerator &&
                    serverWorld.dimension().equals(World.OVERWORLD)) {
                return;
            }

            if (!Dungeon.whitelistedDimensions.contains(serverWorld.dimension().location().toString())) {
                return;
            }

            if (serverWorld.getChunkSource().generator.getSpawnHeight() < 32) {
                DungeonCrawl.LOGGER.info("Ignoring dimension {} because it's spawn height is too low.", serverWorld.dimension().location());
                return;
            }

            DungeonCrawl.LOGGER.debug("Generating in dimension: {}", serverWorld.dimension().location());

            Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
            tempMap.putIfAbsent(ModStructures.DUNGEON.get(), DimensionStructuresSettings.DEFAULTS.get(ModStructures.DUNGEON.get()));
            serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
        }
    }

}
