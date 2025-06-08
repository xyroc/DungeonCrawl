package xiroc.dungeoncrawl.util.random.value;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import xiroc.dungeoncrawl.util.StorageHelper;

import java.util.Random;

public interface RandomValue {
    Codec<RandomValue> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<RandomValue, T>> decode(DynamicOps<T> ops, T input) {
            var range = Range.CODEC.decode(ops, input);
            if (range.result().isPresent()) {
                return range.map(StorageHelper::repack);
            }
            return Constant.CODEC.decode(ops, input).map(StorageHelper::repack);
        }

        @Override
        public <T> DataResult<T> encode(RandomValue input, DynamicOps<T> ops, T prefix) {
            if (input instanceof Range range) {
                return Range.CODEC.encode(range, ops, prefix);
            }
            if (input instanceof Constant constant) {
                return Constant.CODEC.encode(constant, ops, prefix);
            }
            return DataResult.error("Invalid random value type: " + input.getClass().getName());
        }
    };

    int nextInt(Random random);

    /**
     * @return whether any value provided will be within the given bounds which are considered inclusive.
     */
    boolean isAlwaysWithin(int lowerBound, int upperBound);

    /**
     * @return whether any value provided will be greater than zero.
     */
    default boolean isAlwaysPositive() {
        return isAlwaysWithin(1, Integer.MAX_VALUE);
    }

    /**
     * @return whether any value provided will be greater than or equal to zero.
     */
    default boolean isAlwaysNonNegative() {
        return isAlwaysWithin(0, Integer.MAX_VALUE);
    }

    /**
     * @return whether any value provided will be less than zero.
     */
    default boolean isAlwaysNegative() {
        return isAlwaysWithin(Integer.MIN_VALUE, -1);
    }

    /**
     * @return whether any value provided will be less than or equal to zero.
     */
    default boolean isAlwaysNonPositive() {
        return isAlwaysWithin(Integer.MIN_VALUE, 0);
    }
}
