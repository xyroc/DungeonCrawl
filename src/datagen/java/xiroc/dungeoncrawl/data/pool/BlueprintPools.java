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

    ResourceLocation __PART__ = DataGen.resource("part");

    ResourceLocation __CEILING__ = DataGen.resource(__PART__, "ceiling");
    ResourceLocation __CEILING_CENTERED_ = DataGen.resource(__CEILING__, "centered");
    ResourceKey<IRandom<Holder<Blueprint>>> CEILING_CENTERED_5X5 = key(DataGen.resource(__CEILING_CENTERED_, "5x5"));

    ResourceLocation __CRYPT__ = DataGen.resource(__PART__, "crypt");
    ResourceKey<IRandom<Holder<Blueprint>>> CRYPT_WALL = key(DataGen.resource(__CRYPT__, "wall"));

    ResourceLocation __CRYPT_CEILING__ = DataGen.resource(__CEILING__, "ceiling");
    ResourceLocation __CRYPT_CEILING_CENTERED__ = DataGen.resource(__CRYPT_CEILING__, "centered");
    ResourceKey<IRandom<Holder<Blueprint>>> CRYPT_CEILING_CENTERED_5X5 = key(DataGen.resource(__CRYPT_CEILING_CENTERED__, "5x5"));

    ResourceLocation __CRYPT_SARCOPHAGUS__ = DataGen.resource(__CRYPT__, "sarcophagus");
    ResourceKey<IRandom<Holder<Blueprint>>> CRYPT_SARCOPHAGUS_SMALL = key(DataGen.resource(__CRYPT_SARCOPHAGUS__, "small"));

    ResourceLocation __LAB__ = DataGen.resource(__PART__, "lab");
    ResourceKey<IRandom<Holder<Blueprint>>> LAB_CONTAINER = key(DataGen.resource(__LAB__, "container"));

    static void generate(BootstrapContext<IRandom<Holder<Blueprint>>> context) {
        var blueprints = context.lookup(DatapackRegistries.BLUEPRINT);

        context.register(CEILING_CENTERED_5X5, IRandom.<Holder<Blueprint>>builder()
                .add(blueprints.getOrThrow(BlueprintKeys.Part.CEILING_CENTERED_5X5_FLAT))
                .add(blueprints.getOrThrow(BlueprintKeys.Part.CEILING_CENTERED_5X5_RAISED))
                .build());

        context.register(CRYPT_CEILING_CENTERED_5X5, IRandom.<Holder<Blueprint>>builder()
                .add(blueprints.getOrThrow(BlueprintKeys.Part.CEILING_CENTERED_5X5_ORNATE), 2)
                .build());

        context.register(CRYPT_SARCOPHAGUS_SMALL, IRandom.<Holder<Blueprint>>builder()
                .add(blueprints.getOrThrow(BlueprintKeys.Part.SARCOPHAGUS_SMALL_EMPTY))
                .add(blueprints.getOrThrow(BlueprintKeys.Part.SARCOPHAGUS_SMALL_GRAVE))
                .build());

        context.register(CRYPT_WALL, IRandom.<Holder<Blueprint>>builder()
                .add(blueprints.getOrThrow(BlueprintKeys.Part.CRYPT_WALL_MAUSOLEUM), 2)
                .add(blueprints.getOrThrow(BlueprintKeys.Part.CRYPT_WALL_PLAIN))
                .build());

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
