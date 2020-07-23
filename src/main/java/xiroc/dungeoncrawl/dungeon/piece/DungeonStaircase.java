package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
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
    public boolean func_230383_a_(ISeedReader worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {

        DungeonModel model = DungeonModels.STAIRCASE;
        Theme buildTheme = Theme.get(theme);
        build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), buildTheme, Theme.getSub(subTheme),
                Treasure.Type.DEFAULT, stage, true);
        return true;
    }


    @Override
    public int getType() {
        return 13;
    }

}
