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

package longbow.transformations;


import static longbow.metadata.MedalMetadata.*;

import java.lang.ref.WeakReference;

import longbow.*;

/**
 * @see #isExecutable()
 * @see #inject(Object)
 * 
 * @author Philip van Oosten
 * 
 */
public class Setter implements Transformation {

	public static final String OUT_DATA = "data";

	private final Metadata dataMetadata;

	private DataWrapper dataWrapper;

	private Object data;

	private boolean persistent = false;

	private WeakReference<TransformationContext> context;

	public Setter() {
		this(acceptAll());
	}

	/**
	 * Creates a new {@link Setter}.
	 * 
	 * @param outputMetadata
	 *            the metadata that will be applied to the output of this
	 *            {@link Setter}
	 */
	public Setter(final Metadata outputMetadata) {
		dataMetadata = outputMetadata;
	}

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addOutput(OUT_DATA, dataMetadata);
		this.context = new WeakReference<TransformationContext>(context);
	}

	public void contextChange(final ContextEvent event) {
	}

	public void contextRemoved(final ContextEvent event) {
	}

	public void exportData() {
		dataWrapper.setData(data);
	}

	public void importData() {
	}

	/**
	 * Inject a pojo in a workflow. Don't forget to mark the context of this if
	 * necessary.
	 * 
	 * @param data
	 *            The pojo to inject in a {@link Workflow}.
	 * @return whether the data is accepted by the {@link Metadata} of the
	 *         {@link Output} of this.
	 */
	public boolean inject(final Object data) {
		final boolean accepted = dataMetadata.acceptsData(data);
		if (accepted && this.data != data) {
			this.data = data;
			// mark can only take effect in run mode
			if (context != null && context.get() != null) {
				context.get().mark();
			}
		}
		return accepted;
	}

	/**
	 * In order to be executable, appropriate data must be already injected
	 * before execution.
	 * 
	 * {@inheritDoc}
	 */
	public boolean isExecutable() {
		// metadata decides if data can be null
		return dataMetadata.acceptsData(data);
	}

	public void processData() {
		// data will be set in exportData()
	}

	public void setPersistent(final boolean persistent) {
		this.persistent = persistent;
	}

	public void startExecution(final TransformationContext context) {
		assert this.context.get() == context;
		// initialize outputs
		dataWrapper = this.context.get().getOutputWrapper(OUT_DATA);
	}

	public void stopExecution() {
		context = null;
		if (!persistent) {
			// don't keep data for next execution.
			data = null;
		}
		dataWrapper = null;
	}
}
