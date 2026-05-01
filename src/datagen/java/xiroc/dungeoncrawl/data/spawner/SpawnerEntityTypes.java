package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.data.tags.ItemPoolTags;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityProperties;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerEntityTypes {
    static void generate(BootstrapContext<SpawnerEntityType> context) {
        var itemPools = context.lookup(DatapackRegistries.ITEM_POOLS);

        context.register(SharedKeys.Spawner.EntityType.CAVE_SPIDER_LEVEL_0, new SpawnerEntityType.Builder()
                .entity(getKey(EntityType.CAVE_SPIDER))
                .properties(Holder.direct(new SpawnerEntityProperties.Builder()
                        .helmet(IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_0)))
                                .mainHand(IRandom.<Item>builder()
                                        .add(Items.WOODEN_HOE)
                                        .add(Items.WOODEN_SHOVEL))
                        .build()))
                .build());

        context.register(SharedKeys.Spawner.EntityType.CAVE_SPIDER_LEVEL_1, new SpawnerEntityType.Builder()
                .entity(getKey(EntityType.CAVE_SPIDER))
                .properties(Holder.direct(new SpawnerEntityProperties.Builder()
                        .helmet(IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_1)))
                        .mainHand(IRandom.<Item>builder()
                                .add(Items.WOODEN_SWORD)
                                .add(Items.WOODEN_SHOVEL))
                        .build()))
                .build());

        context.register(SharedKeys.Spawner.EntityType.CAVE_SPIDER_LEVEL_2, new SpawnerEntityType.Builder()
                .entity(getKey(EntityType.CAVE_SPIDER))
                .properties(Holder.direct(new SpawnerEntityProperties.Builder()
                        .helmet(IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_2)))
                        .mainHand(IRandom.<Item>builder()
                                .add(Items.WOODEN_SWORD, 3)
                                .add(Items.STONE_SHOVEL, 2)
                                .add(Items.WOODEN_AXE)
                                .add(Items.STONE_AXE))
                        .build()))
                .build());

        context.register(SharedKeys.Spawner.EntityType.CAVE_SPIDER_LEVEL_3, new SpawnerEntityType.Builder()
                .entity(getKey(EntityType.CAVE_SPIDER))
                .properties(Holder.direct(new SpawnerEntityProperties.Builder()
                        .helmet(IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_3)))
                        .mainHand(IRandom.<Item>builder()
                                .add(Items.STONE_AXE, 3)
                                .add(Items.IRON_AXE))
                        .build()))
                .build());

        context.register(SharedKeys.Spawner.EntityType.CAVE_SPIDER_LEVEL_4, new SpawnerEntityType.Builder()
                .entity(getKey(EntityType.CAVE_SPIDER))
                .properties(Holder.direct(new SpawnerEntityProperties.Builder()
                        .helmet(IRandom.<Item>builder()
                                .setPools(itemPools.getOrThrow(ItemPoolTags.HELMET_LEVEL_4)))
                        .mainHand(IRandom.<Item>builder()
                                .add(Items.STONE_AXE, 2)
                                .add(Items.IRON_AXE, 4)
                                .add(Items.DIAMOND_AXE, 2)
                                .add(Items.NETHERITE_AXE))
                        .build()))
                .build());

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
