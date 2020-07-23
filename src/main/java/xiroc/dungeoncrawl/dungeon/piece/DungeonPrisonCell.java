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
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonPrisonCell extends DungeonPiece {

    public DungeonPrisonCell() {
        super(StructurePieceTypes.PRISONER_CELL);
    }

    public DungeonPrisonCell(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.PRISONER_CELL, nbt);
    }

    @Override
    public boolean func_230383_a_(ISeedReader worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {


        buildRotated(DungeonModels.MODELS.get(modelID), worldIn, structureBoundingBoxIn, new BlockPos(x, y, z),
                Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, false);

        return true;
    }

    @Override
    public int getType() {
        return 12;
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        return DungeonModels.PRISON_CELL.id;
    }

    @Override
    public void setupBoundingBox() {
        switch (rotation) {
            case NONE:
            case CLOCKWISE_180:
                this.boundingBox = new MutableBoundingBox(x, y, z, x + 3, y + 4, z + 4);
                return;
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                this.boundingBox = new MutableBoundingBox(x, y, z, x + 4, y + 4, z + 3);
                return;
        }
    }

}
