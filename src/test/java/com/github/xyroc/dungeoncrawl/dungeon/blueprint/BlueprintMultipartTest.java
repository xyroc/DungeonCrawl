package com.github.xyroc.dungeoncrawl.dungeon.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintMultipart;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.generator.plan.ListPlan;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.random.SingleValueRandom;
import xiroc.dungeoncrawl.util.random.value.Constant;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class BlueprintMultipartTest {
    private static LevelGenerator generator;

    @BeforeEach
    void setup() {
        LevelType.LevelRooms rooms = mock();
        when(rooms.cluster()).thenReturn(null);

        LevelType levelType = mock();
        when(levelType.settings()).thenReturn(new LevelGeneratorSettings.Builder()
                .maxRooms(0)
                .maxClusterNodes(0)
                .maxDepth(0)
                .minStairsDepth(0)
                .minSeparation(0)
                .corridorLength(new Constant(0))
                .build());
        when(levelType.rooms()).thenReturn(rooms);
        when(levelType.secretRooms()).thenReturn(List.of());
        when(levelType.specialRooms()).thenReturn(List.of());

        generator = new LevelGenerator(levelType,
                new ListPlan(BoundingBox.infinite()),
                100,
                0,
                new SingleThreadedRandomSource(10),
                null,
                null,
                false,
                List.of()
        );
    }

    private static void setBlueprintSize(Blueprint mock, int xSpan, int ySpan, int zSpan) {
        when(mock.xSpan()).thenReturn(xSpan);
        when(mock.ySpan()).thenReturn(ySpan);
        when(mock.zSpan()).thenReturn(zSpan);
        when(mock.boundingBox(any())).thenCallRealMethod();
        when(mock.coordinateSpace(any())).thenCallRealMethod();
    }

    private static Holder<Blueprint> createMockBlueprintHolder(Blueprint blueprint, ResourceLocation key) {
        Holder<Blueprint> holder = mock();
        ResourceKey<Blueprint> selfReferringBlueprintResourceKey = ResourceKey.create(DatapackRegistries.BLUEPRINT, key);
        when(holder.value()).thenReturn(blueprint);
        when(holder.getKey()).thenReturn(selfReferringBlueprintResourceKey);
        return holder;
    }

    @Test
    void abortsOnSelfReference() {
        Blueprint selfReferringBlueprint = mock(Blueprint.class);
        ResourceLocation testPlacement = ResourceLocation.fromNamespaceAndPath("test", "test_anchor");
        when(selfReferringBlueprint.anchors()).thenReturn(Map.of(testPlacement, List.of(new Anchor(new BlockPos(0, 0, 0), Direction.NORTH))));
        setBlueprintSize(selfReferringBlueprint, 1, 1, 1);

        Holder<Blueprint> holderOfSelfReferringBlueprint = createMockBlueprintHolder(selfReferringBlueprint, ResourceLocation.fromNamespaceAndPath("test", "test_blueprint"));

        BlueprintMultipart multipart = new BlueprintMultipart(testPlacement, new SingleValueRandom<>(holderOfSelfReferringBlueprint));
        when(selfReferringBlueprint.parts()).thenReturn(List.of(multipart));

        DungeonWorldGenContext context = mock();
        BlueprintPiece piece = new BlueprintPiece(new BlueprintComponent(holderOfSelfReferringBlueprint, BlockPos.ZERO, Rotation.NONE), context);

        // Populating should fail
        assertThat(multipart.addParts(piece, piece.base, generator)).isTrue();
    }

    @Test
    void placesNestedParts() {
        Blueprint bottomLevelBlueprint = mock(Blueprint.class);
        setBlueprintSize(bottomLevelBlueprint, 1, 1, 1);
        ResourceLocation bottomLevelPlacement = ResourceLocation.fromNamespaceAndPath("test", "bottom_level_placement");
        when(bottomLevelBlueprint.anchors()).thenReturn(Map.of(
                BuiltinAnchorTypes.JUNCTURE, List.of(new Anchor(BlockPos.ZERO, Direction.WEST))
        ));
        Holder<Blueprint> bottomLevelBlueprintHolder = createMockBlueprintHolder(bottomLevelBlueprint, ResourceLocation.fromNamespaceAndPath("test", "bottom_level"));

        Blueprint midLevelBlueprint = mock(Blueprint.class);
        setBlueprintSize(midLevelBlueprint, 3, 3, 3);
        ResourceLocation midLevelPlacement = ResourceLocation.fromNamespaceAndPath("test", "mid_level_placement");
        when(midLevelBlueprint.anchors()).thenReturn(Map.of(
                bottomLevelPlacement, List.of(new Anchor(BlockPos.ZERO, Direction.EAST)),
                BuiltinAnchorTypes.JUNCTURE, List.of(new Anchor(BlockPos.ZERO, Direction.WEST))
        ));
        Holder<Blueprint> midLevelBlueprintHolder = createMockBlueprintHolder(midLevelBlueprint, ResourceLocation.fromNamespaceAndPath("test", "mid_level"));
        BlueprintMultipart midLevelParts = new BlueprintMultipart(bottomLevelPlacement, new SingleValueRandom<>(bottomLevelBlueprintHolder));
        when(midLevelBlueprint.parts()).thenReturn(List.of(midLevelParts));

        Blueprint topLevelBlueprint = mock(Blueprint.class);
        setBlueprintSize(topLevelBlueprint, 5, 5, 5);
        when(topLevelBlueprint.anchors()).thenReturn(Map.of(
                midLevelPlacement, List.of(new Anchor(BlockPos.ZERO, Direction.EAST))
        ));
        BlueprintMultipart topLevelParts = new BlueprintMultipart(midLevelPlacement, new SingleValueRandom<>(midLevelBlueprintHolder));
        when(topLevelBlueprint.parts()).thenReturn(List.of(topLevelParts));
        Holder<Blueprint> topLevelBlueprintHolder = createMockBlueprintHolder(topLevelBlueprint, ResourceLocation.fromNamespaceAndPath("test", "top_level"));

        DungeonWorldGenContext context = mock();
        BlueprintPiece piece = new BlueprintPiece(new BlueprintComponent(topLevelBlueprintHolder, BlockPos.ZERO, Rotation.NONE), context);

        // Placement should succeed.
        assertThat(topLevelParts.addParts(piece, piece.base, generator)).isFalse();
    }

}
