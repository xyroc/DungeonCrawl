package xiroc.dungeoncrawl.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.List;

public interface StorageHelper {
    static <T> Tag encode(T value, Codec<T> codec) {
        return codec.encodeStart(NbtOps.INSTANCE, value).result().orElseThrow();
    }

    static <T> T decode(Tag tag, Codec<T> codec) {
        return codec.decode(NbtOps.INSTANCE, tag).result().map(Pair::getFirst).orElseThrow();
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
     * @return The pair data result.
     */
    static <T, U> DataResult<Pair<T, U>> unpack(Pair<DataResult<T>, DataResult<U>> input) {
        return input.getFirst().flatMap(first -> input.getSecond().map(second -> Pair.of(first, second)));
    }
}
