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

import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.command.SpawnDungeonCommand;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.ResourceReloadHandler;
import xiroc.dungeoncrawl.util.tools.Tools;

import java.util.HashMap;
import java.util.Map;

@Mod(DungeonCrawl.MOD_ID)
public class DungeonCrawl {

    public static final String MOD_ID = "dungeoncrawl";
    public static final String NAME = "Dungeon Crawl";
    public static final String VERSION = "2.3.0-dev";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final JsonParser JSON_PARSER = new JsonParser();

    public DungeonCrawl() {
        LOGGER.info("Here we go! Launching Dungeon Crawl {}...", VERSION);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addGenericListener(StructureFeature.class, this::onRegisterStructures);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(this::onAddReloadListener);
        forgeEventBus.addListener(this::onWorldLoad);
        forgeEventBus.addListener(EventPriority.HIGH, this::onBiomeLoad);
        forgeEventBus.addListener(this::onServerStart);

        Treasure.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common Setup");
        //ModLoadingContext.get().registerConfig(Type.COMMON, Config.CONFIG);
        Config.load(FMLPaths.CONFIGDIR.get().resolve("dungeon_crawl.toml"));

        if (Config.SPACING.get() <= Config.SEPARATION.get()) {
            throw new IllegalArgumentException("Invalid spacing/separation setting in the config.");
        }

        StructurePieceTypes.register();

        if (Config.ENABLE_TOOLS.get()) {
            MinecraftForge.EVENT_BUS.register(new Tools());
        }

    }

    private void onRegisterStructures(final RegistryEvent.Register<StructureFeature<?>> event) {
        Dungeon.register();
    }

    private void onBiomeLoad(final BiomeLoadingEvent event) {
        if (Dungeon.ALLOWED_CATEGORIES.contains(event.getCategory())) {
            LOGGER.debug("Generation Biome: {}", event.getName());
            event.getGeneration().addStructureStart(Dungeon.CONFIGURED_DUNGEON);
        }
    }

    private void onWorldLoad(final WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel serverWorld) {

            if (serverWorld.getChunkSource().getGenerator() instanceof FlatLevelSource &&
                    serverWorld.dimension().equals(Level.OVERWORLD)) {
                return;
            }

            Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
            tempMap.putIfAbsent(Dungeon.DUNGEON, StructureSettings.DEFAULTS.get(Dungeon.DUNGEON));
            serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
        } else {
            LOGGER.info("Skipping world {}", event.getWorld().getClass());
        }
    }

    private void onAddReloadListener(final AddReloadListenerEvent event) {
        event.addListener(new ResourceReloadHandler());
    }

    private void onServerStart(FMLServerStartingEvent event) {
        SpawnDungeonCommand.register(event.getServer().getCommands().getDispatcher());
    }

    public static ResourceLocation locate(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /**
     * Creates a key for a given resource location. Removes the base directory, the following slash and the file ending.
     *
     * @param resourceLocation the initial resource location.
     * @param baseDirectory    the base path without the last slash. ( dirA/dirB not dirA/dirB/ )
     * @param fileEnding       the file ending to remove at the end of the path
     * @return the key
     */
    public static ResourceLocation key(ResourceLocation resourceLocation, String baseDirectory, String fileEnding) {
        String path = resourceLocation.getPath();
        return new ResourceLocation(resourceLocation.getNamespace(), path.substring(baseDirectory.length() + 1, path.length() - fileEnding.length()));
    }

}
