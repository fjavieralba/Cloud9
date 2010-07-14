package edu.umd.cloud9.io.fastutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.io.IOException;
import java.util.Random;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import edu.umd.cloud9.io.fastuil.Int2IntOpenHashMapWritable;

public class Int2IntOpenHashMapWritableTest {

	@Test
	public void testBasic() throws IOException {
		Int2IntOpenHashMapWritable m = new Int2IntOpenHashMapWritable();

		m.put(2, 5);
		m.put(1, 22);

		int value;

		assertEquals(m.size(), 2);

		value = m.get(2);
		assertEquals(5, value);

		value = m.remove(2);
		assertEquals(m.size(), 1);

		value = m.get(1);
		assertEquals(22, value);
	}

	@Test
	public void testSerialize1() throws IOException {
		Int2IntOpenHashMapWritable m1 = new Int2IntOpenHashMapWritable();

		m1.put(3, 5);
		m1.put(4, 22);

		Int2IntOpenHashMapWritable n2 = Int2IntOpenHashMapWritable.create(m1.serialize());

		int value;

		assertEquals(n2.size(), 2);

		value = n2.get(3);
		assertEquals(5, value);

		value = n2.remove(3);
		assertEquals(n2.size(), 1);

		value = n2.get(4);
		assertEquals(value, 22);
	}

	@Test
	public void testSerializeLazy1() throws IOException {
		Int2IntOpenHashMapWritable.setLazyDecodeFlag(true);
		Int2IntOpenHashMapWritable m1 = new Int2IntOpenHashMapWritable();

		m1.put(3, 5);
		m1.put(4, 22);

		Int2IntOpenHashMapWritable m2 = Int2IntOpenHashMapWritable.create(m1.serialize());

		assertEquals(0, m2.size());

		int[] keys = m2.getKeys();
		int[] values = m2.getValues();

		assertTrue(keys[0] == 3);
		assertTrue(keys[1] == 4);

		assertTrue(values[0] == 5.0f);
		assertTrue(values[1] == 22.0f);

		m2.decode();
		float value;
		assertEquals(m2.size(), 2);

		value = m2.get(3);
		assertTrue(value == 5.0f);

		value = m2.remove(3);
		assertEquals(m2.size(), 1);

		value = m2.get(4);
		assertTrue(value == 22.0f);
	}

	@Test
	public void testSerializeEmpty() throws IOException {
		Int2IntOpenHashMapWritable m1 = new Int2IntOpenHashMapWritable();

		// make sure this does nothing
		m1.decode();

		assertTrue(m1.size() == 0);

		Int2IntMap m2 = Int2IntOpenHashMapWritable.create(m1.serialize());

		assertTrue(m2.size() == 0);
	}

	@Test
	public void testBasic1() {

		int size = 100000;
		Random r = new Random();
		int[] ints = new int[size];

		Int2IntMap map = new Int2IntOpenHashMapWritable();
		for (int i = 0; i < size; i++) {
			int k = r.nextInt(size);
			map.put(i, k);
			ints[i] = k;
		}

		for (int i = 0; i < size; i++) {
			int v = map.get(i);

			assertEquals(ints[i], v);
			assertTrue(map.containsKey(i));
		}

	}

	@Test
	public void testUpdate() {

		int size = 100000;
		Random r = new Random();
		int[] ints = new int[size];

		Int2IntMap map = new Int2IntOpenHashMapWritable();
		for (int i = 0; i < size; i++) {
			int k = r.nextInt(size);
			map.put(i, k);
			ints[i] = k;
		}

		assertEquals(size, map.size());

		for (int i = 0; i < size; i++) {
			map.put(i, ints[i] + 1);
		}

		assertEquals(size, map.size());

		for (int i = 0; i < size; i++) {
			int v = map.get(i);

			assertEquals(ints[i] + 1, v);
			assertTrue(map.containsKey(i));
		}

	}

	@Test
	public void testPlus() throws IOException {
		Int2IntOpenHashMapWritable m1 = new Int2IntOpenHashMapWritable();

		m1.put(1, 5);
		m1.put(2, 22);

		Int2IntOpenHashMapWritable m2 = new Int2IntOpenHashMapWritable();

		m2.put(1, 4);
		m2.put(3, 5);

		m1.plus(m2);

		assertEquals(m1.size(), 3);
		assertTrue(m1.get(1) == 9);
		assertTrue(m1.get(2) == 22);
		assertTrue(m1.get(3) == 5);
	}

	@Test
	public void testLazyPlus() throws IOException {
		Int2IntOpenHashMapWritable m1 = new Int2IntOpenHashMapWritable();

		m1.put(1, 5);
		m1.put(2, 22);

		Int2IntOpenHashMapWritable m2 = new Int2IntOpenHashMapWritable();

		m2.put(1, 4);
		m2.put(3, 5);

		Int2IntOpenHashMapWritable.setLazyDecodeFlag(true);
		Int2IntOpenHashMapWritable m3 = Int2IntOpenHashMapWritable.create(m2.serialize());

		assertEquals(0, m3.size());
		
		m1.lazyplus(m3);
		
		assertEquals(m1.size(), 3);
		assertTrue(m1.get(1) == 9);
		assertTrue(m1.get(2) == 22);
		assertTrue(m1.get(3) == 5);
	}
	
	@Test
	public void testDot() throws IOException {
		Int2IntOpenHashMapWritable m1 = new Int2IntOpenHashMapWritable();

		m1.put(1, 2);
		m1.put(2, 1);
		m1.put(3, 3);

		Int2IntOpenHashMapWritable m2 = new Int2IntOpenHashMapWritable();

		m2.put(1, 1);
		m2.put(2, 4);
		m2.put(4, 5);

		int s = m1.dot(m2);

		assertTrue(s == 6);
	}

	@Test
	public void testSortedEntries1() {
		Int2IntOpenHashMapWritable m = new Int2IntOpenHashMapWritable();

		m.put(1, 5);
		m.put(2, 2);
		m.put(3, 3);
		m.put(4, 3);
		m.put(5, 1);

		Int2IntMap.Entry[] e = m.getEntriesSortedByValue();
		assertEquals(5, e.length);

		assertEquals(1, e[0].getIntKey());
		assertEquals(5, e[0].getIntValue());

		assertEquals(3, e[1].getIntKey());
		assertEquals(3, e[1].getIntValue());

		assertEquals(4, e[2].getIntKey());
		assertEquals(3, e[2].getIntValue());

		assertEquals(2, e[3].getIntKey());
		assertEquals(2, e[3].getIntValue());

		assertEquals(5, e[4].getIntKey());
		assertEquals(1, e[4].getIntValue());
	}

	@Test
	public void testSortedEntries2() {
		Int2IntOpenHashMapWritable m = new Int2IntOpenHashMapWritable();

		m.put(1, 5);
		m.put(2, 2);
		m.put(3, 3);
		m.put(4, 3);
		m.put(5, 1);

		Int2IntMap.Entry[] e = m.getEntriesSortedByValue(2);

		assertEquals(2, e.length);

		assertEquals(1, e[0].getIntKey());
		assertEquals(5, e[0].getIntValue());

		assertEquals(3, e[1].getIntKey());
		assertEquals(3, e[1].getIntValue());
	}

	@Test
	public void testSortedEntries3() {
		Int2IntOpenHashMapWritable m = new Int2IntOpenHashMapWritable();

		m.put(1, 5);
		m.put(2, 2);

		Int2IntMap.Entry[] e = m.getEntriesSortedByValue(5);

		assertEquals(2, e.length);

		assertEquals(1, e[0].getIntKey());
		assertEquals(5, e[0].getIntValue());

		assertEquals(2, e[1].getIntKey());
		assertEquals(2, e[1].getIntValue());
	}

	@Test
	public void testSortedEntries4() {
		Int2IntOpenHashMapWritable m = new Int2IntOpenHashMapWritable();

		Int2IntMap.Entry[] e = m.getEntriesSortedByValue();
		assertTrue(e == null);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(Int2IntOpenHashMapWritableTest.class);
	}

}
