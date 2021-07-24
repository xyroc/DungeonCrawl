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

import net.minecraft.world.level.ChunkPos;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonType;
import xiroc.dungeoncrawl.dungeon.generator.layer.LayerGenerator;

import java.util.Random;

/**
 * The base class for all dungeon generator types.
 */
public abstract class DungeonGenerator extends LayerGenerator {

    /**
     * The dungeon type currently in use.
     */
    protected DungeonType type;

    /**
     * Called once before the layout generation for a dungeon starts.
     */
    public void initializeDungeon(DungeonType type, DungeonBuilder dungeonBuilder, ChunkPos chunkPos, Random rand) {
        this.type = type;
    }

    /**
     * @return the amount of layers the dungeon will have.
     */
    public abstract int layerCount(Random rand, int height);

}
