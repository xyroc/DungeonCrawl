/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.util;

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
import xiroc.dungeoncrawl.dungeon.model.ModelBlockDefinition;
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
        }).then(Commands.argument("name", StringArgumentType.string()).executes((command) -> {
                    UUID uuid = command.getSource().asPlayer().getUniqueID();

                    if (!POSITIONS.containsKey(uuid)) {
                        command.getSource().sendFeedback(
                                new StringTextComponent(TextFormatting.RED + "Please select two positions."),
                                true);
                        return 1;
                    }

                    Tuple<BlockPos, BlockPos> positions = POSITIONS.get(uuid);

                    BlockPos p1 = positions.getA(), p2 = positions.getB();

                    if (p1 != null && p2 != null) {
                        BlockPos pos1 = new BlockPos(Math.min(p1.getX(), p2.getX()),
                                Math.min(p1.getY(), p2.getY()), Math.min(p1.getZ(), p2.getZ())),
                                pos2 = new BlockPos(Math.max(p1.getX(), p2.getX()),
                                        Math.max(p1.getY(), p2.getY()), Math.max(p1.getZ(), p2.getZ()));
                        ModelHandler.readAndSaveModelToFile(StringArgumentType.getString(command, "name"), ModelBlockDefinition.DEFAULT_DEFINITION,
                                command.getSource().asPlayer().world, pos1, pos2.getX() - pos1.getX() + 1,
                                pos2.getY() - pos1.getY() + 1, pos2.getZ() - pos1.getZ() + 1);
                        command.getSource().sendFeedback(new StringTextComponent("Saving a model..."), true);
                        return 0;
                    } else {
                        command.getSource().sendFeedback(
                                new StringTextComponent(TextFormatting.RED + "Please select two positions."),
                                true);
                        return 1;
                    }
                }).then(Commands.argument("block definition", StringArgumentType.word()).executes((command) -> {
                    UUID uuid = command.getSource().asPlayer().getUniqueID();

                    if (!POSITIONS.containsKey(uuid)) {
                        command.getSource().sendFeedback(
                                new StringTextComponent(TextFormatting.RED + "Please select two positions."),
                                true);
                        return 1;
                    }

                    Tuple<BlockPos, BlockPos> positions = POSITIONS.get(uuid);

                    BlockPos p1 = positions.getA(), p2 = positions.getB();

                    String blockDefinition = StringArgumentType.getString(command, "block definition");
                    if (!ModelBlockDefinition.DEFINITIONS.containsKey(blockDefinition)) {
                        command.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + "Unknown block definition path \"" + blockDefinition + "\""), true);
                        return 1;
                    }

                    if (p1 != null && p2 != null) {
                        BlockPos pos1 = new BlockPos(Math.min(p1.getX(), p2.getX()),
                                Math.min(p1.getY(), p2.getY()), Math.min(p1.getZ(), p2.getZ())),
                                pos2 = new BlockPos(Math.max(p1.getX(), p2.getX()),
                                        Math.max(p1.getY(), p2.getY()), Math.max(p1.getZ(), p2.getZ()));
                        ModelHandler.readAndSaveModelToFile(StringArgumentType.getString(command, "name"),
                                ModelBlockDefinition.DEFINITIONS.get(blockDefinition),
                                command.getSource().asPlayer().world, pos1, pos2.getX() - pos1.getX() + 1,
                                pos2.getY() - pos1.getY() + 1, pos2.getZ() - pos1.getZ() + 1);
                        command.getSource().sendFeedback(new StringTextComponent("Saving a model..."), true);
                        return 0;
                    } else {
                        command.getSource().sendFeedback(
                                new StringTextComponent(TextFormatting.RED + "Please select two positions."),
                                true);
                        return 1;
                    }
                }))
        ));

        event.getServer().getCommandManager().getDispatcher().register(Commands.literal("buildmodel").requires((a) -> a.hasPermissionLevel(2))
                .then(Commands.argument("id", IntegerArgumentType.integer()).executes((command) -> {
                    int id = command.getArgument("id", int.class);
                    DungeonModel model = DungeonModels.LEGACY_MODELS.get(id);
                    if (model != null) {
                        BlockPos pos = command.getSource().asPlayer().getPosition();
                        buildModel(model, command.getSource().asPlayer().world, pos, ModelBlockDefinition.DEFAULT_DEFINITION);
                    } else {
                        command.getSource().sendErrorMessage(new StringTextComponent("Unknown model id: " + id));
                        return 1;
                    }
                    return 0;
                }).then(Commands.argument("location", Vec3Argument.vec3()).executes((command) -> {
                    int id = command.getArgument("id", int.class);
                    DungeonModel model = DungeonModels.LEGACY_MODELS.get(id);
                    if (model != null) {
                        BlockPos pos = Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource());
                        buildModel(model, command.getSource().asPlayer().world, pos, ModelBlockDefinition.DEFAULT_DEFINITION);
                    } else {
                        command.getSource().sendErrorMessage(new StringTextComponent("Unknown model id: " + id));
                        return 1;
                    }
                    return 0;
                })).then(Commands.argument("block definition", StringArgumentType.word()).executes((command) -> {
                    int id = command.getArgument("id", int.class);
                    DungeonModel model = DungeonModels.LEGACY_MODELS.get(id);
                    String key = StringArgumentType.getString(command, "block definition");
                    if (model != null) {
                        if (ModelBlockDefinition.DEFINITIONS.containsKey(key)) {
                            BlockPos pos = command.getSource().asPlayer().getPosition();
                            buildModel(model, command.getSource().asPlayer().world, pos, ModelBlockDefinition.DEFINITIONS.get(key));
                        } else {
                            command.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + "Unknown block definition: " + key), true);
                            return 1;
                        }
                    } else {
                        command.getSource().sendErrorMessage(new StringTextComponent("Unknown model id: " + id));
                        return 1;
                    }
                    return 0;
                }).then(Commands.argument("location", Vec3Argument.vec3()).executes((command) -> {
                    int id = command.getArgument("id", int.class);
                    DungeonModel model = DungeonModels.LEGACY_MODELS.get(id);
                    String key = StringArgumentType.getString(command, "block definition");
                    if (model != null) {
                        if (ModelBlockDefinition.DEFINITIONS.containsKey(key)) {
                            BlockPos pos = Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource());
                            buildModel(model, command.getSource().asPlayer().world, pos, ModelBlockDefinition.DEFINITIONS.get(key));
                        } else {
                            command.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + "Unknown block definition: " + key), true);
                            return 1;
                        }
                    } else {
                        command.getSource().sendErrorMessage(new StringTextComponent("Unknown model id: " + id));
                        return 1;
                    }
                    return 0;
                })))).then(Commands.argument("key", StringArgumentType.string()).executes((command) -> {
                    String key = StringArgumentType.getString(command, "key");
                    DungeonModel model = DungeonModels.MODELS.get(key);
                    if (model != null) {
                        BlockPos pos = command.getSource().asPlayer().getPosition();
                        buildModel(model, command.getSource().asPlayer().world, pos, ModelBlockDefinition.DEFAULT_DEFINITION);
                    } else {
                        command.getSource().sendErrorMessage(new StringTextComponent("Unknown model key: " + key));
                        return 1;
                    }
                    return 0;
                }).then(Commands.argument("location", Vec3Argument.vec3()).executes((command) -> {
                    String key = StringArgumentType.getString(command, "key");
                    DungeonModel model = DungeonModels.MODELS.get(key);
                    if (model != null) {
                        BlockPos pos = Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource());
                        buildModel(model, command.getSource().asPlayer().world, pos, ModelBlockDefinition.DEFAULT_DEFINITION);
                    } else {
                        command.getSource().sendErrorMessage(new StringTextComponent("Unknown model key: " + key));
                        return 1;
                    }
                    return 0;
                })).then(Commands.argument("block definition", StringArgumentType.word()).executes((command) -> {
                    String key = StringArgumentType.getString(command, "key");
                    String blockDefinition = StringArgumentType.getString(command, "block definition");
                    DungeonModel model = DungeonModels.MODELS.get(key);
                    if (model != null) {
                        if (ModelBlockDefinition.DEFINITIONS.containsKey(blockDefinition)) {
                            BlockPos pos = command.getSource().asPlayer().getPosition();
                            buildModel(model, command.getSource().asPlayer().world, pos, ModelBlockDefinition.DEFINITIONS.get(blockDefinition));
                        } else {
                            command.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + "Unknown block definition: " + blockDefinition), true);
                            return 1;
                        }
                    } else {
                        command.getSource().sendErrorMessage(new StringTextComponent("Unknown model key: " + key));
                        return 1;
                    }
                    return 0;
                }).then(Commands.argument("location", Vec3Argument.vec3()).executes((command) -> {
                    String key = StringArgumentType.getString(command, "key");
                    String blockDefinition = StringArgumentType.getString(command, "block definition");
                    DungeonModel model = DungeonModels.MODELS.get(key);
                    if (model != null) {
                        if (ModelBlockDefinition.DEFINITIONS.containsKey(blockDefinition)) {
                            BlockPos pos = Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource());
                            buildModel(model, command.getSource().asPlayer().world, pos, ModelBlockDefinition.DEFINITIONS.get(blockDefinition));
                        } else {
                            command.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + "Unknown block definition: " + blockDefinition), true);
                            return 1;
                        }
                    } else {
                        command.getSource().sendErrorMessage(new StringTextComponent("Unknown model key: " + key));
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

    public static void buildModel(DungeonModel model, IWorld world, BlockPos pos, ModelBlockDefinition definition) {
        for (int y = 0; y < model.height; y++) {
            for (int x = 0; x < model.width; x++) {
                for (int z = 0; z < model.length; z++) {
                    BlockPos placePos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (model.model[x][y][z] == null) {
                        world.setBlockState(placePos, AIR, 3);
                    } else {
                        Block block = definition.getBlock(model.model[x][y][z]);
                        if (block == null)
                            block = Blocks.AIR;
                        world.setBlockState(placePos, model.model[x][y][z].create(definition.getBlock(model.model[x][y][z]).getDefaultState(), world, pos, Rotation.NONE).getA(), 3);
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
