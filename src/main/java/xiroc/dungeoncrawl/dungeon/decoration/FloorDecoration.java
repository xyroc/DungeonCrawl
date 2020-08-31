package xiroc.dungeoncrawl.dungeon.decoration;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.dungeon.model.PlacementBehaviour;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class FloorDecoration implements IDungeonDecoration {

    private final float chance;

    private final IBlockStateProvider blockStateProvider;

    public FloorDecoration(BlockState state, float chance) {
        this.blockStateProvider = () -> state;
        this.chance = chance;
    }

    public FloorDecoration(IBlockStateProvider blockStateProvider, float chance) {
        this.blockStateProvider = blockStateProvider;
        this.chance = chance;
    }

    @Override
    public void decorate(DungeonModel model, IWorld world, BlockPos pos, int width, int height, int length, MutableBoundingBox boundingBox, DungeonPiece piece, int stage) {

        for (int x = 0; x < model.width; x++) {
            for (int z = 0; z < model.length; z++) {
                for (int y = 0; y < model.height; y++) {
                    if (model.model[x][y][z] != null && model.model[x][y][z].type == DungeonModelBlockType.FLOOR && y < model.height - 1 && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                        piece.setBlockState(blockStateProvider.get(), world, boundingBox, null, pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0, 0, PlacementBehaviour.NON_SOLID);
                    }
                }
            }
        }

    }

}
