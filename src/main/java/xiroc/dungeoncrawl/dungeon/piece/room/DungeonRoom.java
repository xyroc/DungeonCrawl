package xiroc.dungeoncrawl.dungeon.piece.room;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelFeature;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

public class DungeonRoom extends DungeonPiece {

    public DungeonRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.ROOM, p_i51343_2_);
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        if (rand.nextFloat() < 0.4 || connectedSides < 2) {
            return DungeonModels.ModelCategory.get(DungeonModels.ModelCategory.ROOM, DungeonModels.ModelCategory.getCategoryForStage(stage)).roll(rand);
        }
        return DungeonModels.ModelCategory.get(DungeonModels.ModelCategory.CORRIDOR_LINKER,
                DungeonModels.ModelCategory.getCategoryForStage(stage)).roll(rand);
    }


    @Override
    public void customSetup(Random rand) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        if (model.metadata.featureMetadata != null && model.featurePositions != null && model.featurePositions.length > 0) {
            DungeonModelFeature.setup(this, model, model.featurePositions, rotation, rand, model.metadata.featureMetadata, x, y, z);
        }
    }

    @Override
    public boolean func_225577_a_(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                  ChunkPos p_74875_4_) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);

        if (model == null)
            return false;

        BlockPos pos = new BlockPos(x, y, z);

        build(model, worldIn, structureBoundingBoxIn, pos, Theme.get(theme), Theme.getSub(subTheme),
                Treasure.Type.DEFAULT, stage, false);

        entrances(worldIn, structureBoundingBoxIn, model);

        if (model.metadata.feature != null && featurePositions != null) {
            model.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
        }

//        if (featurePositions != null) {
//            DungeonCrawl.LOGGER.info("SPAWNER ROOM {} {} {} ({}) BOUNDS: [{} {} {} {} {} {}]", x, y, z, featurePositions.length,
//                    structureBoundingBoxIn.minX, structureBoundingBoxIn.minY, structureBoundingBoxIn.minZ,
//                    structureBoundingBoxIn.maxX, structureBoundingBoxIn.maxY, structureBoundingBoxIn.maxZ);
//            for (DirectionalBlockPos pos : featurePositions) {
//                DungeonCrawl.LOGGER.info("VEC ({} {} {}) INSIDE: {}", pos.position.getX(), pos.position.getY(), pos.position.getZ(), structureBoundingBoxIn.isVecInside(pos.position));
//                if (structureBoundingBoxIn.isVecInside(pos.position)) {
//                    IBlockPlacementHandler.getHandler(Blocks.CHEST).placeBlock(worldIn,
//                            Blocks.CHEST.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, pos.direction),
//                            pos.position, randomIn, Treasure.Type.DEFAULT, theme, stage);
//                }
//            }
//        }

        if (Config.NO_SPAWNERS.get())
            spawnMobs(worldIn, this, model.width, model.length, new int[]{0});
        return true;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
    }

    @Override
    public void setupBoundingBox() {
        this.boundingBox = new MutableBoundingBox(x, y, z, x + 8, y + 8, z + 8);
    }

    @Override
    public int getType() {
        return 8;
    }

}
