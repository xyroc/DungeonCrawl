package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
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
    public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
        return true;
    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound) {
    }
}
