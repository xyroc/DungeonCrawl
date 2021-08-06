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
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.Dungeon;

import java.util.HashMap;
import java.util.Map;

public class ModStructures {

    public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DungeonCrawl.MOD_ID);

    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> DUNGEON = STRUCTURES.register("dungeon", Dungeon::new);

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        STRUCTURES.register(modEventBus);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(ModStructures::onWorldLoad);
        forgeEventBus.addListener(EventPriority.HIGH, ModStructures::onBiomeLoad);
    }

    public static void register() {
        StructureFeatureConfiguration dungeonSeparationSettings;

        if (Config.SPACING.get() > Config.SEPARATION.get() && Config.SEPARATION.get() >= 0) {
            dungeonSeparationSettings = new StructureFeatureConfiguration(Config.SPACING.get(), Config.SEPARATION.get(), 10387313);
        } else {
            throw new RuntimeException("Invalid dungeon spacing/separation settings in the config.");
        }

        registerStructure(DUNGEON, ModStructureFeatures.CONFIGURED_DUNGEON, dungeonSeparationSettings);
    }

    private static <FC extends FeatureConfiguration> void registerStructure(RegistryObject<StructureFeature<FC>> structure,
                                                                            ConfiguredStructureFeature<FC, ? extends StructureFeature<FC>> configuredFeature,
                                                                            StructureFeatureConfiguration separationSettings) {
        ResourceLocation registryName = structure.getId();

        StructureFeature.STRUCTURES_REGISTRY.put(registryName.toString(), structure.get());

        StructureSettings.DEFAULTS =
                ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder()
                        .putAll(StructureSettings.DEFAULTS)
                        .put(structure.get(), separationSettings)
                        .build();

        Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, registryName, configuredFeature);

        FlatLevelGeneratorSettings.STRUCTURE_FEATURES.put(structure.get(), configuredFeature);

        BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
            Map<StructureFeature<?>, StructureFeatureConfiguration> structureConfiguration = settings.getValue().structureSettings().structureConfig();

            if (structureConfiguration instanceof ImmutableMap) {
                Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(structureConfiguration);
                tempMap.put(structure.get(), separationSettings);
                settings.getValue().structureSettings().structureConfig = tempMap;
            } else {
                structureConfiguration.put(structure.get(), separationSettings);
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
        if (event.getWorld() instanceof ServerLevel serverLevel) {

            if (serverLevel.getChunkSource().getGenerator() instanceof FlatLevelSource &&
                    serverLevel.dimension().equals(Level.OVERWORLD)) {
                return;
            }

            if (!Dungeon.whitelistedDimensions.contains(serverLevel.dimension().location().toString())) {
                return;
            }

            DungeonCrawl.LOGGER.debug("Generating in dimension: {}", serverLevel.dimension().location());

            Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(serverLevel.getChunkSource().generator.getSettings().structureConfig());
            tempMap.putIfAbsent(DUNGEON.get(), StructureSettings.DEFAULTS.get(DUNGEON.get()));
            serverLevel.getChunkSource().generator.getSettings().structureConfig = tempMap;
        }
    }

}
