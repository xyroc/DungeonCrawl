package xiroc.dungeoncrawl.dungeon.block;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure.Type;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Furnace implements IBlockPlacementHandler {

    public static final RandomValueRange COAL_AMOUNT = new RandomValueRange(1, 16);

    @Override
    public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
                           int theme, int lootLevel) {
        world.setBlockState(pos, state, 3);
        if (world.getTileEntity(pos) instanceof FurnaceTileEntity) {
            FurnaceTileEntity tile = (FurnaceTileEntity) world.getTileEntity(pos);
            tile.setInventorySlotContents(1, new ItemStack(Items.COAL, COAL_AMOUNT.generateInt(rand)));
        } else
            DungeonCrawl.LOGGER.warn("Failed to fetch a furnace entity at {}", pos.toString());

    }

    public static class Smoker implements IBlockPlacementHandler {

        @Override
        public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Type treasureType, int theme,
                               int lootLevel) {
            world.setBlockState(pos, state, 3);
            if (world.getTileEntity(pos) instanceof SmokerTileEntity) {
                SmokerTileEntity tile = (SmokerTileEntity) world.getTileEntity(pos);
                tile.setInventorySlotContents(1, new ItemStack(Items.CHARCOAL, COAL_AMOUNT.generateInt(rand)));
//				tile.setInventorySlotContents(2, theme == 3 ? Kitchen.SMOKER_OCEAN.getItemStack((ServerWorld) world.getWorld(), rand, theme, lootLevel)
//				: Kitchen.SMOKER.getItemStack((ServerWorld) world.getWorld(), rand, theme, lootLevel));
            } else
                DungeonCrawl.LOGGER.warn("Failed to fetch a smoker entity at {}", pos.toString());

        }

    }

}
