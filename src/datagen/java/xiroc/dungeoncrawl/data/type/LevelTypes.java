package xiroc.dungeoncrawl.data.type;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.data.blueprint.BlueprintKeys;
import xiroc.dungeoncrawl.data.loot.chest.ChestLootTableKeys;
import xiroc.dungeoncrawl.data.tags.spawner.SpawnerTypePoolTags;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.generator.level.ClusterNodeSettings;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;
import xiroc.dungeoncrawl.dungeon.type.level.CorridorStyle;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.dungeon.type.level.SpecialRoom;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Constant;
import xiroc.dungeoncrawl.util.random.value.Range;

public interface LevelTypes {
    static void generate(BootstrapContext<LevelType> context) {
        final var spawnerTypePools = context.lookup(DatapackRegistries.SPAWNER_TYPE_POOLS);
        final var blueprints = context.lookup(DatapackRegistries.BLUEPRINT);

        context.register(SharedKeys.Dungeon.Level.DEFAULT, new LevelType.Builder()
                .settings(new LevelGeneratorSettings.Builder()
                        .maxRooms(64)
                        .corridorLength(new Range(3, 20))
                        .maxDepth(8)
                        .minStairsDepth(3)
                        .minSeparation(10)
                        .maxClusterNodes(2))
                .rooms(new LevelType.LevelRooms.Builder()
                        .ordinary(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.BRICK))
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.CORNER))
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.DARK_HALL))
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.ENIKO))
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.FORT))
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.POTION_LAB))
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.PRISON), 4)
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.SARCOPHAGUS))
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.SEWER))
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.SPIDER))
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.STORAGE)))
                        .lowerStaircase(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.STAIRCASE_LOWER)))
                        .upperStaircase(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.STAIRCASE_UPPER)))
                        .cluster(new IRandom.Builder<ClusterNodeSettings>()
                                .add(new ClusterNodeSettings(
                                        3,
                                        6,
                                        1.0f,
                                        IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Room.CRYPT_TURN))
                                                .add(blueprints.getOrThrow(BlueprintKeys.Room.CRYPT_CROSSING))
                                                .build(),
                                        IRandom.<Holder<Blueprint>>builder()
                                                .add(blueprints.getOrThrow(BlueprintKeys.Room.CRYPT_TURN))
                                                .add(blueprints.getOrThrow(BlueprintKeys.Room.CRYPT_CROSSING))
                                                .build()
                                )))
                        .build())
                .corridorStyles(new IRandom.Builder<CorridorStyle>()
                        .add(new CorridorStyle.Builder()
                                .segment(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Corridor.Segment.BASE))
                                        .build())
                                .segment(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Corridor.Segment.ARCH))
                                        .build())
                                .sideSegments(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Corridor.Side.BASE))
                                        .add(blueprints.getOrThrow(BlueprintKeys.Corridor.Side.CROPS))
                                        .add(blueprints.getOrThrow(BlueprintKeys.Corridor.Side.FIRE))
                                        .add(blueprints.getOrThrow(BlueprintKeys.Corridor.Side.FLOWER_POT))
                                        .add(blueprints.getOrThrow(BlueprintKeys.Corridor.Side.MASONRY))
                                        .build())
                                .build()))
                .specialRoom(new SpecialRoom(
                        new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.POOL))
                                .build(),
                        new Constant(4),
                        new Constant(1)
                ))
                .secretRoom(new SecretRoom.Builder()
                        .variants(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.SMITHY))
                                .build())
                        .level(new Range(1, 2))
                        .amount(new Constant(1))
                        .entrances(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(BlueprintKeys.Corridor.Side.DOOR))
                                .build()))
                .spawnerTypes(new IRandom.Builder<Holder<SpawnerType>>()
                        .setPools(spawnerTypePools.getOrThrow(SpawnerTypePoolTags.LEVEL_O)))
                .lootTable(ChestLootTableKeys.LEVEL_0)
                .secretRoom(new SecretRoom.Builder()
                        .amount(new Constant(1))
                        .entrances(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(BlueprintKeys.Corridor.Side.DOOR))
                                .build())
                        .variants(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(BlueprintKeys.Room.BEDROOM))
                                .build()))
                .build());
    }
}
