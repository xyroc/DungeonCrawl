package xiroc.dungeoncrawl.dungeon.decoration;

import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

public class VineDecoration implements IDungeonDecoration {

    @Override
    public void decorate(DungeonModel model, IWorld world, BlockPos pos, int width, int height, int length, MutableBoundingBox boundingBox, DungeonPiece piece, int stage) {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 1; z < length - 1; z++) {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (world.isAirBlock(currentPos) && boundingBox.isVecInside(currentPos)) {
                        BlockPos north = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z - 1);
                        BlockPos east = new BlockPos(north.getX() + 1, north.getY(), pos.getZ() + z);
                        BlockPos south = new BlockPos(north.getX(), north.getY(), east.getZ() + 1);
                        BlockPos west = new BlockPos(north.getX() - 1, north.getY(), east.getZ());
                        BlockPos up = new BlockPos(north.getX(), north.getY() + 1, east.getZ());

                        boolean _north = boundingBox.isVecInside(north) && north.getZ() >= 1 && world.getBlockState(north).isNormalCube(world, north) && !world.isAirBlock(north);
                        boolean _east = boundingBox.isVecInside(east) && east.getX() < model.width - 1 && world.getBlockState(east).isNormalCube(world, east) && !world.isAirBlock(east);
                        boolean _south = boundingBox.isVecInside(south) && south.getZ() < model.length - 1 && world.getBlockState(south).isNormalCube(world, south) && !world.isAirBlock(south);
                        boolean _west = boundingBox.isVecInside(west) && west.getX() >= 1 && world.getBlockState(west).isNormalCube(world, east) && !world.isAirBlock(west);
                        boolean _up = boundingBox.isVecInside(up) && world.getBlockState(up).isNormalCube(world, up) && !world.isAirBlock(up);


                        if ((_north || _east || _south || _west || _up) && WeightedRandomBlock.RANDOM.nextFloat() < 0.35F) {
                            BlockPos p = new BlockPos(north.getX(), north.getY(), east.getZ());
                            world.setBlockState(p, Blocks.VINE.getDefaultState().with(BlockStateProperties.NORTH, _north)
                                    .with(BlockStateProperties.EAST, _east).with(BlockStateProperties.SOUTH, _south)
                                    .with(BlockStateProperties.WEST, _west).with(BlockStateProperties.UP, _up), 2);
                        }
                    }
                }
            }
        }
    }
}