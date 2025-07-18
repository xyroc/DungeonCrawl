package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityProperties;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerEntityTypes {
    static void generate(BootstrapContext<SpawnerEntityType> context) {
        context.register(SharedKeys.Spawner.EntityType.ZOMBIE, new SpawnerEntityType.Builder()
                .entity(getKey(EntityType.ZOMBIE))
                .properties(Holder.direct(new SpawnerEntityProperties.Builder()
                        .helmet(IRandom.<Item>builder()
                                .add(Items.GOLDEN_HELMET))
                        .mainHand(IRandom.<Item>builder()
                                .add(Items.STONE_SWORD))
                        .build()))
                .build());
    }

    private static ResourceKey<EntityType<?>> getKey(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getResourceKey(type).orElseThrow();
    }
}
