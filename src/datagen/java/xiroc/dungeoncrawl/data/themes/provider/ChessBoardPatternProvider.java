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

package xiroc.dungeoncrawl.data.themes.provider;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChessBoardPatternProvider implements AbstractProvider {

    private final AbstractProvider block1;
    private final AbstractProvider block2;

    public ChessBoardPatternProvider(AbstractProvider block1, AbstractProvider block2) {
        this.block1 = block1;
        this.block2 = block2;
    }

    @Override
    public JsonElement get() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "pattern");
        object.addProperty("pattern_type", "chess_board");
        object.add("block_1", block1.get());
        object.add("block_2", block2.get());
        return null;
    }

}
