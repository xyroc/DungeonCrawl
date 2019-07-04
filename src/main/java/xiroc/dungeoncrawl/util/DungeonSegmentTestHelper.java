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
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonSegmentTestHelper {
	
	@SubscribeEvent
	public void onItemUse(final PlayerInteractEvent.RightClickBlock event) {
		if (event.getEntityPlayer().isCreative() && event.getItemStack().getItem() == Items.STICK) {
			if (event.getItemStack().getDisplayName().getString().equals("STONE_BRICKS")) {
				event.getWorld().setBlockState(event.getPos(), BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED.get());
			} else if (event.getItemStack().getDisplayName().getString().equals("DUNGEON")) {
				DungeonCrawl.LOGGER.info("Building a test dungeon map...");
				DungeonLayer layer = new DungeonLayer(DungeonLayerType.NORMAL);
				layer.buildMap(new Random());
				//layer.testBuildToWorld(event.getWorld(), event.getPos());
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_TEST")) {
				DungeonCrawl.LOGGER.info("Building a dungeon model...");
				DungeonSegmentModel.build(DungeonSegmentModelRegistry.STAIRS_BOTTOM_1, event.getWorld(), event.getPos(), Theme.TEST);
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_TEST_ROTATED")) {
				DungeonCrawl.LOGGER.info("Building a dungeon model...");
				DungeonSegmentModel.buildRotated(DungeonSegmentModelRegistry.STAIRS_TOP_2_2, event.getWorld(), event.getPos(), Theme.TEST, Rotation.CLOCKWISE_90);
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_READ")) {
				if (event.getWorld().isRemote)
					return;
				DungeonCrawl.LOGGER.info("Reading a dungeon model...");
				DungeonSegmentModelReader.readModelToFile(event.getWorld(), event.getPos(), 8, 8, 8);
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_READ2")) {
				if (event.getWorld().isRemote)
					return;
				DungeonCrawl.LOGGER.info("Reading a custom sized dungeon model...");
				DungeonSegmentModelReader.readModelToFile(event.getWorld(), event.getPos(), 7, 8, 7);
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_PRINT")) {
				if (event.getWorld().isRemote)
					return;
				DungeonCrawl.LOGGER.info("Printing a dungeon model...");
				DungeonCrawl.LOGGER.info(DungeonSegmentModelReader.readModelToArrayString(event.getWorld(), event.getPos(), 8, 8, 8));
			}
		}
	}

}
