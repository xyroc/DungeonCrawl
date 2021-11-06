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

package xiroc.dungeoncrawl.data.themes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.function.Supplier;

public final class PropertyHolder implements Supplier<JsonElement> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    HashMap<String, JsonElement> properties;

    public <T extends Comparable<T>> PropertyHolder set(Property<T> property, T value) {
        properties.put(property.getName(), GSON.toJsonTree(value));
        return this;
    }

    @Override
    public JsonElement get() {
        JsonObject properties = new JsonObject();
        this.properties.forEach(properties::add);
        return properties;
    }

}
