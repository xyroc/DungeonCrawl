package xiroc.dungeoncrawl.util;

import java.util.Random;

import net.minecraft.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.build.block.BlockRegistry;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.DungeonLayerType;

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
			}
		}
	}

}
