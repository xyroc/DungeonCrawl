package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonStairs extends DungeonPiece {

    private static final BlockState IRON_BARS = Blocks.IRON_BARS.getDefaultState();

    public int stairType; // 0: bottom stairs, 1: top stairs

    public DungeonStairs(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.STAIRS, p_i51343_2_);
        this.stairType = p_i51343_2_.getInt("stairType");
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        switch (stairType) {
            case 0:
                return stage > 0 ? DungeonModels.STAIRS_BOTTOM_2.id : DungeonModels.STAIRS_BOTTOM.id;
            case 1:
                return DungeonModels.STAIRS_TOP.id;
            default:
                return 0;
        }
    }

    @Override
    public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                     ChunkPos p_74875_4_) {
        switch (stairType) {
            case 0: {
                DungeonModel model = DungeonModels.MODELS.get(modelID);
                build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme),
                        Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, false);

                ironBars(worldIn, structureBoundingBoxIn, model);

                if (x == -742 && y == 30) {
                    DungeonCrawl.LOGGER.debug("Stairs bottom Boundingbox: {}, {}, {} -> {}, {}, {}", boundingBox.minX,
                            boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                    DungeonCrawl.LOGGER.debug("north: {} east: {} south: {} west: {}", sides[0], sides[1], sides[2],
                            sides[3]);
                }

                //buildBoundingBox(worldIn, boundingBox, Blocks.PRISMARINE_BRICK_SLAB);

                return true;
            }
            case 1: {
                DungeonModel model = DungeonModels.MODELS.get(modelID);
                build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme),
                        Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, false);
//			DungeonCrawl.LOGGER.debug("Stairs top Boundingbox: {}, {}, {} -> {}, {}, {}", boundingBox.minX,
//					boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                entrances(worldIn, structureBoundingBoxIn, model);
                return true;
            }
            default:
                return true;
        }

    }

//	public void ironBars(IWorld world, MutableBoundingBox bounds, DungeonModel model) {
//		BlockState ironBars = Blocks.IRON_BARS.getDefaultState();
//
////		DungeonModelBlock stairs = new DungeonModelBlock(DungeonModelBlockType.STAIRS);
////		stairs.half = Half.TOP;
//
//		int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;
//
////		for (int i = 0; i < sides.length - 2; i++) {
////			switch (Direction.byHorizontalIndex(i + 2)) {
////			case EAST:
////				if (sides[i]) {
////					for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
////						for (int y0 = 1; y0 < 4; y0++)
////							setBlockState(world, ironBars, x + model.width - 1, y + y0, z + z0, bounds);
////				}
////				continue;
////			case NORTH:
////				if (sides[i]) {
////					for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
////						for (int y0 = 1; y0 < 4; y0++)
////							setBlockState(world, ironBars, x + x0, y + y0, z, bounds);
////				}
////				continue;
////			case SOUTH:
////				if (sides[i]) {
////					for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
////						for (int y0 = 1; y0 < 4; y0++)
////							setBlockState(world, ironBars, x + x0, y + y0, z + model.length - 1, bounds);
////				}
////				continue;
////			case WEST:
////				if (sides[i]) {
////					for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
////						for (int y0 = 1; y0 < 4; y0++)
////							setBlockState(world, ironBars, x, y + y0, z + z0, bounds);
////				}
////				continue;
////			default:
////				continue;
////
////			}
////		}
//
//		if (sides[0]) {
//			for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
//				for (int y0 = 1; y0 < 4; y0++)
//					setBlockState(world, ironBars, x + x0, y + y0, z, bounds);
//		}
//
//		if (sides[1]) {
//			for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
//				for (int y0 = 1; y0 < 4; y0++)
//					setBlockState(world, ironBars, x + model.width - 1, y + y0, z + z0, bounds);
//		}
//
//		if (sides[2]) {
//			for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
//				for (int y0 = 1; y0 < 4; y0++)
//					setBlockState(world, ironBars, x + x0, y + y0, z + model.length - 1, bounds);
//		}
//
//		if (sides[3]) {
//			for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
//				for (int y0 = 1; y0 < 4; y0++)
//					setBlockState(world, ironBars, x, y + y0, z + z0, bounds);
//		}
//
//	}

    public void ironBars(IWorld world, MutableBoundingBox bounds, DungeonModel model) {
        int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;

        if (sides[0]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x + x0, y + y0, z, bounds);
        }
        if (sides[1]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x + model.width - 1, y + y0, z + z0, bounds);
        }
        if (sides[2]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x + x0, y + y0, z + model.length - 1, bounds);
        }
        if (sides[3]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x, y + y0, z + z0, bounds);
        }

    }

    @Override
    public void setupBoundingBox() {
        this.boundingBox = new MutableBoundingBox(x, y, z, x + 8, y + 8, z + 8);
    }

    @Override
    public boolean canConnect(Direction side) {
        return true;
    }

    public DungeonStairs bottom() {
        this.stairType = 0;
        return this;
    }

    public DungeonStairs top() {
        this.stairType = 1;
        return this;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putInt("stairType", stairType);
    }

    @Override
    public int getType() {
        return 1;
    }

}