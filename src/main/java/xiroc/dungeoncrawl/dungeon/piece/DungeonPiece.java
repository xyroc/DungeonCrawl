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
    protected static final String NBT_KEY_LOOT_TABLE = "LootTable";
    protected static final String NBT_KEY_ENTRANCES_X_AXIS = "Entrances_X";
    protected static final String NBT_KEY_ENTRANCES_Z_AXIS = "Entrances_Z";

    public int stage;
    public ResourceLocation lootTable;
    public Blueprint blueprint;

    // Entrances aligned to the x and z axis respectively
    private final List<BlockPos> entrancesX;
    private final List<BlockPos> entrancesZ;

    public DungeonPiece() {
        this(ModStructurePieceTypes.GENERIC_PIECE, new BoundingBox(0, 0, 0, 0, 0, 0));
    }

    public DungeonPiece(CompoundTag nbt) {
        this(ModStructurePieceTypes.GENERIC_PIECE, nbt);
    }

    public DungeonPiece(StructurePieceType type, BoundingBox boundingBox) {
        super(type, boundingBox);
        this.entrancesX = new ArrayList<>();
        this.entrancesZ = new ArrayList<>();
    }

    public DungeonPiece(StructurePieceType type, CompoundTag nbt) {
        super(type, nbt);
        this.stage = nbt.getInt(NBT_KEY_STAGE);

        if (nbt.contains(NBT_KEY_BLUEPRINT)) {
            this.blueprint = Blueprints.getBlueprint(new ResourceLocation(nbt.getString(NBT_KEY_BLUEPRINT)));
        }

        if (nbt.contains(NBT_KEY_LOOT_TABLE)) {
            this.lootTable = new ResourceLocation(nbt.getString(NBT_KEY_LOOT_TABLE));
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

        createBoundingBox();
    }

    @Override
    public void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        nbt.putInt(NBT_KEY_STAGE, stage);

        if (blueprint != null) {
            nbt.putString(NBT_KEY_BLUEPRINT, blueprint.key().toString());
        }

        if (lootTable != null) {
            nbt.putString(NBT_KEY_LOOT_TABLE, lootTable.toString());
        }

        if (entrancesX != null) {
            nbt.put(NBT_KEY_ENTRANCES_X_AXIS, StorageHelper.encode(entrancesX, StorageHelper.BLOCK_POS_LIST_CODEC));
        }
        if (entrancesZ != null) {
            nbt.put(NBT_KEY_ENTRANCES_Z_AXIS, StorageHelper.encode(entrancesZ, StorageHelper.BLOCK_POS_LIST_CODEC));
        }
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_73428_, ChunkGenerator chunkGenerator, Random random, BoundingBox worldGenBounds, ChunkPos p_73432_, BlockPos pos) {
        // TODO
        blueprint.build(level, this.position, this.rotation, worldGenBounds, random, this.primaryTheme, this.secondaryTheme, this.stage);
        placeEntrances(level, worldGenBounds, random);
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

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    @Override
    public Rotation getRotation() {
        return this.rotation;
    }

    public void setPosition(int x, int y, int z) {
        this.position = new BlockPos(x, y, z);
    }

    public void addEntrance(BlockPos position, Direction direction) {
        switch (direction.getClockWise().getAxis()) {
            case X -> entrancesX.add(position);
            case Z -> entrancesZ.add(position);
        }
    }


}