package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import xiroc.dungeoncrawl.DungeonCrawl;

public class BlockStateIndex {
	
	static {
		BlockState state = Blocks.TRIPWIRE.getDefaultState();
		DungeonCrawl.LOGGER.info(state.toString());
	}

}
