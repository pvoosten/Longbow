/*
 * Copyright 2007-2008 Philip van Oosten (Mentoring Systems BVBA)
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

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
@SuppressWarnings("nls")
public class CoreWorkflowTest {

	private Workflow workflow;

	private Transformation niceTransformation;

	private Transformation t1;

	private Transformation t2;

	private Metadata metadata;

	private String outputid;

	private String inputid;

	private LongbowFactory factory;

	@Before
	public void setUp() {
		factory = new DefaultLongbowFactory();
		workflow = factory.createWorkflow();
		niceTransformation = createNiceMock(Transformation.class);
		t1 = createMock(Transformation.class);
		t2 = createMock(Transformation.class);
		metadata = createMock(Metadata.class);
		expect(metadata.acceptsData(anyObject())).andReturn(true).times(0, Integer.MAX_VALUE);
		expect(metadata.acceptsMetadata(isA(Metadata.class))).andReturn(true).times(0, Integer.MAX_VALUE);
		replay(metadata);
		outputid = "out";
		inputid = "in";
	}

	@Test
	public void testAddInputContextEvent() {
		t1.addToContext(isA(ContextEvent.class));
		t1.contextChange(isA(ContextEvent.class));
		replay(t1);
		final TransformationContext context = workflow.add(t1);
		context.addInput("inputid", metadata);
		verify(t1);
	}

	@Test
	public void testAddOutputContextEvent() {
		t1.addToContext(isA(ContextEvent.class));
		t1.contextChange(isA(ContextEvent.class));
		replay(t1);
		final TransformationContext context = workflow.add(t1);
		context.addOutput("outputid", metadata);
		verify(t1);
	}

	/**
	 * Adding a transformation as listener to a context should not cause the
	 * transformation to listen to the same context twice.
	 */
	@Test
	public void testAddTransFormationAsContextListener() {
		// actions to add transformation to workflow
		reset(t1);
		t1.addToContext(isA(ContextEvent.class));
		replay(t1);
		final TransformationContext context = workflow.add(t1);
		verify(t1);
		// after adding context listener: event only once
		reset(t1);
		t1.contextChange(isA(ContextEvent.class));
		replay(t1);
		context.addContextListener(t1);
		context.addInput("dummy", metadata);
		verify(t1);
		// after removing context listener: event still once
		reset(t1);
		t1.contextChange(isA(ContextEvent.class));
		replay(t1);
		context.removeContextListener(t1);
		context.addInput("dummy2", metadata);
		verify(t1);
		// after removing context listener second time: event still once
		reset(t1);
		t1.contextChange(isA(ContextEvent.class));
		replay(t1);
		context.removeContextListener(t1);
		context.addInput("dummy3", metadata);
		verify(t1);
	}

	/**
	 * Establishing a connection fails if the input or output is already
	 * connected
	 */
	@Test
	public void testConnectAlreadyConnected() {
		expectAddToWorkflow(t1, t2);
		replay(t1, t2, niceTransformation);
		final TransformationContext c1 = workflow.add(t1);
		final TransformationContext c2 = workflow.add(t2);
		final TransformationContext middle = workflow.add(niceTransformation);
		verify(t1, t2);
		// add ports
		reset(t1, t2);
		t1.contextChange(isA(ContextEvent.class));
		t2.contextChange(isA(ContextEvent.class));
		replay(t1, t2);
		c1.addOutput(outputid, metadata);
		c2.addInput(inputid, metadata);
		middle.addInput(inputid, metadata);
		middle.addOutput(outputid, metadata);
		verify(t1, t2);

		// connect c1 to middle
		reset(t1, t2);
		t1.contextChange(isA(ContextEvent.class));
		replay(t1, t2);
		workflow.connect(c1, outputid, middle, inputid);
		verify(t1, t2);

		// try to connect c1 to c2 and succeed, because c1's output
		// can connect to multiple inputs
		reset(t1, t2);
		// the context of t1 does not change, because it was connected, 
		// and after connecting to another input, it is still connected.
		t2.contextChange(isA(ContextEvent.class));
		replay(t1, t2);
		workflow.connect(c1, outputid, c2, inputid);
		verify(t1);
		verify(t2);
		//		verify(t1, t2);

		// connect c2 to middle and fail
		reset(t1, t2);
		// nothing happens
		replay(t1, t2);
		workflow.connect(middle, outputid, c2, inputid);
		verify(t1, t2);
	}

	/**
	 * Test connecting transformations. The context of a transformation changes
	 * when one of its ports is connected.
	 */
	@Test
	public void testContextChangeOnConnection() {
		// add to workflow and add ports
		expectAddToWorkflow(t1, t2);
		t1.contextChange(isA(ContextEvent.class));
		t2.contextChange(isA(ContextEvent.class));
		replay(t1, t2);
		final TransformationContext c1 = workflow.add(t1);
		final TransformationContext c2 = workflow.add(t2);
		c1.addOutput(outputid, metadata);
		c2.addInput(inputid, metadata);
		verify(t1, t2);

		// connect
		reset(t1, t2);
		t1.contextChange(isA(ContextEvent.class));
		t2.contextChange(isA(ContextEvent.class));
		replay(t1, t2);
		workflow.connect(c1, outputid, c2, inputid);
		verify(t1, t2);
	}

	@Test
	public void testCreateCycleOneTransformation() {
		expectAddToWorkflow(t1);
		t1.contextChange(isA(ContextEvent.class));
		expectLastCall().times(2);
		expect(t1.isExecutable()).andReturn(true); // for assertFalse

		replay(t1);
		final TransformationContext c1 = workflow.add(t1);
		c1.addInput("in1", metadata);
		c1.addOutput("out1", metadata);
		workflow.connect(c1, "out1", c1, "in1");
		assertFalse(c1.getInput("in1").isConnected());
		assertFalse(c1.getOutput("out1").isConnected());
		assertFalse(c1.isExecutable());
		verify(t1);
	}

	/**
	 * It should not be possible to connect two transformations in a cycle
	 */
	@Test
	public void testCreateCycleTwoTransformations() {
		expect(niceTransformation.isExecutable()).andReturn(true).times(0, Integer.MAX_VALUE);
		replay(niceTransformation);
		final TransformationContext c1 = workflow.add(niceTransformation);
		final TransformationContext c2 = workflow.add(niceTransformation);
		c1.addInput(inputid, metadata);
		c1.addOutput(outputid, metadata);
		c2.addInput(inputid, metadata);
		c2.addOutput(outputid, metadata);
		workflow.connect(c1, outputid, c2, inputid);
		assertTrue("first connect: output of c1 must be connected", c1.getOutput(outputid).isConnected());
		assertTrue("first connect: input of c2 must be connected", c2.getInput(inputid).isConnected());
		assertFalse("first connect: input of c1 must not be connected", c1.getInput(inputid).isConnected());
		assertFalse("first connect: output of c2 must not be connected", c2.getOutput(outputid).isConnected());
		workflow.connect(c2, outputid, c1, inputid);
		assertTrue("second connect: output of c1 must be connected", c1.getOutput(outputid).isConnected());
		assertTrue("second connect: input of c2 must be connected", c2.getInput(inputid).isConnected());
		assertFalse("second connect: input of c1 must not be connected", c1.getInput(inputid).isConnected());
		assertFalse("second connect: output of c2 must not be connected", c2.getOutput(outputid).isConnected());
	}

	@Test
	public void testDisconnect() {
		expectAddToWorkflow(t1, t2);
		expectContextChange(2, t1, t2);
		replay(t1, t2);
		final TransformationContext c1 = workflow.add(t1);
		final TransformationContext c2 = workflow.add(t2);
		c1.addOutput(outputid, metadata);
		c2.addInput(inputid, metadata);
		// disconnecting unconnected ports has no effect
		workflow.disconnect(c1, outputid, c2, inputid);
		workflow.connect(c1, outputid, c2, inputid);
		verify(t1, t2);
		reset(t1, t2);
		expectContextChange(t1, t2);
		replay(t1, t2);
		// disconnect causes context change
		workflow.disconnect(c1, outputid, c2, inputid);
		verify(t1, t2);
		reset(t1, t2);
		// disconnecting unconnected ports has no effect
		replay(t1, t2);
		workflow.disconnect(c1, outputid, c2, inputid);
		verify(t1, t2);
	}

	@Test
	public void testDisconnectOutputTwice() {
		expectAddToWorkflow(t1); // already includes 1 context change
		expectContextChange(2, t1);
		expect(niceTransformation.isExecutable()).andReturn(true).times(0, Integer.MAX_VALUE);
		replay(t1, niceTransformation);
		final TransformationContext c1 = workflow.add(t1);
		final TransformationContext c2 = workflow.add(niceTransformation);
		final TransformationContext c3 = workflow.add(niceTransformation);
		c1.addOutput("one", metadata);
		c2.addInput("two", metadata);
		c3.addInput("three", metadata);
		workflow.connect(c1, "one", c2, "two");
		verify(t1);
		reset(t1);
		// expect no context change, because c1 is already connected
		replay(t1);
		workflow.connect(c1, "one", c3, "three");
		verify(t1);
		reset(t1);
		// expect no context change, because c1 is still connected
		replay(t1);
		workflow.disconnect(c1, "one", c2, "two");
		verify(t1);
		reset(t1);
		expectContextChange(t1);
		replay(t1);
		workflow.disconnect(c1, "one", c3, "three");
		verify(t1);
	}

	@Test
	public void testEmptyWorkflowNotExecutable() {
		assertFalse("Empty workflow executable", workflow.isExecutable());
		// add and delete a transformation
		final TransformationContext context = workflow.add(niceTransformation);
		workflow.remove(context);
		// must still be not executable
		assertFalse("Empty workflow executable", workflow.isExecutable());
	}

	@Test
	public void testInitialMode() {
		assertSame("Workflow not created in design mode", Mode.DESIGN, workflow.getMode());
	}

	@Test
	public void testMetadataMockAcceptsItself() {
		assertTrue(metadata.acceptsMetadata(metadata));
	}

	/**
	 * The context of a transformation does not change if an attempt to connect
	 * fails. Connection fails if metadata of the ends of the connection are not
	 * compatible.
	 */
	@Test
	public void testNoContextChangeOnFailToConnect() {
		// create contexts and ports
		final Metadata noConnect = createMock(Metadata.class);
		expectAddToWorkflow(t1, t2);
		t1.contextChange(isA(ContextEvent.class));
		t2.contextChange(isA(ContextEvent.class));
		replay(noConnect, t1, t2);
		final TransformationContext c1 = workflow.add(t1);
		final TransformationContext c2 = workflow.add(t2);
		c1.addOutput(outputid, metadata);
		c2.addInput(inputid, noConnect);
		verify(noConnect, t1, t2);

		// connect
		reset(noConnect, t1, t2);
		expect(noConnect.acceptsMetadata(metadata)).andReturn(false);
		// don't expect contextChange
		replay(noConnect, t1, t2);
		workflow.connect(c1, outputid, c2, inputid);
		verify(noConnect, t1, t2);
	}

	/**
	 * Only a transformation should be able to change something to the context,
	 * in response to another context change. Other context listeners should
	 * only have "read-only" access to the context.
	 */
	@Test
	public void testOnlyTransformationCanChangeContext() {
		//TransformationContext context
	}

	/**
	 * Changing tranformation of a context should unregister the removed
	 * transformation as context listener
	 */
	@Test
	public void testSetTransformationContextListener() {
		t1.addToContext(isA(ContextEvent.class));
		// change context
		t1.contextChange(isA(ContextEvent.class));
		// setting t1 again does not invoke methods
		// set t2 as transformation
		t2.addToContext(isA(ContextEvent.class));
		// change context
		t2.contextChange(isA(ContextEvent.class));

		replay(t1, t2);
		final TransformationContext context = workflow.add(t1);
		context.addInput("dummy1", metadata);
		context.setTransformation(t2);
		context.addInput("dummy2", metadata);
		verify(t1, t2);
	}

	/**
	 * Setting a different transformation for a context effectively removes the
	 * transformation as listener for the context,
	 * removeContextListener(Transformation) does not always.
	 */
	@Test
	public void testSetTransformationRemovesContextListener() {
		t1.addToContext(isA(ContextEvent.class));
		replay(t1);
		final TransformationContext context = workflow.add(t1);
		context.setTransformation(niceTransformation);
		context.addInput("dummy", metadata);
		verify(t1);
	}

	/**
	 * Both transformations between which a connection is being created must be
	 * contained in the same workflow.
	 */
	@Test
	public void testWorkflowMustContainBothTransformationsInConnection() {
		final Workflow w1 = factory.createWorkflow();
		final Workflow w2 = factory.createWorkflow();
		final Transformation tr1 = createMock(Transformation.class);
		final Transformation tr2 = createMock(Transformation.class);
		expectAddToWorkflow(tr1, tr2);
		tr1.contextChange(isA(ContextEvent.class));
		tr2.contextChange(isA(ContextEvent.class));
		replay(tr1, tr2);
		final TransformationContext c1 = w1.add(tr1);
		final TransformationContext c2 = w2.add(tr2);
		c1.addOutput(outputid, metadata);
		c2.addInput(inputid, metadata);
		w1.connect(c1, outputid, c2, inputid);
		verify(tr1, tr2);
	}

	/**
	 * A workflow with only one transformation without ports is executable if
	 * the transformation is.
	 */
	@Test
	public void testWorkflowWithOneTransformationExecutable() {
		t1.addToContext(isA(ContextEvent.class));
		expect(t1.isExecutable()).andReturn(true);
		replay(t1);
		workflow.add(t1);
		assertTrue("Workflow should be executable now", workflow.isExecutable());
		verify(t1);
	}

	/**
	 * A workflow with only one transformation is not executable if the
	 * transformation is not.
	 */
	@Test
	public void testWorkflowWithOneTransformationNotExecutable() {
		t1.addToContext(isA(ContextEvent.class));
		expect(t1.isExecutable()).andReturn(false);
		replay(t1);
		workflow.add(t1);
		assertFalse(workflow.isExecutable());
		verify(t1);
	}

	private void expectContextChange(final int times, final Transformation... transformations) {
		for (final Transformation transformation : transformations) {
			transformation.contextChange(isA(ContextEvent.class));
			expectLastCall().times(times);
		}
	}

	private void expectContextChange(final Transformation... transformations) {
		expectContextChange(1, transformations);
	}

	static void expectAddToWorkflow(final Transformation... transformations) {
		for (final Transformation transformation : transformations) {
			transformation.addToContext(isA(ContextEvent.class));
		}
	}

}
