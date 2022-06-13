package xiroc.dungeoncrawl.init;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import xiroc.dungeoncrawl.config.Config;

public class ModStructureSets {

    protected static Holder<StructureSet> DUNGEONS;

    public static void register() {
        DUNGEONS = register(BuiltinModStructureSets.DUNGEONS, ModStructures.DUNGEON, new RandomSpreadStructurePlacement(Config.SPACING.get(), Config.SEPARATION.get(), RandomSpreadType.LINEAR, 10387313));
    }

    private static Holder<StructureSet> register(ResourceKey<StructureSet> p_211129_, StructureSet p_211130_) {
        return BuiltinRegistries.register(BuiltinRegistries.STRUCTURE_SETS, p_211129_, p_211130_);
    }

    private static Holder<StructureSet> register(ResourceKey<StructureSet> p_211132_, Holder<Structure> p_211133_, StructurePlacement p_211134_) {
        return register(p_211132_, new StructureSet(p_211133_, p_211134_));
    }

}
