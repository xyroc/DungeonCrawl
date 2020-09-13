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

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Cancel this event to prevent the dungeon from getting placed.
 */

@Cancelable
public class DungeonPlacementCheckEvent extends Event {

    public final ChunkGenerator chunkGenerator;
    public final Biome biome;
    public final int chunkX, chunkZ;

    public DungeonPlacementCheckEvent(ChunkGenerator chunkGenerator, Biome biome, int chunkX, int chunkZ) {
        this.chunkGenerator = chunkGenerator;
        this.biome = biome;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

}
