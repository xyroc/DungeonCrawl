package xiroc.dungeoncrawl.data.blueprint.provider;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.data.blueprint.BlueprintKeys;
import xiroc.dungeoncrawl.data.blueprint.TemplateKeys;
import xiroc.dungeoncrawl.dungeon.block.provider.RandomBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.FlowerPotFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.FixedTemplateBlockType;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.Constant;

import java.util.Optional;

public interface CorridorBlueprints {
    static void generate(BootstrapContext<Blueprint> context) {
        // Corridor Segments
        context.register(BlueprintKeys.Corridor.Segment.ARCH, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Corridor.Segment.ARCH)
                .build()));

        context.register(BlueprintKeys.Corridor.Segment.BASE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Corridor.Segment.BASE)
                .build()));

        // Corridor Side Segments
        context.register(BlueprintKeys.Corridor.Side.BASE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Corridor.Side.BASE)
                .build()));

        context.register(BlueprintKeys.Corridor.Side.CROPS, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Corridor.Side.CROPS)
                .mapBlock(Blocks.LIME_STAINED_GLASS, new FixedTemplateBlockType(new RandomBlock(new IRandom.Builder<BlockState>()
                        .add(Blocks.CAVE_AIR.defaultBlockState(), 2)
                        .add(Blocks.WHEAT.defaultBlockState())
                        .add(Blocks.CARROTS.defaultBlockState())
                        .add(Blocks.POTATOES.defaultBlockState())
                        .add(Blocks.BEETROOTS.defaultBlockState())
                        .build())))
                .build()));

        context.register(BlueprintKeys.Corridor.Side.DOOR, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Corridor.Side.DOOR)
                .build()));

        context.register(BlueprintKeys.Corridor.Side.FIRE, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Corridor.Side.FIRE)
                .build()));

        context.register(BlueprintKeys.Corridor.Side.FLOWER_POT, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Corridor.Side.FLOWER_POT)
                .feature(new FlowerPotFeature(
                        new PlacementSettings(Optional.of(SharedKeys.Anchor.Feature.FLOWER_POT), new Constant(1)),
                        Blocks.PODZOL,
                        new RandomBlock(new IRandom.Builder<BlockState>()
                                .add(Blocks.ROSE_BUSH.defaultBlockState(), 2)
                                .add(Blocks.LILAC.defaultBlockState(), 2)
                                .add(Blocks.PEONY.defaultBlockState(), 2)
                                .add(Blocks.DANDELION.defaultBlockState())
                                .add(Blocks.OXEYE_DAISY.defaultBlockState())
                                .add(Blocks.RED_TULIP.defaultBlockState())
                                .add(Blocks.ORANGE_TULIP.defaultBlockState())
                                .add(Blocks.WHITE_TULIP.defaultBlockState())
                                .add(Blocks.PINK_TULIP.defaultBlockState())
                                .add(Blocks.POPPY.defaultBlockState())
                                .add(Blocks.CORNFLOWER.defaultBlockState())
                                .add(Blocks.LILY_OF_THE_VALLEY.defaultBlockState())
                                .add(Blocks.AZURE_BLUET.defaultBlockState())
                                .add(Blocks.ALLIUM.defaultBlockState())
                                .add(Blocks.DEAD_BUSH.defaultBlockState())
                                .build())))
                .build()));

        context.register(BlueprintKeys.Corridor.Side.MASONRY, new Blueprint(new BlueprintConfiguration.Builder()
                .template(TemplateKeys.Corridor.Side.MASONRY)
                .build()));
    }
}
