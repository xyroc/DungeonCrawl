package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.*;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;

import java.util.Random;

public class DungeonEntrance extends DungeonPiece {

    public int cursorHeight = 0;

    public DungeonEntrance(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.ENTRANCE_BUILDER, nbt);
        if (nbt.contains("cursorHeight"))
            cursorHeight = nbt.getInt("cursorHeight");
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        return DungeonBuilder.ENTRANCE.roll(rand).id;
    }

    @Override
    public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                     ChunkPos p_74875_4_) {
        int height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x + 4, z + 4);

        Theme buildTheme = Theme.get(theme);
        SubTheme sub = Theme.getSub(subTheme);
        if (cursorHeight == 0) {
            cursorHeight = y;

            while (cursorHeight < height) {
                if (height - cursorHeight <= 4)
                    break;
                build(DungeonModels.STAIRCASE, worldIn, structureBoundingBoxIn,
                        new BlockPos(x + 2, cursorHeight, z + 2), buildTheme, sub, Treasure.Type.DEFAULT, stage, true);
                cursorHeight += 8;
            }
        }

//		Random rand = worldIn.getRandom();
//		if (rand == null) {
//			DungeonCrawl.LOGGER.warn("Failed to receive a random object from worldIn: {}, {}", worldIn,
//					worldIn.getClass());
//			rand = new Random();
//		}

        DungeonModel entrance = DungeonModels.MODELS.get(modelID);

        if (entrance == null) {
            DungeonCrawl.LOGGER.warn("Entrance Model is null");
            return false;
        }

        Vec3i offset = DungeonModels.getOffset(entrance.id);

        DungeonCrawl.LOGGER.info("Entrance data: Position: ({}|{}|{}), Model: {}, Entrance id: {}, {}", x, cursorHeight, z,
                entrance, entrance.id, offset);

        DungeonCrawl.LOGGER.debug("StructureBoundingBox: [{},{},{}] -> [{},{},{}]", structureBoundingBoxIn.minX,
                structureBoundingBoxIn.minY, structureBoundingBoxIn.minZ, structureBoundingBoxIn.maxX,
                structureBoundingBoxIn.maxY, structureBoundingBoxIn.maxZ);

        DungeonCrawl.LOGGER.debug("BoundingBox: [{},{},{}] -> [{},{},{}]", boundingBox.minX, boundingBox.minY,
                boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);

//		int startX = Math.max(x, structureBoundingBoxIn.minX) - x,
//				startZ = Math.max(z, structureBoundingBoxIn.minZ) - z;

//		buildRotatedPart(entrance, worldIn, structureBoundingBoxIn,
//				new BlockPos(Math.max(x + offset.getA(), structureBoundingBoxIn.minX), cursorHeight,
//						Math.max(z + offset.getB(), structureBoundingBoxIn.minZ)),
//				theme, subTheme, Treasure.Type.SUPPLY, stage, Rotation.NONE, startX, 0, startZ, entrance.width - startX,
//				entrance.height, entrance.length - startZ);

        build(entrance, worldIn, structureBoundingBoxIn,
                new BlockPos(x + offset.getX(), cursorHeight + offset.getY(), z + offset.getZ()), Theme.get(theme),
                Theme.getSub(subTheme), Treasure.Type.SUPPLY, stage, true);

//		DungeonBuilder.ENTRANCE_PROCESSORS.getOrDefault(entrance.id, DungeonBuilder.DEFAULT_PROCESSOR).process(worldIn,
//				new BlockPos(x + offset.getA(), cursorHeight, z + offset.getB()), theme, this);

        return true;
    }

    @Override
    public void setupBoundingBox() {
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        Vec3i offset = DungeonModels.getOffset(modelID);
        this.boundingBox = new MutableBoundingBox(x + offset.getX(), y + offset.getY(), z + offset.getZ(),
                x + model.width - 1, y + model.height - 1, z + model.length - 1);
    }

    @Override
    public int getType() {
        return 6;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putInt("cursorHeight", cursorHeight);
    }

    @Override
    public void build(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                      SubTheme subTheme, Treasure.Type treasureType, int lootLevel, boolean fillAir) {

        int xStart = Math.max(boundsIn.minX, pos.getX()) - pos.getX(),
                width = Math.min(model.width, boundsIn.maxX - pos.getX() - xStart + 1);
        int zStart = Math.max(boundsIn.minZ, pos.getZ()) - pos.getZ(),
                length = Math.min(model.length, boundsIn.maxZ - pos.getZ() - zStart + 1);

        for (int x = xStart; x < width; x++) {
            for (int y = 0; y < model.height; y++) {
                for (int z = zStart; z < length; z++) {
                    if (model.model[x][y][z] == null) {
                        setBlockState(CAVE_AIR, world, boundsIn, treasureType, pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                                this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                    } else {
                        BlockPos position = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z], Rotation.NONE, world,
                                position, theme, subTheme,
                                WeightedRandomBlock.RANDOM, lootLevel);
                        if (result == null)
                            continue;
                        setBlockState(result.getA(), world, boundsIn, treasureType, pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                                this.theme, lootLevel, fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);

                        if (result.getB()) {
                            world.getChunk(position).markBlockForPostprocessing(position);
                        }

                        if (y == 0 && world.isAirBlock(position.down())
                                && model.model[x][0][z].type == DungeonModelBlockType.SOLID) {
                            DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(), boundsIn);
                        }
                    }
                }
            }
        }
    }

}