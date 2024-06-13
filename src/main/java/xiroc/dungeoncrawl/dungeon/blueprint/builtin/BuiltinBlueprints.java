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
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.segment.CorridorArchSegment;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.segment.CorridorBaseSegment;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.segment.CorridorSideSegment;

import java.util.function.BiConsumer;

public interface BuiltinBlueprints {
    ResourceLocation EMPTY_ROOM = key("room/empty");
    ResourceLocation CORNER_ROOM = key("room/corner");
    ResourceLocation UPPER_STAIRCASE = key("room/upper_staircase");
    ResourceLocation LOWER_STAIRCASE = key("room/lower_staircase");
    ResourceLocation CORRIDOR_BASE_SEGMENT = key("segment/corridor/base");
    ResourceLocation CORRIDOR_SIDE_SEGMENT = key("segment/corridor/side");
    ResourceLocation CORRIDOR_ARCH_SEGMENT = key("segment/corridor/arch");

    private static ResourceLocation key(String path) {
        return new ResourceLocation(DatapackNamespaces.BUILT_IN, path);
    }

    static void register(BiConsumer<ResourceLocation, Blueprint> consumer) {
        consumer.accept(EMPTY_ROOM, new EmptyRoomBlueprint(
                ImmutableMap.of(BuiltinAnchorTypes.ENTRANCE, ImmutableList.of(
                        Anchor.of(0, 0, 3, Direction.WEST),
                        Anchor.of(3, 0, 0, Direction.NORTH),
                        Anchor.of(6, 0, 3, Direction.EAST),
                        Anchor.of(3, 0, 6, Direction.SOUTH))),
                ImmutableList.of(),
                BlueprintSettings.builder().build()));

        consumer.accept(CORNER_ROOM, new CornerRoomBlueprint(
                ImmutableMap.of(BuiltinAnchorTypes.ENTRANCE, ImmutableList.of(
                        Anchor.of(0, 0, 3, Direction.WEST),
                        Anchor.of(3, 0, 0, Direction.NORTH),
                        Anchor.of(6, 0, 3, Direction.EAST),
                        Anchor.of(3, 0, 6, Direction.SOUTH))),
                ImmutableList.of(),
                BlueprintSettings.builder().build()));

        consumer.accept(UPPER_STAIRCASE, new UpperStaircaseBlueprint(ImmutableMap.of(
                BuiltinAnchorTypes.STAIRCASE, ImmutableList.of(Anchor.of(4, 1, 4, Direction.DOWN)),
                BuiltinAnchorTypes.ENTRANCE, ImmutableList.of(
                        Anchor.of(0, 0, 4, Direction.WEST),
                        Anchor.of(4, 0, 0, Direction.NORTH),
                        Anchor.of(8, 0, 4, Direction.EAST),
                        Anchor.of(4, 0, 8, Direction.SOUTH))),
                ImmutableList.of(),
                BlueprintSettings.builder().build()));

        consumer.accept(LOWER_STAIRCASE, new LowerStaircaseBlueprint(ImmutableMap.of(
                BuiltinAnchorTypes.STAIRCASE, ImmutableList.of(Anchor.of(4, 0, 4, Direction.UP)),
                BuiltinAnchorTypes.ENTRANCE, ImmutableList.of(
                        Anchor.of(0, 0, 4, Direction.WEST),
                        Anchor.of(4, 0, 0, Direction.NORTH),
                        Anchor.of(8, 0, 4, Direction.EAST),
                        Anchor.of(4, 0, 8, Direction.SOUTH))),
                ImmutableList.of(),
                BlueprintSettings.builder().build()));

        consumer.accept(CORRIDOR_BASE_SEGMENT, new CorridorBaseSegment(ImmutableMap.of(
                BuiltinAnchorTypes.JUNCTURE, ImmutableList.of(
                        Anchor.of(1, 0, 0, Direction.NORTH),
                        Anchor.of(1, 0, 2, Direction.SOUTH))),
                ImmutableList.of(),
                BlueprintSettings.builder().build()));

        consumer.accept(CORRIDOR_SIDE_SEGMENT, new CorridorSideSegment(
                ImmutableMap.of(BuiltinAnchorTypes.JUNCTURE, ImmutableList.of(Anchor.of(1, 0, 1, Direction.SOUTH))),
                ImmutableList.of(),
                BlueprintSettings.builder().build()));

        consumer.accept(CORRIDOR_ARCH_SEGMENT, new CorridorArchSegment(ImmutableMap.of(), ImmutableList.of(), BlueprintSettings.builder().build()));
    }
}