package xiroc.dungeoncrawl.util.random;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Randomly shuffles an array in-place to realize an urn problem without replacement.
 */
public class ArrayUrn<T> implements IRandom<T> {
    private final T[] elements;
    private int pivot;

    /**
     * Instantiates an array-urn.
     *
     * @param elements the elements to use
     * @param copy     whether the array of elements should be copied or used as is
     */
    public ArrayUrn(T[] elements, boolean copy) {
        this.elements = copy ? Arrays.copyOf(elements, elements.length) : elements;
        this.pivot = 0;
    }

    /**
     * @return the amount of elements currently eligible
     */
    public int elementsLeft() {
        return elements.length - pivot;
    }

    /**
     * @return true, if there are any elements left.
     */
    public boolean hasMore() {
        return pivot < elements.length;
    }

    /**
     * Resets the state of this urn so that any element may be chosen again.
     */
    public void reset() {
        this.pivot = 0;
    }

    /**
     * Randomly draws one of the remaining elements.
     *
     * @param rand the source of randomness.
     * @return the chosen element.
     * @throws IllegalStateException if there are no more elements left.
     */
    @Override
    public T roll(Random rand) {
        int valuesLeft = elements.length - pivot;
        if (valuesLeft == 0) {
            throw new IllegalStateException("There are no more elements left to choose from.");
        }

        int chosenIndex = pivot + rand.nextInt(valuesLeft);
        T chosenElement = elements[chosenIndex];
        elements[chosenIndex] = elements[pivot];
        elements[pivot] = chosenElement;

        ++pivot;
        return chosenElement;
    }

    /**
     * Randomly chooses one of all existing elements without removing it from the urn.
     *
     * @param rand the random source.
     * @return the chosen element.
     */
    public T rollIndependent(Random rand) {
        return elements[rand.nextInt(elements.length)];
    }

    /**
     * Applies the consumer to all existing entries.
     */
    public void forEachExisting(Consumer<T> consumer) {
        for (T element : elements) {
            consumer.accept(element);
        }
    }

    /**
     * Applies the consumer to all entries that have not been drawn yet.
     */
    public void forEachRemaining(Consumer<T> consumer) {
        for (int i = pivot; i < elements.length; ++i) {
            consumer.accept(elements[i]);
        }
    }
}