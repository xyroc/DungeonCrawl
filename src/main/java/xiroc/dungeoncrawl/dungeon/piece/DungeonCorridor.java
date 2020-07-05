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
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.RandomDungeonModel;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonCorridor extends DungeonPiece {

    public int specialType;

    public DungeonCorridor(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.CORRIDOR, p_i51343_2_);
        specialType = p_i51343_2_.getInt("specialType");
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        if (connectedSides == 2) {
            //                case 1:
            ////				return DungeonModels.CORRIDOR_FIRE;
            //                    return DungeonModels.CORRIDOR.id;
            //                case 2:
            ////				return DungeonModels.CORRIDOR_GRASS;
            //                    return DungeonModels.CORRIDOR.id;
            if (sides[0] && sides[2] || sides[1] && sides[3]) {
                //return RandomDungeonModel.CORRIDOR_STRAIGHT.roll(rand).id;
                return DungeonModels.ModelCategory.get(DungeonModels.ModelCategory.CORRIDOR, DungeonModels.ModelCategory.getCategoryForStage(stage)).roll(rand);
            }
        }
        return DungeonModels.ModelCategory.get(DungeonModels.ModelCategory.CORRIDOR_LINKER,
                DungeonModels.ModelCategory.getCategoryForStage(stage)).roll(rand);
    }

    @Override
    public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                     ChunkPos p_74875_4_) {

        DungeonModel model = DungeonModels.MODELS.get(modelID);

        if (model == null) {
            DungeonCrawl.LOGGER.error("Corridor model is null.");
            return false;
        }


        boolean ew = rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180;

        buildRotated(model, worldIn, structureBoundingBoxIn,
                new BlockPos(ew ? x : x + (9 - model.length) / 2, y, ew ? z + (9 - model.length) / 2 : z),
                Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, false);

        if (connectedSides > 2) {
            entrances(worldIn, structureBoundingBoxIn, model);
        }

        if (Config.NO_SPAWNERS.get())
            spawnMobs(worldIn, this, model.width, model.length, new int[]{1});
        return true;
    }

    @Override
    public void setupBoundingBox() {
        this.boundingBox = new MutableBoundingBox(x, y, z, x + 8, y + 8, z + 8);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putInt("specialType", specialType);
    }

    public boolean isStraight() {
        return sides[0] && sides[2] || sides[1] && sides[3];
    }

}
