package uk.ac.rhul.cs.cl1.support;

import uk.ac.rhul.cs.utils.Ordered;

import java.util.*;

/**
 * Queue subclass that stores <code>Ordered&lt;?&gt;</code> elements and ensures that inserted
 * elements are removed from the queue in ascending order of their sequence numbers.
 *
 * <p>Internally, the queue maintains a counter that stores the expected sequence number of
 * the next element to be popped from the queue. When an item is inserted into the queue,
 * its sequence number is compared with the internal counter. If the sequence number of the
 * inserted element is <em>less</em> than the internal counter, an exception is thrown.
 * If the sequence number of the inserted element is <em>equal</em> to the internal counter,
 * the element is stored in the queue and the counter is increased one. If the sequence number
 * of the inserted element is <em>greater</em> than the internal counter, the element is
 * put aside into a heap and will be inserted into the queue if the internal counter
 * reaches the sequence number of the element.</p>
 */
public class OrderMaintainingQueue<T> extends AbstractQueue<Ordered<T>> implements Queue<Ordered<T>> {

    /**
     * Internal counter that stores the sequence number of the next element that
     * can be inserted immediately into the queue.
     */
    private int nextSequenceNumber;

    /**
     * Queue that stores the items that are actually inserted into the queue.
     */
    private ArrayDeque<Ordered<T>> items;

    /**
     * Heap that stores pending items waiting to be inserted into the queue when the
     * next sequence number of the queue reaches their own sequence numbers.
     */
    private PriorityQueue<Ordered<T>> pendingItems;

    /**
     * Constructor.
     */
    public OrderMaintainingQueue() {
        super();
        clear();
    }

    /**
     * Clears the queue and resets the sequence number counter to zero.
     */
    public void clear() {
        nextSequenceNumber = 0;

        if (items == null) {
            items = new ArrayDeque<Ordered<T>>();
        } else {
            items.clear();
        }

        if (pendingItems == null) {
            pendingItems = new PriorityQueue<Ordered<T>>();
        } else {
            pendingItems.clear();
        }
    }

    @Override
    public Iterator<Ordered<T>> iterator() {
        return items.iterator();
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean offer(Ordered<T> item) {
        if (item.sequenceNumber == nextSequenceNumber) {
            // This is easy, just put the item into the actual queue.
            items.add(item);
            nextSequenceNumber++;
            // Try to move some pending items to the actual queue.
            flushPendingItems();
        } else if (item.sequenceNumber < nextSequenceNumber) {
            // This item should have been seen before, so throw an exception.
            throw new IllegalArgumentException("item with sequence number " + item.sequenceNumber +
                    " inserted into an order-maintaining queue with expected sequence number = " + nextSequenceNumber);
        } else {
            // This item has to wait until all the items with smaller sequence numbers
            // arrive.
            pendingItems.add(item);
        }
        return true;
    }

    @Override
    public Ordered<T> poll() {
        return items.poll();
    }

    @Override
    public Ordered<T> peek() {
        return items.peek();
    }

    /**
     * Tries to move pending items to the actual queue if possible. This method must be
     * called every time <code>nextSequenceNumber</code> is increased.
     */
    private void flushPendingItems() {
        while (!pendingItems.isEmpty()) {
            Ordered<T> nextPendingItem = pendingItems.peek();
            if (nextPendingItem.sequenceNumber == nextSequenceNumber) {
                items.add(nextPendingItem);
                pendingItems.remove();
                nextSequenceNumber++;
            } else {
                break;
            }
        }
    }
}
