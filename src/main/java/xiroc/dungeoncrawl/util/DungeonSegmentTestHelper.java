package xiroc.dungeoncrawl.util;

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
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonSegmentTestHelper {

	@SubscribeEvent
	public void onItemUse(final PlayerInteractEvent.RightClickBlock event) {
		if (event.getEntityPlayer().isCreative() && event.getItemStack().getItem() == Items.STICK) {
			if (event.getItemStack().getDisplayName().getString().equals("STONE_BRICKS")) {
				event.getWorld().setBlockState(event.getPos(), BlockRegistry.STONE_BRICKS_NORMAL_MOSSY_CRACKED.get());
			} else if (event.getItemStack().getDisplayName().getString().equals("TT_002")) {
				DungeonBuilder builder = new DungeonBuilder(event.getWorld(), new ChunkPos(event.getPos()), event.getWorld().rand);
				for (DungeonPiece piece : builder.build())
					piece.addComponentParts(event.getWorld(), event.getWorld().rand, null, new ChunkPos(new BlockPos(piece.x, piece.y, piece.z)));
				// layer.testBuildToWorld(event.getWorld(), event.getPos());
			} else if (event.getItemStack().getDisplayName().getString().equals("TT_003")) {
				IBlockPlacementHandler.getHandler(Blocks.SPAWNER).setupBlock(event.getWorld(), Blocks.SPAWNER.getDefaultState(), event.getPos(), event.getWorld().rand, 0);
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_TEST")) {
				DungeonSegmentModel.build(DungeonSegmentModelRegistry.BRIDGE_ALL_SIDES, event.getWorld(), event.getPos(), Theme.TEST , 0);
//				DungeonPiece piece = new DungeonPieces.Stairs(null, DungeonPieces.DEFAULT_NBT);
//				piece.setRealPosition(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
//				piece.theme = -1;
//				piece.addComponentParts(event.getWorld(), event.getWorld().rand, new MutableBoundingBox(), new ChunkPos(event.getPos().getX() >> 4, event.getPos().getZ() >> 4));
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
				DungeonCrawl.LOGGER.info("Reading a custom sized dungeon model: " + width + "x" + height + "x" + length);
				DungeonSegmentModelReader.readModelToFile(event.getWorld(), event.getPos(), width, height, length);
			} else if (event.getItemStack().getDisplayName().getString().equals("MODEL_PRINT")) {
				if (event.getWorld().isRemote)
					return;
				DungeonCrawl.LOGGER.info("Printing a dungeon model...");
				DungeonCrawl.LOGGER.info(DungeonSegmentModelReader.readModelToArrayString(event.getWorld(), event.getPos(), 8, 8, 8));
			}
		}
	}

}
