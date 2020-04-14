package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.Blocks;
import net.minecraft.command.Commands;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker.LayerStatTracker;
import xiroc.dungeoncrawl.dungeon.misc.Banner;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.ModelHandler;
import xiroc.dungeoncrawl.dungeon.treasure.Book;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.part.block.BlockRegistry;

public class Tools {

	private static BlockPos pos1, pos2;

	@SubscribeEvent
	public void onServerStart(FMLServerStartingEvent event) {
		DungeonCrawl.LOGGER.debug("Registering Commands...");
		event.getServer().getCommandManager().getDispatcher().register(Commands.literal("readmodel").requires((a) -> {
			try {
				return a.asPlayer().isCreative();
			} catch (CommandSyntaxException e) {
				a.sendErrorMessage(new StringTextComponent("You must be a player!"));
				return false;
			}
		}).then(Commands.argument("name", StringArgumentType.string())
				.then(Commands.argument("entranceType", IntegerArgumentType.integer(0, 1)).executes((command) -> {
					if (Tools.pos1 != null && Tools.pos2 != null) {
						BlockPos pos1 = new BlockPos(Math.min(Tools.pos1.getX(), Tools.pos2.getX()),
								Math.min(Tools.pos1.getY(), Tools.pos2.getY()),
								Math.min(Tools.pos1.getZ(), Tools.pos2.getZ())),
								pos2 = new BlockPos(Math.max(Tools.pos1.getX(), Tools.pos2.getX()),
										Math.max(Tools.pos1.getY(), Tools.pos2.getY()),
										Math.max(Tools.pos1.getZ(), Tools.pos2.getZ()));
						ModelHandler.readModelToFile(command.getArgument("name", String.class),
								command.getArgument("entranceType", int.class) == 0
										? DungeonModel.EntranceType.OPEN
										: DungeonModel.EntranceType.CLOSED,
								command.getSource().asPlayer().world, pos1, pos2.getX() - pos1.getX() + 1,
								pos2.getY() - pos1.getY() + 1, pos2.getZ() - pos1.getZ() + 1);
						return 1;
					} else
						return 0;
				}))));
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!event.getWorld().isRemote() && event.getPlayer().isCreative()
				&& event.getPlayer().getItemStackFromSlot(EquipmentSlotType.MAINHAND).getItem() == Items.DIAMOND_AXE) {
			event.setCanceled(true);
			pos1 = event.getPos();
			event.getPlayer().sendMessage(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "Position 1 set to ("
					+ pos1.getX() + " | " + pos1.getY() + " | " + pos1.getZ() + ") "));
		}
	}

	@SubscribeEvent
	public void onItemUse(final PlayerInteractEvent.RightClickBlock event) {
		if (!event.getWorld().isRemote && event.getPlayer().isCreative()) {
			if (event.getItemStack().getItem() == Items.DIAMOND_AXE) {
				event.setCanceled(true);
				pos2 = event.getPos();
				event.getPlayer().sendMessage(new StringTextComponent(TextFormatting.LIGHT_PURPLE
						+ "Position 2 set to (" + pos2.getX() + " | " + pos2.getY() + " | " + pos2.getZ() + ") "));
			} else if (event.getItemStack().getItem() == Items.STICK) {
				if (event.getItemStack().getDisplayName().getString().equals("STONE_BRICKS")) {
					event.getWorld().setBlockState(event.getPos(),
							BlockRegistry.STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE.get());
				} else if (event.getItemStack().getDisplayName().getString().equals("TT_002")) {

//				DungeonBuilder builder = new DungeonBuilder(event.getWorld().getChunkProvider().func_225313_a(0, 0)),
//						new ChunkPos(event.getPos()), event.getWorld().rand);
//				for (DungeonPiece piece : builder.build())
//					piece.func_225577_a_(event.getWorld(), null ,event.getWorld().rand, null,
//							new ChunkPos(new BlockPos(piece.x, piece.y, piece.z)));
				} else if (event.getItemStack().getDisplayName().getString().equals("TT_003")) {
					if (!event.getWorld().isRemote)
						IBlockPlacementHandler.getHandler(Blocks.CHEST).setupBlock(event.getWorld(),
								Blocks.CHEST.getDefaultState(), event.getPos(), event.getWorld().rand,
								Treasure.Type.DEFAULT, 0, 0);
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
				}
			}
		}
	}

}
