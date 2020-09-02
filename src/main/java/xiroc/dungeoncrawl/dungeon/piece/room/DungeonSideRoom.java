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

package xiroc.dungeoncrawl.dungeon.piece.room;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonSideRoom extends DungeonPiece {

    public DungeonSideRoom() {
        super(StructurePieceTypes.SIDE_ROOM);
    }

    public DungeonSideRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.SIDE_ROOM, p_i51343_2_);
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        return 0;
    }

    @Override
    public boolean func_225577_a_(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                  ChunkPos chunkPosIn) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        Vec3i offset = DungeonModels.getOffset(modelID);
        BlockPos pos = new BlockPos(x + offset.getX(), y + offset.getY(), z + offset.getZ());

        buildRotated(model, worldIn, structureBoundingBoxIn, pos,
                Theme.get(theme), Theme.getSub(subTheme), Treasure.MODEL_TREASURE_TYPES.getOrDefault(modelID, Treasure.Type.DEFAULT), stage, rotation, true);
        decorate(worldIn, pos, model.width, model.height, model.length, Theme.get(theme), structureBoundingBoxIn, boundingBox, model);

        if (Config.NO_SPAWNERS.get())
            spawnMobs(worldIn, this, model.width, model.length, new int[]{1});
        return true;
    }

    @Override
    public void setupBoundingBox() {
        Vec3i offset = DungeonModels.getOffset(modelID);
        this.boundingBox = new MutableBoundingBox(x + offset.getX(), y + offset.getY(),
                z + offset.getZ(), x + offset.getX() + 7, y + offset.getY() + 7, z + offset.getZ() + 7);
    }

    @Override
    public int getType() {
        return 9;
    }

}