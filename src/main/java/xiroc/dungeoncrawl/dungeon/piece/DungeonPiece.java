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

package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprints;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.FeatureSet;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.PlacedFeature;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonPiece extends BaseDungeonPiece {
    protected static final String NBT_KEY_BLUEPRINT = "Blueprint";
    protected static final String NBT_KEY_FEATURES = "Features";
    protected static final String NBT_KEY_STAGE = "Stage";
    protected static final String NBT_KEY_ENTRANCES_X_AXIS = "Entrances_X";
    protected static final String NBT_KEY_ENTRANCES_Z_AXIS = "Entrances_Z";

    public final Blueprint blueprint;
    public final int stage;

    // Entrances aligned to the x and z axis respectively
    private final List<BlockPos> entrancesX;
    private final List<BlockPos> entrancesZ;

    private final List<FeatureSet> features;

    public DungeonPiece(Blueprint blueprint, BlockPos position, Rotation rotation, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        this(ModStructurePieceTypes.GENERIC, blueprint, position, rotation, primaryTheme, secondaryTheme, stage);
    }

    public DungeonPiece(StructurePieceType type, Blueprint blueprint, BlockPos position, Rotation rotation, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        super(type, null, position, rotation, primaryTheme, secondaryTheme);
        this.blueprint = blueprint;
        this.stage = stage;
        this.entrancesX = new ArrayList<>();
        this.entrancesZ = new ArrayList<>();
        this.features = new ArrayList<>();
        createBoundingBox();
    }

    public DungeonPiece(CompoundTag nbt) {
        this(ModStructurePieceTypes.GENERIC, nbt);
    }

    public DungeonPiece(StructurePieceType type, CompoundTag nbt) {
        super(type, nbt);
        this.stage = nbt.getInt(NBT_KEY_STAGE);

        if (nbt.contains(NBT_KEY_BLUEPRINT)) {
            this.blueprint = Blueprints.getBlueprint(new ResourceLocation(nbt.getString(NBT_KEY_BLUEPRINT)));
        } else {
            this.blueprint = Blueprint.EMPTY;
        }

        if (nbt.contains(NBT_KEY_ENTRANCES_X_AXIS)) {
            this.entrancesX = StorageHelper.decode(nbt.get(NBT_KEY_ENTRANCES_X_AXIS), StorageHelper.BLOCK_POS_LIST_CODEC);
        } else {
            this.entrancesX = null;
        }
        if (nbt.contains(NBT_KEY_ENTRANCES_Z_AXIS)) {
            this.entrancesZ = StorageHelper.decode(nbt.get(NBT_KEY_ENTRANCES_Z_AXIS), StorageHelper.BLOCK_POS_LIST_CODEC);
        } else {
            this.entrancesZ = null;
        }

        if (nbt.contains(NBT_KEY_FEATURES)) {
            this.features = StorageHelper.decode(nbt.get(NBT_KEY_FEATURES), FeatureSet.CODEC.listOf());
        } else {
            this.features = null;
        }

        createBoundingBox();
    }

    @Override
    public void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        nbt.putInt(NBT_KEY_STAGE, stage);
        nbt.putString(NBT_KEY_BLUEPRINT, blueprint.key().toString());
        if (entrancesX != null) {
            nbt.put(NBT_KEY_ENTRANCES_X_AXIS, StorageHelper.encode(entrancesX, StorageHelper.BLOCK_POS_LIST_CODEC));
        }
        if (entrancesZ != null) {
            nbt.put(NBT_KEY_ENTRANCES_Z_AXIS, StorageHelper.encode(entrancesZ, StorageHelper.BLOCK_POS_LIST_CODEC));
        }
        if (features != null) {
            nbt.put(NBT_KEY_FEATURES, StorageHelper.encode(features, FeatureSet.CODEC.listOf()));
        }
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_73428_, ChunkGenerator chunkGenerator, Random random, BoundingBox worldGenBounds, ChunkPos p_73432_, BlockPos pos) {
        // TODO
        blueprint.build(level, this.position, this.rotation, worldGenBounds, random, this.primaryTheme, this.secondaryTheme, this.stage);
        placeEntrances(level, worldGenBounds, random);
        if (this.features != null) {
            for (FeatureSet features : this.features) {
                for (PlacedFeature feature : features.features()) {
                    feature.place(level, random, worldGenBounds, primaryTheme, secondaryTheme, stage);
                }
            }
        }
    }

    protected void placeEntrances(WorldGenLevel level, BoundingBox worldGenBounds, Random random) {
        if (this.entrancesX != null) {
            for (BlockPos entrance : entrancesX) {
                WorldEditor.placeEntrance(level, primaryTheme.stairs(), entrance, Direction.EAST, worldGenBounds, random, false, true);
            }
        }
        if (this.entrancesZ != null) {
            for (BlockPos entrance : entrancesZ) {
                WorldEditor.placeEntrance(level, primaryTheme.stairs(), entrance, Direction.SOUTH, worldGenBounds, random, false, true);
            }
        }
    }

    protected void decorate(LevelAccessor world, BlockPos pos, PrimaryTheme primaryTheme, Random random, BoundingBox worldGenBounds, BoundingBox structureBounds, Blueprint blueprint) {
        // TODO
    }

    protected void placeFeatures(LevelAccessor world, BoundingBox bounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, Random rand, int stage) {
        // TODO
    }

    public void createBoundingBox() {
        // TODO
        if (this.blueprint != null) {
            this.boundingBox = this.blueprint.createBoundingBox(this.position, this.rotation);
        }
    }

    @Override
    public Rotation getRotation() {
        return this.rotation;
    }

    public void addEntrance(BlockPos position, Direction direction) {
        switch (direction.getClockWise().getAxis()) {
            case X -> entrancesX.add(position);
            case Z -> entrancesZ.add(position);
        }
    }

    public void addFeature(FeatureSet features) {
        this.features.add(features);
    }
}