package xiroc.dungeoncrawl.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.RecordBuilder;

import java.util.List;
import java.util.function.Function;

public interface StorageHelper {
    /**
     * Maps the list and entry results to a result of the list containing the entry.
     * @param list The result for the list to add the entry to.
     * @param entry The result for the entry to add to the list.
     * @return The result for the resulting list.
     */
    static <T> DataResult<List<T>> addToList(DataResult<List<T>> list, DataResult<T> entry) {
        return list.flatMap(actualList -> StorageHelper.addToList(actualList, entry));
    }

    /**
     * Given a list and a result for a value, adds the value to the list if it is present.
     * Otherwise, returns the error for the value's result.
     *
     * @return A data result holding either the list or an error.
     */
    static <T> DataResult<List<T>> addToList(List<T> list, DataResult<T> entry) {
        return entry.map(value -> {
            list.add(value);
            return list;
        });
    }

    /**
     * Given a map builder and results for a key and a value, adds the mapping to the map builder if
     * both key and value are present.
     * Otherwise, returns the error for either the key or the value, prioritizing the former.
     *
     * @return A data result holding either the map builder or an error.
     */
    static <T> DataResult<RecordBuilder<T>> addToMap(RecordBuilder<T> map, DataResult<T> key, DataResult<T> value) {
        return key.flatMap(k -> value.map(v -> map.add(k, v)));
    }

    /**
     * Appends the second list to the first list if both are present.
     * Otherwise, returns the error for either the first or the second list, prioritizing the former.
     *
     * @return A data result holding either the concatenated list or an error.
     */
    static <T> DataResult<List<T>> concatenateLists(DataResult<List<T>> first, DataResult<List<T>> second) {
        return first.flatMap(firstList -> second.map(secondList -> {
            firstList.addAll(secondList);
            return firstList;
        }));
    }

    /**
     * Transforms a pair of data results into a data result of a pair.
     * If either of the two results is an error, that error is returned, prioritizing the first one.
     *
     * @return The pair data result.
     */
    static <T, U> DataResult<Pair<T, U>> unpack(Pair<DataResult<T>, DataResult<U>> input) {
        return input.getFirst().flatMap(first -> input.getSecond().map(second -> Pair.of(first, second)));
    }

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
}
