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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprints;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.Themes;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.Random;

public class DungeonPiece extends StructurePiece {

    protected static final String NBT_KEY_POSITION_X = "x";
    protected static final String NBT_KEY_POSITION_Y = "y";
    protected static final String NBT_KEY_POSITION_Z = "z";
    protected static final String NBT_KEY_BLUEPRINT = "Blueprint";
    protected static final String NBT_KEY_ROTATION = "Rotation";
    protected static final String NBT_KEY_FEATURES = "Features";
    protected static final String NBT_KEY_PRIMARY_THEME = "PrimaryTheme";
    protected static final String NBT_KEY_SECONDARY_THEME = "SecondaryTheme";
    protected static final String NBT_KEY_STAGE = "Stage";
    protected static final String NBT_KEY_LOOT_TABLE = "LootTable";

    public Rotation rotation;
    public BlockPos position;
    public int stage;
    public PrimaryTheme primaryTheme;
    public SecondaryTheme secondaryTheme;
    public ResourceLocation lootTable;
    public Blueprint blueprint;

    public DungeonPiece() {
        this(StructurePieceTypes.GENERIC_PIECE, new BoundingBox(0 ,0 ,0 ,0 ,0 ,0));
    }

    public DungeonPiece(CompoundTag nbt) {
        this(StructurePieceTypes.GENERIC_PIECE, nbt);
    }

    public DungeonPiece(StructurePieceType type, BoundingBox boundingBox) {
        super(type, 0, boundingBox);
        this.rotation = Rotation.NONE;
    }

    public DungeonPiece(StructurePieceType type, CompoundTag nbt) {
        super(type, nbt);
        this.position = new BlockPos(nbt.getInt(NBT_KEY_POSITION_X), nbt.getInt(NBT_KEY_POSITION_Y), nbt.getInt(NBT_KEY_POSITION_Z));
        this.rotation = Orientation.getRotation(nbt.getInt(NBT_KEY_ROTATION));
        this.stage = nbt.getInt(NBT_KEY_STAGE);

        if (nbt.contains(NBT_KEY_BLUEPRINT)) {
            this.blueprint = Blueprints.getBlueprint(new ResourceLocation(nbt.getString(NBT_KEY_BLUEPRINT)));
        }

        if (nbt.contains(NBT_KEY_PRIMARY_THEME)) {
            this.primaryTheme = Themes.getPrimary(new ResourceLocation(nbt.getString(NBT_KEY_PRIMARY_THEME)));
        }

        if (nbt.contains(NBT_KEY_SECONDARY_THEME)) {
            this.secondaryTheme = Themes.getSecondary(new ResourceLocation(nbt.getString(NBT_KEY_SECONDARY_THEME)));
        }

        if (nbt.contains(NBT_KEY_LOOT_TABLE)) {
            this.lootTable = new ResourceLocation(nbt.getString(NBT_KEY_LOOT_TABLE));
        }
        createBoundingBox();
    }

    @Override
    public void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        nbt.putInt(NBT_KEY_POSITION_X, position.getX());
        nbt.putInt(NBT_KEY_POSITION_Y, position.getY());
        nbt.putInt(NBT_KEY_POSITION_Z, position.getZ());

        nbt.putInt(NBT_KEY_ROTATION, Orientation.rotationAsInt(this.rotation));
        nbt.putInt(NBT_KEY_STAGE, stage);

        if (blueprint != null) {
            nbt.putString(NBT_KEY_BLUEPRINT, blueprint.key().toString());
        }

        if (primaryTheme != null) {
            nbt.putString(NBT_KEY_PRIMARY_THEME, primaryTheme.key().toString());
        }

        if (secondaryTheme != null) {
            nbt.putString(NBT_KEY_SECONDARY_THEME, secondaryTheme.key().toString());
        }

        if (lootTable != null) {
            nbt.putString(NBT_KEY_LOOT_TABLE, lootTable.toString());
        }
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_73428_, ChunkGenerator chunkGenerator, Random random, BoundingBox worldGenBounds, ChunkPos p_73432_, BlockPos pos) {
        // TODO
    }

    protected void decorate(LevelAccessor world, BlockPos pos, PrimaryTheme primaryTheme, Random random, BoundingBox worldGenBounds, BoundingBox structureBounds, Blueprint blueprint) {
        // TODO
    }

    protected void placeFeatures(LevelAccessor world, BoundingBox bounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, Random rand, int stage) {
        // TODO
    }

    public void createBoundingBox() {
        // TODO
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

}