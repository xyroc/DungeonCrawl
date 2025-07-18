package xiroc.dungeoncrawl.dungeon.blueprint.feature.settings;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

public record PlacementSettings(Optional<ResourceLocation> positions, RandomValue amount) {
    public static final Codec<PlacementSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("positions").forGetter(PlacementSettings::positions),
            RandomValue.CODEC.fieldOf("amount").forGetter(PlacementSettings::amount)
    ).apply(instance, PlacementSettings::new));

    public Optional<ArrayList<Anchor>> anchors(Blueprint blueprint) {
        return positions.map(blueprint.anchors()::get).map(Lists::newArrayList);
    }

    public void drawPositions(ArrayList<Anchor> positions, RandomSource random, Consumer<Anchor> consumer) {
        for (int count = amount.nextInt(random); count > 0 && !positions.isEmpty(); --count) {
            int anchor = random.nextInt(positions.size());
            consumer.accept(positions.remove(anchor));
        }
    }
}
