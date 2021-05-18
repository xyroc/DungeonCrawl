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

import xiroc.dungeoncrawl.dungeon.generator.layer.CatacombLayerGenerator;
import xiroc.dungeoncrawl.dungeon.generator.layer.DefaultLayerGenerator;
import xiroc.dungeoncrawl.dungeon.generator.layer.LayerGenerator;
import xiroc.dungeoncrawl.dungeon.generator.layer.RoguelikeLayerGenerator;

import java.util.Hashtable;

public enum DungeonLayerType {

    DEFAULT(DefaultLayerGenerator.INSTANCE),
    ROGUELIKE(RoguelikeLayerGenerator.INSTANCE),
    ROGUELIKE_MST(null),
    CATACOMBS(CatacombLayerGenerator.INSTANCE),
    LOWER_CATACOMBS(CatacombLayerGenerator.INSTANCE),
    HELL(DefaultLayerGenerator.INSTANCE);

    public static final Hashtable<String, DungeonLayerType> NAME_TO_TYPE = new Hashtable<>();

    static {
        NAME_TO_TYPE.put("default", DEFAULT);
        NAME_TO_TYPE.put("roguelike", ROGUELIKE);
        NAME_TO_TYPE.put("catacombs", CATACOMBS);
        NAME_TO_TYPE.put("lower_catacombs", LOWER_CATACOMBS);
        NAME_TO_TYPE.put("hell", HELL);
    }

    public final LayerGenerator layerGenerator;

    DungeonLayerType(LayerGenerator layerGenerator) {
        this.layerGenerator = layerGenerator;
    }

}
