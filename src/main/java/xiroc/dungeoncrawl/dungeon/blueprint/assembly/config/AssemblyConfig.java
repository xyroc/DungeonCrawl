package xiroc.dungeoncrawl.dungeon.blueprint.assembly.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.RegisterEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.type.AssemblyType;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.type.AssemblyTypeBuilder;
import xiroc.dungeoncrawl.init.ModRegistries;

import java.util.function.Function;

/**
 * Provides static configuration values to create an {@link AssemblyType} via an {@link AssemblyTypeBuilder}.
 * See {@link AssemblyType} for more information on blueprint assembly.
 */
public interface AssemblyConfig {
    Codec<AssemblyConfig> CODEC = ModRegistries.BLUEPRINT_ASSEMBLY_CONFIG_TYPE.byNameCodec().dispatch(
            AssemblyConfig::type,
            Function.identity()
    );

    static void register(RegisterEvent.RegisterHelper<MapCodec<? extends AssemblyConfig>> registry) {
        registry.register(DungeonCrawl.locate("default"), SimpleAssemblyConfig.MAP_CODEC);
        registry.register(DungeonCrawl.locate("cardinal_entrances"), CardinalEntrancesAssemblyConfig.MAP_CODEC);
    }

    AssemblyTypeBuilder makeTypeBuilder();

    MapCodec<? extends AssemblyConfig> type();
}
