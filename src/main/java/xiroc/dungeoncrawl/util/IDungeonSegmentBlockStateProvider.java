package xiroc.dungeoncrawl.util;

import java.util.Random;

import net.minecraft.block.BlockState;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelBlock;
import xiroc.dungeoncrawl.theme.Theme;

public interface IDungeonSegmentBlockStateProvider {
	
	BlockState get(DungeonSegmentModelBlock block, Theme theme, Random rand);

}
