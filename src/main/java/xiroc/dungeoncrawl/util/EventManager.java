package xiroc.dungeoncrawl.util;

import java.util.Random;

import net.minecraft.item.Items;
import net.minecraft.util.Rotation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.build.block.BlockRegistry;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.DungeonLayerType;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;

public class EventManager {

	@SubscribeEvent
	public void onItemUse(final PlayerInteractEvent.RightClickBlock event) {
		if (event.getEntityPlayer().isCreative() && event.getItemStack().getItem() == Items.STICK) {
			if (event.getItemStack().getDisplayName().getString().equals("STONE_BRICKS")) {
				event.getWorld().setBlockState(event.getPos(), BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED.get());
			} else if (event.getItemStack().getDisplayName().getString().equals("DUNGEON")) {
				DungeonCrawl.LOGGER.info("Building a test dungeon map...");
				DungeonLayer layer = new DungeonLayer(DungeonLayerType.NORMAL);
				layer.buildMap(new Random());
				layer.testBuildToWorld(event.getWorld(), event.getPos());
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_TEST")) {
				DungeonCrawl.LOGGER.info("Building a dungeon model...");
				DungeonSegmentModel.build(DungeonSegmentModel.CORRIDOR_EAST_WEST_ALL_OPEN, event.getWorld(), event.getPos());
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_TEST_ROTATED")) {
				DungeonCrawl.LOGGER.info("Building a dungeon model...");
				DungeonSegmentModel.buildRotated(DungeonSegmentModel.CORRIDOR_EAST_WEST_ALL_OPEN, event.getWorld(), event.getPos(), Rotation.COUNTERCLOCKWISE_90);
			}
		}
	}

}
