package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelHandler;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.HashMap;
import java.util.UUID;

public class Tools {

    private static final HashMap<UUID, Tuple<BlockPos, BlockPos>> POSITIONS = new HashMap<UUID, Tuple<BlockPos, BlockPos>>();

    private static final BlockState AIR = Blocks.AIR.getDefaultState();

    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent event) {
        DungeonCrawl.LOGGER.debug("Registering Commands...");
        event.getServer().getCommandManager().getDispatcher().register(Commands.literal("savemodel").requires((a) -> {
            try {
                return a.asPlayer().isCreative();
            } catch (CommandSyntaxException e) {
                a.sendErrorMessage(new StringTextComponent("You must be a player!"));
                return false;
            }
        }).then(Commands.argument("name", StringArgumentType.string())
                .then(Commands.argument("spawnerType", IntegerArgumentType.integer(0, 2))
                        .then(Commands.argument("chestType", IntegerArgumentType.integer(0, 3)).executes((command) -> {
                            UUID uuid = command.getSource().asPlayer().getUniqueID();

                            if (!POSITIONS.containsKey(uuid)) {
                                command.getSource().sendFeedback(
                                        new StringTextComponent(ChatFormatting.RED + "Please select two positions."),
                                        true);
                                return 0;
                            }

                            Tuple<BlockPos, BlockPos> positions = POSITIONS.get(uuid);

                            BlockPos p1 = positions.getA(), p2 = positions.getB();

                            if (p1 != null && p2 != null) {
                                BlockPos pos1 = new BlockPos(Math.min(p1.getX(), p2.getX()),
                                        Math.min(p1.getY(), p2.getY()), Math.min(p1.getZ(), p2.getZ())),
                                        pos2 = new BlockPos(Math.max(p1.getX(), p2.getX()),
                                                Math.max(p1.getY(), p2.getY()), Math.max(p1.getZ(), p2.getZ()));
                                ModelHandler.readAndSaveModelToFile(command.getArgument("name", String.class),
                                        command.getSource().asPlayer().world, pos1, pos2.getX() - pos1.getX() + 1,
                                        pos2.getY() - pos1.getY() + 1, pos2.getZ() - pos1.getZ() + 1,
                                        command.getArgument("spawnerType", int.class),
                                        command.getArgument("chestType", int.class));
                                command.getSource().sendFeedback(new StringTextComponent("Saving a model..."), true);
                                return 1;
                            } else {
                                command.getSource().sendFeedback(
                                        new StringTextComponent(ChatFormatting.RED + "Please select two positions."),
                                        true);
                                return 0;
                            }
                        })))));

        event.getServer().getCommandManager().getDispatcher().register(Commands.literal("buildmodel").requires((a) -> {
            return a.hasPermissionLevel(2);
        }).then(Commands.argument("name", StringArgumentType.word()).then(Commands.argument("location", Vec3Argument.vec3()).executes((command) -> {
            String name = command.getArgument("name", String.class);
            DungeonModel model = DungeonModels.NAME_TO_MODEL.get(name);
            if (model != null) {
                BlockPos pos = Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource());
                buildModel(model, command.getSource().asPlayer().world, pos, Theme.MODEL, Theme.MODEL_SUB);
            } else {
                command.getSource().sendErrorMessage(new StringTextComponent("Unknown model name: " + name));
                return 1;
            }
            return 0;
        }))));
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getWorld().isRemote() && event.getPlayer().isCreative()
                && event.getPlayer().getItemStackFromSlot(EquipmentSlotType.MAINHAND).getItem() == Items.DIAMOND_AXE) {
            event.setCanceled(true);
            BlockPos pos = event.getPos();

            UUID uuid = event.getPlayer().getGameProfile().getId();
            if (POSITIONS.containsKey(uuid)) {
                BlockPos position2 = POSITIONS.get(uuid).getB();
                POSITIONS.put(uuid, new Tuple<BlockPos, BlockPos>(pos, position2));
            } else {
                POSITIONS.put(uuid, new Tuple<BlockPos, BlockPos>(pos, null));
            }

            event.getPlayer().sendMessage(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "Position 1 set to ("
                    + pos.getX() + " | " + pos.getY() + " | " + pos.getZ() + ") "));
        }
    }

    @SubscribeEvent
    public void onItemUse(final PlayerInteractEvent.RightClickBlock event) {
        IWorld world = event.getPlayer().world;
        if (!world.isRemote() && event.getPlayer().isCreative()) {
            if (event.getItemStack().getItem() == Items.DIAMOND_AXE) {
                event.setCanceled(true);

                BlockPos pos = event.getPos();

                UUID uuid = event.getPlayer().getGameProfile().getId();
                if (POSITIONS.containsKey(uuid)) {
                    BlockPos position1 = POSITIONS.get(uuid).getA();
                    POSITIONS.put(uuid, new Tuple<BlockPos, BlockPos>(position1, pos));
                } else {
                    POSITIONS.put(uuid, new Tuple<BlockPos, BlockPos>(null, pos));
                }

                event.getPlayer().sendMessage(new StringTextComponent(TextFormatting.LIGHT_PURPLE
                        + "Position 2 set to (" + pos.getX() + " | " + pos.getY() + " | " + pos.getZ() + ") "));
            }
        }
    }

    public static void buildModel(DungeonModel model, IWorld world, BlockPos pos, Theme theme,
                                  Theme.SubTheme subTheme) {
        for (int y = 0; y < model.height; y++) {
            for (int x = 0; x < model.width; x++) {
                for (int z = 0; z < model.length; z++) {
                    BlockPos placePos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (model.model[x][y][z] == null) {
                        world.setBlockState(placePos, AIR, 2);
                    } else {
                        Block block = model.model[x][y][z].type.getBaseBlock(model.model[x][y][z]);
                        if (block == null)
                            continue;
                        world.setBlockState(placePos, model.model[x][y][z].create(block.getDefaultState(), world, placePos, Rotation.NONE).getA(), 3);
                        world.notifyNeighbors(placePos, world.getBlockState(placePos).getBlock());
                    }
                }
            }
        }

        if (model.featurePositions != null) {
            for (DungeonModel.FeaturePosition featurePosition : model.featurePositions) {
                DirectionalBlockPos blockPos = featurePosition.directionalBlockPos(pos.getX(), pos.getY(), pos.getZ());
                world.setBlockState(blockPos.position, Blocks.JIGSAW.getDefaultState().with(BlockStateProperties.FACING, blockPos.direction), 3);
            }
        }

//        for (int y = 0; y < model.height; y++) {
//            for (int x = 0; x < model.width; x++) {
//                for (int z = 0; z < model.length; z++) {
//                    BlockPos placePos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
//                }
//            }
//        }


    }

}
