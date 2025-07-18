package xiroc.dungeoncrawl.data.type;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.type.DungeonSection;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Constant;
import xiroc.dungeoncrawl.util.random.value.Range;

public interface DungeonTypes {
    static void generate(BootstrapContext<DungeonType> context) {
        final var primaryThemeMappings = context.lookup(DatapackRegistries.PRIMARY_THEME_MAPPINGS);
        final var secondaryThemeMappings = context.lookup(DatapackRegistries.SECONDARY_THEME_MAPPINGS);
        final var levelTypes = context.lookup(DatapackRegistries.LEVEL_TYPE);
        final var blueprints = context.lookup(DatapackRegistries.BLUEPRINT);

        context.register(SharedKeys.Dungeon.DEFAULT, new DungeonType.Builder()
                .entrances(new IRandom.Builder<Holder<Blueprint>>()
                        .add(blueprints.getOrThrow(SharedKeys.Blueprints.Entrance.ENIKO_TOWER))
                        .build())
                .section(new DungeonSection.Builder()
                        .primaryThemes(primaryThemeMappings.getOrThrow(SharedKeys.ThemeMappings.Primary.DEFAULT))
                        .secondaryThemes(secondaryThemeMappings.getOrThrow(SharedKeys.ThemeMappings.Secondary.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .level(levelTypes.getOrThrow(SharedKeys.Dungeon.Level.DEFAULT))
                        .build())
                .secretRoom(new SecretRoom.Builder()
                        .variants(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(SharedKeys.Blueprints.Room.SMITHY))
                                .build())
                        .level(new Range(1, 5))
                        .amount(new Constant(5))
                        .entrances(new IRandom.Builder<Holder<Blueprint>>()
                                .add(blueprints.getOrThrow(SharedKeys.Blueprints.Corridor.Side.DOOR))
                                .build()))
                .build());
    }
}
