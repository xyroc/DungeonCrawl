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

import com.google.gson.JsonParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;
import org.jline.utils.InputStreamReader;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel.Metadata;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.io.IOException;
import java.util.Hashtable;

public class DungeonModels {

    public static final Hashtable<String, DungeonModel> KEY_TO_MODEL = new Hashtable<>();
    public static final Hashtable<Integer, DungeonModel> ID_TO_MODEL = new Hashtable<>();

    public static final Vector3i NO_OFFSET = new Vector3i(0, 0, 0);

    private static final String FILE_PATH = "models";

    public static synchronized void load(IResourceManager resourceManager) {
        ID_TO_MODEL.clear();
        KEY_TO_MODEL.clear();

        resourceManager.getAllResourceLocations(FILE_PATH, (s) -> s.endsWith(".nbt"))
                .forEach((resource) -> load(resource, resourceManager));
    }

    private static void load(ResourceLocation resource, IResourceManager resourceManager) {
        DungeonModel model = loadModel(resource, resourceManager);
        ResourceLocation metadata = new ResourceLocation(resource.getNamespace(),
                resource.getPath().substring(0, resource.getPath().indexOf(".nbt")) + ".json");

        if (resourceManager.hasResource(metadata)) {
            DungeonCrawl.LOGGER.debug("Loading {}", metadata);
            try {
                Metadata data = Metadata.fromJson(new JsonParser().parse(new InputStreamReader(resourceManager.getResource(metadata).getInputStream())).getAsJsonObject(), metadata);
                model.loadMetadata(data);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load metadata for {}", resource.getPath());
                e.printStackTrace();
            }
        }
    }

    private static DungeonModel loadModel(ResourceLocation resource, IResourceManager resourceManager) {
        DungeonCrawl.LOGGER.debug("Loading {}", resource);

        try {
            CompoundNBT nbt = CompressedStreamTools.readCompressed(resourceManager.getResource(resource).getInputStream());
            DungeonModel model = ModelHandler.loadModelFromNBT(nbt, resource);

            String path = resource.getPath();
            String key = path.substring(FILE_PATH.length() + 1, path.indexOf(".nbt"));
            model.setKey(key);
            KEY_TO_MODEL.put(key, model);

            model.setLocation(resource);
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new DatapackLoadException("Failed to load " + resource);
    }

}
