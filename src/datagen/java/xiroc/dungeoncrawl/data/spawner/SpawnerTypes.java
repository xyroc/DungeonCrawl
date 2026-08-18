package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.SpawnData;
import xiroc.dungeoncrawl.data.ScalingInteger;
import xiroc.dungeoncrawl.data.tags.spawner.SpawnerEntityTypePoolTags;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Range;

public interface SpawnerTypes {
    static void generate(BootstrapContext<SpawnerType> context) {
        var spawnerEntityTypePools = context.lookup(DatapackRegistries.SPAWNER_ENTITY_TYPE_POOLS);

        context.register(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_0, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.CAVE_SPIDER_LEVEL_0)))
                .spawnAmount(new Range(1, 3))
                .spawnDelay(new Range(5, 20), new Range(120, 160))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 12), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_1, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.CAVE_SPIDER_LEVEL_1)))
                .spawnAmount(new Range(1, 3))
                .spawnDelay(new Range(5, 20), new Range(115, 155))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 12), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_2, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.CAVE_SPIDER_LEVEL_2)))
                .spawnAmount(new Range(1, 3))
                .spawnDelay(new Range(5, 20), new Range(110, 150))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 12), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_3, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.CAVE_SPIDER_LEVEL_3)))
                .spawnAmount(new Range(2, 3))
                .spawnDelay(new Range(5, 20), new Range(105, 145))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 12), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.CAVE_SPIDER_LEVEL_4, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.CAVE_SPIDER_LEVEL_4)))
                .spawnAmount(new Range(2, 3))
                .spawnDelay(new Range(5, 20), new Range(100, 140))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        ScalingInteger minSpawnDelay = new ScalingInteger(120, 0, 0.95);
        ScalingInteger maxSpawnDelay = new ScalingInteger(240, 0, 0.95);

        context.register(SpawnerTypeKeys.LEVEL_0, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.LEVEL_0)))
                .spawnAmount(new Range(1, 2))
                .spawnDelay(new Range(5, 10), new Range(minSpawnDelay.atStep(0), maxSpawnDelay.atStep(0)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.LEVEL_1, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.LEVEL_1)))
                .spawnAmount(new Range(1, 2))
                .spawnDelay(new Range(5, 10), new Range(minSpawnDelay.atStep(1), maxSpawnDelay.atStep(1)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.LEVEL_2, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.LEVEL_2)))
                .spawnAmount(new Range(1, 3))
                .spawnDelay(new Range(0, 10), new Range(minSpawnDelay.atStep(2), maxSpawnDelay.atStep(2)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.LEVEL_3, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.LEVEL_3)))
                .spawnAmount(new Range(1, 3))
                .spawnDelay(new Range(0, 10), new Range(minSpawnDelay.atStep(3), maxSpawnDelay.atStep(3)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.LEVEL_4, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.LEVEL_4)))
                .spawnAmount(new Range(2, 3))
                .spawnDelay(new Range(0, 10), new Range(minSpawnDelay.atStep(4), maxSpawnDelay.atStep(4)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.DROWNED_LEVEL_0, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.DROWNED_LEVEL_0)))
                .spawnAmount(new Range(1, 2))
                .spawnDelay(new Range(5, 10), new Range(minSpawnDelay.atStep(0), maxSpawnDelay.atStep(0)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.DROWNED_LEVEL_1, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.DROWNED_LEVEL_1)))
                .spawnAmount(new Range(1, 2))
                .spawnDelay(new Range(0, 10), new Range(minSpawnDelay.atStep(1), maxSpawnDelay.atStep(1)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.DROWNED_LEVEL_2, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.DROWNED_LEVEL_2)))
                .spawnAmount(new Range(1, 3))
                .spawnDelay(new Range(0, 10), new Range(minSpawnDelay.atStep(2), maxSpawnDelay.atStep(2)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.DROWNED_LEVEL_3, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.DROWNED_LEVEL_3)))
                .spawnAmount(new Range(1, 3))
                .spawnDelay(new Range(0, 10), new Range(minSpawnDelay.atStep(3), maxSpawnDelay.atStep(3)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());

        context.register(SpawnerTypeKeys.DROWNED_LEVEL_4, new SpawnerType.Builder()
                .entities(IRandom.<Holder<SpawnerEntityType>>builder()
                        .setPools(spawnerEntityTypePools.getOrThrow(SpawnerEntityTypePoolTags.DROWNED_LEVEL_4)))
                .spawnAmount(new Range(1, 3))
                .spawnDelay(new Range(0, 10), new Range(minSpawnDelay.atStep(4), maxSpawnDelay.atStep(4)))
                .spawnRules(new SpawnData.CustomSpawnRules(new InclusiveRange<>(0, 15), new InclusiveRange<>(0, 10)))
                .build());
    }
}