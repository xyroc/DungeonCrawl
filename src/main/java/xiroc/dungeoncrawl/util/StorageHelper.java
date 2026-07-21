package xiroc.dungeoncrawl.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface StorageHelper {
    /**
     * Converts a {@code Pair<? extends A, T>} to a {@code Pair<A, T>}.
     *
     * @return The converted pair.
     */
    @SuppressWarnings("unchecked")
    static <A, B extends A, T> Pair<A, T> repack(Pair<B, T> pair) {
        return (Pair<A, T>) pair;
    }

    /**
     * Creates a function that returns a data result of the original function's output.
     * If any exception is thrown, it produces an error result with the exception message.
     *
     * @param function The original function.
     * @return The wrapped function.
     */
    static <A, B> Function<A, DataResult<B>> tryToApply(Function<A, B> function) {
        return input -> {
            try {
                return DataResult.success(function.apply(input));
            } catch (Exception e) {
                return DataResult.error(e::getMessage);
            }
        };
    }

    /**
     * Returns a map with all entries of {@code a} that do not have an equal counterpart in {@code b}.
     * Value comparison is done using {@link Objects#equals(Object, Object)}.
     *
     * @param a   The map to take the entries from.
     * @param b   A map holding the entries that should not be included in the result.
     * @param <K> The key type of map {@code a}. Map {@code b} may use a different type.
     * @param <V> The value type of map {@code a}. Map {@code b} may use a different type.
     * @return The resulting difference map.
     */
    static <K, V> Map<K, V> mapDifference(Map<K, V> a, Map<?, ?> b) {
        return a.entrySet().stream()
                .filter(entry -> !Objects.equals(entry.getValue(), b.get(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
