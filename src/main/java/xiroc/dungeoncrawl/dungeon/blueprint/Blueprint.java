package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

import java.lang.reflect.Type;

public interface Blueprint {
    Codec<Delegate<Blueprint>> CODEC = ResourceLocation.CODEC.xmap(DatapackRegistries.BLUEPRINT::delegateOrThrow, Delegate::key);

    /**
     * Holds types representing the different contexts this class is used in.
     */
    interface Types {
        Type DELEGATE = new TypeToken<Delegate<Blueprint>>() {}.getType();

        Type RANDOM = new TypeToken<IRandom<Delegate<Blueprint>>>() {}.getType();
        Type RANDOM_BUILDER = new TypeToken<IRandom.Builder<Delegate<Blueprint>>>() {}.getType();

        Type RANDOM_RANDOM_BUILDER = new TypeToken<IRandom.Builder<IRandom<Blueprint>>>() {}.getType();
    }

    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(Types.DELEGATE, new Delegate.Serializer<>(DatapackRegistries.BLUEPRINT, null));
        builder.registerTypeAdapter(Types.RANDOM_BUILDER, new IRandom.BuilderSerializer<Delegate<Blueprint>>(Types.DELEGATE, "blueprint").wrapped());
        builder.registerTypeAdapter(Types.RANDOM, new IRandom.DirectSerializer<Delegate<Blueprint>>(Types.RANDOM_BUILDER));
        builder.registerTypeAdapter(Types.RANDOM_RANDOM_BUILDER, new IRandom.BuilderSerializer<IRandom<Delegate<Blueprint>>>(Types.RANDOM, "blueprints"));
    }

    void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext);

    int xSpan();

    int ySpan();

    int zSpan();

    ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors();

    ImmutableList<BlueprintFeature> features();

    ImmutableList<Entrance> entrances();

    default ImmutableList<BlueprintMultipart> parts() {
        return ImmutableList.of();
    }

    default BoundingBoxBuilder boundingBox(Rotation rotation) {
        return switch (rotation) {
            case NONE, CLOCKWISE_180 -> new BoundingBoxBuilder(0, 0, 0, xSpan() - 1, ySpan() - 1, zSpan() - 1);
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> new BoundingBoxBuilder(0, 0, 0, zSpan() - 1, ySpan() - 1, xSpan() - 1);
        };
    }

    default CoordinateSpace coordinateSpace(BlockPos offset) {
        return new CoordinateSpace(offset, xSpan(), zSpan());
    }
}