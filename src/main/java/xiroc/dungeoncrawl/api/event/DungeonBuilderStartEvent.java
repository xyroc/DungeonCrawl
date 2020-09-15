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

package xiroc.dungeoncrawl.api.event;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.eventbus.api.Event;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker;

public class DungeonBuilderStartEvent extends Event {

    public final ChunkGenerator chunkGen;
    public final int layers;
    public final BlockPos startPos;
    public int theme, subTheme;
    public DungeonStatTracker statTracker;

    public DungeonBuilderStartEvent(ChunkGenerator chunkGen, BlockPos startPos, DungeonStatTracker statTracker, int layers) {
        this.chunkGen = chunkGen;
        this.startPos = startPos;
        this.statTracker = statTracker;
        this.layers = layers;
    }

}
