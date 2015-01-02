package uk.ac.rhul.cs.cl1.support;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.rhul.cs.utils.Ordered;

public class OrderMaintainingQueueTest {
    @Test
    public void testAdditionInCorrectOrder() {
        OrderMaintainingQueue<Integer> queue = new OrderMaintainingQueue<Integer>();

        Assert.assertTrue(queue.isEmpty());

        queue.add(new Ordered<Integer>(0, 100));
        queue.add(new Ordered<Integer>(1, 200));
        queue.add(new Ordered<Integer>(2, 300));

        Assert.assertEquals(3, queue.size());

        Assert.assertEquals(new Ordered<Integer>(0, 100), queue.remove());
        Assert.assertEquals(new Ordered<Integer>(1, 200), queue.remove());
        Assert.assertEquals(new Ordered<Integer>(2, 300), queue.remove());

        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void testAdditionInReversedOrder() {
        OrderMaintainingQueue<Integer> queue = new OrderMaintainingQueue<Integer>();

        Assert.assertTrue(queue.isEmpty());

        queue.add(new Ordered<Integer>(2, 300));
        queue.add(new Ordered<Integer>(1, 200));

        // size should still be zero here -- both items are pending
        Assert.assertEquals(0, queue.size());

        queue.add(new Ordered<Integer>(0, 100));

        // size suddenly grows to three as all the pending items are now in the queue
        Assert.assertEquals(3, queue.size());

        // flush the queue
        Assert.assertEquals(new Ordered<Integer>(0, 100), queue.remove());
        Assert.assertEquals(new Ordered<Integer>(1, 200), queue.remove());
        Assert.assertEquals(new Ordered<Integer>(2, 300), queue.remove());

        Assert.assertTrue(queue.isEmpty());
    }
}
