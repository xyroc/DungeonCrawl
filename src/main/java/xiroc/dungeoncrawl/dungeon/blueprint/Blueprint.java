package xiroc.dungeoncrawl.dungeon.blueprint;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockColumn;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.TemplateBlockPlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Blueprint {
    // Lazy initialization to break up cycle on class load
    public static final Codec<Blueprint> DIRECT_CODEC = Codec.lazyInitialized(() -> BlueprintConfiguration.CODEC.xmap(Blueprint::new, blueprint -> blueprint.configuration));
    public static final Codec<Holder<Blueprint>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.BLUEPRINT, DIRECT_CODEC, false);
    public static final Codec<IRandom<Holder<Blueprint>>> RANDOM_HOLDER_CODEC = IRandom.<Holder<Blueprint>>codecBuilder()
            .valueCodec("blueprint", HOLDER_CODEC)
            .pools(DatapackRegistries.BLUEPRINT_POOLS)
            .build();
    public static final Codec<IRandom<IRandom<Holder<Blueprint>>>> RANDOM_RANDOM_HOLDER_CODEC = IRandom.makeCodec(IRandom.makeBuilderCodec(RANDOM_HOLDER_CODEC, "blueprints", null));

    protected final BlueprintConfiguration configuration;
    protected Vec3i size;
    protected List<TemplateBlockColumn> blockColumns;
    protected Map<ResourceLocation, List<Anchor>> anchors;
    protected List<Entrance> entrances;
    protected List<Entrance> clusterEntrances;

    public Blueprint(BlueprintConfiguration configuration) {
        this.configuration = configuration;
    }

    public void generate(LevelAccessor level, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        PrimaryTheme primaryTheme = worldGenContext.primaryTheme().value();
        SecondaryTheme secondaryTheme = worldGenContext.secondaryTheme().value();
        CoordinateSpace coordinateSpace = coordinateSpace(position);

        this.blockColumns.forEach((column) -> {
            BlockPos columnPos = coordinateSpace.rotateAndTranslateToOrigin(column.x(), column.lowestY(), column.z(), rotation);
            if (!worldGenBounds.isInside(columnPos)) {
                return;
            }
            for (TemplateBlock templateBlock : column.blocks()) {
                BlockPos blockPosition = coordinateSpace.rotateAndTranslateToOrigin(templateBlock.position(), rotation);
                if (!worldGenBounds.isInside(blockPosition)) {
                    return;
                }
                boolean isAir = level.getBlockState(blockPosition).isAir();
                TemplateBlockPlacementSettings placementSettings = templateBlock.settings();
                TemplateBlockType templateBlockType = placementSettings.canPlace(isAir) ? templateBlock.type() : placementSettings.alternative();
                if (templateBlockType == null) {
                    continue;
                }

                BlockStateProvider provider = templateBlockType.chooseProvider(primaryTheme, secondaryTheme);
                BlockState state = templateBlock.block()
                        .applyProperties(provider.get(position, random))
                        .rotate(level, position, rotation);
                templateBlockType.handlePlacement(level, blockPosition, state, true);

                if (placementSettings.lootTable() != null && level.getBlockEntity(blockPosition) instanceof RandomizableContainerBlockEntity containerBlockEntity) {
                    containerBlockEntity.setLootTable(placementSettings.lootTable().forTier(worldGenContext.level()));
                }
            }
            if (columnPos.getY() <= worldGenContext.foundationHeight() && !level.getBlockState(columnPos).isAir()) {
                WorldEditor.buildFoundation(level, columnPos, random, worldGenBounds, worldGenContext);
            }
        });
    }

    /**
     * Instantiates the features of this blueprint and passes them to the consumer.
     * @param levelGenerator The generator for the level this blueprint is placed in. Holds the source of randomness.
     * @param offset The offset in the world where this blueprint is placed.
     * @param rotation The rotation this blueprint was placed with.
     * @param collector A consumer to receive the resulting components.
     */
    public void populateFeatures(LevelGenerator levelGenerator, BlockPos offset, Rotation rotation, Consumer<DungeonComponent> collector) {
        for (BlueprintFeature feature : features()) {
            feature.create(levelGenerator, collector, null, this, offset, rotation);
        }
    }

    public int xSpan() {
        return this.size.getX();
    }

    public int ySpan() {
        return this.size.getY();
    }

    public int zSpan() {
        return this.size.getZ();
    }

    public Map<ResourceLocation, List<Anchor>> anchors() {
        return this.anchors;
    }

    public List<BlueprintFeature> features() {
        return this.configuration.features;
    }

    public List<BlueprintMultipart> parts() {
        return this.configuration.parts;
    }

    public List<Entrance> entrances() {
        return this.entrances;
    }

    public List<Entrance> clusterEntrances() {
        return clusterEntrances;
    }

    public BoundingBoxBuilder boundingBox(Rotation rotation) {
        return switch (rotation) {
            case NONE, CLOCKWISE_180 -> new BoundingBoxBuilder(0, 0, 0, xSpan() - 1, ySpan() - 1, zSpan() - 1);
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> new BoundingBoxBuilder(0, 0, 0, zSpan() - 1, ySpan() - 1, xSpan() - 1);
        };
    }

    public CoordinateSpace coordinateSpace(BlockPos offset) {
        return new CoordinateSpace(offset, xSpan(), zSpan());
    }
}