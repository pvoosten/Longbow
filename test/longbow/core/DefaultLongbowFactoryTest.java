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

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DefaultLongbowFactoryTest {

	private LongbowFactory factory;

	@Before
	public void setUp() {
		factory = new DefaultLongbowFactory();
	}

	@Test
	public void testCreateDataNode() {
		final Metadata metadata = createMock(Metadata.class);
		replay(metadata);
		final DataNode node = factory.createDataNode(metadata);
		assertTrue(node instanceof DefaultDataNode);
	}

	@Test
	public void testCreateDataWrapper() {
		final DataWrapper wrapper = factory.createDataWrapper();
		assertTrue(wrapper instanceof DefaultDataWrapper);
		assertNull(wrapper.getData());
	}

	@Test
	public void testCreateInput() {
		final TransformationContext context = createMock(TransformationContext.class);
		final Metadata metadata = createMock(Metadata.class);
		replay(context, metadata);
		final Input input = factory.createInput(context, metadata);
		assertTrue(input instanceof DefaultInput);
		assertNull(input.getConnection());
		assertSame(metadata, input.getMetadata());
		assertSame(context, input.getTransformationContext());
		assertFalse(input.isConnected());
		assertFalse(input.isOptional());
		verify(context, metadata);
	}

	@Test
	public void testCreateOutput() {
		final TransformationContext context = createMock(TransformationContext.class);
		final Metadata metadata = createMock(Metadata.class);
		replay(context, metadata);
		final Output output = factory.createOutput(context, metadata);
		assertTrue(output instanceof DefaultOutput);
		assertNull(output.getConnection());
		assertSame(metadata, output.getMetadata());
		assertSame(context, output.getTransformationContext());
		assertFalse(output.isConnected());
		assertFalse(output.isOptional());
		verify(context, metadata);
	}

	@Test
	public void testCreateRunner() {
		final Workflow workflow = createMock(Workflow.class);
		replay(workflow);
		final Runner runner = factory.createRunner(workflow);
		assertTrue(runner instanceof DefaultRunner);
		verify(workflow);
	}

	@Test
	public void testCreateTransformationContext() {
		//		factory.createTransformationContext(workflow)
	}

	@Test
	public void testCreateWorkflow() {
		final Workflow workflow = factory.createWorkflow();
		assertEquals(0, workflow.size());
		assertTrue(workflow.isEmpty());
		assertTrue(workflow instanceof DefaultWorkflow);
	}

	@Test
	public void testEquals() {
	}

	@Test
	public void testToString() {
	}

}
