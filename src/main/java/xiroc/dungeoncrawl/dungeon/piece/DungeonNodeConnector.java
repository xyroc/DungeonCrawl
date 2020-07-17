package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

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
        DungeonModel model = DungeonModels.MODELS.get(modelID);

        BlockPos pos = new BlockPos(x, y, z);

        buildRotated(model, worldIn, structureBoundingBoxIn, pos, Theme.get(theme),
                Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, false);
        return true;
    }

    @Override
    public int getType() {
        return 11;
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        return DungeonModels.ModelCategory.get(DungeonModels.ModelCategory.NODE_CONNECTOR,
                DungeonModels.ModelCategory.getCategoryForStage(stage)).roll(rand);
    }

    public void adjustPositionAndBounds() {
        DungeonModel model = DungeonModels.MODELS.get(modelID);

        if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
            int minZ = z - (model.length - 3) / 2;
            this.boundingBox = new MutableBoundingBox(x, y, minZ, x + 4, y + model.height - 1,
                    minZ + model.length - 1);
        } else {
            int minX = x - (model.length - 3) / 2;
            this.boundingBox = new MutableBoundingBox(minX, y, z, minX + model.length - 1,
                    y + model.height - 1, z + 4);
        }

        setRealPosition(this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ);
    }

    @Override
    public void setupBoundingBox() {
        DungeonModel model = DungeonModels.MODELS.get(modelID);

        if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
            this.boundingBox = new MutableBoundingBox(x, y, z, x + 4, y + model.height - 1,
                    z + model.length - 1);
        } else {
            this.boundingBox = new MutableBoundingBox(x, y, z, x + model.length - 1,
                    y + model.height - 1, z + 4);
        }

    }

}
