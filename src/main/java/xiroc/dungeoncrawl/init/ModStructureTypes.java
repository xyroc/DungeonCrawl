package xiroc.dungeoncrawl.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Dungeon;

public interface ModStructureTypes {
    DeferredRegister<StructureType<?>> REGISTER = DeferredRegister.create(Registries.STRUCTURE_TYPE, DungeonCrawl.MOD_ID);

    DeferredHolder<StructureType<?>, ?> DUNGEON = REGISTER.register("dungeon", () -> () -> Dungeon.CODEC);
}
