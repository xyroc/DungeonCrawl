package xiroc.dungeoncrawl.dungeon.generator;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.generator.plan.ListPlan;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.CoordinateSpace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoguelikeDungeonGenerator implements DungeonGenerator {
    private static final int VERTICAL_SEPARATION = 10;

    @Override
    public List<? extends StructurePiece> generateDungeon(DungeonBuilder dungeonBuilder, int startHeight, StaircaseBuilder staircaseBuilder, Random random) {
        DungeonPlan plan = new ListPlan();
        ArrayList<StructurePiece> pieces = new ArrayList<>();
        for (int stage = 0; stage < 5; stage++) {
            LevelGenerator levelGenerator = new LevelGenerator(plan, startHeight, stage, random);
            levelGenerator.generateLevel(staircaseBuilder);
            if (levelGenerator.start() == null) {
                break;
            }
            pieces.add(staircaseBuilder.make());

            NodeElement end = levelGenerator.end();
            if (end == null) {
                break;
            }

            DungeonPiece piece = end.piece();
            ImmutableList<Anchor> anchors = piece.blueprint.anchors().get(BuiltinAnchorTypes.STAIRCASE);
            if (anchors == null || anchors.isEmpty()) {
                break;
            }
            Anchor anchor = anchors.get(random.nextInt(anchors.size()));
            if (anchor.direction() != Direction.DOWN) {
                break;
            }
            BlockPos offset = CoordinateSpace.rotate(anchor.position(), piece.rotation, piece.blueprint.xSpan(), piece.blueprint.zSpan());
            staircaseBuilder = new StaircaseBuilder(piece.position.getX() + offset.getX(), piece.position.getZ() + offset.getZ());
            staircaseBuilder.top(offset, piece.position.getY());
            startHeight = staircaseBuilder.wallTop().getY();
        }
        plan.forEach((element) -> element.createPieces(pieces::add));
        DungeonCrawl.LOGGER.info("Generated {} pieces.", pieces.size());
        return pieces;
    }
}
