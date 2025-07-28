package xiroc.dungeoncrawl.data.structure;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import xiroc.dungeoncrawl.datapack.DatapackNamespaces;

public interface ModStructureSets {
    ResourceKey<StructureSet> DUNGEONS = ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(DatapackNamespaces.DEFAULT, "dungeons"));

    static void generate(BootstrapContext<StructureSet> context) {
        var structures = context.lookup(Registries.STRUCTURE);

        context.register(DUNGEONS, new StructureSet(structures.getOrThrow(ModStructures.DUNGEON), new RandomSpreadStructurePlacement(
                32,// spacing
                12,// separation
                RandomSpreadType.LINEAR,
                10387313 // salt
        )));
    }

}
