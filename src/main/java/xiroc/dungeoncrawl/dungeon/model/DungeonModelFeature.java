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

package xiroc.dungeoncrawl.dungeon.model;

import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public interface DungeonModelFeature {

    HashMap<String, DungeonModelFeature> FEATURES = new HashMap<>();

    DungeonModelFeature CHESTS = (world, context, rand, pos, positions, bounds, theme, subTheme, stage) -> {
        for (DirectionalBlockPos position : positions) {
            if (!DungeonBuilder.isBlockProtected(world, position.position, context)
                    && bounds.isVecInside(position.position)
                    && world.getBlockState(position.position.down()).isSolid()) {
                IBlockPlacementHandler.CHEST.place(world, DungeonBlocks.CHEST.with(BlockStateProperties.HORIZONTAL_FACING, position.direction),
                        position.position, rand, context, Treasure.Type.DEFAULT, theme, subTheme, stage);
            }
        }
    };

    DungeonModelFeature TNT_CHESTS = (world, context, rand, pos, positions, bounds, theme, subTheme, stage) -> {
        for (DirectionalBlockPos position : positions) {
            if (!DungeonBuilder.isBlockProtected(world, position.position, context)
                    && bounds.isVecInside(position.position)
                    && world.getBlockState(position.position.down()).isSolid()) {
                IBlockPlacementHandler.CHEST.place(world, Blocks.TRAPPED_CHEST.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING,
                        Orientation.RANDOM_HORIZONTAL_FACING.roll(rand)),
                        position.position, rand, context, Treasure.Type.DEFAULT, theme, subTheme, stage);
                BlockPos down = position.position.down(2);
                if (!DungeonBuilder.isBlockProtected(world, down, context) && !world.isAirBlock(down)) {
                    world.setBlockState(position.position.down(2), Blocks.TNT.getDefaultState(), 2);
                }
            }
        }
    };

    DungeonModelFeature SPAWNERS = (world, context, rand, pos, positions, bounds, theme, subTheme, stage) -> {
        if (Config.NO_SPAWNERS.get()) {
            return;
        }
        for (DirectionalBlockPos position : positions) {
            if (!DungeonBuilder.isBlockProtected(world, position.position, context)
                    && bounds.isVecInside(position.position)
                    && world.getBlockState(position.position.down()).isSolid()) {
                IBlockPlacementHandler.SPAWNER.place(world, DungeonBlocks.SPAWNER,
                        position.position, rand, context, null, theme, subTheme, stage);
            }
        }
    };

    DungeonModelFeature CATACOMB = (world, context, rand, pos, positions, bounds, theme, subTheme, stage) -> {

        for (DirectionalBlockPos position : positions) {
            if (!DungeonBuilder.isBlockProtected(world, position.position, context)
                    && bounds.isVecInside(position.position)
                    && world.getBlockState(position.position.down()).isSolid()) {
                IBlockPlacementHandler.CHEST.place(world, DungeonBlocks.CHEST.with(BlockStateProperties.HORIZONTAL_FACING, position.direction),
                        position.position, rand, context, Treasure.Type.CATACOMB, theme, subTheme, stage);
            }
            if (Config.NO_SPAWNERS.get()) {
                return;
            }

            BlockPos spawner = position.position.offset(position.direction);
            if (!DungeonBuilder.isBlockProtected(world, spawner, context)
                    && bounds.isVecInside(spawner)
                    && world.getBlockState(spawner.down()).isSolid()) {
                IBlockPlacementHandler.SPAWNER.place(world, DungeonBlocks.SPAWNER, spawner, rand, context, null, theme, subTheme, stage);
            }
        }
    };

    void build(IWorld world, PlacementContext context, Random rand, BlockPos pos, DirectionalBlockPos[] positions, MutableBoundingBox bounds, Theme theme, Theme.SubTheme subTheme, int stage);

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
    // TODO: just use a list ;)
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

    static void init() {
        FEATURES.put("chests", CHESTS);
        FEATURES.put("tnt_chests", TNT_CHESTS);
        FEATURES.put("spawners", SPAWNERS);
        FEATURES.put("catacomb", CATACOMB);
    }

    static DungeonModelFeature getFromName(String name) {
        DungeonModelFeature feature = FEATURES.get(name.toLowerCase(Locale.ROOT));
        if (feature != null) {
            return feature;
        }
        throw new IllegalArgumentException("Unknown model feature: " + name);
    }

    class Metadata {

        public final int min, max;

        public Metadata(JsonObject object) {
            this.min = object.get("min").getAsInt();
            this.max = object.get("max").getAsInt();
        }

    }

}
