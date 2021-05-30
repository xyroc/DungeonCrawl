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

package xiroc.dungeoncrawl.dungeon.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.JSONUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class DungeonModelFeature {

    private final Type type;
    private final Position[] positions;
    private final RandomValueRange amount;
    @Nullable
    private final DungeonModelFeature followup;

    private DungeonModelFeature(Type type, Position[] positions, RandomValueRange amount, @Nullable DungeonModelFeature followup) {
        this.type = type;
        this.positions = positions;
        this.amount = amount;
        this.followup = followup;
    }

    public void setup(DungeonModel model, int x, int y, int z, Rotation rotation, List<Instance> features, Random rand) {
        setup(model, x, y, z, rotation, features, Lists.newArrayList(this.positions), rand);
    }

    private void setup(DungeonModel model, int x, int y, int z, Rotation rotation, List<Instance> features, List<Position> positions, Random rand) {
        if (positions.isEmpty()) return;
        int count = amount.generateInt(rand);
        if (count >= positions.size()) {
            features.add(new Instance(type, positions.stream().map((pos) -> pos.translate(x, y, z, rotation, model)).toArray(DirectionalBlockPos[]::new)));
        } else {
            DirectionalBlockPos[] resultingPositions = new DirectionalBlockPos[count];
            for (int i = 0; i < count; i++) {
                Position pos = positions.get(rand.nextInt(positions.size()));
                DirectionalBlockPos position = pos.translate(x, y, z, rotation, model);
                positions.remove(pos);
                resultingPositions[i] = position;
            }
            features.add(new Instance(type, resultingPositions));
        }
        if (followup != null && !positions.isEmpty()) {
            followup.setup(model, x, y, z, rotation, features, positions, rand);
        }
    }

    private static void placeChest(IWorld world, BlockPos pos, BlockState chest, Theme theme, Theme.SecondaryTheme secondaryTheme, int lootLevel, Random rand) {
        world.setBlockState(pos, chest, 2);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof LockableLootTileEntity) {
            Loot.setLoot((LockableLootTileEntity) tileEntity, Loot.getLootTable(lootLevel, rand), theme, secondaryTheme, rand);
        }
    }

    public static class Instance {

        private final Type type;
        private final DirectionalBlockPos[] positions;

        private Instance(Type type, DirectionalBlockPos[] positions) {
            this.type = type;
            this.positions = positions;
        }

        public void place(IWorld world, PlacementContext context, MutableBoundingBox worldGenBounds, Random rand, Theme theme, Theme.SecondaryTheme secondaryTheme, int stage) {
            for (DirectionalBlockPos pos : positions) {
                this.type.place(world, context, rand, pos.position, pos.direction, worldGenBounds, theme, secondaryTheme, stage);
            }
        }

        public static Instance read(CompoundNBT nbt) {
            Type type = Type.TYPES.get(nbt.getString("type"));
            ListNBT nbtPositions = nbt.getList("positions", 10);
            DirectionalBlockPos[] positions = new DirectionalBlockPos[nbtPositions.size()];
            for (int i = 0; i < nbtPositions.size(); i++) {
                positions[i] = DirectionalBlockPos.fromNBT(nbtPositions.getCompound(i));
            }
            return new Instance(type, positions);
        }

        public void write(CompoundNBT nbt) {
            nbt.putString("type", type.getName());
            ListNBT positions = new ListNBT();
            for (DirectionalBlockPos position : this.positions) {
                CompoundNBT pos = new CompoundNBT();
                position.writeToNBT(pos);
                positions.add(pos);
            }
            nbt.put("positions", positions);
        }

    }

    private interface Type {
        Type CHESTS = new Type() {
            @Override
            public void place(IWorld world, PlacementContext context, Random rand, BlockPos pos, Direction direction, MutableBoundingBox bounds, Theme theme, Theme.SecondaryTheme secondaryTheme, int stage) {
                if (bounds.isVecInside(pos)
                        && !DungeonBuilder.isBlockProtected(world, pos, context)
                        && world.getBlockState(pos.down()).isSolid()) {
                    placeChest(world, pos, DungeonBlocks.CHEST.with(BlockStateProperties.HORIZONTAL_FACING, direction), theme, secondaryTheme, stage, rand);
                }
            }

            @Override
            public String getName() {
                return "chests";
            }
        };

        Type TNT_CHESTS = new Type() {
            @Override
            public void place(IWorld world, PlacementContext context, Random rand, BlockPos pos, Direction direction, MutableBoundingBox bounds, Theme theme, Theme.SecondaryTheme secondaryTheme, int stage) {
                if (bounds.isVecInside(pos)
                        && !DungeonBuilder.isBlockProtected(world, pos, context)
                        && world.getBlockState(pos.down()).isSolid()) {
                    placeChest(world, pos, DungeonBlocks.CHEST.with(BlockStateProperties.HORIZONTAL_FACING, direction), theme, secondaryTheme, stage, rand);
                    BlockPos down = pos.down(2);
                    if (!DungeonBuilder.isBlockProtected(world, down, context) && !world.isAirBlock(down)) {
                        world.setBlockState(pos.down(2), Blocks.TNT.getDefaultState(), 2);
                    }
                }
            }

            @Override
            public String getName() {
                return "tnt_chests";
            }
        };

        Type SPAWNERS = new Type() {
            @Override
            public void place(IWorld world, PlacementContext context, Random rand, BlockPos pos, Direction direction, MutableBoundingBox bounds, Theme theme, Theme.SecondaryTheme secondaryTheme, int stage) {
                if (Config.NO_SPAWNERS.get()) {
                    return;
                }
                if (bounds.isVecInside(pos)
                        && !DungeonBuilder.isBlockProtected(world, pos, context)
                        && world.getBlockState(pos.down()).isSolid()) {
                    IBlockPlacementHandler.SPAWNER.place(world, DungeonBlocks.SPAWNER,
                            pos, rand, context, theme, secondaryTheme, stage);
                }
            }

            @Override
            public String getName() {
                return "spawners";
            }
        };

        Type SPAWNER_GRAVE = new Type() {
            @Override
            public void place(IWorld world, PlacementContext context, Random rand, BlockPos pos, Direction direction, MutableBoundingBox bounds, Theme theme, Theme.SecondaryTheme secondaryTheme, int stage) {
                if (bounds.isVecInside(pos)
                        && !DungeonBuilder.isBlockProtected(world, pos, context)
                        && world.getBlockState(pos.down()).isSolid()) {
                    placeChest(world, pos, DungeonBlocks.CHEST.with(BlockStateProperties.HORIZONTAL_FACING, direction), theme, secondaryTheme, stage, rand);
                }

                if (Config.NO_SPAWNERS.get()) {
                    return;
                }

                BlockPos spawner = pos.offset(direction);
                if (bounds.isVecInside(spawner)
                        && !DungeonBuilder.isBlockProtected(world, spawner, context)
                        && world.getBlockState(spawner.down()).isSolid()) {
                    IBlockPlacementHandler.SPAWNER.place(world, DungeonBlocks.SPAWNER, spawner, rand, context, theme, secondaryTheme, stage);
                }

                BlockPos p = pos.offset(direction, 2);
                if (bounds.isVecInside(p)) {
                    world.setBlockState(p, theme.material.get(p), 2);
                }
            }

            @Override
            public String getName() {
                return "spawner_grave";
            }
        };

        Type EMPTY_GRAVE = new Type() {
            @Override
            public void place(IWorld world, PlacementContext context, Random rand, BlockPos pos, Direction direction, MutableBoundingBox bounds, Theme theme, Theme.SecondaryTheme secondaryTheme, int stage) {
                BlockPos position = pos.offset(direction, 2);
                if (bounds.isVecInside(position)) {
                    world.setBlockState(position, theme.material.get(position), 2);
                }
            }

            @Override
            public String getName() {
                return "empty_grave";
            }
        };

        ImmutableMap<String, Type> TYPES = new ImmutableMap.Builder<String, Type>()
                .put(CHESTS.getName(), CHESTS)
                .put(TNT_CHESTS.getName(), TNT_CHESTS)
                .put(SPAWNER_GRAVE.getName(), SPAWNER_GRAVE)
                .put(EMPTY_GRAVE.getName(), EMPTY_GRAVE)
                .put(SPAWNERS.getName(), SPAWNERS)
                .build();

        void place(IWorld world, PlacementContext context, Random rand, BlockPos pos, Direction direction, MutableBoundingBox bounds, Theme theme, Theme.SecondaryTheme secondaryTheme, int stage);

        String getName();

    }

    public static DungeonModelFeature fromJson(JsonObject object, ResourceLocation file) {
        return fromJson(object, file, null);
    }

    private static DungeonModelFeature fromJson(JsonObject object, ResourceLocation file, Position[] positions) {
        String type = object.get("type").getAsString();
        if (!Type.TYPES.containsKey(type)) {
            throw new DatapackLoadException("Unknown feature type " + type + " in " + file);
        }

        if (positions == null) {
            JsonArray positionsArray = object.getAsJsonArray("positions");
            positions = new Position[positionsArray.size()];
            for (int i = 0; i < positions.length; i++) {
                positions[i] = Position.fromJson(positionsArray.get(i).getAsJsonObject());
            }
        }

        JsonObject jsonAmount = object.getAsJsonObject("amount");
        RandomValueRange amount = new RandomValueRange(jsonAmount.get("min").getAsInt(), jsonAmount.get("max").getAsInt());

        DungeonModelFeature followUp = null;

        if (object.has("follow_up")) {
            followUp = fromJson(object.getAsJsonObject("follow_up"), file, positions);
        }

        return new DungeonModelFeature(Type.TYPES.get(type), positions, amount, followUp);
    }

    private static class Position {

        public final Vector3i position;
        public final Direction facing;

        private Position(Vector3i position, Direction facing) {
            this.position = position;
            this.facing = facing;
        }

        public static Position fromJson(JsonObject object) {
            Vector3i position = JSONUtils.getOffset(object);
            Direction facing = Direction.valueOf(object.get("facing").getAsString().toUpperCase(Locale.ROOT));
            return new Position(position, facing);
        }

        public BlockPos blockPos(BlockPos base) {
            return base.add(position);
        }

        public BlockPos blockPos(int x, int y, int z, Rotation rotation, DungeonModel model) {
            switch (rotation) {
                case CLOCKWISE_90:
                    return new BlockPos(x + model.length - position.getZ() - 1, y + position.getY(), z + position.getX());
                case CLOCKWISE_180:
                    return new BlockPos(x + model.length - position.getZ() - 1, y + position.getY(), z + model.width - position.getX() - 1);
                case COUNTERCLOCKWISE_90:
                    return new BlockPos(x + position.getZ(), y + position.getY(), z + model.width - position.getX() - 1);
                default:
                    return new BlockPos(x + position.getX(), y + position.getY(), z + position.getZ());
            }
        }

        public DirectionalBlockPos translate(int x, int y, int z, Rotation rotation, DungeonModel model) {
            switch (rotation) {
                case CLOCKWISE_90:
                    return new DirectionalBlockPos(x + model.length - position.getZ() - 1, y + position.getY(), z + position.getX(),
                            facing.getAxis() != Direction.Axis.Y ? facing.rotateY() : facing);
                case CLOCKWISE_180:
                    return new DirectionalBlockPos(x + model.width - position.getX() - 1, y + position.getY(), z + model.length - position.getZ() - 1,
                            facing.getAxis() != Direction.Axis.Y ? facing.getOpposite() : facing);
                case COUNTERCLOCKWISE_90:
                    return new DirectionalBlockPos(x + position.getZ(), y + position.getY(), z + model.width - position.getX() - 1,
                            facing.getAxis() != Direction.Axis.Y ? facing.rotateYCCW() : facing);
                default:
                    return new DirectionalBlockPos(x + position.getX(), y + position.getY(), z + position.getZ(), facing);
            }
        }

    }

}
