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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonType;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonNodeConnector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.List;
import java.util.Random;

public class DungeonNodeRoom extends DungeonPiece {

    public boolean lootRoom;

    public DungeonNodeRoom() {
        super(StructurePieceTypes.NODE_ROOM);
    }

    public DungeonNodeRoom(ServerLevel serverLevel, CompoundTag nbt) {
        super(StructurePieceTypes.NODE_ROOM, nbt);
        this.lootRoom = nbt.getBoolean("lootRoom");
        setupBoundingBox();
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
        if (lootRoom) {
            this.model = DungeonModels.KEY_TO_MODEL.get(DungeonModels.LOOT_ROOM);
            return;
        }

        switch (connectedSides) {
            case 1:
                this.model = modelSelector.deadEndNodes.roll(rand);
                break;
            case 2:
                if (sides[0] && sides[2] || sides[1] && sides[3]) {
                    this.model = modelSelector.straightNodes.roll(rand);
                } else {
                    this.model = modelSelector.turnNodes.roll(rand);
                }
                break;
            case 3:
                this.model = modelSelector.forkNodes.roll(rand);
                break;
            default:
                this.model = modelSelector.fullNodes.roll(rand);
        }
    }

    @Override
    public void setWorldPosition(int x, int y, int z) {
        super.setWorldPosition(x - 4, y, z - 4);
    }


    @Override
    public boolean postProcess(WorldGenLevel worldIn, StructureFeatureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, BoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for  {}", this);
            return true;
        }

        Vec3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x, y, z).offset(offset);

        buildRotated(model, worldIn, structureBoundingBoxIn, pos, theme, secondaryTheme, stage, rotation, worldGen, false, false);
        entrances(worldIn, structureBoundingBoxIn, model, worldGen);
        placeFeatures(worldIn, structureBoundingBoxIn, theme, secondaryTheme, randomIn, stage, worldGen);
        decorate(worldIn, pos, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model, worldGen);
        return true;
    }

    @Override
    public int getDungeonPieceType() {
        return NODE_ROOM;
    }

    @Override
    public boolean canConnect(Direction side, int x, int z) {
        return x == this.gridPosition.x || z == this.gridPosition.z;
    }

    @Override
    public boolean hasChildPieces() {
        return true;
    }

    @Override
    public void addChildPieces(List<DungeonPiece> pieces, DungeonBuilder builder, DungeonType type, ModelSelector modelSelector, int layer, Random rand) {
        super.addChildPieces(pieces, builder, type, modelSelector, layer, rand);

        if (sides[0]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.NORTH);
            connector.theme = theme;
            connector.secondaryTheme = secondaryTheme;
            connector.stage = stage;
            connector.setupModel(builder, modelSelector, pieces, rand);
            connector.setWorldPosition(x + 7, y, z - 5);
            connector.adjustPositionAndBounds();
            connector.customSetup(rand);
            pieces.add(connector);
        }

        if (sides[1]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.EAST);
            connector.theme = theme;
            connector.secondaryTheme = secondaryTheme;
            connector.stage = stage;
            connector.setupModel(builder, modelSelector, pieces, rand);
            connector.setWorldPosition(x + 17, y, z + 7);
            connector.adjustPositionAndBounds();
            connector.customSetup(rand);
            pieces.add(connector);
        }

        if (sides[2]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.SOUTH);
            connector.theme = theme;
            connector.secondaryTheme = secondaryTheme;
            connector.stage = stage;
            connector.setupModel(builder, modelSelector, pieces, rand);
            connector.setWorldPosition(x + 7, y, z + 17);
            connector.adjustPositionAndBounds();
            connector.customSetup(rand);
            pieces.add(connector);
        }

        if (sides[3]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.WEST);
            connector.theme = theme;
            connector.secondaryTheme = secondaryTheme;
            connector.stage = stage;
            connector.setupModel(builder, modelSelector, pieces, rand);
            connector.setWorldPosition(x - 5, y, z + 7);
            connector.adjustPositionAndBounds();
            connector.customSetup(rand);
            pieces.add(connector);
        }
    }

    @Override
    public void addAdditionalSaveData(ServerLevel serverLevel, CompoundTag tagCompound) {
        super.addAdditionalSaveData(serverLevel, tagCompound);
        tagCompound.putBoolean("lootRoom", lootRoom);
    }

}
