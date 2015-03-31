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


import longbow.*;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DefaultDataNode implements DataNode {

	/**
	 * The number of {@link Input}s connected to this.
	 */
	private int numInputsConnected;

	/**
	 * If an {@link Output} is connected to this.
	 */
	private boolean outputConnected;

	private final Metadata metadata;

	private DataWrapper dataWrapper;

	private final LongbowFactory factory;

	/**
	 * Creates a new {@link DefaultDataNode}.
	 * 
	 * @param metadata
	 *            The metadata to associate with this {@link DataNode}
	 * @param factory
	 *            The factory used to generate a {@link DataWrapper} for this
	 *            {@link DataNode}
	 */
	public DefaultDataNode(final Metadata metadata, final LongbowFactory factory) {
		this.metadata = metadata;
		outputConnected = false;
		numInputsConnected = 0;
		this.factory = factory;
	}

	/**
	 * {@inheritDoc}
	 */
	public int connectedInputs() {
		return numInputsConnected;
	}

	/**
	 * {@inheritDoc}
	 */
	public DataWrapper getDataWrapper() {
		if (dataWrapper == null) {
			dataWrapper = factory.createDataWrapper();
		}
		return dataWrapper;
	}

	/**
	 * {@inheritDoc}
	 */
	public int inputConnected() {
		return ++numInputsConnected;
	}

	/**
	 * {@inheritDoc}
	 */
	public int inputDisconnected() {
		numInputsConnected--;
		if (numInputsConnected < 0) {
			throw new LongbowException("Can't disconnect an input from a data node if none are connected");
		}
		return numInputsConnected;

	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOutputConnected() {
		return outputConnected;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOutputConnected(final boolean connect) {
		if (outputConnected == connect) {
			throw new LongbowException("Mustn't connect or disconnect an output with the same data node twice or more in a row");
		}
		outputConnected = connect;
	}

	/**
	 * Set the data for this {@link DataNode}, while checking the
	 * {@link Metadata}.
	 * 
	 * @param data
	 *            The data to set
	 * @return if the data has been set
	 */
	boolean setData(final Object data) {
		if (dataWrapper.getData() == data) {
			return true;
		}
		final boolean accept = metadata.acceptsData(data);
		if (accept) {
			dataWrapper.setData(data);
		}
		return accept;
	}

}
