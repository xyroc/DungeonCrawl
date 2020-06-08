package xiroc.dungeoncrawl.dungeon.block;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.dungeon.block.BlockRegistry.TupleFloatBlock;
import xiroc.dungeoncrawl.util.IBlockStateProvider;
import xiroc.dungeoncrawl.util.IRandom;

public class WeightedRandomBlock implements IRandom<BlockState>, IBlockStateProvider {

	public static final Random RANDOM = new Random();

	private int totalWeight;
	private TupleFloatBlock[] blocks;

	private WeightedRandomBlock() {}

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
