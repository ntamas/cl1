package uk.ac.rhul.cs.utils;

import static org.junit.Assert.*;

import java.util.ListIterator;

import org.junit.Test;

public class IntegerRangeTest {
	@Test
	public void testSize() {
		assertEquals(new IntegerRange(0, 10).size(), 10);
		assertEquals(new IntegerRange(0, 5).size(), 5);
		assertEquals(new IntegerRange(3, 42).size(), 39);
		assertEquals(new IntegerRange(3, 42, 2).size(), 20);
		assertEquals(new IntegerRange(3, 42, 7).size(), 6);
		assertEquals(new IntegerRange(3, 4, 7).size(), 1);
		assertEquals(new IntegerRange(42, 3).size(), 0);
		assertEquals(new IntegerRange(42, 3, -1).size(), 39);
		assertEquals(new IntegerRange(42, 3, -3).size(), 13);
		assertEquals(new IntegerRange(42, 42).size(), 0);
	}

	@Test
	public void testGetInt() {
		IntegerRange range = new IntegerRange(3, 42);
		for (int i = 0; i < range.size(); i++)
			assertEquals((int)range.get(i), 3+i);
		
		range = new IntegerRange(3, 42, 5);
		for (int i = 0; i < range.size(); i++)
			assertEquals((int)range.get(i), 3+i*5);
		
		range = new IntegerRange(42, 3, -4);
		for (int i = 0; i < range.size(); i++)
			assertEquals((int)range.get(i), 42-i*4);
	}

	@Test
	public void testIterator() {
		IntegerRange range = new IntegerRange(3, 42);
		int j = 0;
		for (int i: range) {
			assertEquals(i, 3 + j);
			j++;
		}
		assertEquals(39, j);
		
		range = new IntegerRange(3, 42, 7);
		j = 0;
		for (int i: range) {
			assertEquals(i, 3 + 7*j);
			j++;
		}
		assertEquals(6, j);
		
		range = new IntegerRange(6, -6, -2);
		j = 0;
		for (int i: range) {
			assertEquals(i, 6 - 2*j);
			j++;
		}
		assertEquals(6, j);
	}

	@Test
	public void testListIterator() {
		IntegerRange range = new IntegerRange(3, 42, 3);
		int j = 0;
		ListIterator<Integer> it = range.listIterator();
		
		while (it.hasNext()) {
			assertEquals(j, it.nextIndex());
			assertEquals(3+j*3, (int)it.next());
			j++;
		}
		assertEquals(range.size(), j);
		
		while (it.hasPrevious()) {
			j--;
			assertEquals(j, it.previousIndex());
			assertEquals(3+j*3, (int)it.previous());
		}
		assertEquals(0, j);
	}
}
