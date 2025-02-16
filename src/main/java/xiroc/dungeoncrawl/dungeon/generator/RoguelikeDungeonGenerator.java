package xiroc.dungeoncrawl.dungeon.generator;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.generator.plan.ListPlan;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.DungeonSection;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.CoordinateSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RoguelikeDungeonGenerator implements DungeonGenerator {
    @Override
    public void generateDungeon(DungeonBuilder dungeonBuilder, int startHeight, StaircaseBuilder staircaseBuilder, Random random) {
        DungeonPlan plan = new ListPlan(dungeonBuilder.maximumBounds);
        int stage = 0;

        final ImmutableList<DungeonSection> sections = dungeonBuilder.dungeonType.get().sections();
        // Secret rooms defined via the dungeon type, which could be applied to any layer.
        final Map<Integer, List<SecretRoom>> globalSecretRooms = gatherGlobalSecretRooms(dungeonBuilder.dungeonType, random);

        outerLoop:
        for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
            final DungeonSection section = sections.get(sectionIndex);
            final Delegate<PrimaryTheme> primaryTheme = section.primaryThemes().get().roll(dungeonBuilder.biomeKey, random);
            final Delegate<SecondaryTheme> secondaryTheme = section.secondaryThemes().get().roll(dungeonBuilder.biomeKey, random);

            final ImmutableList<Delegate<LevelType>> levels = section.levels();

            final boolean lastSection = sectionIndex == sections.size() - 1;

            for (int levelIndex = 0; levelIndex < levels.size(); levelIndex++) {
                final Delegate<LevelType> levelType = levels.get(levelIndex);

                // Try to determine whether there will be another layer below this one.
                // This information is needed to tell the generator for this layer whether it should generate a staircase room or not.
                final int approximateBlocksBelowLayer = startHeight
                        - levelType.get().settings().minSeparation
                        - dungeonBuilder.heightAccessor.getMinBuildHeight()
                        - DungeonBuilder.WORLD_BOTTOM_CUTOFF; // The lowest layers of blocks are usually bedrock and should not be considered usable.
                final boolean lastLayerInSection = levelIndex == levels.size() - 1;
                final Delegate<LevelType> nextLevel = lastLayerInSection ? (lastSection ? null : sections.get(sectionIndex + 1).levels().get(0)) : levels.get(levelIndex + 1);
                final boolean lastLayer = lastSection && lastLayerInSection || nextLevel.get().settings().minSeparation >= approximateBlocksBelowLayer;

                final List<SecretRoom> additionalSecretRooms = globalSecretRooms.getOrDefault(stage, List.of());
                final LevelGenerator levelGenerator = new LevelGenerator(levelType.get(), plan, startHeight, stage, random, primaryTheme, secondaryTheme, !lastLayer, additionalSecretRooms);
                ++stage;

                levelGenerator.generateLevel(staircaseBuilder);
                if (levelGenerator.start() == null) {
                    DungeonCrawl.LOGGER.debug("Ending dungeon generation early because level generation failed. The level type was {}.", levelType.key());
                    break outerLoop;
                }
                dungeonBuilder.structurePiecesBuilder.addPiece(staircaseBuilder.make(stage, primaryTheme, secondaryTheme));

                NodeElement end = levelGenerator.end();
                if (end == null) {
                    break outerLoop;
                }

                BlueprintPiece piece = end.piece();
                ImmutableList<Anchor> anchors = piece.base.blueprint().get().anchors().get(BuiltinAnchorTypes.STAIRCASE);
                if (anchors == null || anchors.isEmpty()) {
                    DungeonCrawl.LOGGER.warn("Blueprint {} does not have any staircase anchors.", piece.base.blueprint().key());
                    break outerLoop;
                }
                Anchor anchor = anchors.get(random.nextInt(anchors.size()));
                if (anchor.direction() != Direction.DOWN) {
                    DungeonCrawl.LOGGER.warn("Blueprint {} has a staircase anchor that isn't facing downwards.", piece.base.blueprint().key());
                    break outerLoop;
                }

                BlockPos offset = CoordinateSpace.rotate(anchor.position(), piece.base.rotation(), piece.base.blueprint().get().xSpan(), piece.base.blueprint().get().zSpan());
                staircaseBuilder = new StaircaseBuilder(piece.base.position().getX() + offset.getX(), piece.base.position().getZ() + offset.getZ());
                staircaseBuilder.top(offset, piece.base.position().getY());
                startHeight = staircaseBuilder.wallTop();
            }
        }

        DungeonCrawl.LOGGER.debug("Generated a dungeon of type {} with {} pieces.", dungeonBuilder.dungeonType.key(), plan.pieceCount());
        plan.forEach((element) -> element.createPieces(dungeonBuilder.structurePiecesBuilder::addPiece, random));
    }

    private Map<Integer, List<SecretRoom>> gatherGlobalSecretRooms(Delegate<DungeonType> dungeonType, Random random) {
        final Map<Integer, List<SecretRoom>> globalSecretRooms = new HashMap<>();
        for (final SecretRoom secretRoom : dungeonType.get().secretRooms()) {
            if (secretRoom.level() == null) {
                DungeonCrawl.LOGGER.warn("A secret room type defined in the dungeon type {} does not have a specified range of levels to generate in. It cannot generate.", dungeonType.key());
                continue;
            }
            final int amount = secretRoom.amount().nextInt(random);
            for (int i = 0; i < amount; ++i) {
                final int level = secretRoom.level().nextInt(random);
                globalSecretRooms.computeIfAbsent(level, (ignored) -> new ArrayList<>()).add(secretRoom);
            }
        }
        return globalSecretRooms;
    }
}
