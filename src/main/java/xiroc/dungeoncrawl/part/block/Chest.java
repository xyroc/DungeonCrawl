package xiroc.dungeoncrawl.part.block;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.loot.LootTables;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure.Type;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.RotationHelper;

public class Chest implements IBlockPlacementHandler {

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
			int theme, int lootLevel) {
		world.setBlockState(pos, state, 2);
		if (world.getTileEntity(pos) instanceof LockableLootTileEntity) {
			ResourceLocation lootTable = Treasure.SPECIAL_LOOT_TABLES.get(treasureType);
			LockableLootTileEntity.setLootTable(world, world.getRandom(), pos,
					lootTable == null ? getLootTable(theme, lootLevel, rand) : lootTable);
		} else
			DungeonCrawl.LOGGER.warn("Failed to fetch a chest/barrel entity at {}", pos.toString());
	}

	public static ResourceLocation getLootTable(int theme, int lootLevel, Random rand) {
		switch (lootLevel) {
		case 0:
			return theme != 3 ? rand.nextFloat() < 0.1 ? LootTables.CHESTS_PILLAGER_OUTPOST : Loot.CHEST_STAGE_1
					: Loot.CHEST_STAGE_1_OCEAN;
		case 1:
			return theme != 3 ? rand.nextFloat() < 0.1 ? LootTables.CHESTS_SIMPLE_DUNGEON : Loot.CHEST_STAGE_2
					: Loot.CHEST_STAGE_2_OCEAN;
		case 2:
			return theme != 3 ? rand.nextFloat() < 0.1 ? LootTables.CHESTS_STRONGHOLD_CORRIDOR : Loot.CHEST_STAGE_3
					: Loot.CHEST_STAGE_3_OCEAN;
		default:
			DungeonCrawl.LOGGER.warn("Unknown Vanilla Loot Level: {}", lootLevel);
			return null;
		}
	}

	public static class TrappedChest implements IBlockPlacementHandler {

		@Override
		public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Type treasureType, int theme,
				int lootLevel) {
			if (state.has(BlockStateProperties.HORIZONTAL_FACING))
				state = state.with(BlockStateProperties.HORIZONTAL_FACING, RotationHelper.RANDOM_FACING_FLAT.roll(rand));
			world.setBlockState(pos, state, 2);
			if (world.getTileEntity(pos) instanceof LockableLootTileEntity) {
				ResourceLocation lootTable = Treasure.SPECIAL_LOOT_TABLES.get(treasureType);
				LockableLootTileEntity.setLootTable(world, world.getRandom(), pos,
						lootTable == null ? getLootTable(theme, lootLevel, rand) : lootTable);
			} else
				DungeonCrawl.LOGGER.warn("Failed to fetch a trapped chest entity at {}", pos.toString());
		}

	}

//	public static class Barrel implements IBlockPlacementHandler {
//
//		@Override
//		public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
//				int theme, int lootLevel) {
//			world.setBlockState(pos, state, 2);
//			ResourceLocation lootTable = Treasure.SPECIAL_LOOT_TABLES.get(treasureType);
//			LockableLootTileEntity.setLootTable(world, world.getRandom(), pos,
//					lootTable == null ? getLootTable(theme, lootLevel) : lootTable);
//		}
//
//	}

}
