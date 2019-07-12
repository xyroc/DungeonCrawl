package xiroc.dungeoncrawl.part.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.loot.LootTables;
import xiroc.dungeoncrawl.DungeonCrawl;

public class Chest {

	public static void setupChest(IWorld world, BlockState state, BlockPos pos, int lootLevel, long seedIn) {
		world.setBlockState(pos, state, 2);
		// TODO loot
		// DungeonsFeature
		((ChestTileEntity)world.getTileEntity(pos)).setLootTable(getLootTable(lootLevel), seedIn);
	}

	public static ResourceLocation getLootTable(int lootLevel) {
		switch (lootLevel) {
		case 0:
			return LootTables.CHESTS_SIMPLE_DUNGEON;
		case 1:
			return LootTables.CHESTS_DESERT_PYRAMID;
		case 2:
			return LootTables.CHESTS_NETHER_BRIDGE;
		case 3:
			return LootTables.CHESTS_STRONGHOLD_CORRIDOR;
		default:
			DungeonCrawl.LOGGER.warn("Unknown Loot Level: " + lootLevel);
			return null;
		}
	}

}
