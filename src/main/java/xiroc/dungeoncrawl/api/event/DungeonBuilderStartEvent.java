package xiroc.dungeoncrawl.api.event;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.eventbus.api.Event;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker;

public class DungeonBuilderStartEvent extends Event {
	
	public final ChunkGenerator<?> chunkGen;
	public final int layers;
	public final BlockPos startPos;
	public int theme, subTheme;
	public DungeonStatTracker statTracker;
	
	public DungeonBuilderStartEvent(ChunkGenerator<?> chunkGen, BlockPos startPos, DungeonStatTracker statTracker, int layers) {
		this.chunkGen = chunkGen;
		this.startPos = startPos;
		this.statTracker = statTracker;
		this.layers = layers;
	}

}
