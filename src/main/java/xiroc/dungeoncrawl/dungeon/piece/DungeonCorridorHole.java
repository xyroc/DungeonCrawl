package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonCorridorHole extends DungeonPiece {

    public DungeonCorridorHole(TemplateManager p_i51343_1_, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.HOLE, p_i51343_2_);
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        return 0;
    }

    @Override
    public boolean func_225577_a_(IWorld worldIn, ChunkGenerator<?> chunkGen, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                  ChunkPos p_74875_4_) {
        build(DungeonModels.CORRIDOR, worldIn,
                structureBoundingBoxIn, new BlockPos(x, y - 15, z), Theme.get(theme), Theme.getSub(subTheme),
                Treasure.Type.DEFAULT, stage, true);
        addWalls(this, worldIn, structureBoundingBoxIn, theme);
        if (theme == 3 && getBlocks(worldIn, Blocks.WATER, x, y - 16, z, 8, 8) > 5)
            addColumns(this, worldIn, structureBoundingBoxIn, 16, theme);
        return true;
    }

    @Override
    public void setupBoundingBox() {
        this.boundingBox = new MutableBoundingBox(x, y, z, x + 7, y + 7, z + 7);
    }

    @Override
    public int getType() {
        return 2;
    }

}