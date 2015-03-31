/*
 * Copyright 2008 Philip van Oosten (Mentoring Systems BVBA)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package longbow.core;


import static junit.framework.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class PartialOrderTest {

	private PartialOrderImpl<Integer> porder;

	private void checkOrder(final int last) throws AssertionError {
		final Iterator<Integer> it = porder.iterator();
		assertTrue(it.hasNext());
		int num = it.next();
		assertEquals(0, num);
		final boolean[] hits = new boolean[4];
		final int firstmiddle = last == 5 ? 1 : 2;
		final int lastmiddle = last == 5 ? 4 : 5;
		final int dec = last == 5 ? 1 : 2;
		for (int i = firstmiddle; i <= lastmiddle; i++) {
			hits[i - dec] = false;
		}
		for (int i = firstmiddle; i <= lastmiddle; i++) {
			assertTrue(it.hasNext());
			num = it.next();
			final int element = num - dec;
			hits[element] = true;
		}
		for (int i = firstmiddle; i <= lastmiddle; i++) {
			assertTrue(hits[i - dec]);
		}
		assertTrue(it.hasNext());
		num = it.next();
		assertEquals("last in partial order", last, num);
		assertFalse(it.hasNext());
	}

	@Before
	public void setUp() {
		porder = new PartialOrderImpl<Integer>();
	}

	/**
	 * Test precondition for other tests
	 */
	@Test
	public void testAutoboxingSet() {
		final Set<Integer> set = new HashSet<Integer>();
		set.add(1);
		assertTrue("set must contain autoboxed integer", set.contains(1));
	}

	@Test
	public void testConnectAndDisconnect() {
		porder.add(1);
		porder.add(2);
		assertFalse("can't disconnect unconnected elements", porder.disconnect(
		        1, 2));
		assertTrue("can connect unconnected contained elements", porder
		        .connect(1, 2));
		assertTrue("can disconnect connected elements", porder.disconnect(1, 2));
		assertFalse("can't disconnect unconnected elements", porder.disconnect(
		        1, 2));
		assertTrue("reversely connect disconnected elements", porder.connect(2,
		        1));
		assertTrue("can add second connection", porder.connect(2, 1));
		assertTrue("disconnect double connected elements", porder.disconnect(2,
		        1));
		assertFalse("can't reversely disconnect", porder.disconnect(1, 2));
		assertTrue("disconnect last connection", porder.disconnect(2, 1));
		assertFalse("can't disconnect unconnected elements", porder.disconnect(
		        2, 1));
	}

	@Test
	public void testConnectAndFollows() {
		porder.add(1);
		porder.add(2);
		assertFalse(porder.follows(1, 2));
		assertFalse(porder.follows(2, 1));
		assertTrue("can connect new elements", porder.connect(2, 1));
		assertFalse(porder.follows(1, 2));
		assertTrue("connected elements follow each other", porder.follows(2, 1));
		assertTrue("can connect already connected elements", porder.connect(2,
		        1));
		assertTrue(porder.follows(2, 1));
		assertFalse(porder.follows(1, 2));
		assertFalse("can't reversely connect connected elements", porder
		        .connect(1, 2));
		assertTrue(porder.follows(2, 1));
		assertFalse(porder.follows(1, 2));
	}

	@Test
	public void testConnectSideways() {
		for (int i = 0; i < 6; i++) {
			assertTrue(porder.add(i));
		}
		assertTrue(porder.connect(0, 1));
		assertTrue(porder.connect(0, 2));
		assertTrue(porder.connect(2, 3));
		assertTrue(porder.connect(2, 4));
		assertTrue(porder.connect(3, 5));
		assertTrue(porder.connect(4, 5));
		for (int i = 2; i <= 5; i++) {
			assertTrue(porder.connect(i, 1));
			assertFalse(porder.connect(i, 0));
		}
		for (int i = 2; i <= 5; i++) {
			assertFalse(porder.disconnect(1, i));
		}
		// 1 is now last an 0 still first in partial order
		// 2 to 5 are also traversed by the iterator
		checkOrder(1);
		// disconnect sideways connections and reconnect in reverse way
		for (int i = 5; i >= 2; i--) {
			assertTrue(porder.disconnect(i, 1));
			assertTrue(porder.connect(1, i));
		}
		checkOrder(5);
	}

	/**
	 * Test adding unordered elements
	 */
	@Test
	public void testNoOrder() {
		assertTrue(porder.isEmpty());
		assertTrue(porder.add(3));
		assertFalse(porder.add(3));
		assertTrue(porder.contains(3));
		assertFalse(porder.isEmpty());
		porder.clear();
		assertTrue(porder.isEmpty());
		assertTrue(porder.add(5));
		assertEquals(1, porder.size());
		assertTrue(porder.remove(5));
		assertEquals(0, porder.size());
	}

}
