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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.data.ProviderJsonFile;
import xiroc.dungeoncrawl.data.ProviderJsonObject;
import xiroc.dungeoncrawl.data.themes.decoration.Decoration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface ProviderTheme extends Supplier<JsonObject> {

    abstract class AbstractTheme extends ProviderJsonFile {
        Integer legacyId = null;
        ProviderJsonObject theme = new ProviderJsonObject();

        public AbstractTheme legacyId(int id) {
            this.legacyId = id;
            return this;
        }

        @Override
        public JsonObject get() {
            JsonObject object = super.get();
            if (legacyId != null) {
                object.addProperty("id", legacyId);
            }
            object.add("theme", theme.get());
            return object;
        }
    }

    class Primary extends AbstractTheme implements ProviderTheme {

        private final List<Decoration> decorations = new ArrayList<>();
        private final List<Tuple<ResourceLocation, Integer>> secondaryThemes = new ArrayList<>();

        public Primary addDecoration(Decoration decoration) {
            this.decorations.add(decoration);
            return this;
        }

        public Primary secondaryTheme(ResourceLocation key) {
            return secondaryTheme(key, 1);
        }

        public Primary secondaryTheme(ResourceLocation key, int weight) {
            secondaryThemes.add(new Tuple<>(key, weight));
            return this;
        }

        public Primary solid(Supplier<JsonElement> solid) {
            theme.data.put("solid", solid);
            return this;
        }

        public Primary generic(Supplier<JsonElement> generic) {
            theme.data.put("generic", generic);
            return this;
        }

        public Primary pillar(Supplier<JsonElement> pillar) {
            theme.data.put("pillar", pillar);
            return this;
        }

        public Primary fencing(Supplier<JsonElement> fencing) {
            theme.data.put("fencing", fencing);
            return this;
        }

        public Primary floor(Supplier<JsonElement> floor) {
            theme.data.put("floor", floor);
            return this;
        }

        public Primary fluid(Supplier<JsonElement> fluid) {
            theme.data.put("fluid", fluid);
            return this;
        }

        public Primary material(Supplier<JsonElement> material) {
            theme.data.put("material", material);
            return this;
        }

        public Primary stairs(Supplier<JsonElement> stairs) {
            theme.data.put("stairs", stairs);
            return this;
        }

        public Primary solid_stairs(Supplier<JsonElement> solid_stairs) {
            theme.data.put("solid_stairs", solid_stairs);
            return this;
        }

        public Primary slab(Supplier<JsonElement> slab) {
            theme.data.put("slab", slab);
            return this;
        }

        public Primary solid_slab(Supplier<JsonElement> solid_slab) {
            theme.data.put("solid_slab", solid_slab);
            return this;
        }

        public Primary wall(Supplier<JsonElement> wall) {
            theme.data.put("wall", wall);
            return this;
        }

        @Override
        public JsonObject get() {
            JsonObject object = super.get();
            if (!decorations.isEmpty()) {
                JsonArray array = new JsonArray();
                decorations.forEach((decoration -> array.add(decoration.get())));
                object.add("decorations", array);
            }
            if (!secondaryThemes.isEmpty()) {
                JsonArray array = new JsonArray();
                secondaryThemes.forEach((tuple) -> {
                    JsonObject object1 = new JsonObject();
                    object1.addProperty("key", tuple.getA().toString());
                    if (tuple.getB() != 1) {
                        object1.addProperty("weight", tuple.getB());
                    }
                });
                object.add("secondaryTheme", array);
            }
            return object;
        }

    }

    class Secondary extends AbstractTheme implements ProviderTheme {

        public Secondary pillar(Supplier<JsonElement> pillar) {
            theme.data.put("pillar", pillar);
            return this;
        }

        public Secondary trapdoor(Supplier<JsonElement> trapdoor) {
            theme.data.put("trapdoor", trapdoor);
            return this;
        }

        public Secondary door(Supplier<JsonElement> door) {
            theme.data.put("door", door);
            return this;
        }

        public Secondary material(Supplier<JsonElement> material) {
            theme.data.put("material", material);
            return this;
        }

        public Secondary stairs(Supplier<JsonElement> stairs) {
            theme.data.put("stairs", stairs);
            return this;
        }

        public Secondary slab(Supplier<JsonElement> slab) {
            theme.data.put("slab", slab);
            return this;
        }

        public Secondary fence(Supplier<JsonElement> fence) {
            theme.data.put("fence", fence);
            return this;
        }

        public Secondary fence_gate(Supplier<JsonElement> fence_gate) {
            theme.data.put("fence_gate", fence_gate);
            return this;
        }

        public Secondary button(Supplier<JsonElement> button) {
            theme.data.put("button", button);
            return this;
        }

        public Secondary pressure_plate(Supplier<JsonElement> pressure_plate) {
            theme.data.put("pressure_plate", pressure_plate);
            return this;
        }

    }

}
