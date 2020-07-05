package xiroc.dungeoncrawl.dungeon.piece.room;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.Node;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelFeature;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels.ModelCategory;
import xiroc.dungeoncrawl.dungeon.piece.DungeonNodeConnector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;
import xiroc.dungeoncrawl.util.WeightedRandomInteger;

import java.util.List;
import java.util.Random;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

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
    public int determineModel(DungeonBuilder builder, Random rand) {
        if (lootRoom) {
            node = Node.ALL;
            return DungeonModels.LOOT_ROOM.id;
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

        WeightedRandomInteger provider = large
                ? ModelCategory.get(ModelCategory.LARGE_NODE, ModelCategory.getCategoryForStage(stage), base)
                : ModelCategory.get(ModelCategory.NORMAL_NODE, ModelCategory.getCategoryForStage(stage), base);

        if (provider == null) {
            DungeonCrawl.LOGGER.error("Didnt find a model provider for {} in stage {}. Connected Sides: {}, Base: {}",
                    this, stage, connectedSides, base);

            return large ? DungeonModels.LARGE_NODE.id : DungeonModels.NODE_2.id;
        }

        Integer id = provider.roll(rand);

        if (id == null) {
            DungeonCrawl.LOGGER.error("Empty model provider for {} in stage {}. Connected Sides: {}, Base: {}",
                    this, stage, connectedSides, base);
            return large ? DungeonModels.LARGE_NODE.id : DungeonModels.NODE_2.id;
        }

        return id;
    }

    @Override
    public void setRealPosition(int x, int y, int z) {
        if (large)
            super.setRealPosition(x - 9, y, z - 9);
        else
            super.setRealPosition(x - 4, y, z - 4);
    }

    @Override
    public void customSetup(Random rand) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        if (model.metadata.featureMetadata != null && model.featurePositions != null && model.featurePositions.length > 0) {
            DungeonModelFeature.setup(this, model, model.featurePositions, rotation, rand, model.metadata.featureMetadata, x, y, z);
        }
    }

    @Override
    public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                     ChunkPos chunkPosIn) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);

        Vec3i offset = DungeonModels.getOffset(modelID);
        BlockPos pos = new BlockPos(x + offset.getX(), y + offset.getY(), z + offset.getZ());

        buildRotated(model, worldIn, structureBoundingBoxIn, pos
                , Theme.get(theme),
                Theme.getSub(subTheme), Treasure.MODEL_TREASURE_TYPES.getOrDefault(modelID, Treasure.Type.DEFAULT),
                stage, rotation, false);

        entrances(worldIn, structureBoundingBoxIn, model);

        if (model.metadata.feature != null && featurePositions != null) {
            model.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
        }

        //buildBoundingBox(worldIn, boundingBox, Blocks.NETHER_BRICK_FENCE);

        if (Config.NO_SPAWNERS.get())
            spawnMobs(worldIn, this, model.width, model.length, new int[]{offset.getY()});
        return true;
    }

    @Override
    public void setupBoundingBox() {
        this.boundingBox = large ? new MutableBoundingBox(x, y, z, x + 26, y + 8, z + 26)
                : new MutableBoundingBox(x, y, z, x + 16, y + 8, z + 16);
    }

    @Override
    public int getType() {
        return 10;
    }

    @Override
    public boolean canConnect(Direction side) {
        return node.canConnect(side);
    }

    @Override
    public boolean hasChildPieces() {
        return true;
    }

    @Override
    public void addChildPieces(List<DungeonPiece> list, DungeonBuilder builder, int layer, Random rand) {
        if (large) {
            return;
        }

        if (sides[0]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.NORTH);
            connector.theme = theme;
            connector.subTheme = subTheme;
            connector.stage = stage;
            connector.modelID = connector.determineModel(builder, rand);
            connector.setRealPosition(x + 7, y, z - 5);
            connector.adjustPositionAndBounds();
            list.add(connector);
        }

        if (sides[1]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.EAST);
            connector.theme = theme;
            connector.subTheme = subTheme;
            connector.stage = stage;
            connector.modelID = connector.determineModel(builder, rand);
            connector.setRealPosition(x + 17, y, z + 7);
            connector.adjustPositionAndBounds();
            list.add(connector);
        }

        if (sides[2]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.SOUTH);
            connector.theme = theme;
            connector.subTheme = subTheme;
            connector.stage = stage;
            connector.modelID = connector.determineModel(builder, rand);
            connector.setRealPosition(x + 7, y, z + 17);
            connector.adjustPositionAndBounds();
            list.add(connector);
        }

        if (sides[3]) {
            DungeonNodeConnector connector = new DungeonNodeConnector();
            connector.rotation = Orientation.getOppositeRotationFromFacing(Direction.WEST);
            connector.theme = theme;
            connector.subTheme = subTheme;
            connector.stage = stage;
            connector.modelID = connector.determineModel(builder, rand);
            connector.setRealPosition(x - 5, y, z + 7);
            connector.adjustPositionAndBounds();
            list.add(connector);
        }
    }

    @Override
    public Tuple<Position2D, Position2D> getAlternativePath(Position2D current, Position2D end) {

        if (!current.hasFacing()) {
            throw new RuntimeException("The current Position needs to provide a facing.");
        }

        Position2D center = new Position2D(posX, posZ);

        return new Tuple<Position2D, Position2D>(center.shift(node.findClosest(current.facing), 1),
                center.shift(findExitToPosition(end), 1));
    }

    @Override
    public boolean hasAlternativePath() {
        return true;
    }

    private Direction findExitToPosition(Position2D pos) {

        if (pos.hasFacing())
            return node.findClosest(pos.facing);

        if (pos.x > posX) {
            if (pos.z > posZ)
                return Direction.SOUTH;
            else if (pos.z < posZ)
                return Direction.NORTH;
            else
                return Direction.EAST;
        } else if (pos.x < posX) {
            if (pos.z > posZ)
                return Direction.SOUTH;
            else if (pos.z < posZ)
                return Direction.NORTH;
            else
                return Direction.WEST;
        } else {
            if (pos.z > posZ)
                return Direction.SOUTH;
            else if (pos.z < posZ)
                return Direction.NORTH;
            else {
                DungeonCrawl.LOGGER.error("Invalid Position: {},{}", pos.x, pos.z);
                throw new RuntimeException("Invalid position: (" + pos.x + "/" + pos.z + ")");
            }
        }

    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putBoolean("large", large);
        tagCompound.putBoolean("lootRoom", lootRoom);
    }

}
