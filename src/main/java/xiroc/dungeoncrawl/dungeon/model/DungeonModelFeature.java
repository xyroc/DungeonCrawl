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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.Range;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public final class DungeonModelFeature {

    private final Type type;
    private final Position[] positions;
    private final Range amount;
    @Nullable
    private final DungeonModelFeature followup;

    private DungeonModelFeature(Type type, Position[] positions, Range amount, @Nullable DungeonModelFeature followup) {
        this.type = type;
        this.positions = positions;
        this.amount = amount;
        this.followup = followup;
    }

    public void setup(DungeonModel model, int x, int y, int z, Rotation rotation, List<Instance> features, RandomSource rand) {
        setup(model, x, y, z, rotation, features, Lists.newArrayList(this.positions), rand);
    }

    private void setup(DungeonModel model, int x, int y, int z, Rotation rotation, List<Instance> features, List<Position> positions, RandomSource rand) {
        if (positions.isEmpty()) return;
        int count = amount.nextInt(rand);
        if (count >= positions.size()) {
            features.add(new Instance(type, positions.stream().map((pos) -> pos.worldPos(x, y, z, rotation, model)).toArray(DirectionalBlockPos[]::new)));
        } else {
            DirectionalBlockPos[] resultingPositions = new DirectionalBlockPos[count];
            for (int i = 0; i < count; i++) {
                Position pos = positions.get(rand.nextInt(positions.size()));
                DirectionalBlockPos position = pos.worldPos(x, y, z, rotation, model);
                positions.remove(pos);
                resultingPositions[i] = position;
            }
            features.add(new Instance(type, resultingPositions));
        }
        if (followup != null && !positions.isEmpty()) {
            followup.setup(model, x, y, z, rotation, features, positions, rand);
        }
    }

    private static void placeChest(LevelAccessor world, BlockPos pos, BlockState chest, Theme theme, SecondaryTheme secondaryTheme, int lootLevel, RandomSource rand) {
        world.setBlock(pos, chest, 2);
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
            Loot.setLoot(world, pos, randomizableContainerBlockEntity, Loot.getLootTable(lootLevel, rand), theme, secondaryTheme, rand);
        }
    }

    public static class Instance {

        private final Type type;
        private final DirectionalBlockPos[] positions;

        private Instance(Type type, DirectionalBlockPos[] positions) {
            this.type = type;
            this.positions = positions;
        }

        public void place(LevelAccessor world, BoundingBox worldGenBounds, RandomSource rand, Theme theme, SecondaryTheme secondaryTheme, int stage) {
            for (DirectionalBlockPos pos : positions) {
                this.type.place(world, rand, pos.position, pos.direction, worldGenBounds, theme, secondaryTheme, stage);
            }
        }

        public static Instance read(CompoundTag nbt) {
            Type type = Type.TYPES.get(nbt.getString("type"));
            ListTag nbtPositions = nbt.getList("positions", 10);
            DirectionalBlockPos[] positions = new DirectionalBlockPos[nbtPositions.size()];
            for (int i = 0; i < nbtPositions.size(); i++) {
                positions[i] = DirectionalBlockPos.fromNBT(nbtPositions.getCompound(i));
            }
            return new Instance(type, positions);
        }

        public void write(CompoundTag nbt) {
            nbt.putString("type", type.getName());
            ListTag positions = new ListTag();
            for (DirectionalBlockPos position : this.positions) {
                CompoundTag pos = new CompoundTag();
                position.writeToNBT(pos);
                positions.add(pos);
            }
            nbt.put("positions", positions);
        }

    }

    private interface Type {
        Type CHEST = new Type() {
            @Override
            public void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage) {
                if (bounds.isInside(pos)
                        && !DungeonBuilder.isBlockProtected(world, pos)
                        && world.getBlockState(pos.below()).canOcclude()) {
                    placeChest(world, pos, DungeonBlocks.CHEST.setValue(BlockStateProperties.HORIZONTAL_FACING, direction), theme, secondaryTheme, stage, rand);
                }
            }

            @Override
            public String getName() {
                return "chest";
            }
        };

        Type TNT_CHEST = new Type() {
            @Override
            public void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage) {
                if (bounds.isInside(pos)
                        && !DungeonBuilder.isBlockProtected(world, pos)
                        && world.getBlockState(pos.below()).canOcclude()) {
                    placeChest(world, pos, DungeonBlocks.CHEST.setValue(BlockStateProperties.HORIZONTAL_FACING, direction), theme, secondaryTheme, stage, rand);
                    BlockPos down = pos.below(2);
                    if (!DungeonBuilder.isBlockProtected(world, down) && !world.isEmptyBlock(down)) {
                        world.setBlock(pos.below(2), Blocks.TNT.defaultBlockState(), 2);
                    }
                }
            }

            @Override
            public String getName() {
                return "tnt_chest";
            }
        };

        Type SPAWNER = new Type() {
            @Override
            public void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage) {
                if (bounds.isInside(pos)
                        && !DungeonBuilder.isBlockProtected(world, pos)
                        && world.getBlockState(pos.below()).canOcclude()) {
                    IBlockPlacementHandler.SPAWNER.place(world, DungeonBlocks.SPAWNER, pos, rand, theme, secondaryTheme, stage);
                }
            }

            @Override
            public String getName() {
                return "spawner";
            }
        };

        Type CEILING_SPAWNER = new Type() {
            @Override
            public void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage) {
                if (bounds.isInside(pos)
                        && !DungeonBuilder.isBlockProtected(world, pos)
                        && world.getBlockState(pos.above()).canOcclude()) {
                    IBlockPlacementHandler.SPAWNER.place(world, DungeonBlocks.SPAWNER, pos, rand, theme, secondaryTheme, stage);
                }
            }

            @Override
            public String getName() {
                return "ceiling_spawner";
            }
        };

        Type SPAWNER_GRAVE = new Type() {
            @Override
            public void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage) {
                if (bounds.isInside(pos)
                        && !DungeonBuilder.isBlockProtected(world, pos)
                        && world.getBlockState(pos.below()).canOcclude()) {
                    placeChest(world, pos, DungeonBlocks.CHEST.setValue(BlockStateProperties.HORIZONTAL_FACING, direction), theme, secondaryTheme, stage, rand);
                }

                BlockPos spawner = pos.relative(direction);
                if (bounds.isInside(spawner)
                        && !DungeonBuilder.isBlockProtected(world, spawner)
                        && world.getBlockState(spawner.below()).canOcclude()) {
                    IBlockPlacementHandler.SPAWNER.place(world, DungeonBlocks.SPAWNER, spawner, rand, theme, secondaryTheme, stage);
                }

                BlockPos p = pos.relative(direction, 2);
                if (bounds.isInside(p) && world.getBlockState(p.below()).canOcclude()) {
                    world.setBlock(p, Blocks.QUARTZ_BLOCK.defaultBlockState(), 2);
                }
            }

            @Override
            public String getName() {
                return "spawner_grave";
            }
        };

        Type EMPTY_GRAVE = new Type() {
            @Override
            public void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage) {
                BlockPos position = pos.relative(direction, 2);
                if (bounds.isInside(position) && world.getBlockState(position.below()).canOcclude()) {
                    world.setBlock(position, Blocks.QUARTZ_BLOCK.defaultBlockState(), 2);
                }
            }

            @Override
            public String getName() {
                return "empty_grave";
            }
        };

        Type STAIRS = new Type() {
            @Override
            public void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage) {
                if (direction.getAxis() == Direction.Axis.Y) return;
                for (int length = 0; length < 10; length++) {
                    if (bounds.isInside(pos)) {
                        int height = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos).getY();
                        if (height < pos.getY()) {
                            for (; height < pos.getY(); height++) {
                                BlockPos p = new BlockPos(pos.getX(), height, pos.getZ());
                                world.setBlock(p, theme.solid.get(world, p, rand), 2);
                            }
                            world.setBlock(pos, theme.solidStairs.get(world, pos, rand).setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getOpposite()), 2);
                            pos = pos.relative(direction).relative(Direction.DOWN);
                        } else {
                            break;
                        }
                    }
                }
            }

            @Override
            public String getName() {
                return "stairs";
            }
        };

        Type SEWER_HOLE = new Type() {
            private final BlockStateProvider AIR_WATER = new BlockStateProvider() {
                @Override
                public BlockState get(LevelAccessor world, BlockPos pos, RandomSource random, Rotation rotation) {
                    if (pos.getY() > 8) return Blocks.CAVE_AIR.defaultBlockState();
                    return Blocks.WATER.defaultBlockState();
                }

                @Override
                public JsonObject serialize() {
                    return null;
                }
            };

            private final BlockStateProvider AIR_LAVA = new BlockStateProvider() {
                @Override
                public BlockState get(LevelAccessor world, BlockPos pos, RandomSource random, Rotation rotation) {
                    if (pos.getY() > 8) return Blocks.CAVE_AIR.defaultBlockState();
                    return Blocks.LAVA.defaultBlockState();
                }

                @Override
                public JsonObject serialize() {
                    return null;
                }
            };

            @Override
            public void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage) {
                BlockStateProvider inner = stage < 4 ? AIR_WATER : AIR_LAVA;

                buildDown(world, pos, rand, bounds, inner);

                BlockPos east = pos.east();
                buildDown(world, east, rand, bounds, inner);

                BlockPos west = pos.west();
                buildDown(world, west, rand, bounds, inner);

                BlockPos north = pos.north();
                buildDown(world, north, rand, bounds, inner);

                BlockPos south = pos.south();
                buildDown(world, pos.south(), rand, bounds, inner);

                buildDown(world, east.north(), rand, bounds, inner);
                buildDown(world, east.south(), rand, bounds, inner);

                buildDown(world, west.north(), rand, bounds, inner);
                buildDown(world, west.south(), rand, bounds, inner);

                // Walls
                buildDown(world, east.offset(1, -1, 0), rand, bounds, theme.generic);
                buildDown(world, east.offset(1, -1, -1), rand, bounds, theme.generic);
                buildDown(world, east.offset(1, -1, 1), rand, bounds, theme.generic);

                buildDown(world, south.offset(0, -1, 1), rand, bounds, theme.generic);
                buildDown(world, south.offset(1, -1, 1), rand, bounds, theme.generic);
                buildDown(world, south.offset(-1, -1, 1), rand, bounds, theme.generic);

                buildDown(world, west.offset(-1, -1, 0), rand, bounds, theme.generic);
                buildDown(world, west.offset(-1, -1, -1), rand, bounds, theme.generic);
                buildDown(world, west.offset(-1, -1, 1), rand, bounds, theme.generic);

                buildDown(world, north.offset(0, -1, -1), rand, bounds, theme.generic);
                buildDown(world, north.offset(1, -1, -1), rand, bounds, theme.generic);
                buildDown(world, north.offset(-1, -1, -1), rand, bounds, theme.generic);
            }

            @Override
            public String getName() {
                return "sewer_hole";
            }

            private void buildDown(LevelAccessor world, BlockPos pos, RandomSource random, BoundingBox bounds, BlockStateProvider blockStateProvider) {
                if (!bounds.isInside(pos)) return;
                for (; pos.getY() > 0; pos = pos.below()) {
                    if (!DungeonBuilder.isBlockProtected(world, pos) && !world.isEmptyBlock(pos)) {
                        world.setBlock(pos, blockStateProvider.get(world, pos, random), 2);
                        FluidState state = world.getFluidState(pos);
                        if (!state.isEmpty()) {
                            world.scheduleTick(pos, state.getType(), 0);
                        }
                    }
                }
            }
        };

        Type CROPS = new Type() {
            @Override
            public void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage) {
                if (bounds.isInside(pos) && world.getBlockState(pos.below()).getBlock() instanceof FarmBlock) {
                    ForgeRegistries.BLOCKS.tags().getTag(BlockTags.CROPS).getRandomElement(rand).ifPresent((cropBlock) -> {
                        BlockState crop = cropBlock.defaultBlockState();
                        if (crop.hasProperty(BlockStateProperties.AGE_7))
                            crop = crop.setValue(BlockStateProperties.AGE_7, 4 + rand.nextInt(4));
                        world.setBlock(pos, crop, 2);
                    });
                }
            }

            @Override
            public String getName() {
                return "crops";
            }
        };

        ImmutableMap<String, Type> TYPES = new ImmutableMap.Builder<String, Type>()
                .put(CHEST.getName(), CHEST)
                .put(TNT_CHEST.getName(), TNT_CHEST)
                .put(SPAWNER_GRAVE.getName(), SPAWNER_GRAVE)
                .put(EMPTY_GRAVE.getName(), EMPTY_GRAVE)
                .put(SPAWNER.getName(), SPAWNER)
                .put(CEILING_SPAWNER.getName(), CEILING_SPAWNER)
                .put(STAIRS.getName(), STAIRS)
                .put(SEWER_HOLE.getName(), SEWER_HOLE)
                .put(CROPS.getName(), CROPS)
                .build();

        void place(LevelAccessor world, RandomSource rand, BlockPos pos, Direction direction, BoundingBox bounds, Theme theme, SecondaryTheme secondaryTheme, int stage);

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
        Range amount = new Range(jsonAmount.get("min").getAsInt(), jsonAmount.get("max").getAsInt());

        DungeonModelFeature followUp = null;

        if (object.has("follow_up")) {
            followUp = fromJson(object.getAsJsonObject("follow_up"), file, positions);
        }

        return new DungeonModelFeature(Type.TYPES.get(type), positions, amount, followUp);
    }

    private record Position(Vec3i position, Direction facing) {

        public static Position fromJson(JsonObject object) {
            Vec3i position = JSONUtils.getOffset(object);
            Direction facing = object.has("facing") ?
                    Direction.valueOf(object.get("facing").getAsString().toUpperCase(Locale.ROOT))
                    : Direction.NORTH;
            return new Position(position, facing);
        }

        public DirectionalBlockPos worldPos(int x, int y, int z, Rotation rotation, DungeonModel model) {
            return switch (rotation) {
                case CLOCKWISE_90 -> new DirectionalBlockPos(x + model.length - position.getZ() - 1, y + position.getY(), z + position.getX(),
                        facing.getAxis() != Direction.Axis.Y ? facing.getClockWise() : facing);
                case CLOCKWISE_180 -> new DirectionalBlockPos(x + model.width - position.getX() - 1, y + position.getY(), z + model.length - position.getZ() - 1,
                        facing.getAxis() != Direction.Axis.Y ? facing.getOpposite() : facing);
                case COUNTERCLOCKWISE_90 -> new DirectionalBlockPos(x + position.getZ(), y + position.getY(), z + model.width - position.getX() - 1,
                        facing.getAxis() != Direction.Axis.Y ? facing.getCounterClockWise() : facing);
                default -> new DirectionalBlockPos(x + position.getX(), y + position.getY(), z + position.getZ(), facing);
            };
        }

    }

}
