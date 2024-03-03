package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.List;

public record FeatureSet(int type, List<PlacedFeature> features) {
    public static final Codec<FeatureSet> CODEC = new Codec<>() {
        private static final String KEY_TYPE = "type";
        private static final String KEY_FEATURES = "features";

        @Override
        public <T> DataResult<Pair<FeatureSet, T>> decode(DynamicOps<T> dynamicOps, T t) {
            return dynamicOps.getMap(t).flatMap(r -> Codec.INT.decode(dynamicOps, r.get(KEY_TYPE))
                    .flatMap(type -> PlacedFeature.codec(type.getFirst()).listOf().decode(dynamicOps, r.get(KEY_FEATURES))
                            .map(features -> Pair.of(new FeatureSet(type.getFirst(), features.getFirst()), features.getSecond()))));
        }

        @Override
        public <T> DataResult<T> encode(FeatureSet featureSet, DynamicOps<T> dynamicOps, T t) {
            var map = dynamicOps.mapBuilder();
            map.add(KEY_TYPE, Codec.INT.encodeStart(dynamicOps, featureSet.type));
            map.add(KEY_FEATURES, PlacedFeature.codec(featureSet.type).listOf().encodeStart(dynamicOps, featureSet.features));
            return map.build(t);
        }
    };
}
