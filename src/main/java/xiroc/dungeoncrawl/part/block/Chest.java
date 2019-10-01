package xiroc.dungeoncrawl.part.block;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.loot.LootTables;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.dungeon.treasure.TreasureLootTable;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Chest implements IBlockPlacementHandler {

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
			int theme, int lootLevel) {
		world.setBlockState(pos, state, 2);
		if (treasureType != Treasure.Type.DEFAULT || lootLevel < 1 || rand.nextDouble() > 0.25) {
			LockableLootTileEntity tile = (LockableLootTileEntity) world.getTileEntity(pos);
			TreasureLootTable lootTable = Treasure.SPECIAL_LOOT_TABLES.get(treasureType);
			if (lootTable != null)
				lootTable.fillInventory(tile, rand, theme, lootLevel);
			else
				getTreasureLootTable(theme, lootLevel).fillInventory(tile, rand, theme, lootLevel);
		} else
			LockableLootTileEntity.setLootTable(world, world.getRandom(), pos, getLootTable(theme, lootLevel));
	}

	public static TreasureLootTable getTreasureLootTable(int theme, int lootLevel) {
		switch (lootLevel) {
		case 0:
			return theme != 3 ? Treasure.CHEST_STAGE_1 : Treasure.CHEST_STAGE_1_OCEAN;
		case 1:
			return theme != 3 ? Treasure.CHEST_STAGE_2 : Treasure.CHEST_STAGE_2_OCEAN;
		case 2:
			return theme != 3 ? Treasure.CHEST_STAGE_3 : Treasure.CHEST_STAGE_3_OCEAN;
		default:
			DungeonCrawl.LOGGER.warn("Unknown Loot Level: " + lootLevel);
			return null;
		}
	}

	public static ResourceLocation getLootTable(int theme, int lootLevel) {
		switch (lootLevel) {
		case 1:
			return theme != 3 ? LootTables.CHESTS_ABANDONED_MINESHAFT : LootTables.CHESTS_UNDERWATER_RUIN_SMALL;
		case 2:
			return theme != 3 ? LootTables.CHESTS_STRONGHOLD_CROSSING : LootTables.CHESTS_SHIPWRECK_TREASURE;
		default:
			DungeonCrawl.LOGGER.warn("Unknown Vanilla Loot Level: {}", lootLevel);
			return null;
		}
	}

	public static class Barrel implements IBlockPlacementHandler {

		@Override
		public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
				int theme, int lootLevel) {
			world.setBlockState(pos, state, 2);
			if (treasureType != Treasure.Type.DEFAULT || lootLevel < 1 || rand.nextDouble() > 0.25) {
				LockableLootTileEntity tile = (LockableLootTileEntity) world.getTileEntity(pos);
				TreasureLootTable lootTable = Treasure.SPECIAL_LOOT_TABLES.get(treasureType);
				if (lootTable != null)
					lootTable.fillInventory(tile, rand, theme, lootLevel);
				else
					getTreasureLootTable(theme, lootLevel).fillInventory(tile, rand, theme, lootLevel);
			} else
				LockableLootTileEntity.setLootTable(world, world.getRandom(), pos,
						Chest.getLootTable(theme, lootLevel));
		}

	}

}
