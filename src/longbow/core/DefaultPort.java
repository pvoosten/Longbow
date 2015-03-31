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


import longbow.Metadata;
import longbow.Port;
import longbow.TransformationContext;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public abstract class DefaultPort implements Port {

	private final Metadata metadata;

	private final TransformationContext context;

	private boolean optional;

	/**
	 * Creates a new {@link DefaultPort}
	 * 
	 * @param context
	 *            the {@link TransformationContext context} to create the port
	 *            for.
	 * @param metadata
	 *            the {@link Metadata} of the port.
	 */
	DefaultPort(final TransformationContext context, final Metadata metadata) {
		this.metadata = metadata;
		this.context = context;
		optional = false; // for clarity
	}

	/**
	 * {@inheritDoc}
	 */
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	public TransformationContext getTransformationContext() {
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	public abstract boolean isConnected();

	/**
	 * {@inheritDoc}
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setOptional(final boolean optional) {
		this.optional = optional;
	}

}
