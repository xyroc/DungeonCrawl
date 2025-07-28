package xiroc.dungeoncrawl.data.structure;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import xiroc.dungeoncrawl.datapack.DatapackNamespaces;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.init.ModTags;

import java.util.Map;

public interface ModStructures {
    ResourceKey<Structure> DUNGEON = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(DatapackNamespaces.DEFAULT, "dungeon"));

    static void generate(BootstrapContext<Structure> context) {
        var biomes = context.lookup(Registries.BIOME);

        context.register(DUNGEON, new Dungeon(new Structure.StructureSettings(
                biomes.get(ModTags.HAS_DUNGEON).orElseThrow(),
                Map.of(),
                GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
                TerrainAdjustment.NONE)
        ));
    }
}
