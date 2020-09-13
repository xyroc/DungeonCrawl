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

package xiroc.dungeoncrawl.dungeon.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks.TupleFloatBlock;
import xiroc.dungeoncrawl.util.IBlockStateProvider;
import xiroc.dungeoncrawl.util.IRandom;

import java.util.Random;

public class WeightedRandomBlock implements IRandom<BlockState>, IBlockStateProvider {

    public static final Random RANDOM = new Random();

    private int totalWeight;
    private TupleFloatBlock[] blocks;

    private WeightedRandomBlock() {
    }

    public WeightedRandomBlock(Tuple<Integer, BlockState>[] entries) {
        int weight = 0;
        for (Tuple<Integer, BlockState> entry : entries)
            weight += entry.getA();
        this.totalWeight = weight;
        this.blocks = new TupleFloatBlock[entries.length];
        this.assign(entries);
    }

    public static WeightedRandomBlock of(Tuple<Integer, Block>[] entries) {
        WeightedRandomBlock block = new WeightedRandomBlock();
        int baseWeight = 0;
        for (Tuple<Integer, Block> entry : entries)
            baseWeight += entry.getA();
        block.totalWeight = baseWeight;
        block.blocks = new TupleFloatBlock[entries.length];

        float f = 0.0F;
        int i = 0;
        for (Tuple<Integer, Block> entry : entries) {
            float weight = (float) entry.getA() / (float) block.totalWeight;
            block.blocks[i] = new TupleFloatBlock(weight + f, entry.getB().getDefaultState());
            f += weight;
            i++;
        }

        return block;
    }

    private void assign(Tuple<Integer, BlockState>[] values) {
        float f = 0.0F;
        int i = 0;
        for (Tuple<Integer, BlockState> entry : values) {
            float weight = (float) entry.getA() / (float) totalWeight;
            blocks[i] = new TupleFloatBlock(weight + f, entry.getB());
            f += weight;
            i++;
        }
    }

    @Override
    public BlockState roll(Random rand) {
        float f = rand.nextFloat();
        for (TupleFloatBlock entry : blocks)
            if (entry.getA() >= f)
                return entry.getB();
        return null;
    }

    @Override
    public BlockState get() {
        return roll(RANDOM);
    }

}
