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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.command.argument.SecondaryThemeArgument;
import xiroc.dungeoncrawl.command.argument.ThemeArgument;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.List;
import java.util.Random;

public class SpawnDungeonCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("roguelike").requires((a)
                -> a.hasPermission(4)).then(Commands.argument("location", Vec3Argument.vec3())
                .executes((command) -> {
                    BlockPos pos = Vec3Argument.getCoordinates(command, "location").getBlockPos(command.getSource());
                    ServerWorld world = command.getSource().getLevel();
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

    private static int spawnDungeon(CommandSource commandSource, ServerWorld world, BlockPos pos, Theme theme, Theme.SecondaryTheme secondaryTheme) {
        commandSource.sendSuccess(new StringTextComponent(TextFormatting.RED + "This is an experimental feature." +
                " Please report any bugs you encounter on the issue tracker on https://github.com/XYROC/DungeonCrawl/issues."), true);
        if (world.getHeight(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()) > 32) {
            long seed = (long) pos.getX() + pos.getZ() << 8 + world.getSeed();
            commandSource.sendSuccess(new StringTextComponent("Dungeon Seed: " + seed), true);
            commandSource.sendSuccess(new StringTextComponent("Building a dungeon..."), true);
            DungeonBuilder builder = new DungeonBuilder(world, pos, new Random(seed));
            builder.theme = theme;
            builder.secondaryTheme = secondaryTheme;
            List<DungeonPiece> pieces = builder.build();
            pieces.forEach((piece) -> {
                piece.context.heightmapType = Heightmap.Type.WORLD_SURFACE;
                piece.context.postProcessing = false;

                if (piece instanceof DungeonEntrance) {
                    Vector3i offset = piece.model.getOffset(piece.rotation);
                    int x = piece.x + 4 + offset.getX(), z = piece.z + 4 + offset.getZ();
                    MutableBoundingBox bounds = new MutableBoundingBox(x, 0, z,
                            x + piece.model.width - 1, world.getMaxBuildHeight(), z + piece.model.length - 1);
                    Vector3i vector3i = bounds.getCenter();
                    piece.postProcess(world, world.structureFeatureManager(), world.getChunkSource().generator, builder.rand,
                            bounds, new ChunkPos(piece.x >> 4, piece.z >> 4), new BlockPos(vector3i.getX(), bounds.y0, vector3i.getZ()));
                } else {
                    MutableBoundingBox boundingBox = piece.getBoundingBox();
                    Vector3i vector3i = boundingBox.getCenter();
                    piece.postProcess(world, world.structureFeatureManager(), world.getChunkSource().generator, builder.rand,
                            boundingBox, new ChunkPos(piece.x >> 4, piece.z >> 4), new BlockPos(vector3i.getX(), boundingBox.y0, vector3i.getZ()));
                }
            });
            commandSource.sendSuccess(new StringTextComponent(TextFormatting.GREEN + "Done."), true);
            return 0;
        } else {
            commandSource.sendFailure(new StringTextComponent("Your current position is unfit for a dungeon."));
            return 1;
        }
    }

}
