package xiroc.dungeoncrawl.dungeon.piece.room;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonSideRoom extends DungeonPiece {

    public DungeonSideRoom() {
        super(StructurePieceTypes.SIDE_ROOM);
    }

    public DungeonSideRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.SIDE_ROOM, p_i51343_2_);
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        return 0;
    }

    @Override
    public boolean func_225577_a_(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                  ChunkPos chunkPosIn) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        Vec3i offset = DungeonModels.getOffset(modelID);
        BlockPos pos = new BlockPos(x + offset.getX(), y + offset.getY(), z + offset.getZ());

        buildRotated(model, worldIn, structureBoundingBoxIn, pos,
                Theme.get(theme), Theme.getSub(subTheme), Treasure.MODEL_TREASURE_TYPES.getOrDefault(modelID, Treasure.Type.DEFAULT), stage, rotation, true);
        decorate(worldIn, pos, model.width, model.height, model.length, Theme.get(theme), structureBoundingBoxIn, model);

        if (Config.NO_SPAWNERS.get())
            spawnMobs(worldIn, this, model.width, model.length, new int[]{1});
        return true;
    }

    @Override
    public void setupBoundingBox() {
        Vec3i offset = DungeonModels.getOffset(modelID);
        this.boundingBox = new MutableBoundingBox(x + offset.getX(), y + offset.getY(),
                z + offset.getZ(), x + offset.getX() + 7, y + offset.getY() + 7, z + offset.getZ() + 7);
    }

    @Override
    public int getType() {
        return 9;
    }

}