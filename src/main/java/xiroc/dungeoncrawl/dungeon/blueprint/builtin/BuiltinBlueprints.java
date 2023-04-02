package xiroc.dungeoncrawl.dungeon.blueprint.builtin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.DatapackNamespaces;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.room.CornerRoomBlueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.room.EmptyRoomBlueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.room.LowerStaircaseBlueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.room.UpperStaircaseBlueprint;

import java.util.function.BiConsumer;

public interface BuiltinBlueprints {
    ResourceLocation EMPTY_ROOM = key("rooms/empty");
    ResourceLocation CORNER_ROOM = key("rooms/corner");
    ResourceLocation UPPER_STAIRCASE = key("rooms/upper_staircase");
    ResourceLocation LOWER_STAIRCASE = key("rooms/lower_staircase");

    private static ResourceLocation key(String path) {
        return new ResourceLocation(DatapackNamespaces.BUILT_IN, path);
    }

    private static void register(BiConsumer<ResourceLocation, Blueprint> consumer, Blueprint blueprint) {
        consumer.accept(blueprint.key(), blueprint);
    }

    static void register(BiConsumer<ResourceLocation, Blueprint> consumer) {
        register(consumer, new EmptyRoomBlueprint(EMPTY_ROOM,
                ImmutableMap.of(BuiltinAnchorTypes.ENTRANCE, ImmutableList.of(
                        Anchor.of(0, 0, 3, Direction.WEST),
                        Anchor.of(3, 0, 0, Direction.NORTH),
                        Anchor.of(6, 0, 3, Direction.EAST),
                        Anchor.of(3, 0, 6, Direction.SOUTH))),
                BlueprintSettings.builder().build()));

        register(consumer, new CornerRoomBlueprint(CORNER_ROOM,
                ImmutableMap.of(BuiltinAnchorTypes.ENTRANCE, ImmutableList.of(
                        Anchor.of(0, 0, 3, Direction.WEST),
                        Anchor.of(3, 0, 0, Direction.NORTH),
                        Anchor.of(6, 0, 3, Direction.EAST),
                        Anchor.of(3, 0, 6, Direction.SOUTH))),
                BlueprintSettings.builder().build()));

        register(consumer, new UpperStaircaseBlueprint(UPPER_STAIRCASE, ImmutableMap.of(
                BuiltinAnchorTypes.STAIRCASE, ImmutableList.of(Anchor.of(4, 1, 4, Direction.DOWN)),
                BuiltinAnchorTypes.ENTRANCE, ImmutableList.of(
                        Anchor.of(0, 0, 4, Direction.WEST),
                        Anchor.of(4, 0, 0, Direction.NORTH),
                        Anchor.of(8, 0, 4, Direction.EAST),
                        Anchor.of(4, 0, 8, Direction.SOUTH))),
                BlueprintSettings.builder().build()));

        register(consumer, new LowerStaircaseBlueprint(LOWER_STAIRCASE, ImmutableMap.of(
                BuiltinAnchorTypes.STAIRCASE, ImmutableList.of(Anchor.of(4, 0, 4, Direction.UP)),
                BuiltinAnchorTypes.ENTRANCE, ImmutableList.of(
                        Anchor.of(0, 0, 4, Direction.WEST),
                        Anchor.of(4, 0, 0, Direction.NORTH),
                        Anchor.of(8, 0, 4, Direction.EAST),
                        Anchor.of(4, 0, 8, Direction.SOUTH))),
                BlueprintSettings.builder().build()));
    }
}