package xiroc.dungeoncrawl.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xiroc.dungeoncrawl.util.Position2D;

public class DungeonLayerMap {

	public int width, length;
	public List<Position2D> freePositions;

	public DungeonLayerMap(int width, int length) {
		this.width = width;
		this.length = length;
		this.freePositions = new ArrayList<Position2D>();
		;
		for (int x = 0; x < width; x++)
			for (int z = 0; z < length; z++)
				freePositions.add(new Position2D(x, z));
	}

	public Position2D getRandomFreePosition(Random rand) {
		Position2D pos = freePositions.get(rand.nextInt(freePositions.size()));
		freePositions.remove(pos);
		return pos;
	}

}
