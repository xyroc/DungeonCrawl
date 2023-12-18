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

package xiroc.dungeoncrawl.dungeon.model;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonParser;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jline.utils.InputStreamReader;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.io.IOException;
import java.util.Hashtable;

public class DungeonModels {

    public static final Hashtable<ResourceLocation, DungeonModel> KEY_TO_MODEL = new Hashtable<>();
    public static final Hashtable<Integer, DungeonModel> ID_TO_MODEL = new Hashtable<>();

    public static final Vec3i NO_OFFSET = new Vec3i(0, 0, 0);

    public static final ResourceLocation SECRET_ROOM_ENTRANCE = DungeonCrawl.locate("default/corridor/secret_room_entrance");
    public static final ResourceLocation STARTER_ROOM = DungeonCrawl.locate("default/room/starter_room");
    public static final ResourceLocation STAIRCASE_LAYER = DungeonCrawl.locate("default/staircase_layer");
    public static final ResourceLocation BOTTOM_STAIRS = DungeonCrawl.locate("default/stairs_bottom");
    public static final ResourceLocation TOP_STAIRS = DungeonCrawl.locate("default/stairs_top");
    public static final ResourceLocation LOOT_ROOM = DungeonCrawl.locate("default/loot_room");
    public static final ResourceLocation SECRET_ROOM = DungeonCrawl.locate("default/room/secret_room");

    private static ImmutableSet<ResourceLocation> KEYS;
    private static ImmutableSet.Builder<ResourceLocation> keySetBuilder;

    private static final String DIRECTORY = "models";

    public static synchronized void load(ResourceManager resourceManager) {
        ID_TO_MODEL.clear();
        KEY_TO_MODEL.clear();

        keySetBuilder = new ImmutableSet.Builder<>();

        resourceManager.listResources(DIRECTORY, (s) -> s.getPath().endsWith(".nbt"))
                .forEach((file, resource) -> load(file, resourceManager));

        KEYS = keySetBuilder.build();
    }

    private static void load(ResourceLocation file, ResourceManager resourceManager) {
        DungeonModel model = loadModel(file, resourceManager);
        ResourceLocation metadataFile = new ResourceLocation(file.getNamespace(),
                file.getPath().substring(0, file.getPath().indexOf(".nbt")) + ".json");

        resourceManager.getResource(metadataFile).ifPresent((metadata) -> {
            DungeonCrawl.LOGGER.debug("Loading {}", metadataFile);
            try {
                model.loadMetadata(JsonParser.parseReader(new InputStreamReader(metadata.open())).getAsJsonObject(), metadataFile);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load metadata for {}", file.getPath());
                e.printStackTrace();
            }
        });
    }

    private static DungeonModel loadModel(ResourceLocation resource, ResourceManager resourceManager) {
        DungeonCrawl.LOGGER.debug("Loading {}", resource);

        try {
            CompoundTag nbt = NbtIo.readCompressed(resourceManager.getResource(resource).orElseThrow().open(), NbtAccounter.unlimitedHeap());

            ResourceLocation key = DungeonCrawl.key(resource, DIRECTORY, ".nbt");
            DungeonModel model = ModelHandler.loadModelFromNBT(nbt, resource, key);

            KEY_TO_MODEL.put(key, model);
            keySetBuilder.add(key);

            if (model.hasId()) {
                ID_TO_MODEL.put(model.getId(), model);
            }

            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new DatapackLoadException("Failed to load " + resource);
    }

    public static ImmutableSet<ResourceLocation> getKeys() {
        return KEYS;
    }
}
