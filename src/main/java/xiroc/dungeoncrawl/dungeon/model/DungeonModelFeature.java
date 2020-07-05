package xiroc.dungeoncrawl.dungeon.model;

import com.google.gson.JsonObject;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

public interface DungeonModelFeature {

    DungeonModelFeature CHESTS = (world, rand, pos, positions, bounds, theme, subTheme, stage) -> {
        for (DirectionalBlockPos position : positions) {
            if (bounds.isVecInside(position.position)) {
                IBlockPlacementHandler.CHEST.placeBlock(world, DungeonBlocks.CHEST.with(BlockStateProperties.HORIZONTAL_FACING, position.direction),
                        position.position, rand, Treasure.Type.DEFAULT, theme, stage);
            }
        }
    };

    DungeonModelFeature CATACOMB = (world, rand, pos, positions, bounds, theme, subTheme, stage) -> {

        for (DirectionalBlockPos position : positions) {
            if (bounds.isVecInside(position.position)) {
                IBlockPlacementHandler.CHEST.placeBlock(world, DungeonBlocks.CHEST.with(BlockStateProperties.HORIZONTAL_FACING, position.direction),
                        position.position, rand, Treasure.Type.CATACOMB, theme, stage);
            }
            BlockPos spawner = position.position.offset(position.direction);
            if (bounds.isVecInside(spawner)) {
                IBlockPlacementHandler.SPAWNER.placeBlock(world, DungeonBlocks.SPAWNER, spawner, rand, null, theme, stage);
            }
        }

    };

    void build(IWorld world, Random rand, BlockPos pos, DirectionalBlockPos[] positions, MutableBoundingBox bounds, int theme, int subTheme, int stage);

    /**
     * An improvised way to generate an array with a predetermined amount of random feature positions while iterating through
     * the position list exactly once, generating only one random number per iteration and not using an arraylist.
     * The checkArray does the trick for this: It is an array of integers with the size of the featurePositions array and
     * starts off with every single value being equal to its position in the array (checkArray[x] == x). Each iteration
     * through the featurePositions array, one random number gets generated. Then, that number gets changed to the value in the checkArray
     * at itself as the position as long as it is not equal to it. This ensures that the resulting number is unique by either
     * confirming that it is indeed unique or changing it to the next unique number if it is not. After that, the value
     * in the checkArray at the position of the final unique number gets increased by one and wrapped around the checkArray
     * size if necessary.
     */
    static void setup(DungeonPiece piece, DungeonModel model, DungeonModel.FeaturePosition[] featurePositions, Rotation rotation, Random rand, Metadata metadata, int x, int y, int z) {
        piece.featurePositions = new DirectionalBlockPos[new RandomValueRange(metadata.min, Math.min(metadata.max, featurePositions.length))
                .generateInt(rand)];

        int[] checkArray = new int[featurePositions.length];
        for (int i = 0; i < checkArray.length; i++) {
            checkArray[i] = i;
        }

        for (int i = 0; i < piece.featurePositions.length; i++) {
            int a = rand.nextInt(featurePositions.length);
            while (checkArray[a] != a) {
                a = checkArray[a];
            }
            checkArray[a] = (a + 1) % checkArray.length;

            piece.featurePositions[i] = featurePositions[a].directionalBlockPos(x, y, z, rotation, model);
        }
    }

    static DungeonModelFeature getFromName(String name) {
        if (name.equals("chests")) {
            return CHESTS;
        }
        if (name.equals("catacomb")) {
            return CATACOMB;
        }
        throw new IllegalArgumentException("Unknown feature " + name);
    }

    class Metadata {

        public final int min, max;

        public Metadata(JsonObject object) {
            this.min = object.get("min").getAsInt();
            this.max = object.get("max").getAsInt();
        }

    }

}
