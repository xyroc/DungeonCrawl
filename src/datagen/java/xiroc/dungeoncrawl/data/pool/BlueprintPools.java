package xiroc.dungeoncrawl.data.pool;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.data.blueprint.BlueprintKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface BlueprintPools {

    ResourceLocation __LAB = DataGen.resource("lab");
    ResourceKey<IRandom<Holder<Blueprint>>> LAB_CONTAINER = key(DataGen.resource(__LAB, "container"));

    static void generate(BootstrapContext<IRandom<Holder<Blueprint>>> context) {
        var blueprints = context.lookup(DatapackRegistries.BLUEPRINT);

        context.register(LAB_CONTAINER, IRandom.<Holder<Blueprint>>builder()
                .add(blueprints.getOrThrow(BlueprintKeys.Part.LAB_CONTAINER_CROPS))
                .add(blueprints.getOrThrow(BlueprintKeys.Part.LAB_CONTAINER_FLOWER))
                .add(blueprints.getOrThrow(BlueprintKeys.Part.LAB_CONTAINER_KELP))
                .add(blueprints.getOrThrow(BlueprintKeys.Part.LAB_CONTAINER_SPAWNER))
                .build());
    }

    private static ResourceKey<IRandom<Holder<Blueprint>>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.BLUEPRINT_POOLS, identifier);
    }
}
