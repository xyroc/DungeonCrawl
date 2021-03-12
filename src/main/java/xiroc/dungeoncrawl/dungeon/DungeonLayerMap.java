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

import xiroc.dungeoncrawl.util.Position2D;

public class DungeonLayerMap {

    public int width, length;
    public boolean[][] map;

    public DungeonLayerMap(int width, int length) {
        this.width = width;
        this.length = length;
        this.map = new boolean[width][length];
    }

    public boolean isPositionFree(int x, int z) {
        return !this.map[x][z];
    }

    public void markPositionAsOccupied(Position2D pos) {
        map[pos.x][pos.z] = true;
    }

}
