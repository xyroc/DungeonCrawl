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

import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

import javax.annotation.Nonnull;

/*
 * Used for objects that contain data for the dungeon layout generation process.
 * The point of this is to properly separate data that is only used during the
 *  layout generation (especially dummy placeholders for node rooms) from
 *  data that actually needs to be stored in the pieces themselves.
 */

public class Tile {

    public final DungeonPiece piece;
    private int flags;

    // Used to determine whether multiple mega node parts belong to the same mega node
    private int id;

    public Tile(@Nonnull DungeonPiece piece) {
        this.piece = piece;
        this.flags = 0;
    }

    public Tile addFlag(Flag flag) {
        flags = flags | flag.value;
        return this;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean hasFlag(Flag flag) {
        return (flags & flag.value) != 0;
    }

    public enum Flag {

        PLACEHOLDER(1),
        FIXED_POSITION(2),
        FIXED_ROTATION(4),
        FIXED_MODEL(8);

        int value;

        Flag(int value) {
            this.value = value;
        }

    }

}
