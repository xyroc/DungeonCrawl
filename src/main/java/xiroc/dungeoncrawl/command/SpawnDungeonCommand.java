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
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.List;
import java.util.Random;

public class SpawnDungeonCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("roguelike").requires((a)
                -> a.hasPermissionLevel(4)).then(Commands.argument("location", Vec3Argument.vec3())
                .executes((command) -> {
                    BlockPos pos = Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource());
                    ServerWorld world = command.getSource().getWorld();
                    return spawnDungeon(command.getSource(), world, pos,
                            Theme.randomTheme(world.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2).getRegistryName().toString(), world.getRandom()),
                            Theme.randomSubTheme(world.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2).getRegistryName().toString(), world.getRandom()));
                })
                .then(Commands.argument("theme", StringArgumentType.string())
                        .executes((command) ->
                                spawnDungeon(command.getSource(), command.getSource().getWorld(),
                                        Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource()),
                                        Theme.getTheme(StringArgumentType.getString(command, "theme")),
                                        Theme.getDefaultSubTheme()))
                        .then(Commands.argument("sub_theme", StringArgumentType.string()).executes((command) ->
                                spawnDungeon(command.getSource(), command.getSource().getWorld(),
                                        Vec3Argument.getLocation(command, "location").getBlockPos(command.getSource()),
                                        Theme.getTheme(StringArgumentType.getString(command, "theme")),
                                        Theme.getSubTheme(StringArgumentType.getString(command, "sub_theme")))
                        )))));
    }

    private static int spawnDungeon(CommandSource commandSource, ServerWorld world, BlockPos pos, Theme theme, Theme.SubTheme subTheme) {
        commandSource.sendFeedback(new StringTextComponent(TextFormatting.RED + "This is an experimental feature." +
                " Please report any bugs you encounter on the issue tracker on https://github.com/XYROC/DungeonCrawl/issues."), true);
        if (world.getHeight(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()) > 32) {
            long seed = (long) pos.getX() + pos.getZ() << 8 + world.getSeed();
            commandSource.sendFeedback(new StringTextComponent("Dungeon Seed: " + seed), true);
            commandSource.sendFeedback(new StringTextComponent("Building a dungeon..."), true);
            DungeonBuilder builder = new DungeonBuilder(world, pos, new Random(seed));
            builder.theme = theme;
            builder.subTheme = subTheme;
            List<DungeonPiece> pieces = builder.build();
            pieces.forEach((piece) -> {
                piece.context.heightmapType = Heightmap.Type.WORLD_SURFACE;
                piece.context.postProcessing = false;
                MutableBoundingBox boundingBox = piece.getBoundingBox();
                if (piece instanceof DungeonEntrance) {
                    Vec3i offset = piece.model.getOffset(piece.rotation);
                    int x = piece.x + 4 + offset.getX(), z = piece.z + 4 + offset.getZ();
                    piece.create(world, world.getChunkProvider().generator, builder.rand,
                            new MutableBoundingBox(x, 0, z,
                                    x + piece.model.width - 1, world.getMaxHeight(), z + piece.model.length - 1),
                            new ChunkPos(piece.x >> 4, piece.z >> 4));
                } else {
                    piece.create(world, world.getChunkProvider().generator, builder.rand,
                            new MutableBoundingBox(boundingBox.minX, 0, boundingBox.minZ, boundingBox.maxX, world.getMaxHeight(), boundingBox.maxZ),
                            new ChunkPos(piece.x >> 4, piece.z >> 4));
                }
            });
            commandSource.sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Done."), true);
            return 0;
        } else {
            commandSource.sendErrorMessage(new StringTextComponent("Your current position is unfit for a dungeon."));
            return 1;
        }
    }

}
