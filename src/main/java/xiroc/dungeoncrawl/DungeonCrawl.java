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

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.init.ModStructures;
import xiroc.dungeoncrawl.util.ResourceReloadHandler;
import xiroc.dungeoncrawl.util.tools.Tools;

@Mod(DungeonCrawl.MOD_ID)
public class DungeonCrawl {

    public static final String MOD_ID = "dungeoncrawl";
    public static final String NAME = "Dungeon Crawl";
    public static final String VERSION = "2.3.12";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public DungeonCrawl() {
        LOGGER.info("Here we go! Launching Dungeon Crawl {}...", VERSION);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(this::onAddReloadListener);

        init();
    }

    private void init() {
        ModStructures.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common Setup");
        //ModLoadingContext.get().registerConfig(Type.COMMON, Config.CONFIG);
        Config.load(FMLPaths.CONFIGDIR.get().resolve("dungeon_crawl.toml"));

        if (Config.ENABLE_TOOLS.get()) {
            MinecraftForge.EVENT_BUS.register(new Tools());
        }

        event.enqueueWork(() -> {
            Loot.init();
            StructurePieceTypes.register();
        });
    }

    private void onWorldLoad(WorldEvent.Load event) {
        LOGGER.info("Biomes without dungeons:");
        event.getWorld().registryAccess().registry(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).ifPresentOrElse((configuredStructureRegistry) -> {
            event.getWorld().registryAccess().registry(Registry.BIOME_REGISTRY).ifPresentOrElse((biomeRegistry) -> {
                HolderSet<Biome> biomes = configuredStructureRegistry.get(locate("dungeon")).biomes();
                biomeRegistry.entrySet().forEach((entry) -> {
                    biomeRegistry.getHolder(entry.getKey()).ifPresentOrElse((biomeHolder -> {
                        if (!biomes.contains(biomeHolder)) {
                            LOGGER.info(entry.getKey());
                        }
                    }), () -> LOGGER.error("Could not retrieve holder of {}", entry.getKey()));
                });
            }, () -> LOGGER.error("Could not retrieve {}", Registry.BIOME_REGISTRY));
        }, () -> LOGGER.error("Could not retrieve {}", Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY));
    }

    private void onAddReloadListener(final AddReloadListenerEvent event) {
        event.addListener(new ResourceReloadHandler());
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
