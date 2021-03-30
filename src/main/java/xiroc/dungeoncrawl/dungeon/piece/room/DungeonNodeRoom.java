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
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.Node;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelFeature;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelCategory;
import xiroc.dungeoncrawl.dungeon.piece.DungeonNodeConnector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.util.List;
import java.util.Random;

public class DungeonNodeRoom extends DungeonPiece {

    public Node node;

    public boolean large, lootRoom;

    public DungeonNodeRoom() {
        super(StructurePieceTypes.NODE_ROOM);
    }

    public DungeonNodeRoom(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.NODE_ROOM, nbt);
        this.large = nbt.getBoolean("large");
        this.lootRoom = nbt.getBoolean("lootRoom");
        setupBoundingBox();
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelCategory layerCategory, List<DungeonPiece> pieces, Random rand) {
        if (lootRoom) {
            node = Node.ALL;
            this.model = DungeonModels.KEY_TO_MODEL.get("loot_room");
            return;
        }

        large = stage >= 2 && rand.nextFloat() < 0.15;

        ModelCategory base;

        switch (connectedSides) {
            case 1:
                base = ModelCategory.NODE_DEAD_END;
                node = Node.DEAD_END;
                break;
            case 2:
                if (sides[0] && sides[2] || sides[1] && sides[3]) {
                    base = ModelCategory.NODE_STRAIGHT;
                    node = Node.STRAIGHT;
                } else {
                    base = ModelCategory.NODE_TURN;
                    node = Node.TURN;
                }
                break;
            case 3:
                base = ModelCategory.NODE_FORK;
                node = Node.FORK;
                break;
            default:
                base = ModelCategory.NODE_FULL;
                node = Node.ALL;
                break;
        }

        WeightedRandom<DungeonModel> provider = large
                ? ModelCategory.get(ModelCategory.LARGE_NODE, layerCategory, base)
                : ModelCategory.get(ModelCategory.NORMAL_NODE, layerCategory, base);

        this.model = provider.roll(rand);
    }

    @Override
    public void setWorldPosition(int x, int y, int z) {
        if (large)
            super.setWorldPosition(x - 9, y, z - 9);
        else
            super.setWorldPosition(x - 4, y, z - 4);
    }

    @Override
    public void customSetup(Random rand) {
        if (model == null) {
            return;
        }
        if (model.metadata != null) {
            if (model.metadata.featureMetadata != null && model.featurePositions != null && model.featurePositions.length > 0) {
                DungeonModelFeature.setup(this, model, model.featurePositions, rotation, rand, model.metadata.featureMetadata, x, y, z);
            }
            if (model.metadata.variation) {
                variation = new byte[16];
                for (int i = 0; i < variation.length; i++) {
                    variation[i] = (byte) rand.nextInt(32);
                }
            }
        }
    }

    @Override
    public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                          ChunkPos chunkPosIn) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for  {}", this);
            return true;
        }

        Vec3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x, y, z).add(offset);

        buildRotated(model, worldIn, structureBoundingBoxIn, pos, theme, subTheme, model.getTreasureType(), stage, rotation, false);

        entrances(worldIn, structureBoundingBoxIn, model);

        if (model.metadata != null && model.metadata.feature != null && featurePositions != null) {
            model.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
        }

        decorate(worldIn, pos, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model);

        if (Config.NO_SPAWNERS.get())
            spawnMobs(worldIn, this, model.width, model.length, new int[]{offset.getY()});
        return true;
    }

    @Override
    public void setupBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
        } else {
            DungeonCrawl.LOGGER.info("NODE MODEL IS NULL");
        }
    }

    @Override
    public int getType() {
        return 10;
    }

    @Override
    public boolean canConnect(Direction side, int x, int z) {
        return x == this.gridX || z == this.gridZ;
    }

    @Override
    public boolean hasChildPieces() {
        return !large;
    }

    @Override
    public void addChildPieces(List<DungeonPiece> pieces, DungeonBuilder builder, ModelCategory layerCategory, int layer, Random rand) {
        super.addChildPieces(pieces, builder, layerCategory, layer, rand);
        if (large) {
            return;
        }

        if (sides[0]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.NORTH);
            connector.theme = theme;
            connector.subTheme = subTheme;
            connector.stage = stage;
            connector.setupModel(builder, layerCategory, pieces, rand);
            connector.setWorldPosition(x + 7, y, z - 5);
            connector.adjustPositionAndBounds();
            pieces.add(connector);
        }

        if (sides[1]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.EAST);
            connector.theme = theme;
            connector.subTheme = subTheme;
            connector.stage = stage;
            connector.setupModel(builder, layerCategory, pieces, rand);
            connector.setWorldPosition(x + 17, y, z + 7);
            connector.adjustPositionAndBounds();
            pieces.add(connector);
        }

        if (sides[2]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.SOUTH);
            connector.theme = theme;
            connector.subTheme = subTheme;
            connector.stage = stage;
            connector.setupModel(builder, layerCategory, pieces, rand);
            connector.setWorldPosition(x + 7, y, z + 17);
            connector.adjustPositionAndBounds();
            pieces.add(connector);
        }

        if (sides[3]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.WEST);
            connector.theme = theme;
            connector.subTheme = subTheme;
            connector.stage = stage;
            connector.setupModel(builder, layerCategory, pieces, rand);
            connector.setWorldPosition(x - 5, y, z + 7);
            connector.adjustPositionAndBounds();
            pieces.add(connector);
        }
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putBoolean("large", large);
        tagCompound.putBoolean("lootRoom", lootRoom);
    }

}
