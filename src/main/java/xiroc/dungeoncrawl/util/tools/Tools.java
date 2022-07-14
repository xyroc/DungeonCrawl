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

package xiroc.dungeoncrawl.util.tools;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.command.argument.DungeonModelArgument;
import xiroc.dungeoncrawl.command.argument.ModelBlockDefinitionArgument;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.ModelBlockDefinition;
import xiroc.dungeoncrawl.dungeon.model.ModelHandler;

import java.util.Hashtable;
import java.util.UUID;

public class Tools {

    private static final Hashtable<UUID, ModelEditContext> CONTEXT_TABLE = new Hashtable<>();

    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        DungeonCrawl.LOGGER.debug("Registering Commands...");
        event.getServer().getCommands().getDispatcher().register(Commands.literal("savemodel").requires((a) -> {
            try {
                return a.getPlayerOrException().isCreative();
            } catch (CommandSyntaxException e) {
                a.sendFailure(Component.literal("You must be a player!"));
                return false;
            }
        }).then(Commands.argument("name", StringArgumentType.string()).executes((command) -> {
                    UUID uuid = command.getSource().getPlayerOrException().getUUID();

                    if (!CONTEXT_TABLE.containsKey(uuid)) {
                        command.getSource().sendSuccess(
                                Component.literal(ChatFormatting.RED + "Please select two positions."),
                                true);
                        return 1;
                    }

                    ModelEditContext context = CONTEXT_TABLE.get(uuid);

                    if (context.arePositionsSet()) {
                        String name = StringArgumentType.getString(command, "name");
                        BlockPos pos1 = new BlockPos(Math.min(context.pos1.getX(), context.pos2.getX()),
                                Math.min(context.pos1.getY(), context.pos2.getY()),
                                Math.min(context.pos1.getZ(), context.pos2.getZ()));
                        BlockPos pos2 = new BlockPos(Math.max(context.pos1.getX(), context.pos2.getX()),
                                Math.max(context.pos1.getY(), context.pos2.getY()),
                                Math.max(context.pos1.getZ(), context.pos2.getZ()));
                        ModelHandler.readAndSaveModelToFile(name,
                                ModelBlockDefinition.getDefaultDefinition(),
                                command.getSource().getPlayerOrException().level, pos1, pos2.getX() - pos1.getX() + 1,
                                pos2.getY() - pos1.getY() + 1, pos2.getZ() - pos1.getZ() + 1);
                        command.getSource().sendSuccess(Component.literal("Saved as " + ChatFormatting.GREEN + name + ".nbt"), true);
                        return 0;
                    } else {
                        command.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Please select two positions."));
                        return 1;
                    }
                }).then(Commands.argument("block definition", ModelBlockDefinitionArgument.modelBlockDefinitionArgument()).executes((command) -> {
                    ModelBlockDefinition blockDefinition = ModelBlockDefinitionArgument.getDefinition(command, "block definition");
                    UUID uuid = command.getSource().getPlayerOrException().getUUID();

                    if (!CONTEXT_TABLE.containsKey(uuid)) {
                        command.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Please select two positions."));
                        return 1;
                    }

                    ModelEditContext context = CONTEXT_TABLE.get(uuid);

                    if (context.arePositionsSet()) {
                        BlockPos pos1 = new BlockPos(Math.min(context.pos1.getX(), context.pos2.getX()),
                                Math.min(context.pos1.getY(), context.pos2.getY()),
                                Math.min(context.pos1.getZ(), context.pos2.getZ()));
                        BlockPos pos2 = new BlockPos(Math.max(context.pos1.getX(), context.pos2.getX()),
                                Math.max(context.pos1.getY(), context.pos2.getY()),
                                Math.max(context.pos1.getZ(), context.pos2.getZ()));
                        String name = StringArgumentType.getString(command, "name");
                        ModelHandler.readAndSaveModelToFile(name,
                                blockDefinition,
                                command.getSource().getPlayerOrException().level, pos1, pos2.getX() - pos1.getX() + 1,
                                pos2.getY() - pos1.getY() + 1, pos2.getZ() - pos1.getZ() + 1);

                        command.getSource().sendSuccess(Component.literal("Saved as " + ChatFormatting.GREEN + name + ".nbt"), true);
                        return 0;
                    } else {
                        command.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Please select two positions."));
                        return 1;
                    }
                }))
        ));


        event.getServer().getCommands().getDispatcher().register(Commands.literal("buildmodel").requires((a) -> a.hasPermission(2))
                .then(Commands.argument("model", DungeonModelArgument.modelArgument()).executes((command) -> {
                    DungeonModel model = DungeonModelArgument.getModel(command, "model");
                    BlockPos pos = command.getSource().getPlayerOrException().blockPosition();
                    buildModel(model, command.getSource().getPlayerOrException().level, pos, ModelBlockDefinition.getDefaultDefinition());
                    setOrigin(command.getSource(), pos);
                    return 0;
                }).then(Commands.argument("location", Vec3Argument.vec3()).executes((command) -> {
                    DungeonModel model = DungeonModelArgument.getModel(command, "model");
                    BlockPos pos = Vec3Argument.getCoordinates(command, "location").getBlockPos(command.getSource());
                    buildModel(model, command.getSource().getPlayerOrException().level, pos, ModelBlockDefinition.getDefaultDefinition());
                    setOrigin(command.getSource(), pos);
                    return 0;
                })).then(Commands.argument("block definition", ModelBlockDefinitionArgument.modelBlockDefinitionArgument()).executes((command) -> {
                    ModelBlockDefinition blockDefinition = ModelBlockDefinitionArgument.getDefinition(command, "block definition");
                    DungeonModel model = DungeonModelArgument.getModel(command, "model");
                    BlockPos pos = command.getSource().getPlayerOrException().blockPosition();
                    buildModel(model, command.getSource().getPlayerOrException().level, pos, blockDefinition);
                    setOrigin(command.getSource(), pos);
                    return 0;
                }).then(Commands.argument("location", Vec3Argument.vec3()).executes((command) -> {
                    ModelBlockDefinition blockDefinition = ModelBlockDefinitionArgument.getDefinition(command, "block definition");
                    DungeonModel model = DungeonModelArgument.getModel(command, "model");
                    BlockPos pos = Vec3Argument.getCoordinates(command, "location").getBlockPos(command.getSource());
                    buildModel(model, command.getSource().getPlayerOrException().level, pos, blockDefinition);
                    setOrigin(command.getSource(), pos);
                    return 0;
                })))));

        event.getServer().getCommands().getDispatcher().register(Commands.literal("origin").requires((source) -> source.hasPermission(2))
                .executes((command) -> {
                    UUID uuid = command.getSource().getPlayerOrException().getUUID();
                    if (!CONTEXT_TABLE.containsKey(uuid)) {
                        command.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Please set your origin with "
                                + ChatFormatting.BOLD + "/origin ~ ~ ~"
                                + ChatFormatting.RED + " first."));
                        return 1;
                    } else {
                        ModelEditContext context = CONTEXT_TABLE.get(uuid);
                        BlockPos pos = command.getSource().getPlayerOrException().blockPosition();
                        command.getSource().sendSuccess(Component.literal("The origin is (x: "
                                + context.origin.getX() + " y: "
                                + context.origin.getY() + " z: "
                                + context.origin.getZ() + ")."), true);
                        command.getSource().sendSuccess(Component.literal("Your coordinates relative to the origin are (x: "
                                + (pos.getX() - context.origin.getX()) + " y: "
                                + (pos.getY() - context.origin.getY()) + " z: "
                                + (pos.getZ() - context.origin.getZ() + ").")), true);
                        return 0;
                    }
                })
                .then(Commands.argument("location", Vec3Argument.vec3())
                        .executes((command) -> {
                            BlockPos location = Vec3Argument.getCoordinates(command, "location").getBlockPos(command.getSource());
                            setOrigin(command.getSource(), location);
                            return 0;
                        }))
                .then(Commands.literal("reset").executes((command) -> {
                    UUID uuid = command.getSource().getPlayerOrException().getUUID();
                    if (CONTEXT_TABLE.containsKey(uuid)) {
                        CONTEXT_TABLE.get(uuid).origin = null;
                        command.getSource().sendSuccess(Component.literal("Origin reset."), true);
                    } else {
                        command.getSource().sendFailure(Component.literal("Nothing to reset."));
                    }
                    return 0;
                })));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getLevel().isClientSide() && event.getPlayer().isCreative()) {
            Item item = event.getPlayer().getItemBySlot(EquipmentSlot.MAINHAND).getItem();
            if (item == Items.DIAMOND_AXE) {
                event.setCanceled(true);
                BlockPos pos = event.getPos();

                UUID uuid = event.getPlayer().getGameProfile().getId();
                CONTEXT_TABLE.computeIfAbsent(uuid, (id) -> new ModelEditContext()).pos1 = pos;
                event.getPlayer().sendSystemMessage(Component.literal(ChatFormatting.LIGHT_PURPLE + "Position 1 set to ("
                        + pos.getX() + " | " + pos.getY() + " | " + pos.getZ() + ") "));
            } else if (item == Items.GOLDEN_AXE) {
                event.setCanceled(true);
                CONTEXT_TABLE.computeIfAbsent(event.getPlayer().getUUID(), (key) -> new ModelEditContext()).origin = event.getPos();
                event.getPlayer().sendSystemMessage(Component.literal("Origin set to (x: "
                        + event.getPos().getX() + " y: "
                        + event.getPos().getY() + " z: "
                        + event.getPos().getZ() + ")."));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onItemUse(final PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntity().level.isClientSide() && event.getEntity().isCreative()) {
            if (event.getItemStack().getItem() == Items.DIAMOND_AXE) {
                event.setCanceled(true);

                BlockPos pos = event.getPos();

                UUID uuid = event.getEntity().getGameProfile().getId();
                CONTEXT_TABLE.computeIfAbsent(uuid, (id) -> new ModelEditContext()).pos2 = pos;

                event.getEntity().sendSystemMessage(Component.literal(ChatFormatting.LIGHT_PURPLE
                        + "Position 2 set to (" + pos.getX() + " | " + pos.getY() + " | " + pos.getZ() + ") "));
            } else if (event.getItemStack().getItem() == Items.GOLDEN_AXE
                    && CONTEXT_TABLE.containsKey(event.getEntity().getUUID())) {
                event.setCanceled(true);
                ModelEditContext context = CONTEXT_TABLE.get(event.getEntity().getUUID());
                if (context.origin != null) {
                    event.getEntity().sendSystemMessage(Component.literal("The coordinates of the block you clicked" +
                            " relative to the origin are (x: "
                            + (event.getPos().getX() - context.origin.getX()) + " y: "
                            + (event.getPos().getY() - context.origin.getY()) + " z: "
                            + (event.getPos().getZ() - context.origin.getZ() + ").")));
                }
            }
        }

    }

    private static void setOrigin(CommandSourceStack source, BlockPos pos) throws CommandSyntaxException {
        ModelEditContext context = CONTEXT_TABLE.computeIfAbsent(source.getPlayerOrException().getUUID(), (id) -> new ModelEditContext());
        context.origin = pos;
        source.sendSuccess(Component.literal("Origin set to (x: "
                + context.origin.getX() + " y: "
                + context.origin.getY() + " z: "
                + context.origin.getZ() + ")."), true);
    }

    public static void buildModel(DungeonModel model, LevelAccessor world, BlockPos pos, ModelBlockDefinition
            definition) {
        for (int y = 0; y < model.height; y++) {
            for (int x = 0; x < model.width; x++) {
                for (int z = 0; z < model.length; z++) {
                    world.setBlock(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), Blocks.BARRIER.defaultBlockState(), 2);
                }
            }
        }

        model.blocks.forEach((modelBlock) -> {
            BlockPos placePos = pos.offset(modelBlock.position);
            Block block = definition.getBlock(modelBlock);

            if (block == null)
                block = Blocks.AIR;

            world.setBlock(placePos, modelBlock.create(block.defaultBlockState(), world, placePos, Rotation.NONE), 3);

        });

    }

}
