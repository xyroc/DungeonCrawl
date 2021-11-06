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

package xiroc.dungeoncrawl.data.themes.provider;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import xiroc.dungeoncrawl.data.themes.PropertyHolder;

public class BlockProvider implements AbstractProvider {
    private final ResourceLocation block;
    private PropertyHolder properties;
    private boolean serializeType = true;

    public BlockProvider(Block block) {
        this.block = block.getRegistryName();
    }

    public BlockProvider(String block) {
        this.block = new ResourceLocation(block);
    }

    public BlockProvider noType() {
        this.serializeType = false;
        return this;
    }

    public BlockProvider properties(PropertyHolder properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public JsonElement get() {
        JsonObject object = new JsonObject();
        if (serializeType) {
            object.addProperty("type", "block");
        }
        if (block != null) {
            object.addProperty("block", block.toString());
        }
        if (properties != null) {
            object.add("properties", properties.get());
        }
        return object;
    }

}
