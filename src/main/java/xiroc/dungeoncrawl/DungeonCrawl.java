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

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.ResourceReloadHandler;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.init.ModRegistries;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.init.ModStructureTypes;

import java.util.Objects;

@Mod(DungeonCrawl.MOD_ID)
public class DungeonCrawl {
    public static final String MOD_ID = "dungeoncrawl";
    public static final String NAME = "Dungeon Crawl";
    public static final String VERSION = "2.3.15";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public DungeonCrawl(ModContainer modContainer) {
        LOGGER.info("Here we go! Launching Dungeon Crawl {}...", VERSION);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.CONFIG, "dungeon_crawl.toml");

        IEventBus modEventBus = Objects.requireNonNull(modContainer.getEventBus());

        ModStructureTypes.REGISTER.register(modEventBus);
        ModStructurePieceTypes.REGISTER.register(modEventBus);
        modEventBus.addListener(DatapackRegistries::register);
        modEventBus.addListener(ModRegistries::register);
        modEventBus.addListener(this::register);

        IEventBus forgeEventBus = NeoForge.EVENT_BUS;
        forgeEventBus.addListener(this::onAddReloadListener);
        forgeEventBus.addListener(this::onTagsUpdated);
    }

    private void register(final RegisterEvent event) {
        event.register(ModRegistries.BLUEPRINT_FEATURE_KEY, BlueprintFeature::register);
    }

    private void onAddReloadListener(final AddReloadListenerEvent event) {
        event.addListener(new ResourceReloadHandler(event.getRegistryAccess()));
    }

    private void onTagsUpdated(final TagsUpdatedEvent event) {
        if (event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            return;
        }
        ResourceReloadHandler.onTagsUpdated(event.getRegistryAccess());
    }

    public static ResourceLocation locate(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Creates a key for a given resource location. Removes the base directory, the following slash and the file ending.
     *
     * @param resourceLocation the initial resource location.
     * @param baseDirectory    the base path without the last slash, e.g., dirA/dirB instead of dirA/dirB/
     * @param fileEnding       the file ending to remove at the end of the path
     * @return the key
     */
    public static ResourceLocation key(ResourceLocation resourceLocation, String baseDirectory, String fileEnding) {
        String path = resourceLocation.getPath();
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), path.substring(baseDirectory.length() + 1, path.length() - fileEnding.length()));
    }
}
