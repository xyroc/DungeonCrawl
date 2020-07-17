package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import xiroc.dungeoncrawl.util.Position2D;

public class DungeonLayerMap {

    public int width, length;
    //	public List<Position2D> freePositions;
    public boolean[][] map;

    public DungeonLayerMap(int width, int length) {
        this.width = width;
        this.length = length;
//		this.freePositions = new ArrayList<Position2D>();
//		for (int x = 0; x < width; x++)
//			for (int z = 0; z < length; z++)
//				freePositions.add(new Position2D(x, z));
        this.map = new boolean[width][length];
    }

    public boolean isPositionFree(int x, int z) {
        return !this.map[x][z];
    }

//	public Position2D getRandomFreePosition(Random rand) {
//		if (freePositions.size() == 0)
//			return null;
//		Position2D pos = freePositions.get(rand.nextInt(freePositions.size()));
//		freePositions.remove(pos);
//		this.map[pos.x][pos.z] = true;
//		return pos;
//	}

    public boolean markPositionAsOccupied(Position2D pos) {
//		Iterator<Position2D> iterator = freePositions.iterator();
//		while (iterator.hasNext()) {
//			Position2D current = iterator.next();
//			if (pos.x == current.x && pos.z == current.z) {
//				this.map[current.x][current.z] = true;
//				iterator.remove();
//				return true;
//			}
//		}
//		return false;
        map[pos.x][pos.z] = true;
        return true;
    }

}
