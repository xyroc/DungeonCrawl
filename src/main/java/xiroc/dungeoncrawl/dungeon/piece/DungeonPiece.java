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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonPiece extends BaseDungeonPiece {
    protected static final String NBT_KEY_COMPONENTS = "Components";
    protected static final String NBT_KEY_STAGE = "Stage";

    private final List<DungeonComponent> components;
    public final int stage;

    public static DungeonPiece withComponents(List<DungeonComponent> components, Delegate<PrimaryTheme> primaryTheme, Delegate<SecondaryTheme> secondaryTheme, int stage) {
        if (components.isEmpty()) {
            throw new IllegalArgumentException("The list of initial components must not be empty.");
        }
        DungeonPiece piece = new DungeonPiece(components.get(0), primaryTheme, secondaryTheme, stage);
        for (int i = 1; i < components.size(); ++i) {
            piece.components.add(components.get(i));
        }
        return piece;
    }

    public DungeonPiece(DungeonComponent component, Delegate<PrimaryTheme> primaryTheme, Delegate<SecondaryTheme> secondaryTheme, int stage) {
        this(ModStructurePieceTypes.GENERIC, component, primaryTheme, secondaryTheme, stage);
    }

    public DungeonPiece(StructurePieceType type, DungeonComponent component, Delegate<PrimaryTheme> primaryTheme, Delegate<SecondaryTheme> secondaryTheme, int stage) {
        super(type, null, primaryTheme, secondaryTheme);
        this.components = new ArrayList<>();
        this.components.add(component);
        this.stage = stage;
        createBoundingBox();
    }

    public DungeonPiece(CompoundTag nbt) {
        this(ModStructurePieceTypes.GENERIC, nbt);
    }

    public DungeonPiece(StructurePieceType type, CompoundTag nbt) {
        super(type, nbt);
        this.stage = nbt.getInt(NBT_KEY_STAGE);
        this.components = StorageHelper.decode(nbt.get(NBT_KEY_COMPONENTS), DungeonComponent.CODEC.listOf());
        createBoundingBox();
    }

    @Override
    public void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        nbt.putInt(NBT_KEY_STAGE, stage);
        nbt.put(NBT_KEY_COMPONENTS, StorageHelper.encode(components, DungeonComponent.CODEC.listOf()));
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_73428_, ChunkGenerator chunkGenerator, Random random, BoundingBox worldGenBounds, ChunkPos p_73432_, BlockPos pos) {
        for (DungeonComponent component : components) {
            component.generate(level, worldGenBounds, random, primaryTheme.get(), secondaryTheme.get(), stage);
        }
    }

    protected void decorate(LevelAccessor world, BlockPos pos, PrimaryTheme primaryTheme, Random random, BoundingBox worldGenBounds, BoundingBox structureBounds, Blueprint blueprint) {
        // TODO
    }

    public void createBoundingBox() {
        BoundingBoxBuilder builder = components.get(0).boundingBox(); // There is always at least one component
        for (int i = 1; i < components.size(); ++i) {
            builder.encapsulate(components.get(i).boundingBox());
        }
        this.boundingBox = builder.create();
    }

    public void addComponent(DungeonComponent component) {
        this.components.add(component);
    }
}