package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.jigsaw.JigsawOrientation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelHandler;

import java.util.HashMap;
import java.util.UUID;

public class Tools {

    private static final HashMap<UUID, Tuple<BlockPos, BlockPos>> POSITIONS = new HashMap<>();

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
                                        new StringTextComponent(TextFormatting.RED + "Please select two positions."),
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
                                        new StringTextComponent(TextFormatting.RED + "Please select two positions."),
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
                buildModel(model, command.getSource().asPlayer().world, pos);
            } else {
                command.getSource().sendErrorMessage(new StringTextComponent("Unknown model name: " + name));
                return 1;
            }
            return 0;
        }))));

        event.getServer().getCommandManager().getDispatcher().register(Commands.literal("debugmodel")
                .then(Commands.argument("name", StringArgumentType.word()).then(Commands.argument("location", Vec3Argument.vec3())
                        .then(Commands.argument("rotation", IntegerArgumentType.integer(0, 3))
                                .executes((command) -> {
                                    String name = command.getArgument("name", String.class);
                                    DungeonModel model = DungeonModels.NAME_TO_MODEL.get(name);
                                    if (model != null) {
                                        BlockPos pos = Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource());
                                        Rotation rotation = Rotation.values()[command.getArgument("rotation", int.class)];
                                        DungeonCrawl.LOGGER.info("ROTATION: {}", rotation);
                                        debugModelRotated(model, command.getSource().asPlayer().world, pos, rotation);
                                    } else {
                                        command.getSource().sendErrorMessage(new StringTextComponent("Unknown model name: " + name));
                                        return 1;
                                    }
                                    return 0;
                                })))));
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
                POSITIONS.put(uuid, new Tuple<>(pos, position2));
            } else {
                POSITIONS.put(uuid, new Tuple<>(pos, null));
            }

            event.getPlayer().sendMessage(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "Position 1 set to ("
                    + pos.getX() + " | " + pos.getY() + " | " + pos.getZ() + ") "), event.getPlayer().getUniqueID());
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
                    POSITIONS.put(uuid, new Tuple<>(position1, pos));
                } else {
                    POSITIONS.put(uuid, new Tuple<>(null, pos));
                }

                event.getPlayer().sendMessage(new StringTextComponent(TextFormatting.LIGHT_PURPLE
                        + "Position 2 set to (" + pos.getX() + " | " + pos.getY() + " | " + pos.getZ() + ") "), event.getPlayer().getUniqueID());

            }
        }
    }

    public static void buildModel(DungeonModel model, IWorld world, BlockPos pos) {
        for (int y = 0; y < model.height; y++) {
            for (int x = 0; x < model.width; x++) {
                for (int z = 0; z < model.length; z++) {
                    BlockPos placePos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (model.model[x][y][z] == null) {
                        world.setBlockState(placePos, AIR, 3);
                    } else {
                        Block block = model.model[x][y][z].type.getBaseBlock(model.model[x][y][z]);
                        if (block == null)
                            continue;
                        world.setBlockState(placePos, model.model[x][y][z].create(block.getDefaultState(), world, placePos, Rotation.NONE).getA(), 3);
                        world.func_230547_a_(placePos, world.getBlockState(placePos).getBlock());
                    }
                }
            }
        }

        if (model.featurePositions != null) {
            for (DungeonModel.FeaturePosition featurePosition : model.featurePositions) {
                DirectionalBlockPos blockPos = featurePosition.directionalBlockPos(pos.getX(), pos.getY(), pos.getZ());
                world.setBlockState(blockPos.position, Blocks.JIGSAW.getDefaultState().with(BlockStateProperties.ORIENTATION, getJigsawOrientation(blockPos.direction)), 3);
            }
        }
    }

    public void debugModel(DungeonModel model, IWorld world, BlockPos pos) {
        for (int x = 0; x < model.width; x++) {
            for (int y = 0; y < model.height; y++) {
                for (int z = 0; z < model.length; z++) {
                    if (model.model[x][y][z] == null) {
                        world.setBlockState(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z),
                                DungeonBlocks.CAVE_AIR, 2);
                    } else {
                        world.setBlockState(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z),
                                Blocks.SLIME_BLOCK.getDefaultState(), 3);
                    }
                }
            }
            if (model.featurePositions != null) {
                for (DungeonModel.FeaturePosition f : model.featurePositions) {
                    world.setBlockState(new BlockPos(pos.getX() + f.position.getX(), pos.getY() + f.position.getY(), pos.getZ() + f.position.getZ()),
                            Blocks.JIGSAW.getDefaultState().with(BlockStateProperties.ORIENTATION, getJigsawOrientation(f.facing)), 3);
                }
            }
        }

        //buildBoundingBox(world, new MutableBoundingBox(pos.getX() + xStart, pos.getY(), pos.getZ() + zStart,
        //        pos.getX() + xStart + width - 1, pos.getY() + 8, pos.getZ() + zStart + length - 1), Blocks.BIRCH_FENCE);
    }

    public void debugModelRotated(DungeonModel model, IWorld world, BlockPos pos, Rotation rotation) {
        //DungeonCrawl.LOGGER.debug("BuildRotated: {} {} {}, {} {}, {} {}, {} {}", pos.getX(), pos.getY(), pos.getZ(), xStart, zStart, width, length, model.width, model.length);
        switch (rotation) {
            case CLOCKWISE_90: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = 0; y < model.height; y++) {
                        for (int z = 0; z < model.length; z++) {
                            if (model.model[x][y][z] == null) {
                                world.setBlockState(new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x),
                                        DungeonBlocks.CAVE_AIR, 2);
                            } else {
                                world.setBlockState(new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x),
                                        Blocks.SLIME_BLOCK.getDefaultState(), 3);
                            }
                        }
                    }
                }
                if (model.featurePositions != null) {
                    for (DungeonModel.FeaturePosition f : model.featurePositions) {
                        world.setBlockState(new BlockPos(pos.getX() + model.length - f.position.getZ() - 1,
                                        pos.getY() + f.position.getY(), pos.getZ() + f.position.getX()),
                                Blocks.JIGSAW.getDefaultState().with(BlockStateProperties.ORIENTATION, getJigsawOrientation(f.facing.rotateY())), 3);
                    }
                }
                //buildBoundingBox(world, new MutableBoundingBox(pos.getX() + xStart, pos.getY(), pos.getZ() + zStart,
                //        pos.getX() + xStart + width - 1, pos.getY() + 8, pos.getZ() + zStart + length - 1), Blocks.ACACIA_FENCE);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = 0; y < model.height; y++) {
                        for (int z = 0; z < model.length; z++) {
                            if (model.model[x][y][z] == null) {
                                world.setBlockState(new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + model.width - x - 1),
                                        DungeonBlocks.CAVE_AIR, 2);
                            } else {
                                world.setBlockState(new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + model.width - x - 1),
                                        Blocks.SLIME_BLOCK.getDefaultState(), 2);
                            }
                        }
                    }
                }
                if (model.featurePositions != null) {
                    for (DungeonModel.FeaturePosition f : model.featurePositions) {
                        world.setBlockState(new BlockPos(pos.getX() + f.position.getZ(),
                                        pos.getY() + f.position.getY(), pos.getZ() + model.width - f.position.getX() - 1),
                                Blocks.JIGSAW.getDefaultState().with(BlockStateProperties.ORIENTATION, getJigsawOrientation(f.facing.rotateYCCW())), 3);
                    }
                }
                //buildBoundingBox(world, new MutableBoundingBox(pos.getX() + xStart, pos.getY(), pos.getZ() + zStart,
                //        pos.getX() + xStart + width - 1, pos.getY() + 8, pos.getZ() + zStart + length - 1), Blocks.ACACIA_FENCE);
                break;
            }
            case CLOCKWISE_180: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = 0; y < model.height; y++) {
                        for (int z = 0; z < model.length; z++) {
                            if (model.model[x][y][z] == null) {
                                world.setBlockState(new BlockPos(pos.getX() + model.width - x - 1, pos.getY() + y, pos.getZ() + model.length - z - 1), DungeonBlocks.CAVE_AIR, 2);
                            } else {
                                world.setBlockState(new BlockPos(pos.getX() + model.width - x - 1, pos.getY() + y, pos.getZ() + model.length - z - 1), Blocks.SLIME_BLOCK.getDefaultState(), 2);
                            }
                        }
                    }
                }
                if (model.featurePositions != null) {
                    for (DungeonModel.FeaturePosition f : model.featurePositions) {
                        world.setBlockState(new BlockPos(pos.getX() + model.width - f.position.getX() - 1,
                                        pos.getY() + f.position.getY(), pos.getZ() + model.length - f.position.getZ() - 1),
                                Blocks.JIGSAW.getDefaultState().with(BlockStateProperties.ORIENTATION, getJigsawOrientation(f.facing.getOpposite())), 3);
                    }
                }
                break;
            }
            case NONE:
                debugModel(model, world, pos);
                break;
            default:
                DungeonCrawl.LOGGER.warn("Failed to build a rotated dungeon segment: Unsupported rotation " + rotation);
                break;
        }

    }

    private static JigsawOrientation getJigsawOrientation(Direction primary) {
        switch (primary) {
            case EAST:
            case WEST:
            case SOUTH:
            case NORTH:
                return JigsawOrientation.func_239641_a_(primary, Direction.UP);
            case UP:
            case DOWN:
                return JigsawOrientation.func_239641_a_(primary, Direction.EAST);
            default:
                return JigsawOrientation.NORTH_UP;
        }
    }
}
