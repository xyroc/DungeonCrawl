package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonNodeConnector extends DungeonPiece {

	public DungeonNodeConnector() {
		super(StructurePieceTypes.NODE_CONNECTOR);
	}

	public DungeonNodeConnector(TemplateManager manager, CompoundNBT nbt) {
		super(StructurePieceTypes.NODE_CONNECTOR, nbt);
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos chunkPosIn) {

		DungeonModel model = DungeonModels.MAP.get(modelID);

		DungeonCrawl.LOGGER.info("Node Model {}, Rotation is {}", modelID, rotation);

		buildRotated(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme),
				Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, false);

		return true;
	}

	@Override
	public int getType() {
		return 11;
	}

	@Override
	public int determineModel(DungeonBuilder builder, Random rand) {
		return DungeonModels.NODE_CONNECTORS[rand.nextInt(DungeonModels.NODE_CONNECTORS.length)].id;
	}

	public void firstTimeBoundingBoxSetup() {
		DungeonModel model = DungeonModels.MAP.get(modelID);

		if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
			this.boundingBox = new MutableBoundingBox(x, y, z - (model.length - 3) / 2, x + 4, y + model.height - 1,
					z + model.length - 1);
		} else {
			this.boundingBox = new MutableBoundingBox(x - (model.length - 3) / 2, y, z, x + +model.length - 1,
					y + model.height - 1, z + 4);
		}
		
		setRealPosition(this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ);
	}

	@Override
	public void setupBoundingBox() {
		DungeonModel model = DungeonModels.MAP.get(modelID);

		if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
			this.boundingBox = new MutableBoundingBox(x, y, z, x + 4, y + model.height - 1,
					z + model.length - 1);
		} else {
			this.boundingBox = new MutableBoundingBox(x, y, z, x + +model.length - 1,
					y + model.height - 1, z + 4);
		}


		DungeonCrawl.LOGGER.debug("{}, {}", x, z);
	}

}
