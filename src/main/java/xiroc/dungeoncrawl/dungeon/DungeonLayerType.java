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

package xiroc.dungeoncrawl.dungeon;

import com.google.common.collect.ImmutableMap;
import xiroc.dungeoncrawl.dungeon.generator.layer.LayerGenerator;
import xiroc.dungeoncrawl.dungeon.generator.layer.NewLayerGenerator;

public enum DungeonLayerType {

    DEFAULT(NewLayerGenerator.INSTANCE);

    public static final ImmutableMap<String, DungeonLayerType> NAME_TO_TYPE = new ImmutableMap.Builder<String, DungeonLayerType>()
            .put("default", DEFAULT)
            .build();

    public final LayerGenerator layerGenerator;

    DungeonLayerType(LayerGenerator layerGenerator) {
        this.layerGenerator = layerGenerator;
    }

}
