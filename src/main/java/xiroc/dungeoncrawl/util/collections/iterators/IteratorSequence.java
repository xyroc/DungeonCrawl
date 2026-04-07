package xiroc.dungeoncrawl.util.collections.iterators;

import java.util.Collection;
import java.util.Iterator;

/**
 * Concatenates a collection of iterators to a single iterator.
 *
 * @param <T> The type of the iterators.
 */
public class IteratorSequence<T> implements Iterator<T> {
    private Iterator<T> iterator;
    private final Iterator<Iterator<T>> iterators;

    public IteratorSequence(Collection<Iterable<T>> iterators) {
        this(iterators.stream().map(Iterable::iterator).iterator());
    }

    public IteratorSequence(Iterator<Iterator<T>> iterators) {
        this.iterators = iterators;
        this.iterator = iterators.next();
    }

    @Override
    public boolean hasNext() {
        while (!iterator.hasNext()) {
            if (iterators.hasNext()) {
                iterator = iterators.next();
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public T next() {
        while (!iterator.hasNext() && iterators.hasNext()) {
            iterator = iterators.next();
        }
        return iterator.next();
    }
}
