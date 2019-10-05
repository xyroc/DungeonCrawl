package xiroc.dungeoncrawl.util;

import java.util.Random;


import net.minecraft.block.Blocks;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker.LayerStatTracker;
import xiroc.dungeoncrawl.dungeon.misc.Banner;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.dungeon.treasure.Book;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.theme.Theme;

public class Tools {

	@SubscribeEvent
	public void onItemUse(final PlayerInteractEvent.RightClickBlock event) {
		if (event.getPlayer().isCreative() && event.getItemStack().getItem() == Items.STICK) {
			if (event.getItemStack().getDisplayName().getString().equals("STONE_BRICKS")) {
				event.getWorld().setBlockState(event.getPos(), BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED.get());
			} else if (event.getItemStack().getDisplayName().getString().equals("TT_002")) {

				DungeonBuilder builder = new DungeonBuilder(event.getWorld().getChunkProvider().getChunkGenerator(),
						new ChunkPos(event.getPos()), event.getWorld().rand);
				for (DungeonPiece piece : builder.build())
					piece.addComponentParts(event.getWorld(), event.getWorld().rand, null,
							new ChunkPos(new BlockPos(piece.x, piece.y, piece.z)));
			} else if (event.getItemStack().getDisplayName().getString().equals("TT_003")) {
				if (!event.getWorld().isRemote)
					IBlockPlacementHandler.getHandler(Blocks.CHEST).setupBlock(event.getWorld(),
							Blocks.CHEST.getDefaultState(), event.getPos(), event.getWorld().rand,
							Treasure.Type.TREASURE, 0, 5);
			} else if (event.getItemStack().getDisplayName().getString().equals("TT_004")) {
				if (!event.getWorld().isRemote) {
					DungeonStatTracker tracker = new DungeonStatTracker(3);
					LayerStatTracker layer = new LayerStatTracker();
					layer.totalObjectives = 42;
					layer.chests = 28;
					layer.rooms = 4;
					layer.spawners = 22;
					layer.traps = 7;
					tracker.stats[0] = layer;
					tracker.stats[1] = new LayerStatTracker();
					tracker.stats[2] = new LayerStatTracker();
					tracker.build();
					event.getPlayer().addItemStackToInventory(Book.createStatBook("Statistics", tracker));
				}
			} else if (event.getItemStack().getDisplayName().getString().equals("TT_Banner")) {
				event.getPlayer().inventory.addItemStackToInventory(Banner.createBanner(new Random()));
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_TEST")) {
				DungeonSegmentModel.build(DungeonSegmentModelRegistry.ENTRANCE_TOWER_1, event.getWorld(),
						event.getPos(), Theme.TEST, Treasure.Type.DEFAULT, 0);
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_TEST_ROTATED")) {
				DungeonCrawl.LOGGER.info("Not building a dungeon model at all...");
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_READ")) {
				if (event.getWorld().isRemote)
					return;
				DungeonCrawl.LOGGER.info("Reading a dungeon model...");
				DungeonSegmentModelReader.readModelToFile(event.getWorld(), event.getPos(), 8, 8, 8);
			} else if (event.getItemStack().getDisplayName().getString().startsWith("MODEL_READ2")) {
				if (event.getWorld().isRemote)
					return;
				String[] s = event.getItemStack().getDisplayName().getString().split("_");
				int width = Integer.parseInt(s[2]);
				int height = Integer.parseInt(s[3]);
				int length = Integer.parseInt(s[4]);
				DungeonCrawl.LOGGER
						.info("Reading a custom sized dungeon model: " + width + "x" + height + "x" + length);
				DungeonSegmentModelReader.readModelToFile(event.getWorld(), event.getPos(), width, height, length);
			}
		}
	}

}
