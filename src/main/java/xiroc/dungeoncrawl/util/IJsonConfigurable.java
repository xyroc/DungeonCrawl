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

package xiroc.dungeoncrawl.util;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.HashMap;

/**
 * This is a part of the json config system. All instances of this interface
 * should be manually added to the static initialization block of JsonConfig.
 */
public interface IJsonConfigurable {

    public File getFile();

    public void load(JsonObject object, File file);

    public JsonObject create(JsonObject object);

    public HashMap<String, Object> getDefaults();

    public String[] getKeys();

    public int getVersion();

    public default boolean deleteOldVersions() {
        return false;
    }

}
