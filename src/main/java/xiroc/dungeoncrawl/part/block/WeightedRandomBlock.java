package xiroc.dungeoncrawl.part.block;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.part.block.BlockRegistry.TupleFloatBlock;
import xiroc.dungeoncrawl.util.IBlockStateProvider;
import xiroc.dungeoncrawl.util.IRandom;

public class WeightedRandomBlock implements IRandom<BlockState>, IBlockStateProvider {

    private final int totalWeight;
    private final TupleFloatBlock[] map;

    public WeightedRandomBlock(Tuple<Integer, BlockState>[] entries) {
	int weight = 0;
	for (Tuple<Integer, BlockState> entry : entries)
	    weight += entry.getA();
	this.totalWeight = weight;
	this.map = new TupleFloatBlock[entries.length];
	this.assign(entries);
    }

    private void assign(Tuple<Integer, BlockState>[] values) {
	float f = 0.0F;
	int i = 0;
	for (Tuple<Integer, BlockState> entry : values) {
	    float weight = (float) entry.getA() / (float) totalWeight;
	    map[i] = new TupleFloatBlock(weight + f, entry.getB());
	    f += weight;
	    i++;
	}
    }

    @Override
    public BlockState roll(Random rand) {
	float f = rand.nextFloat();
	for (TupleFloatBlock entry : map)
	    if (entry.getA() >= f)
		return entry.getB();
	return null;
    }

    @Override
    public BlockState get() {
	return roll(new Random());
    }

}
