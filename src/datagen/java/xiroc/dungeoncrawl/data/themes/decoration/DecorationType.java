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

package xiroc.dungeoncrawl.data.themes.decoration;

import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;

public enum DecorationType {

    VINES(IDungeonDecoration.VINE_DECORATION),
    SCATTERED(IDungeonDecoration.SCATTERED_DECORATION, true),
    FLOOR(IDungeonDecoration.FLOOR_DECORATION, true),
    FLOOR_NEXT_TO_SOLID(IDungeonDecoration.FLOOR_NEXT_TO_SOLID_DECORATION, true);

    public final String name;
    public final boolean supportsCustomBlock;

    DecorationType(String name) {
        this(name, false);
    }

    DecorationType(String name, boolean supportsCustomBlock) {
        this.supportsCustomBlock = supportsCustomBlock;
        this.name = name;
    }

}
