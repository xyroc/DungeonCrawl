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

package xiroc.dungeoncrawl.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.theme.Theme;

public class SpawnDungeonCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
//        dispatcher.register(Commands.literal("dcdungeon").requires((a)
//                -> a.hasPermissionLevel(4)).then(Commands.argument("location", Vec3Argument.vec3())
//                .executes((command) -> {
//                            command.getSource().sendFeedback(new StringTextComponent("Building a dungeon..."), true);
//                            BlockPos pos = Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource());
//                            ServerWorld world = command.getSource().getWorld();
//                            spawnDungeon(world, pos,
//                                    Theme.getTheme(world.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2).getRegistryName().toString(), world.getRandom()),
//                                    Theme.getSubTheme(world.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2).getRegistryName().toString(), world.getRandom()));
//                            return 0;
//                        }
//                ).then(Commands.argument("theme", IntegerArgumentType.integer())
//                        .then(Commands.argument("sub_theme", IntegerArgumentType.integer())
//                                .executes((command) -> {
//                                    command.getSource().sendFeedback(new StringTextComponent("Building a dungeon..."), true);
//                                    spawnDungeon(command.getSource().getWorld(), Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource()),
//                                            command.getArgument("theme", int.class), command.getArgument("sub_theme", int.class));
//                                    return 0;
//                                })))));
    }

//    private static void spawnDungeon(ServerWorld world, BlockPos pos, int theme, int subTheme) {
//        DungeonBuilder builder = new DungeonBuilder(world, new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
//        builder.build(theme, subTheme).forEach((piece) -> {
//            //piece.func_230383_a_(world, null, builder.rand, piece.getBoundingBox(), new ChunkPos(piece.x >> 4, piece.z >> 4));
//        });
//    }

}
