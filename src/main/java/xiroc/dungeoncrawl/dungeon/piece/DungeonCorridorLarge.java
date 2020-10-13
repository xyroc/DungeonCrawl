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

import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.ChildPieceHandler;
import xiroc.dungeoncrawl.dungeon.ChildPieceHandler.ChildPieceSpot;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DungeonCorridorLarge extends DungeonPiece {

    public int type; // 0: start/end 1: TBD / straight, 2: turn, 3: open
    public int maxCellCount;

    public int[] cells; // 0: no cell, 1: iron bars, 2: open

    public DungeonCorridorLarge(DungeonCorridor corridor, int type) {
        super(StructurePieceTypes.LARGE_CORRIDOR, DEFAULT_NBT);
        this.sides = corridor.sides;
        this.connectedSides = corridor.connectedSides;
        this.rotation = corridor.rotation;
        this.stage = corridor.stage;
        this.posX = corridor.posX;
        this.posZ = corridor.posZ;
        this.type = type;
    }

    public DungeonCorridorLarge(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.LARGE_CORRIDOR, nbt);
        this.type = nbt.getInt("type");
        this.maxCellCount = nbt.getInt("maxCellCount");
        this.cells = new int[maxCellCount];

        for (int i = 0; i < maxCellCount; i++) {
            this.cells[i] = nbt.getInt("cell_" + i);
        }
    }

    @Override
    public int determineModel(DungeonBuilder builder, DungeonModels.ModelCategory layerCategory, Random rand) {
        if (type == 0) {
            this.maxCellCount = getMaxCellCount();
            this.cells = new int[maxCellCount];
            return DungeonModels.LARGE_CORRIDOR_START.id;
        } else if (type == 1) {
            switch (connectedSides) {
                case 2:
                    if ((sides[0] && sides[2]) || (sides[1] && sides[3])) {
                        this.maxCellCount = getMaxCellCount();
                        this.cells = new int[maxCellCount];
                        return DungeonModels.LARGE_CORRIDOR_STRAIGHT.id;
                    }
                    this.type = 2;
                    this.maxCellCount = getMaxCellCount();
                    this.cells = new int[maxCellCount];
                    return DungeonModels.LARGE_CORRIDOR_TURN.id;
                case 3:
                    this.type = 3;
                    this.maxCellCount = getMaxCellCount();
                    this.cells = new int[maxCellCount];
                    return DungeonModels.LARGE_CORRIDOR_OPEN.id;
            }
            this.maxCellCount = getMaxCellCount();
            this.cells = new int[maxCellCount];
            return DungeonModels.LARGE_CORRIDOR_STRAIGHT.id;
        }
        // end of the world
        return 0;
    }

    @Override
    public boolean func_225577_a_(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                  ChunkPos chunkPosIn) {

        //DungeonCrawl.LOGGER.debug("x: {}, y: {}, z: {}, rotation: {}", x, y, z, rotation);

        BlockPos pos = new BlockPos(x, y, z);
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        buildRotated(model, worldIn, structureBoundingBoxIn, pos,
                Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, false);

        ChildPieceSpot[] spots = getSpots();

        for (int i = 0; i < spots.length; i++) {
            if (cells[i] == 1) {
                ChildPieceSpot spot = spots[i];

                if (spot.rotation == Rotation.NONE || spot.rotation == Rotation.CLOCKWISE_180) {
                    int xStart = x + spot.offset.getX(), yStart = y + spot.offset.getY();
                    for (int x = xStart; x < xStart + 3; x++) {
                        for (int y = yStart; y < yStart + 3; y++) {
                            setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), x, y, this.z,
                                    structureBoundingBoxIn, false);
                        }
                    }
                } else {
                    int zStart = z + spot.offset.getZ(), yStart = y + spot.offset.getY();
                    for (int z = zStart; z < zStart + 3; z++) {
                        for (int y = yStart; y < yStart + 3; y++) {
                            setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), this.x, y, z,
                                    structureBoundingBoxIn, false);
                        }
                    }
                }

            }
        }

        decorate(worldIn, pos, model.width, model.height, model.length, Theme.get(theme), structureBoundingBoxIn, boundingBox, model);

        return true;
    }

    @Override
    public void setupBoundingBox() {
        this.boundingBox = new MutableBoundingBox(x, y, z, x + 8, y + 8, z + 8);
    }

    @Override
    public int getType() {
        return 7;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putInt("type", type);

        tagCompound.putInt("maxCellCount", maxCellCount);

        for (int i = 0; i < cells.length; i++) {
            tagCompound.putInt("cell_" + i, cells[i]);
        }
    }

    @Override
    public boolean hasChildPieces() {
        // temporarily disabled
        return false;
    }

    @Override
    public void addChildPieces(List<DungeonPiece> list, DungeonBuilder builder, DungeonModels.ModelCategory layerCategory, int layer, Random rand) {
        ChildPieceSpot[] spots = getSpots();

        List<Position2D> positions = Lists.newArrayList();

        for (int i = 0; i < spots.length; i++) {
            ChildPieceSpot spot = spots[i];
            Position2D pos = Position2D.shift(posX, posZ,
                    spot.rotation.rotate(Direction.EAST), 1);
            if (pos.isValid(builder.layers[layer].width, builder.layers[layer].length)
                    && builder.layers[layer].get(pos.x, pos.z) == null && builder.maps[layer].isPositionFree(pos.x, pos.z)
                    && rand.nextFloat() < 0.5) {

                cells[i] = 1;
                DungeonPrisonCell cell = new DungeonPrisonCell();
                cell.modelID = cell.determineModel(builder, layerCategory, rand);
                cell.setPosition(pos.x, pos.z);
                cell.rotation = spot.rotation;

                BlockPos vec = new BlockPos(spot.offset)
                        .offset(spot.rotation.rotate(Direction.EAST), 1);

                cell.setRealPosition(x + vec.getX(), y + vec.getY(), z + vec.getZ());
                cell.setupBoundingBox();

                list.add(cell);
                positions.add(pos);
            }
        }

        for (Position2D pos : positions) {
            builder.maps[layer].markPositionAsOccupied(pos);
        }

    }

    private ChildPieceSpot[] getSpots() {
        switch (type) {
            case 0:
                return ChildPieceHandler.LARGE_CORRIDOR_START.getChildPieceSpots(rotation);
            case 1:
                return ChildPieceHandler.LARGE_CORRIDOR_STRAIGHT.getChildPieceSpots(rotation);
            case 2:
                return ChildPieceHandler.LARGE_CORRIDOR_TURN.getChildPieceSpots(rotation);
            case 3:
                return ChildPieceHandler.LARGE_CORRIDOR_OPEN.getChildPieceSpots(rotation);
            default:
                return null;
        }
    }

    private int getMaxCellCount() {
        switch (type) {
            case 0:
                return 4;
            case 1:
                return 4;
            case 2:
                return 2;
            case 3:
                return 2;
            default:
                return 0;
        }
    }

}
