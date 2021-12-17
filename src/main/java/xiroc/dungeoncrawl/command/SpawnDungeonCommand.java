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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.command.argument.SecondaryThemeArgument;
import xiroc.dungeoncrawl.command.argument.ThemeArgument;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.List;
import java.util.Random;

public class SpawnDungeonCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("roguelike").requires((a)
                -> a.hasPermission(4)).then(Commands.argument("location", Vec3Argument.vec3())
                .executes((command) -> {
                    BlockPos pos = Vec3Argument.getCoordinates(command, "location").getBlockPos(command.getSource());
                    ServerLevel world = command.getSource().getLevel();
                    ResourceLocation biomeKey = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(world.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2));
                    String biome = biomeKey != null ? biomeKey.toString() : "minecraft:plains";
                    return spawnDungeon(command.getSource(), world, pos,
                            Theme.randomTheme(biome, world.getRandom()),
                            Theme.randomSecondaryTheme(biome, world.getRandom()));
                })
                .then(Commands.argument("theme", ThemeArgument.theme())
                        .executes((command) ->
                                spawnDungeon(command.getSource(), command.getSource().getLevel(),
                                        Vec3Argument.getCoordinates(command, "location").getBlockPos(command.getSource()),
                                        ThemeArgument.getTheme(command, "theme"),
                                        Theme.getBuiltinDefaultSecondaryTheme()))
                        .then(Commands.argument("secondary_theme", SecondaryThemeArgument.secondaryTheme()).executes((command) ->
                                spawnDungeon(command.getSource(), command.getSource().getLevel(),
                                        Vec3Argument.getCoordinates(command, "location").getBlockPos(command.getSource()),
                                        ThemeArgument.getTheme(command, "theme"),
                                        SecondaryThemeArgument.getSecondaryTheme(command, "secondary_theme"))
                        )))));
    }

    private static int spawnDungeon(CommandSourceStack commandSource, ServerLevel world, BlockPos pos, Theme theme, SecondaryTheme secondaryTheme) {
        commandSource.sendSuccess(new TextComponent(ChatFormatting.RED + "This is an experimental feature." +
                " Please report any bugs you encounter on the issue tracker on https://github.com/XYROC/DungeonCrawl/issues."), true);
        if (world.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) > 32) {
            long seed = (long) pos.getX() + pos.getZ() << 8 + world.getSeed();
            commandSource.sendSuccess(new TextComponent("Dungeon Seed: " + seed), true);
            commandSource.sendSuccess(new TextComponent("Building a dungeon..."), true);
            DungeonBuilder builder = new DungeonBuilder(world, pos, new Random(seed));
            builder.theme = theme;
            builder.secondaryTheme = secondaryTheme;
            List<DungeonPiece> pieces = builder.build();
            pieces.forEach((piece) -> {
                piece.worldGen = false;
                if (piece instanceof DungeonEntrance) {
                    Vec3i offset = piece.model.getOffset(piece.rotation);
                    int x = piece.x + 4 + offset.getX(), z = piece.z + 4 + offset.getZ();
                    BoundingBox bounds = new BoundingBox(x, 0, z,
                            x + piece.model.width - 1, world.getMaxBuildHeight(), z + piece.model.length - 1);
                    Vec3i vector3i = bounds.getCenter();
                    piece.postProcess(world, world.structureFeatureManager(), world.getChunkSource().generator, builder.rand,
                            bounds, new ChunkPos(piece.x >> 4, piece.z >> 4), new BlockPos(vector3i.getX(), bounds.minY(), vector3i.getZ()));
                } else {
                    BoundingBox boundingBox = piece.getBoundingBox();
                    Vec3i vector3i = boundingBox.getCenter();
                    piece.postProcess(world, world.structureFeatureManager(), world.getChunkSource().generator, builder.rand,
                            boundingBox, new ChunkPos(piece.x >> 4, piece.z >> 4), new BlockPos(vector3i.getX(), boundingBox.minY(), vector3i.getZ()));
                }
            });
            commandSource.sendSuccess(new TextComponent(ChatFormatting.GREEN + "Done."), true);
            return 0;
        } else {
            commandSource.sendFailure(new TextComponent("Your current position is unfit for a dungeon."));
            return 1;
        }
    }

}
