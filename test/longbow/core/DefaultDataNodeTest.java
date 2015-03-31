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
import longbow.DataWrapper;
import longbow.LongbowException;
import longbow.LongbowFactory;
import longbow.Metadata;
import longbow.core.DefaultDataNode;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DefaultDataNodeTest {

	private DataNode datanode;

	private Metadata metadata;

	private LongbowFactory factory;

	@Before
	public void setUp() {
		metadata = createMock(Metadata.class);
		factory = createMock(LongbowFactory.class);
		datanode = new DefaultDataNode(metadata, factory);
	}

	@Test
	public void testConnectedInputs() {
		assertEquals(0, datanode.connectedInputs());
	}

	@Test
	public void testEquals() {
		final DataNode node = new DefaultDataNode(metadata, factory);
		assertFalse(node.equals(datanode));
		assertEquals(datanode, datanode);
	}

	@Test
	public void testGetDataWrapper() {
		// assert that a wrapper is created when it doesn't already exist.
		final DataWrapper wrapper = createMock(DataWrapper.class);
		expect(factory.createDataWrapper()).andReturn(wrapper);
		replay(factory, wrapper);
		assertSame(wrapper, datanode.getDataWrapper());
		assertSame(wrapper, datanode.getDataWrapper());
		verify(factory, wrapper);
	}

	@Test
	public void testInputConnected() {
		assertEquals(1, datanode.inputConnected());
		assertEquals(2, datanode.inputConnected());
	}

	@Test
	public void testInputDisconnected() {
		datanode.inputConnected();
		datanode.inputConnected();
		assertEquals(1, datanode.inputDisconnected());
		assertEquals(0, datanode.inputDisconnected());
	}

	@Test(expected = LongbowException.class)
	public void testInputDisconnectedAndException() {
		datanode.inputDisconnected();
	}

	@Test
	public void testIsOutputConnected() {
		assertFalse(datanode.isOutputConnected());
		datanode.setOutputConnected(true);
		assertTrue(datanode.isOutputConnected());
		datanode.setOutputConnected(false);
		assertFalse(datanode.isOutputConnected());
	}

	@Test(expected = LongbowException.class)
	public void testOutputConnectedConnectAndException() {
		datanode.setOutputConnected(true);
		datanode.setOutputConnected(true);
	}

	@Test(expected = LongbowException.class)
	public void testOutputConnectedDisconnectAndException() {
		datanode.setOutputConnected(false);
	}

	@Test
	public void testToString() {
		assertTrue(datanode.toString().contains("DataNode"));
	}

}
