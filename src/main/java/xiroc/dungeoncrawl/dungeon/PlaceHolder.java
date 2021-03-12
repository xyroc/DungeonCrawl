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

import com.google.common.collect.Lists;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

import java.util.Arrays;
import java.util.List;

/*
 * Used for objects that contain data for the dungeon layout generation process.
 * The point of this is to properly separate data that is only used during the
 *  layout generation (especially dummy placeholders for node rooms) from
 *  data that actually needs to be stored in the pieces themselves.
 */

public class PlaceHolder {

    public final DungeonPiece reference;
    public List<Flag> flags;

    public PlaceHolder(DungeonPiece reference) {
        this.reference = reference;
        this.flags = Lists.newArrayList();
    }

    public PlaceHolder addFlag(Flag flag) {
        flags.add(flag);
        return this;
    }

    public PlaceHolder addFlags(Flag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public boolean hasFlag(Flag flag) {
        return flags.contains(flag);
    }

    public enum Flag {

        PLACEHOLDER, FIXED_POSITION, FIXED_ROTATION, FIXED_MODEL

    }

}
