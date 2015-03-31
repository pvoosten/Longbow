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
import longbow.DataNode;
import longbow.Input;
import longbow.Metadata;
import longbow.TransformationContext;
import longbow.core.DefaultInput;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DefaultInputTest {

	private Input input;

	private TransformationContext context;

	private Metadata metadata;

	@Before
	public void setUp() {
		context = createMock(TransformationContext.class);
		metadata = createMock(Metadata.class);
		replay(context, metadata);
		input = new DefaultInput(context, metadata);
	}

	@After
	public void tearDown() {
		verify(context, metadata);
	}

	@Test
	public void testEquals() {
		assertEquals(input, input);
		assertFalse(input.equals(new DefaultInput(context, metadata)));
	}

	@Test
	public void testGetConnection() {
		assertNull(input.getConnection());
		final DataNode node = createMock(DataNode.class);
		input.setConnection(node);
		assertSame(node, input.getConnection());
	}

	@Test
	public void testGetMetadata() {
		assertSame(metadata, input.getMetadata());
	}

	@Test
	public void testGetTransformationContext() {
		assertSame(context, input.getTransformationContext());
	}

	@Test
	public void testIsConnected() {
		assertFalse(input.isConnected());
		input.setConnection(createMock(DataNode.class));
		assertTrue(input.isConnected());
	}

	@Test
	public void testIsOptional() {
		assertFalse(input.isOptional());
		input.setOptional(true);
		assertTrue(input.isOptional());
		input.setOptional(false);
		assertFalse(input.isOptional());
	}

}
