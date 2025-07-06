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

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

import java.util.List;

public class DungeonPiece extends StructurePiece {
    private static final String NBT_KEY_COMPONENTS = "Components";
    private static final String NBT_KEY_WORLD_GEN_CONTEXT = "WorldGenContext";

    private final DungeonWorldGenContext worldGenContext;
    private final List<DungeonComponent> components;

    public static DungeonPiece withComponents(List<DungeonComponent> components, DungeonWorldGenContext worldGenContext) {
        if (components.isEmpty()) {
            throw new IllegalArgumentException("The list of initial components must not be empty.");
        }
        return new DungeonPiece(components, worldGenContext);
    }

    public DungeonPiece(DungeonComponent component, DungeonWorldGenContext worldGenContext) {
        this(Lists.newArrayList(component), worldGenContext);
    }

    private DungeonPiece(List<DungeonComponent> components, DungeonWorldGenContext worldGenContext) {
        super(ModStructurePieceTypes.GENERIC.get(), 0, null);
        this.components = components;
        this.worldGenContext = worldGenContext;
        if (!this.components.isEmpty()) {
            updateBoundingBox();
        }
    }

    public DungeonPiece(CompoundTag nbt) {
        this(ModStructurePieceTypes.GENERIC.get(), nbt);
    }

    public DungeonPiece(StructurePieceType type, CompoundTag nbt) {
        super(type, nbt);
        this.worldGenContext = StorageHelper.decode(nbt.get(NBT_KEY_WORLD_GEN_CONTEXT), DungeonWorldGenContext.CODEC);
        this.components = StorageHelper.decode(nbt.get(NBT_KEY_COMPONENTS), DungeonComponent.CODEC.listOf());
        updateBoundingBox();
    }

    @Override
    public void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        nbt.put(NBT_KEY_WORLD_GEN_CONTEXT, StorageHelper.encode(worldGenContext, DungeonWorldGenContext.CODEC));
        nbt.put(NBT_KEY_COMPONENTS, StorageHelper.encode(components, DungeonComponent.CODEC.listOf()));
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager p_73428_, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox worldGenBounds, ChunkPos p_73432_, BlockPos pos) {
        for (DungeonComponent component : components) {
            component.generate(level, worldGenBounds, random, worldGenContext);
        }
    }

    public void updateBoundingBox() {
        BoundingBoxBuilder builder = components.getFirst().boundingBox(); // There is always at least one component
        for (int i = 1; i < components.size(); ++i) {
            builder.encapsulate(components.get(i).boundingBox());
        }
        this.boundingBox = builder.create();
    }

    public void addComponent(DungeonComponent component) {
        this.components.add(component);
    }
}