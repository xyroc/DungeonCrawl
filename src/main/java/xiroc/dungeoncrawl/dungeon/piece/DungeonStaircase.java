package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonStaircase extends DungeonPiece {

    public DungeonStaircase(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.STAIRCASE, p_i51343_2_);
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        return DungeonModels.STAIRCASE.id;
    }

    @Override
    public void setupBoundingBox() {
        this.boundingBox = new MutableBoundingBox(x, y, z, x + 4, y + 8, z + 4);
    }

    @Override
    public boolean func_225577_a_(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                  ChunkPos chunkPosIn) {
        DungeonModel model = DungeonModels.STAIRCASE;
        Theme buildTheme = Theme.get(theme);
        BlockPos pos = new BlockPos(x, y, z);

        build(model, worldIn, structureBoundingBoxIn, pos, buildTheme, Theme.getSub(subTheme),
                Treasure.Type.DEFAULT, stage, true);
        decorate(worldIn, pos, model.width, model.height, model.length, buildTheme, structureBoundingBoxIn, model);
        return true;
    }

    @Override
    public int getType() {
        return 13;
    }

}
