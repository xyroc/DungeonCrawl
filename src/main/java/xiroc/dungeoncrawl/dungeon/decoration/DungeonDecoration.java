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

package xiroc.dungeoncrawl.dungeon.decoration;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.theme.JsonTheming;

import java.util.Random;

public interface DungeonDecoration {

    String VINE_DECORATION = "vines";
    String SCATTERED_DECORATION = "scattered";
    String FLOOR_DECORATION = "floor";
    String FLOOR_NEXT_TO_SOLID_DECORATION = "floor_next_to_solid";

    void decorate(DungeonModel model, IWorld world, BlockPos pos, Random random, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds, DungeonPiece piece);

    JsonObject serialize();

    static DungeonDecoration fromJson(JsonObject object, ResourceLocation file) {
        if (object.has("type")) {
            String type = object.get("type").getAsString().toLowerCase();
            switch (type) {
                case VINE_DECORATION:
                    return new VineDecoration(object.has("chance") ? object.get("chance").getAsFloat() : 0.35F);
                case SCATTERED_DECORATION: {
                    float chance = object.has("chance") ? object.get("chance").getAsFloat() : 0.25F;
                    BlockStateProvider blockStateProvider;

                    if (object.has("block")) {
                        blockStateProvider = JsonTheming.deserialize(object, "block", file);
                        if (blockStateProvider != null) {
                            return new ScatteredDecoration(blockStateProvider, chance);
                        }
                    } else {
                        DungeonCrawl.LOGGER.warn("Missing entry 'block'");
                        return null;
                    }
                }
                case FLOOR_DECORATION: {
                    float chance = object.has("chance") ? object.get("chance").getAsFloat() : 0.5F;
                    BlockStateProvider blockStateProvider;

                    if (object.has("block")) {
                        blockStateProvider = JsonTheming.deserialize(object, "block", file);
                        if (blockStateProvider != null) {
                            return new FloorDecoration(blockStateProvider, chance);
                        }
                    } else {
                        DungeonCrawl.LOGGER.warn("Missing entry 'block'");
                        return null;
                    }
                }
                case FLOOR_NEXT_TO_SOLID_DECORATION: {
                    float chance = object.has("chance") ? object.get("chance").getAsFloat() : 0.5F;
                    BlockStateProvider blockStateProvider;

                    if (object.has("block")) {
                        blockStateProvider = JsonTheming.deserialize(object, "block", file);
                        if (blockStateProvider != null) {
                            return new FloorDecoration.NextToSolid(blockStateProvider, chance);
                        }
                    } else {
                        DungeonCrawl.LOGGER.warn("Missing entry 'block'");
                        return null;
                    }
                }
                default:
                    DungeonCrawl.LOGGER.warn("Unknown decoration type '{}'", type);
                    return null;
            }
        } else {
            return null;
        }
    }

    static BlockPos getRotatedBlockPos(int x, int y, int z, BlockPos base, DungeonModel model, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_90:
                return new BlockPos(base.getX() + model.length - z - 1, base.getY() + y, base.getZ() + x);
            case CLOCKWISE_180:
                return new BlockPos(base.getX() + model.width - x - 1, base.getY() + y, base.getZ() + model.length - z - 1);
            case COUNTERCLOCKWISE_90:
                return new BlockPos(base.getX() + z, base.getY() + y, base.getZ() + model.width - x - 1);
            default:
                return new BlockPos(base.getX() + x, base.getY() + y, base.getZ() + z);
        }
    }

}
