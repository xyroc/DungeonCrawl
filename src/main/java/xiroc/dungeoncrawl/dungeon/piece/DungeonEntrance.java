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

package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;

import java.util.Random;

public class DungeonEntrance extends DungeonPiece {
    public DungeonEntrance(Blueprint blueprint, BlockPos position, Rotation rotation, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, Random random) {
        super(ModStructurePieceTypes.ENTRANCE, blueprint, position, rotation, primaryTheme, secondaryTheme, 0, random);
    }

    public DungeonEntrance(CompoundTag nbt) {
        super(ModStructurePieceTypes.ENTRANCE, nbt);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_230383_2_, ChunkGenerator p_230383_3_, Random random, BoundingBox worldGenBounds, ChunkPos p_230383_6_, BlockPos p_230383_7_) {

    }
}