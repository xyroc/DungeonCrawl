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

package xiroc.dungeoncrawl.dungeon.generator;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

public class DungeonGeneratorSettings {

    /**
     * The maximum allowed amount of layers.
     */
    public final int maxLayers;

    public DungeonGeneratorSettings(int maxLayers) {
        this.maxLayers = maxLayers;
    }

    public static DungeonGeneratorSettings fromJson(JsonObject settings, ResourceLocation file) {
        if (settings.has("max_layers")) {
            return new DungeonGeneratorSettings(settings.get("max_layers").getAsInt());
        } else {
            throw new DatapackLoadException("Missing entry max_layers in " + file);
        }
    }

}
