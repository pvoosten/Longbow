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
import static org.easymock.EasyMock.*;
import longbow.MarkAndSweepEvent;
import longbow.MarkAndSweepListener;
import longbow.TransformationContext;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class MarkAndSweepSupportTest {

	private MarkAndSweepSupport mss;

	private MarkAndSweepListener listener;

	@Before
	public void setUp() {
		final TransformationContext context = createNiceMock(TransformationContext.class);
		mss = new MarkAndSweepSupport(context);
		assertFalse(mss.isMarkAndSweepListenersEnabled());
		mss.setListenersEnabled(true);
		listener = createMock(MarkAndSweepListener.class);
		mss.addMarkAndSweepListener(listener);
	}

	@Test
	public void testDisableListeners() {
		mss.setListenersEnabled(false);
		replay(listener);
		mss.fireMarking();
		mss.fireMarked();
		mss.fireSweeping();
		mss.fireSwept();
		verify(listener);
	}

	@Test
	public void testInitiallyMarked() {
		assertFalse(mss.isValid());
	}

	@Test(expected = IllegalStateException.class)
	public void testInitThenMarked() {
		mss.fireMarked();
	}

	@Test
	public void testInitThenMarking() {
		listener.marking(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireMarking();
		verify(listener);
	}

	@Test
	public void testInitThenSweeping() {
		listener.sweeping(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireSweeping();
		verify(listener);
	}

	@Test(expected = IllegalStateException.class)
	public void testInitThenSwept() {
		mss.fireSwept();
	}

	@Test(expected = IllegalStateException.class)
	public void testMarkedThenMarked() {
		listener.marking(isA(MarkAndSweepEvent.class));
		listener.marked(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireMarking();
		mss.fireMarked();
		verify(listener);
		mss.fireMarked();
	}

	@Test
	public void testMarkedThenMarking() {
		listener.marking(isA(MarkAndSweepEvent.class));
		listener.marked(isA(MarkAndSweepEvent.class));
		listener.marking(isA(MarkAndSweepEvent.class));

		replay(listener);
		mss.fireMarking();
		mss.fireMarked();
		mss.fireMarking();
		verify(listener);
	}

	@Test
	public void testMarkedThenSweeping() {
		listener.marking(isA(MarkAndSweepEvent.class));
		listener.marked(isA(MarkAndSweepEvent.class));
		listener.sweeping(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireMarking();
		mss.fireMarked();
		mss.fireSweeping();
		verify(listener);
	}

	@Test(expected = IllegalStateException.class)
	public void testMarkedThenSwept() {
		listener.marking(isA(MarkAndSweepEvent.class));
		listener.marked(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireMarking();
		mss.fireMarked();
		verify(listener);
		mss.fireSwept();
	}

	@Test
	public void testMarkingThenMarked() {
		listener.marking(isA(MarkAndSweepEvent.class));
		listener.marked(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireMarking();
		mss.fireMarked();
		verify(listener);
	}

	@Test
	public void testMarkingThenMarking() {
		listener.marking(isA(MarkAndSweepEvent.class));
		listener.marking(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireMarking();
		mss.fireMarking();
		verify(listener);
	}

	@Test(expected = IllegalStateException.class)
	public void testMarkingThenSweeping() {
		listener.marking(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireMarking();
		verify(listener);
		mss.fireSweeping();
	}

	@Test(expected = IllegalStateException.class)
	public void testMarkingThenSwept() {
		listener.marking(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireMarking();
		verify(listener);
		mss.fireSwept();
	}

	@Test
	public void testSetUpAndTearDown() {
		// fails only if setUp or tearDown fail.
	}

	@Test(expected = IllegalStateException.class)
	public void testSweepingThenMarked() {
		listener.sweeping(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireSweeping();
		verify(listener);
		mss.fireMarked();
	}

	@Test
	public void testSweepingThenMarking() {
		listener.sweeping(isA(MarkAndSweepEvent.class));
		listener.marking(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireSweeping();
		mss.fireMarking();
		verify(listener);
	}

	@Test(expected = IllegalStateException.class)
	public void testSweepingThenSweeping() {
		listener.sweeping(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireSweeping();
		verify(listener);
		mss.fireSweeping();
	}

	@Test
	public void testSweepingThenSwept() {
		listener.sweeping(isA(MarkAndSweepEvent.class));
		listener.swept(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireSweeping();
		mss.fireSwept();
		verify(listener);
	}

	@Test(expected = IllegalStateException.class)
	public void testSweptThenMarked() {
		listener.sweeping(isA(MarkAndSweepEvent.class));
		listener.swept(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireSweeping();
		mss.fireSwept();
		verify(listener);
		mss.fireMarked();
	}

	@Test
	public void testSweptThenMarking() {
		listener.sweeping(isA(MarkAndSweepEvent.class));
		listener.swept(isA(MarkAndSweepEvent.class));
		listener.marking(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireSweeping();
		mss.fireSwept();
		mss.fireMarking();
		verify(listener);
	}

	@Test(expected = IllegalStateException.class)
	public void testSweptThenSweeping() {
		listener.sweeping(isA(MarkAndSweepEvent.class));
		listener.swept(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireSweeping();
		mss.fireSwept();
		verify(listener);
		mss.fireSweeping();
	}

	@Test(expected = IllegalStateException.class)
	public void testSweptThenSwept() {
		listener.sweeping(isA(MarkAndSweepEvent.class));
		listener.swept(isA(MarkAndSweepEvent.class));
		replay(listener);
		mss.fireSweeping();
		mss.fireSwept();
		verify(listener);
		mss.fireSwept();
	}

}
