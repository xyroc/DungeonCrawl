package xiroc.dungeoncrawl.dungeon.block;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Dispenser implements IBlockPlacementHandler {

    @Override
    public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
                           int theme, int lootLevel) {
        world.setBlockState(pos, state, 3);
        if (world.getTileEntity(pos) instanceof DispenserTileEntity) {
            DispenserTileEntity dispenser = (DispenserTileEntity) world.getTileEntity(pos);
            dispenser.setLootTable(getLootTable(lootLevel), rand.nextLong());
        } else
            DungeonCrawl.LOGGER.warn("Failed to fetch a dispenser entity at {}", pos.toString());
    }

    public static ResourceLocation getLootTable(int lootLevel) {
        switch (lootLevel) {
            case 0:
                return Loot.DISPENSER_STAGE_1;
            case 1:
                return Loot.DISPENSER_STAGE_2;
            case 2:
                return Loot.DISPENSER_STAGE_3;
            default:
                DungeonCrawl.LOGGER.warn("Unknown Loot Level: " + lootLevel);
                return null;
        }
    }

}
