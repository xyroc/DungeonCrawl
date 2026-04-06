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

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.init.ModStructureTypes;
import xiroc.dungeoncrawl.util.ResourceReloadHandler;

import java.util.Objects;

@Mod(DungeonCrawl.MOD_ID)
public class DungeonCrawl {

    public static final String MOD_ID = "dungeoncrawl";
    public static final String NAME = "Dungeon Crawl";
    public static final String VERSION = "2.3.17";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(Registries.STRUCTURE_TYPE, MOD_ID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPE = DeferredRegister.create(Registries.STRUCTURE_PIECE, MOD_ID);

    public DungeonCrawl(ModContainer modContainer) {
        LOGGER.info("Here we go! Launching Dungeon Crawl {}...", VERSION);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.CONFIG, "dungeon_crawl.toml");

        IEventBus modEventBus = Objects.requireNonNull(modContainer.getEventBus());

        STRUCTURE_TYPE.register(modEventBus);
        STRUCTURE_PIECE_TYPE.register(modEventBus);

        modEventBus.addListener(this::onRegister);

        IEventBus forgeEventBus = NeoForge.EVENT_BUS;
        forgeEventBus.addListener(this::onAddReloadListener);

        ModStructureTypes.init();https://github.com/xyroc/DungeonCrawl-Dev.git
        ModStructurePieceTypes.init();
    }

    private void onRegister(final RegisterEvent event) {
        event.register(Registries.LOOT_FUNCTION_TYPE, Loot::registerLootFunctions);
    }

    private void onAddReloadListener(final AddServerReloadListenersEvent event) {
        event.addListener(locate("server_resources"), new ResourceReloadHandler());
    }

    public static Identifier locate(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Creates a key for a given resource location. Removes the base directory, the following slash and the file ending.
     *
     * @param identifier the initial resource location.
     * @param baseDirectory    the base path without the last slash. ( dirA/dirB not dirA/dirB/ )
     * @param fileEnding       the file ending to remove at the end of the path
     * @return the key
     */
    public static Identifier key(Identifier identifier, String baseDirectory, String fileEnding) {
        String path = identifier.getPath();
        return Identifier.fromNamespaceAndPath(identifier.getNamespace(), path.substring(baseDirectory.length() + 1, path.length() - fileEnding.length()));
    }

}
