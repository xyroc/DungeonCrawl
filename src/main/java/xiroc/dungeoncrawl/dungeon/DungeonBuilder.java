package xiroc.dungeoncrawl.dungeon;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegment;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.util.Position2D;

public class DungeonBuilder {

	public Random rand;
	public Position2D start;

	public DungeonLayer[] layers;

	public BlockPos startPos;

	public DungeonBuilder(IWorld world, ChunkPos pos, Random rand) {
		this.rand = rand;
		this.start = new Position2D(rand.nextInt(16), rand.nextInt(16));
		this.startPos = new BlockPos(pos.x * 16 + rand.nextInt(16), world.getChunkProvider().getChunkGenerator().getGroundHeight(), pos.z * 16 + rand.nextInt(16));
		this.layers = new DungeonLayer[world.getChunkProvider().getChunkGenerator().getSeaLevel() / 16];
		DungeonCrawl.LOGGER.info(this.layers.length + " layers");
		for (int i = 0; i < layers.length; i++) {
			this.layers[i] = new DungeonLayer(DungeonLayerType.NORMAL);
			this.layers[i].buildMap(rand, (i == 0) ? this.start : layers[i - 1].end, false);
		}
	}

	public List<DungeonPiece> build() {
		List<DungeonPiece> list = Lists.newArrayList();
		for (int i = 0; i < layers.length; i++) {
			list.addAll(buildLayer(layers[i], i, startPos));
		}
		return list;
	}

	public List<DungeonPiece> buildLayer(DungeonLayer layer, int lyr, BlockPos startPos) {
		List<DungeonPiece> list = Lists.newArrayList();
		for (int x = 0; x < layer.width; x++) {
			for (int z = 0; z < layer.length; z++) {
				if (layer.segments[x][z] != null) {
					layer.segments[x][z].setRealPosition(startPos.getX() + x * 8, startPos.getY() - lyr * 16, startPos.getZ() + z * 8);
					list.add(layer.segments[x][z]);
				}
			}
		}
		return list;
	}

	public static DungeonSegmentModel getModel(DungeonSegment segment) {
		boolean north = segment.sides[0];
		boolean east = segment.sides[1];
		boolean south = segment.sides[2];
		boolean west = segment.sides[3];
		switch (segment.type) {
		case START:
			switch (segment.connectedSegments) {
			case 1:
				return DungeonSegmentModelRegistry.STAIRS_BOTTOM_1;
			case 2:
				if (north && south || east && west)
					return DungeonSegmentModelRegistry.STAIRS_BOTTOM_2_1;
				return DungeonSegmentModelRegistry.STAIRS_BOTTOM_2_2;
			case 3:
				return DungeonSegmentModelRegistry.STAIRS_BOTTOM_3;
			case 4:
				return DungeonSegmentModelRegistry.STAIRS_BOTTOM_4;
			default:
				return null;
			}
		case CORRIDOR:
			switch (segment.connectedSegments) {
			case 2:
				if (north && south || east && west)
					return DungeonSegmentModelRegistry.CORRIDOR_EW;
				return DungeonSegmentModelRegistry.CORRIDOR_EW_TURN;
			case 3:
				return DungeonSegmentModelRegistry.CORRIDOR_EW_OPEN;
			case 4:
				return DungeonSegmentModelRegistry.CORRIDOR_EW_ALL_OPEN;
			default:
				return null;
			}
		case STAIRS:
			return DungeonSegmentModelRegistry.STAIRS;
		case ROOM:
			return null;
		case END:
			switch (segment.connectedSegments) {
			case 1:
				return DungeonSegmentModelRegistry.STAIRS_TOP_1;
			case 2:
				if (north && south || east && west)
					return DungeonSegmentModelRegistry.STAIRS_TOP_2_1;
				return DungeonSegmentModelRegistry.STAIRS_TOP_2_2;
			case 3:
				return DungeonSegmentModelRegistry.STAIRS_TOP_3;
			case 4:
				return DungeonSegmentModelRegistry.STAIRS_TOP_4;
			default:
				return null;
			}
		default:
			return null;
		}
	}

}
