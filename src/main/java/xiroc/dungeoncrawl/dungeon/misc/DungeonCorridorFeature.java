package xiroc.dungeoncrawl.dungeon.misc;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;

import java.util.HashMap;

public interface DungeonCorridorFeature {

    DungeonCorridorFeature CROPS = (corridor, world, pos, bounds, theme, subTheme, stage) -> {
        corridor.buildRotated(DungeonModels.CORRIDOR_CROPS, world, bounds, pos.position, theme, subTheme, Treasure.Type.DEFAULT, stage, pos.rotation, false);
    };

    DungeonCorridorFeature LIGHT = (corridor, world, pos, bounds, theme, subTheme, stage) -> {
        corridor.buildRotated(DungeonModels.CORRIDOR_LIGHT, world, bounds, pos.position, theme, subTheme, Treasure.Type.DEFAULT, stage, pos.rotation, false);
    };

    DungeonCorridorFeature STAIRS = (corridor, world, pos, bounds, theme, subTheme, stage) -> {
        corridor.setBlockState(world, DungeonBlocks.applyProperty(subTheme.stairs.get(), BlockStateProperties.HORIZONTAL_FACING,
                pos.direction.getOpposite()), pos.position.getX(), pos.position.getY(), pos.position.getZ(), bounds);
        BlockPos second = pos.position.offset(pos.direction, 2);
        corridor.setBlockState(world, DungeonBlocks.applyProperty(subTheme.stairs.get(), BlockStateProperties.FACING,
                pos.direction), second.getX(), second.getY(), second.getZ(), bounds);
    };

    DungeonCorridorFeature CHEST = (corridor, world, pos, bounds, theme, subTheme, stage) -> {
        corridor.buildRotated(DungeonModels.CORRIDOR_CHEST, world, bounds, pos.position, theme, subTheme, Treasure.Type.DEFAULT, stage, pos.rotation, false);
    };

    DungeonCorridorFeature SPAWNER = (corridor, world, pos, bounds, theme, subTheme, stage) -> {
        corridor.buildRotated(DungeonModels.CORRIDOR_SPAWNER, world, bounds, pos.position, theme, subTheme, Treasure.Type.DEFAULT, stage, pos.rotation, false);
    };

    DungeonCorridorFeature SECRET_ROOM_ENTRANCE = (corridor, world, pos, bounds, theme, subTheme, stage) -> {
        corridor.buildRotated(DungeonModels.SECRET_ROOM_ENTRANCE, world, bounds, pos.position, theme, subTheme, Treasure.Type.DEFAULT, stage, pos.rotation, false);
    };

    HashMap<Byte, DungeonCorridorFeature> FEATURES = new HashMap<>();

    static void load() {
        FEATURES.put((byte) 0, CROPS);
        FEATURES.put((byte) 1, LIGHT);
        FEATURES.put((byte) 2, STAIRS);
        FEATURES.put((byte) 3, CHEST);
        FEATURES.put((byte) 4, SPAWNER);
        FEATURES.put((byte) 16, SECRET_ROOM_ENTRANCE);
    }

    static DirectionalBlockPos setup(DungeonCorridor corridor, DungeonModel model, DungeonModel.FeaturePosition featurePosition, int feature) {
        switch (feature) {
            case 0: {
                boolean otherSide = isOnOtherSide(featurePosition, model, corridor.rotation);
                switch (corridor.rotation) {
                    case NONE:
                    case CLOCKWISE_90: {
                        BlockPos pos = featurePosition.blockPos(corridor.x, corridor.y, corridor.z).offset(featurePosition.facing).offset(Direction.DOWN);
                        return new DirectionalBlockPos(pos, featurePosition.facing, getRotation(featurePosition.facing, otherSide));
                    }
                    case CLOCKWISE_180: {
                        BlockPos pos = featurePosition.blockPos(corridor.x - 2, corridor.y, corridor.z).offset(featurePosition.facing).offset(Direction.DOWN);
                        return new DirectionalBlockPos(pos, featurePosition.facing, getRotation(featurePosition.facing, otherSide));
                    }
                    case COUNTERCLOCKWISE_90: {
                        BlockPos pos = featurePosition.blockPos(corridor.x, corridor.y, corridor.z - 2).offset(featurePosition.facing).offset(Direction.DOWN);
                        return new DirectionalBlockPos(pos, featurePosition.facing, getRotation(featurePosition.facing, otherSide));
                    }
                }
            }
            case 1: {
                boolean otherSide = isOnOtherSide(featurePosition, model, corridor.rotation);
                switch (corridor.rotation) {
                    case NONE:
                    case CLOCKWISE_90: {
                        BlockPos pos = featurePosition.blockPos(corridor.x, corridor.y, corridor.z).offset(featurePosition.facing).offset(Direction.DOWN);
                        return new DirectionalBlockPos(pos, featurePosition.facing, getRotation(featurePosition.facing, otherSide));
                    }
                    case CLOCKWISE_180: {
                        BlockPos pos = featurePosition.blockPos(corridor.x - 2, corridor.y, corridor.z).offset(featurePosition.facing).offset(Direction.DOWN);
                        return new DirectionalBlockPos(pos, featurePosition.facing, getRotation(featurePosition.facing, otherSide));
                    }
                    case COUNTERCLOCKWISE_90: {
                        BlockPos pos = featurePosition.blockPos(corridor.x, corridor.y, corridor.z - 2).offset(featurePosition.facing).offset(Direction.DOWN);
                        return new DirectionalBlockPos(pos, featurePosition.facing, getRotation(featurePosition.facing, otherSide));
                    }
                }

            }
            case 2: {
                switch (corridor.rotation) {
                    case NONE:
                        return featurePosition.directionalBlockPos(corridor.x, corridor.y, corridor.z, Direction.EAST);
                    case CLOCKWISE_90:
                        return featurePosition.directionalBlockPos(corridor.x, corridor.y, corridor.z, Direction.SOUTH);
                    case CLOCKWISE_180:
                        return featurePosition.directionalBlockPos(corridor.x - 2, corridor.y, corridor.z, Direction.EAST);
                    case COUNTERCLOCKWISE_90:
                        return featurePosition.directionalBlockPos(corridor.x, corridor.y, corridor.z - 2, Direction.SOUTH);
                }
                return null;
            }
            case 3:
            case 4:
            case 16:
                boolean otherSide = isOnOtherSide(featurePosition, model, corridor.rotation);
                switch (corridor.rotation) {
                    case NONE:
                    case CLOCKWISE_90: {
                        BlockPos pos = featurePosition.blockPos(corridor.x, corridor.y, corridor.z);
                        return new DirectionalBlockPos(pos, featurePosition.facing, getRotation(featurePosition.facing, otherSide));
                    }
                    case CLOCKWISE_180: {
                        BlockPos pos = featurePosition.blockPos(corridor.x - 2, corridor.y, corridor.z);
                        return new DirectionalBlockPos(pos, featurePosition.facing, getRotation(featurePosition.facing, otherSide));
                    }
                    case COUNTERCLOCKWISE_90: {
                        BlockPos pos = featurePosition.blockPos(corridor.x, corridor.y, corridor.z - 2);
                        return new DirectionalBlockPos(pos, featurePosition.facing, getRotation(featurePosition.facing, otherSide));
                    }
                }
            default:
                return null;
        }
    }

    static void setupBounds(DungeonCorridor corridor, DungeonModel model, MutableBoundingBox bounds, BlockPos pos,
                            int feature) {
        switch (feature) {
            case 0:
            case 1:
            case 16:
                expandBounds(bounds, model, pos, corridor.rotation);
        }
    }

    static void expandBounds(MutableBoundingBox base, DungeonModel model, BlockPos pos, Rotation rotation) {
        boolean ew = rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180;
        base.expandTo(new MutableBoundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + (ew ? model.width : model.length),
                pos.getY() + model.height, pos.getZ() + (ew ? model.length : model.width)));
    }

    static boolean isOnOtherSide(DungeonModel.FeaturePosition position, DungeonModel model, Rotation rotation) {
        switch (rotation) {
            case NONE:
                //DungeonCrawl.LOGGER.debug("NONE: {} ({} >= {})", position.position.getZ() >= model.length / 2, position.position.getZ(), model.length / 2);
                return position.position.getZ() >= model.length / 2;
            case CLOCKWISE_180:
                //DungeonCrawl.LOGGER.debug("180: {} ({} <= {})", position.position.getZ() <= model.length / 2, position.position.getZ(), model.length / 2);
                return position.position.getZ() <= model.length / 2;
            case CLOCKWISE_90:
                //DungeonCrawl.LOGGER.debug("90: {} ({} >= {})", position.position.getX() >= model.width / 2, position.position.getX(), model.width / 2);
                return position.position.getX() >= model.width / 2;
            case COUNTERCLOCKWISE_90:
                //DungeonCrawl.LOGGER.debug("90CCW: {} ({} <= {})", position.position.getX() <= model.length / 2, position.position.getX(), model.width / 2);
                return position.position.getX() <= model.width / 2;
        }
        return false;
    }

    static Rotation getRotation(Direction direction, boolean otherSide) {
        switch (direction) {
            case EAST:
                return otherSide ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
            case SOUTH:
                return otherSide ? Rotation.NONE : Rotation.CLOCKWISE_180;
            case WEST:
                return otherSide ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
            default:
                return otherSide ? Rotation.CLOCKWISE_180 : Rotation.NONE;
        }
    }

    void build(DungeonCorridor piece, IWorld world, DirectionalBlockPos pos, MutableBoundingBox bounds, Theme
            theme, Theme.SubTheme subTheme, int stage);

}
