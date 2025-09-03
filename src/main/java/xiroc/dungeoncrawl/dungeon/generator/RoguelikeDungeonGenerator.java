package xiroc.dungeoncrawl.dungeon.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.element.NodeElement;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.generator.plan.DungeonPlan;
import xiroc.dungeoncrawl.dungeon.generator.plan.ListPlan;
import xiroc.dungeoncrawl.dungeon.generator.staircase.StaircasePlanner;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.DungeonSection;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.SecretRoom;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoguelikeDungeonGenerator implements DungeonGenerator {
    @Override
    public void generateDungeon(DungeonBuilder dungeonBuilder) {
        final RandomSource random = dungeonBuilder.random;
        final DungeonPlan plan = new ListPlan(dungeonBuilder.maximumBounds);
        int stage = 0;

        StaircasePlanner staircasePlanner = new StaircasePlanner(dungeonBuilder.groundPos.getX(), dungeonBuilder.groundPos.getZ());

        if (!createEntrance(dungeonBuilder, staircasePlanner, random)) {
            DungeonCrawl.LOGGER.warn("Could not create an entrance for dungeon of type {}. Aborting dungeon generation.", dungeonBuilder.dungeonType.getKey());
            return;
        }

        int startHeight = dungeonBuilder.startHeight;

        final List<DungeonSection> sections = dungeonBuilder.dungeonType.value().sections();
        // Secret rooms defined via the dungeon type, which could be applied to any layer.
        final Map<Integer, List<SecretRoom>> globalSecretRooms = gatherGlobalSecretRooms(dungeonBuilder.dungeonType, random);

        outerLoop:
        for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
            final DungeonSection section = sections.get(sectionIndex);
            final Holder<PrimaryTheme> primaryTheme = section.primaryThemes().value().roll(dungeonBuilder.biome, random);
            final Holder<SecondaryTheme> secondaryTheme = section.secondaryThemes().value().roll(dungeonBuilder.biome, random);

            final List<Holder<LevelType>> levels = section.levels();

            final boolean lastSection = sectionIndex == sections.size() - 1;

            for (int levelIndex = 0; levelIndex < levels.size(); levelIndex++) {
                final Holder<LevelType> levelType = levels.get(levelIndex);

                // Try to determine whether there will be another layer below this one.
                // This information is needed to tell the generator for this layer whether it should generate a staircase room or not.
                final int approximateBlocksBelowLayer = startHeight
                        - levelType.value().settings().minSeparation
                        - dungeonBuilder.context.heightAccessor().getMinBuildHeight()
                        - DungeonBuilder.WORLD_BOTTOM_CUTOFF; // The lowest layers of blocks are usually bedrock and should not be considered usable.
                final boolean lastLayerInSection = levelIndex == levels.size() - 1;
                final Holder<LevelType> nextLevel = lastLayerInSection ? (lastSection ? null : sections.get(sectionIndex + 1).levels().getFirst()) : levels.get(levelIndex + 1);
                final boolean lastLayer = lastSection && lastLayerInSection || nextLevel.value().settings().minSeparation >= approximateBlocksBelowLayer;

                final List<SecretRoom> additionalSecretRooms = globalSecretRooms.getOrDefault(stage, List.of());
                final LevelGenerator levelGenerator = new LevelGenerator(levelType.value(), plan, startHeight, stage, random, primaryTheme, secondaryTheme, !lastLayer, additionalSecretRooms);
                ++stage;

                levelGenerator.generateLevel(staircasePlanner);
                if (levelGenerator.start() == null) {
                    DungeonCrawl.LOGGER.debug("Ending dungeon generation early because level generation failed. The level type was {}.", levelType.getKey());
                    break outerLoop;
                }
                dungeonBuilder.structurePiecesBuilder.addPiece(staircasePlanner.make(stage, primaryTheme, secondaryTheme));

                NodeElement end = levelGenerator.end();
                if (end == null) {
                    break outerLoop;
                }

                staircasePlanner = makeStaircase(end.piece(), random);
                if (staircasePlanner == null) {
                    break outerLoop;
                }
                startHeight = staircasePlanner.getWallTop();
            }
        }

        final String dungeonTypeName = Optional.ofNullable(dungeonBuilder.dungeonType.getKey())
                .map(ResourceKey::location)
                .map(ResourceLocation::toString)
                .orElse("null");
        DungeonCrawl.LOGGER.debug("Generated a dungeon of type {} with {} pieces.", dungeonTypeName, plan.pieceCount());
        plan.forEach((element) -> element.createPieces(dungeonBuilder.structurePiecesBuilder::addPiece));
    }

    private Map<Integer, List<SecretRoom>> gatherGlobalSecretRooms(Holder<DungeonType> dungeonType, RandomSource random) {
        final Map<Integer, List<SecretRoom>> globalSecretRooms = new HashMap<>();
        for (final SecretRoom secretRoom : dungeonType.value().secretRooms()) {
            final int amount = secretRoom.amount().nextInt(random);
            for (int i = 0; i < amount; ++i) {
                final int level = secretRoom.level().nextInt(random);
                globalSecretRooms.computeIfAbsent(level, (ignored) -> new ArrayList<>()).add(secretRoom);
            }
        }
        return globalSecretRooms;
    }

    @Nullable
    private Anchor randomStaircaseAnchor(Holder<Blueprint> upperStaircaseRoom, RandomSource random) {
        final List<Anchor> staircaseAnchors = upperStaircaseRoom.value().anchors().get(BuiltinAnchorTypes.STAIRCASE);
        if (staircaseAnchors == null) {
            DungeonCrawl.LOGGER.warn("Blueprint {} is used as an upper staircase room but does not have any staircase anchors.", upperStaircaseRoom.getKey());
            return null;
        }
        final Anchor staircaseAnchor = staircaseAnchors.get(random.nextInt(staircaseAnchors.size()));
        if (staircaseAnchor.direction() == Direction.UP) {
            DungeonCrawl.LOGGER.warn("Blueprint {} has a staircase anchor that is facing upwards but is used as an upper staircase room, which requires a downwards facing staircase",
                    upperStaircaseRoom.getKey());
            return null;
        }
        return staircaseAnchor;
    }

    private boolean createEntrance(DungeonBuilder dungeonBuilder, StaircasePlanner staircasePlanner, RandomSource random) {
        final Holder<Blueprint> entrance = dungeonBuilder.dungeonType.value().entrances().roll(random);
        Anchor staircaseAnchor = randomStaircaseAnchor(entrance, random);
        if (staircaseAnchor == null) {
            return false;
        }

        final Rotation entranceRotation = Rotation.getRandom(random);
        staircaseAnchor = entrance.value().coordinateSpace(BlockPos.ZERO).rotateAndTranslateToOrigin(staircaseAnchor, entranceRotation);
        final BlockPos entrancePosition = dungeonBuilder.groundPos.above().offset(
                -staircaseAnchor.position().getX(),
                0,
                -staircaseAnchor.position().getZ()
        );

        final Direction staircaseConstraint = staircaseAnchor.direction().getAxis().isHorizontal() ? staircaseAnchor.direction() : null;
        staircasePlanner.setTop(staircaseAnchor.position().getY(), entrancePosition.getY(), staircaseConstraint);

        final var section = dungeonBuilder.dungeonType.value().sections().getFirst();
        final var primaryTheme = section.primaryThemes().value().roll(dungeonBuilder.biome, random);
        final var secondaryTheme = section.secondaryThemes().value().roll(dungeonBuilder.biome, random);
        final BlueprintComponent entranceComponent = new BlueprintComponent(entrance, entrancePosition, entranceRotation);
        final DungeonPiece entrancePiece = new BlueprintPiece(entranceComponent, new DungeonWorldGenContext(primaryTheme, secondaryTheme, entranceComponent.position().getY(), 0));
        dungeonBuilder.structurePiecesBuilder.addPiece(entrancePiece);
        return true;
    }

    @Nullable
    private StaircasePlanner makeStaircase(BlueprintPiece piece, RandomSource random) {
        final Anchor staircaseAnchor = randomStaircaseAnchor(piece.base.blueprint(), random);
        if (staircaseAnchor == null) {
            return null;
        }

        // Horizontal anchor is interpreted as downwards anchor with a staircase facing constraint.
        final Direction staircaseConstraint = staircaseAnchor.direction().getAxis().isHorizontal() ? staircaseAnchor.direction() : null;
        final BlockPos offset = CoordinateSpace.rotate(staircaseAnchor.position(), piece.base.rotation(), piece.base.blueprint().value().xSpan(), piece.base.blueprint().value().zSpan());
        final StaircasePlanner staircasePlanner = new StaircasePlanner(piece.base.position().getX() + offset.getX(), piece.base.position().getZ() + offset.getZ());
        staircasePlanner.setTop(offset.getY(), piece.base.position().getY(), staircaseConstraint);
        return staircasePlanner;
    }
}
