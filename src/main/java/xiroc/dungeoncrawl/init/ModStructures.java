package xiroc.dungeoncrawl.init;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.Dungeon;

import java.util.Map;

public class ModStructures {

    protected static Holder<Structure> DUNGEON;

    public static void register() {
        TerrainAdjustment dungeonTerrainAdjustment = Config.BURY.get() ? TerrainAdjustment.BURY : TerrainAdjustment.NONE;
        DUNGEON = register(BuiltinModStructures.DUNGEON, new Dungeon(structure(ModTags.HAS_DUNGEON, Dungeon.GENERATION_STEP, dungeonTerrainAdjustment)));
    }

    private static Structure.StructureSettings structure(TagKey<Biome> p_236546_, Map<MobCategory, StructureSpawnOverride> p_236547_, GenerationStep.Decoration p_236548_, TerrainAdjustment p_236549_) {
        return new Structure.StructureSettings(biomes(p_236546_), p_236547_, p_236548_, p_236549_);
    }

    private static Structure.StructureSettings structure(TagKey<Biome> p_236539_, GenerationStep.Decoration p_236540_, TerrainAdjustment p_236541_) {
        return structure(p_236539_, Map.of(), p_236540_, p_236541_);
    }

    private static Structure.StructureSettings structure(TagKey<Biome> p_236543_, TerrainAdjustment p_236544_) {
        return structure(p_236543_, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, p_236544_);
    }

    private static Holder<Structure> register(ResourceKey<Structure> p_236534_, Structure p_236535_) {
        return BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, p_236534_, p_236535_);
    }

    private static HolderSet<Biome> biomes(TagKey<Biome> p_236537_) {
        return BuiltinRegistries.BIOME.getOrCreateTag(p_236537_);
    }

}
