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
import longbow.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class ExecuteWorkflowTest {

	private Workflow workflow;

	private LongbowFactory factory;

	private Metadata metadata;

	@Before
	public void setUp() {
		factory = new DefaultLongbowFactory();
		workflow = factory.createWorkflow();
		metadata = createMock(Metadata.class);
		checkOrder(metadata, false);
		expect(metadata.acceptsData(anyObject())).andReturn(true).anyTimes();
		expect(metadata.acceptsMetadata(isA(Metadata.class))).andReturn(true).anyTimes();
		replay(metadata);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testExecuteWorkflowOneTransformation() {
		final MarkAndSweepListener listener = createMock(MarkAndSweepListener.class);
		listener.marking(isA(MarkAndSweepEvent.class));
		listener.marked(isA(MarkAndSweepEvent.class));
		listener.sweeping(isA(MarkAndSweepEvent.class));
		listener.swept(isA(MarkAndSweepEvent.class));
		assertNotSame(Mode.RUN, workflow.getMode());
		final Transformation t = createTransformation();
		replay(t);
		final TransformationContext context = workflow.add(t);
		expectExecute(t, 1);
		assertTrue(t.isExecutable());
		assertTrue(workflow.isExecutable());
		workflow.execute();
		assertSame(Mode.RUN, workflow.getMode());
		context.mark();
		context.sweep();
		assertSame(Mode.RUN, workflow.getMode());
		workflow.terminate();
		assertSame(Mode.DESIGN, workflow.getMode());
		verify(t);
	}

	@Test
	public void testExecuteWorkflowSeveralTimes() {
		final int[] counts = { 2, 5, 10, 20 };
		for (int i = 0; i < counts.length; i++) {
			final int count = counts[i];
			testExecuteWorkflow2(count);
		}
	}

	/**
	 * Check what performance loss is, taking EasyMock invocations into account.
	 */
	@Test
	public void testNormalExecutionWithoutExceptions() {
		final int executions = 100;
		final int tfns = 1000;
		final Transformation t = createNiceMock(Transformation.class);
		long time = System.currentTimeMillis();
		for (int i = 0; i < executions; i++) {
			t.startExecution(isA(TransformationContext.class));
			for (int j = 0; j < tfns; j++) {
				doSomething(t);
			}
			t.stopExecution();
		}
		time = System.currentTimeMillis() - time;
	}

	@Test
	public void testSetUp() {
		final Transformation t = createTransformation();
		replay(t);
		workflow.add(t);
		assertTrue(t.isExecutable());
		verify(t);

	}

	void doSomething(final Transformation t) {
		t.importData();
		t.processData();
		t.exportData();
	}

	private void testExecuteWorkflow2(final int count) throws AssertionError {
		final int times[] = { 5, 6, 7, 10 };
		final Transformation[] t = new Transformation[count];
		final TransformationContext[] c = new TransformationContext[count];
		for (int i = 0; i < t.length; i++) {
			t[i] = createTransformation();
			replay(t[i]);
			c[i] = workflow.add(t[i]);
			if (i > 0) {
				c[i].addInput("in", metadata);
				c[i - 1].addOutput("out", metadata);
				workflow.connect(c[i - 1], "out", c[i], "in");
			}
		}
		for (int i = 0; i < t.length; i++) {
			expectExecute(t[i], times);
		}
		for (final int num : times) {
			workflow.execute();
			for (int i = 0; i < num; i++) {
				c[0].mark();
				c[count - 1].sweep();
			}
			workflow.terminate();
		}
		verify((Object[]) t);
		workflow.clear();
	}

	private static Transformation createTransformation() {
		final Transformation transformation = createMock(Transformation.class);
		checkOrder(transformation, false);
		transformation.addToContext(isA(ContextEvent.class));
		expectLastCall().anyTimes();
		transformation.contextChange(isA(ContextEvent.class));
		expectLastCall().anyTimes();
		expect(transformation.isExecutable()).andReturn(true).anyTimes();
		return transformation;
	}

	private static void expectExecute(final Transformation t, final int... times) {
		verify(t);
		reset(t);
		checkOrder(t, true);
		for (int i = 0; i < times.length; i++) {
			expect(t.isExecutable()).andReturn(true).anyTimes();
			t.startExecution(isA(TransformationContext.class));
			for (int j = 0; j < times[i]; j++) {
				t.importData();
				t.processData();
				t.exportData();
			}
			t.stopExecution();
		}
		replay(t);
	}
}
