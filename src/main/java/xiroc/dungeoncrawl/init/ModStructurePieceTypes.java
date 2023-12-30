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

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.piece.CompoundPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.StaircasePiece;
import xiroc.dungeoncrawl.dungeon.piece.TestPiece;

public interface ModStructurePieceTypes {
    StructurePieceType GENERIC_PIECE = setPieceId(DungeonPiece::new, key("generic_piece"));
    StructurePieceType ENTRANCE = setPieceId(DungeonEntrance::new, key("entrance"));
    StructurePieceType COMPOUND = setPieceId(CompoundPiece::new, key("compound"));
    StructurePieceType STAIRCASE = setPieceId(StaircasePiece::new, key("staircase"));
    StructurePieceType TEST_PIECE = setPieceId(TestPiece::new, key("test_piece"));

    private static ResourceLocation key(String path) {
        return DungeonCrawl.locate(path);
    }

    private static StructurePieceType setFullContextPieceId(StructurePieceType type, ResourceLocation key) {
        return Registry.register(Registry.STRUCTURE_PIECE, key, type);
    }

    private static StructurePieceType setPieceId(StructurePieceType.ContextlessType type, ResourceLocation key) {
        return setFullContextPieceId(type, key);
    }
}
