package xiroc.dungeoncrawl.dungeon.generator;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprints;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.BuiltinBlueprints;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.generator.plan.ListPlan;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.type.LevelType;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoguelikeDungeonGenerator implements DungeonGenerator {
    private static final LevelType DUMMY_LEVEL_TYPE = new LevelType(new LevelGeneratorSettings.Builder()
            .maxRooms(64)
            .corridorLength(new Range(3, 10))
            .maxDepth(8)
            .minStairsDepth(3)
            .minSeparation(10)
            .build(),
            new IRandom.Builder<Delegate<Blueprint>>()
                    .add(Delegate.of(Blueprints.getBlueprint(BuiltinBlueprints.CORNER_ROOM), BuiltinBlueprints.CORNER_ROOM))
                    .build(),
            new IRandom.Builder<Delegate<Blueprint>>()
                    .add(Delegate.of(Blueprints.getBlueprint(BuiltinBlueprints.CORRIDOR_BASE_SEGMENT), BuiltinBlueprints.CORRIDOR_BASE_SEGMENT))
                    .add(Delegate.of(Blueprints.getBlueprint(BuiltinBlueprints.CORRIDOR_ARCH_SEGMENT), BuiltinBlueprints.CORRIDOR_ARCH_SEGMENT))
                    .build(),
            new IRandom.Builder<Delegate<Blueprint>>()
                    .add(Delegate.of(Blueprints.getBlueprint(BuiltinBlueprints.CORRIDOR_SIDE_SEGMENT), BuiltinBlueprints.CORRIDOR_SIDE_SEGMENT))
                    .build(),
            new IRandom.Builder<Delegate<Blueprint>>()
                    .add(Delegate.of(Blueprints.getBlueprint(BuiltinBlueprints.UPPER_STAIRCASE), BuiltinBlueprints.UPPER_STAIRCASE))
                    .build(),
            new IRandom.Builder<Delegate<Blueprint>>()
                    .add(Delegate.of(Blueprints.getBlueprint(BuiltinBlueprints.LOWER_STAIRCASE), BuiltinBlueprints.LOWER_STAIRCASE))
                    .build(),
            null,
            new IRandom.Builder<Delegate<SpawnerType>>()
                    .add(Delegate.of(DungeonCrawl.locate("default")))
                    .build(),
            null);

    @Override
    public List<? extends StructurePiece> generateDungeon(DungeonBuilder dungeonBuilder, int startHeight, StaircaseBuilder staircaseBuilder, Random random) {
        DungeonPlan plan = new ListPlan();
        ArrayList<StructurePiece> pieces = new ArrayList<>();
        for (int stage = 0; stage < 5; stage++) {
            LevelGenerator levelGenerator = new LevelGenerator(DUMMY_LEVEL_TYPE, plan, startHeight, stage, random);
            levelGenerator.generateLevel(staircaseBuilder);
            if (levelGenerator.start() == null) {
                break;
            }
            pieces.add(staircaseBuilder.make());

            NodeElement end = levelGenerator.end();
            if (end == null) {
                break;
            }

            BlueprintPiece piece = end.piece();
            ImmutableList<Anchor> anchors = piece.base.blueprint().get().anchors().get(BuiltinAnchorTypes.STAIRCASE);
            if (anchors == null || anchors.isEmpty()) {
                break;
            }
            Anchor anchor = anchors.get(random.nextInt(anchors.size()));
            if (anchor.direction() != Direction.DOWN) {
                break;
            }
            BlockPos offset = CoordinateSpace.rotate(anchor.position(), piece.base.rotation(), piece.base.blueprint().get().xSpan(), piece.base.blueprint().get().zSpan());
            staircaseBuilder = new StaircaseBuilder(piece.base.position().getX() + offset.getX(), piece.base.position().getZ() + offset.getZ());
            staircaseBuilder.top(offset, piece.base.position().getY());
            startHeight = staircaseBuilder.wallTop().getY();
        }
        plan.forEach((element) -> element.createPieces(pieces::add));
        DungeonCrawl.LOGGER.info("Generated {} pieces.", pieces.size());
        return pieces;
    }
}
