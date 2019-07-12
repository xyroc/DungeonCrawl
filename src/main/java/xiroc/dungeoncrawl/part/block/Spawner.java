package xiroc.dungeoncrawl.part.block;

import java.util.Random;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class Spawner {

	public static void setupSpawner(IWorld world, BlockPos pos, EntityType<?> type) {
		world.setBlockState(pos, BlockRegistry.SPAWNER, 0);
		// MobSpawnerTileEntity tile = (MobSpawnerTileEntity) world.getTileEntity(pos);
		//tile.getSpawnerBaseLogic().setEntityType(type);
	}

	public static EntityType<?> getRandomEntityType(Random rand) {
		switch(rand.nextInt(6)) {
		case 0:
			return EntityType.ZOMBIE;
		case 1:
			return EntityType.SKELETON;
		case 2:
			return EntityType.SPIDER;
		case 3:
			return EntityType.CAVE_SPIDER;
		case 4:
			return EntityType.ZOMBIE_VILLAGER;
		case 5:
			return EntityType.HUSK;
		}
		return null;
	}

}
