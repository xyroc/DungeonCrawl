package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.lang.reflect.Type;
import java.util.Arrays;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels.ModelCategory;

public class DungeonModel {

	public Integer id;
	public int width, height, length;

	public DungeonModelBlock[][][] model;

	public FeaturePosition[] featurePositions;

	public Metadata metadata;

	public DungeonModel(DungeonModelBlock[][][] model, FeaturePosition[] featurePositions) {
		this(model, featurePositions, null);
	}

	public DungeonModel(DungeonModelBlock[][][] model, FeaturePosition[] featurePositions, Metadata metadata) {
		this.model = model;
		this.width = model.length;
		this.height = model[0].length;
		this.length = model[0][0].length;
		this.featurePositions = featurePositions;
		this.metadata = metadata;
	}

	public DungeonModel setId(int id) {
		DungeonModels.MAP.put(id, this);
		this.id = id;
		return this;
	}

	public DungeonModel loadMetadata(Metadata metadata) {
		this.metadata = metadata;

		this.id = metadata.id;

		DungeonModels.MAP.put(id, this);

		metadata.type.members.add(this);

		if (metadata.size != null) {
			metadata.size.members.add(this);
		}

		for (int stage : metadata.stages) {
			ModelCategory.getCategoryForStage(stage - 1).members.add(this);
		}

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

	@Override
	public String toString() {
		return "{" + id + (metadata != null ? ", " + (metadata.type.toString() + ", " + Arrays.toString(metadata.stages)) + "}" : "}");
	}

	public static class FeaturePosition {

		public Vec3i position;
		public Direction facing;

		public FeaturePosition(int x, int y, int z) {
			this.position = new Vec3i(x, y, z);
		}

		public FeaturePosition(int x, int y, int z, Direction facing) {
			this.position = new Vec3i(x, y, z);
			this.facing = facing;
		}

		public BlockPos blockPos(int x, int y, int z) {
			return new BlockPos(x + position.getX(), y + position.getY(), z + position.getZ());
		}

	}

	public static class Metadata {

		public ModelCategory type, size;

		public int id;

		public int[] stages, weights;

		private Metadata(ModelCategory type, ModelCategory size, int id, int[] stages, int[] weights) {
			this.type = type;
			this.size = size;
			this.id = id;
			this.stages = stages;
			this.weights = weights;
		}

		public static class Deserializer implements JsonDeserializer<Metadata> {

			@Override
			public Metadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {

				JsonObject object = json.getAsJsonObject();
				String modelType = object.get("modelType").getAsString();

				int id = object.get("id").getAsInt();

				JsonObject data = object.getAsJsonObject("data");

				int[] stages = DungeonCrawl.GSON.fromJson(data.get("stages"), int[].class);
				int[] weights = DungeonCrawl.GSON.fromJson(data.get("weights"), int[].class);

				switch (modelType) {
				case "NODE":
					ModelCategory size;
					String sizeString = data.get("size").getAsString(), typeString = data.get("type").getAsString();
					if (sizeString.equals("LARGE")) {
						size = ModelCategory.LARGE_NODE;
					} else if (sizeString.equals("NORMAL")) {
						size = ModelCategory.NORMAL_NODE;
					} else {
						throw new JsonParseException("Unknown node size \" " + sizeString + "\"");
					}
					return new Metadata(ModelCategory.valueOf("NODE_" + typeString), size, id, stages, weights);
				case "CORRIDOR":
					return new Metadata(ModelCategory.CORRIDOR, null, id, stages, weights);
				case "CORRIDOR_LINKER":
					return new Metadata(ModelCategory.CORRIDOR_LINKER, null, id, stages, weights);
				case "NODE_CONNECTOR":
					return new Metadata(ModelCategory.NODE_CONNECTOR, null, id, stages, weights);
				case "SIDE_ROOM":
					return new Metadata(ModelCategory.SIDE_ROOM, null, id, stages, weights);
				default:
					throw new JsonParseException("Unknown model type \"" + modelType + "\"");
				}

			}

		}

	}

}
