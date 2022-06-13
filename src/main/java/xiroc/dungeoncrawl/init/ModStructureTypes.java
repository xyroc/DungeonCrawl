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

package xiroc.dungeoncrawl.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import xiroc.dungeoncrawl.dungeon.Dungeon;

public interface ModStructureTypes {

    StructureType<Dungeon> DUNGEON = register("dungeoncrawl:dungeon", Dungeon.CODEC);

    private static <S extends Structure> StructureType<S> register(String p_226882_, Codec<S> p_226883_) {
        return Registry.register(Registry.STRUCTURE_TYPES, p_226882_, () -> {
            return p_226883_;
        });
    }

    static void load() {
    }

}
