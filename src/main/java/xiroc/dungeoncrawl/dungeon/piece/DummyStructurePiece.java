package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;

import java.util.Random;

/*
A dummy piece that is used to handle old structure pieces.
 */

public class DummyStructurePiece extends StructurePiece {

    public DummyStructurePiece(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.DUMMY, nbt);
    }

    @Override
    public boolean func_230383_a_(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        return true;
    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound) {
    }
}
