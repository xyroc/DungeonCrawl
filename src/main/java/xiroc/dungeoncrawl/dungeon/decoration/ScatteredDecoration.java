package xiroc.dungeoncrawl.dungeon.decoration;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class ScatteredDecoration implements IDungeonDecoration {

    private final IBlockStateProvider blockStateProvider;
    private final float chance;

    public ScatteredDecoration(BlockState state, float chance) {
        this(() -> state, chance);
    }

    public ScatteredDecoration(IBlockStateProvider blockStateProvider, float chance) {
        this.blockStateProvider = blockStateProvider;
        this.chance = chance;
    }

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
                        BlockPos down = new BlockPos(north.getX(), north.getY() - 1, east.getZ());

                        if (boundingBox.isVecInside(north) && world.isAirBlock(north) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                            world.setBlockState(north, blockStateProvider.get(), 2);
                        }

                        if (boundingBox.isVecInside(east) && world.isAirBlock(east) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                            world.setBlockState(east, blockStateProvider.get(), 2);
                        }

                        if (boundingBox.isVecInside(south) && world.isAirBlock(south) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                            world.setBlockState(south, blockStateProvider.get(), 2);
                        }

                        if (boundingBox.isVecInside(west) && world.isAirBlock(west) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                            world.setBlockState(west, blockStateProvider.get(), 2);
                        }

                        if (boundingBox.isVecInside(down) && world.isAirBlock(down) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                            world.setBlockState(down, blockStateProvider.get(), 2);
                        }
                    }
                }
            }
        }
    }

}
