package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.util.math.Vec3i;

public class DungeonModel {

	public Integer id;
	public int width, height, length;
	
	public DungeonModelBlock[][][] model;
	public EntranceType entranceType;
	
	public Vec3i[] featurePositions;

	public DungeonModel(DungeonModelBlock[][][] model, EntranceType entranceType, Vec3i[] featurePositions) {
		this.model = model;
		this.width = model.length;
		this.height = model[0].length;
		this.length = model[0][0].length;
		this.entranceType = entranceType;
		this.featurePositions = featurePositions;
	}

	public DungeonModel setId(int id) {
		DungeonModels.MAP.put(id, this);
		this.id = id;
		return this;
	}

	public DungeonModel set(int id, DungeonModels.ModelCategory... categories) {
		DungeonModels.MAP.put(id, this);
		this.id = id;
		for (DungeonModels.ModelCategory category : categories)
			category.members.add(this);
		return this;
	}

	public DungeonModel build() {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				for (int z = 0; z < length; z++)
					if (model[x][y][z] != null && model[x][y][z].type == DungeonModelBlockType.OTHER)
						model[x][y][z].readResourceLocation();
		return this;
	}

	public enum EntranceType {

		OPEN, CLOSED;

	}

}
