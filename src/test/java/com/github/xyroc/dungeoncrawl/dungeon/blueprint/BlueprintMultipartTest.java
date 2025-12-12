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

    @Test
    void abortsOnSelfReference() {
        ResourceLocation testAnchor = ResourceLocation.fromNamespaceAndPath("test", "test_anchor");
        Blueprint selfReferringBlueprint = mock(Blueprint.class);
        when(selfReferringBlueprint.anchors()).thenReturn(Map.of(testAnchor, List.of(new Anchor(new BlockPos(0, 0, 0), Direction.NORTH))));
        when(selfReferringBlueprint.xSpan()).thenReturn(1);
        when(selfReferringBlueprint.ySpan()).thenReturn(1);
        when(selfReferringBlueprint.zSpan()).thenReturn(1);
        when(selfReferringBlueprint.boundingBox(any())).thenCallRealMethod();
        when(selfReferringBlueprint.coordinateSpace(any())).thenCallRealMethod();

        Holder<Blueprint> holderOfSelfReferringBlueprint = mock();
        ResourceKey<Blueprint> selfReferringBlueprintResourceKey = ResourceKey.create(DatapackRegistries.BLUEPRINT, ResourceLocation.fromNamespaceAndPath("test", "test_blueprint"));
        when(holderOfSelfReferringBlueprint.value()).thenReturn(selfReferringBlueprint);
        when(holderOfSelfReferringBlueprint.getKey()).thenReturn(selfReferringBlueprintResourceKey);

        BlueprintMultipart multipart = new BlueprintMultipart(testAnchor, new SingleValueRandom<>(holderOfSelfReferringBlueprint));
        when(selfReferringBlueprint.parts()).thenReturn(List.of(multipart));

        DungeonWorldGenContext context = mock();
        BlueprintPiece piece = new BlueprintPiece(new BlueprintComponent(holderOfSelfReferringBlueprint, BlockPos.ZERO, Rotation.NONE), context);

        // Populating should fail
        assertThat(multipart.addParts(piece, piece.base, generator)).isTrue();
    }

}
