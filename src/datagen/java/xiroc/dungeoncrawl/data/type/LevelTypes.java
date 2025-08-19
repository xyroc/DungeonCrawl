package xiroc.dungeoncrawl.data.type;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.data.blueprint.BlueprintKeys;
import xiroc.dungeoncrawl.data.loot.chest.ChestLootTableKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;
import xiroc.dungeoncrawl.dungeon.type.level.CorridorStyle;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Constant;
import xiroc.dungeoncrawl.util.random.value.Range;

public interface LevelTypes {
    static void generate(BootstrapContext<LevelType> context) {
        final var spawnerTypes = context.lookup(DatapackRegistries.SPAWNER_TYPE);
        final var blueprints = context.lookup(DatapackRegistries.BLUEPRINT);

        context.register(SharedKeys.Dungeon.Level.DEFAULT, new LevelType.Builder()
                .settings(new LevelGeneratorSettings.Builder()
                        .maxRooms(64)
                        .corridorLength(new Range(3, 20))
                        .maxDepth(8)
                        .minStairsDepth(3)
                        .minSeparation(10)
                        .maxClusterNodes(0))
                        .rooms(new LevelType.LevelRooms.Builder()
                                .ordinary(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Room.CORNER))
                                        .add(blueprints.getOrThrow(BlueprintKeys.Room.DARK_HALL))
                                        .add(blueprints.getOrThrow(BlueprintKeys.Room.ENIKO))
                                        .add(blueprints.getOrThrow(BlueprintKeys.Room.SARCOPHAGUS)))
                                .lowerStaircase(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Room.LOWER_STAIRCASE)))
                                .upperStaircase(new IRandom.Builder<Holder<Blueprint>>()
                                        .add(blueprints.getOrThrow(BlueprintKeys.Room.UPPER_STAIRCASE)))
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
                        .add(spawnerTypes.get(SharedKeys.Spawner.Type.DEFAULT).orElseThrow()))
                .lootTable(ChestLootTableKeys.FOOD_LEVEL_0)
                .build());
    }
}
