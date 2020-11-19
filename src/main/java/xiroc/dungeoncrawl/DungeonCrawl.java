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

package xiroc.dungeoncrawl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.config.JsonConfig;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.util.DataReloadListener;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelFeature;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.module.Modules;
import xiroc.dungeoncrawl.theme.WeightedThemeRandomizer;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Tools;
import xiroc.dungeoncrawl.util.WeightedIntegerEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Mod(DungeonCrawl.MODID)
public class DungeonCrawl {

    public static final String MODID = "dungeoncrawl";
    public static final String NAME = "Dungeon Crawl";
    public static final String VERSION = "2.2.2";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(DungeonModel.Metadata.class, new DungeonModel.Metadata.Deserializer())
            .registerTypeAdapter(WeightedThemeRandomizer.class, new WeightedThemeRandomizer.Deserializer())
            .registerTypeAdapter(WeightedIntegerEntry.class, new WeightedIntegerEntry.Deserializer())
            .setPrettyPrinting().create();

    public static IEventBus EVENT_BUS;

    public DungeonCrawl() {
        LOGGER.info("Here we go! Launching Dungeon Crawl {}...", VERSION);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        Dungeon.DUNGEON.setRegistryName(new ResourceLocation(Dungeon.NAME.toLowerCase(Locale.ROOT)));
        ForgeRegistries.STRUCTURE_FEATURES.register(Dungeon.DUNGEON);
        Structure.field_236365_a_.put(Dungeon.DUNGEON.getRegistryName().toString().toLowerCase(Locale.ROOT), Dungeon.DUNGEON);
        LOGGER.info(Dungeon.FEATURE);
        LOGGER.info(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE.getKey(Dungeon.FEATURE));

        Treasure.init();
        DungeonModelFeature.init();
        DungeonModelBlockType.buildNameTable();

        EVENT_BUS = Bus.MOD.bus().get();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common Setup");
        //ModLoadingContext.get().registerConfig(Type.COMMON, Config.CONFIG);
        Config.load(FMLPaths.CONFIGDIR.get().resolve("dungeon_crawl.toml"));

        StructurePieceTypes.registerAll();

        if (Config.ENABLE_TOOLS.get()) {
            MinecraftForge.EVENT_BUS.register(new Tools());
        }

        DungeonModelBlock.init();
        IBlockPlacementHandler.init();
        DungeonBlocks.init();

        DimensionStructuresSettings.field_236191_b_ = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                .putAll(DimensionStructuresSettings.field_236191_b_)
                .put(Dungeon.DUNGEON, Dungeon.SEPARATION_SETTINGS)
                .build();

        DimensionSettings.field_242740_q.getStructures().func_236195_a_().put(Dungeon.DUNGEON, Dungeon.SEPARATION_SETTINGS);

        Modules.load();
    }

    @SubscribeEvent
    public void addReloadListener(AddReloadListenerEvent event) {
        DungeonCrawl.LOGGER.info("Adding datapack reload listener");
        event.addListener(new DataReloadListener());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomeLoad(BiomeLoadingEvent event) {
        if (!JsonConfig.BIOME_OVERWORLD_BLOCKLIST.contains(event.getName().toString()) && Dungeon.ALLOWED_CATEGORIES.contains(event.getCategory())) {
            LOGGER.debug("Generation Biome: {}", event.getName());
            event.getGeneration().withStructure(Dungeon.FEATURE);
        }
    }

//    @SubscribeEvent
//    public void onServerStart(FMLServerStartingEvent event) {
//        SpawnDungeonCommand.register(event.getCommandDispatcher());
//    }

    public static String getDate() {
        return new SimpleDateFormat().format(new Date());
    }

    public static ResourceLocation locate(String path) {
        return new ResourceLocation(MODID, path);
    }

}
