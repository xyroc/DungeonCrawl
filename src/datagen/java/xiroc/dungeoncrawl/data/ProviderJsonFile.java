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

package xiroc.dungeoncrawl.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ProviderJsonFile extends ProviderJsonObject {

    private final List<String> present = new ArrayList<>();
    private final List<String> absent = new ArrayList<>();

    public ProviderJsonFile present(String modId) {
        this.present.add(modId);
        return this;
    }

    public ProviderJsonFile absent(String modId) {
        this.absent.add(modId);
        return this;
    }

    @Override
    public JsonObject get() {
        JsonObject file = super.get();
        if (!present.isEmpty() || !absent.isEmpty()) {
            JsonObject requirements = new JsonObject();
            if (!present.isEmpty()) {
                JsonArray present = new JsonArray();
                this.present.forEach(present::add);
                requirements.add("present", present);
            }
            if (!absent.isEmpty()) {
                JsonArray absent = new JsonArray();
                this.absent.forEach(absent::add);
                requirements.add("absent", absent);
            }
            file.add("requirements", requirements);
        }
        return file;
    }
}
