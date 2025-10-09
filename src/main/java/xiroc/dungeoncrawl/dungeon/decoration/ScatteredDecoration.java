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

package xiroc.dungeoncrawl.dungeon.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record ScatteredDecoration(BlockStateProvider blockStateProvider, float chance) implements DungeonDecoration {
    public static final MapCodec<ScatteredDecoration> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            BlockStateProvider.CODEC.fieldOf("block").forGetter(ScatteredDecoration::blockStateProvider),
            Codec.FLOAT.fieldOf("chance").forGetter(ScatteredDecoration::chance)
    ).apply(builder, ScatteredDecoration::new));

    @Override
    public void decorate(LevelAccessor level, BoundingBox workingArea, DungeonWorldGenContext worldGenContext, RandomSource random) {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = workingArea.minX(); x < workingArea.maxX(); x++) {
            for (int y = workingArea.minY(); y < workingArea.maxY(); y++) {
                for (int z = workingArea.minZ(); z < workingArea.maxZ(); z++) {
                    cursor.set(x, y, z);
                    if (WorldEditor.Unsafe.isBlockProtected(level, cursor)
                            || !level.isEmptyBlock(cursor)
                            || random.nextFloat() >= chance) {
                        continue;
                    }

                    cursor.set(x, y, z - 1);
                    final boolean supportedNorth = workingArea.isInside(cursor) && level.getBlockState(cursor).isFaceSturdy(level, cursor, Direction.SOUTH);
                    cursor.set(x + 1, y, z);
                    final boolean supportedEast = workingArea.isInside(cursor) && level.getBlockState(cursor).isFaceSturdy(level, cursor, Direction.WEST);
                    cursor.set(x, y, z + 1);
                    final boolean supportedSouth = workingArea.isInside(cursor) && level.getBlockState(cursor).isFaceSturdy(level, cursor, Direction.NORTH);
                    cursor.set(x - 1, y, z);
                    final boolean supportedWest = workingArea.isInside(cursor) && level.getBlockState(cursor).isFaceSturdy(level, cursor, Direction.EAST);
                    cursor.set(x, y + 1, z);
                    final boolean supportedUp = workingArea.isInside(cursor) && level.getBlockState(cursor).isFaceSturdy(level, cursor, Direction.DOWN);
                    cursor.set(x, y - 1, z);
                    final boolean supportedDown = workingArea.isInside(cursor) && level.getBlockState(cursor).isFaceSturdy(level, cursor, Direction.UP);
                    cursor.set(x, y, z);

                    if (supportedNorth || supportedEast || supportedSouth || supportedWest || supportedUp) {
                        BlockState state = blockStateProvider.get(cursor, random);
                        state = DungeonBlocks.applyProperty(state, BlockStateProperties.NORTH, supportedNorth);
                        state = DungeonBlocks.applyProperty(state, BlockStateProperties.EAST, supportedEast);
                        state = DungeonBlocks.applyProperty(state, BlockStateProperties.SOUTH, supportedSouth);
                        state = DungeonBlocks.applyProperty(state, BlockStateProperties.WEST, supportedWest);
                        state = DungeonBlocks.applyProperty(state, BlockStateProperties.UP, supportedUp);
                        state = DungeonBlocks.applyProperty(state, BlockStateProperties.DOWN, supportedDown);
                        level.setBlock(cursor, state, 2);
                    }
                }
            }
        }
    }

    @Override
    public MapCodec<? extends DungeonDecoration> type() {
        return CODEC;
    }
}