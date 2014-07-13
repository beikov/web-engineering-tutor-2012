package at.ac.big.tuwien.ewa.persistence;

import java.util.LinkedList;

/**
 * Limited queue
 * @author pl
 *
 * @param <E>
 */
public class LimitedQueue<E> extends LinkedList<E> {

    private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        super.addFirst(o);
        while (size() > limit) { super.removeLast(); }
        return true;
    }
}
