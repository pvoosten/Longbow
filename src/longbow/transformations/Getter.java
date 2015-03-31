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


import java.lang.ref.WeakReference;

import longbow.ContextEvent;
import longbow.DataWrapper;
import longbow.Input;
import longbow.Metadata;
import longbow.Transformation;
import longbow.TransformationContext;
import longbow.metadata.MedalMetadata;

/**
 * TODO: apply generics in Getter and Setter, to reduce impedance mismatch with
 * Java 1.5
 * 
 * @author Philip van Oosten
 * 
 */
public class Getter implements Transformation {

	public static final String IN_DATA = "data";

	private final Metadata dataMetadata;

	private DataWrapper dataWrapper;

	private Object data;

	/**
	 * The context containing this transformation
	 */
	private WeakReference<TransformationContext> context;

	private boolean persistent;

	public Getter() {
		this(MedalMetadata.parse("accept all"));
	}

	public Getter(final Metadata metadata) {
		dataMetadata = metadata;
	}

	public void addToContext(final ContextEvent event) {
		event.getSource().addInput(IN_DATA, dataMetadata);
	}

	public void contextChange(final ContextEvent event) {
	}

	public void contextRemoved(final ContextEvent event) {
	}

	public void exportData() {
	}

	/**
	 * Get the data from the {@link Input}. Sweeps first.
	 * 
	 * @return The data last fetched by this {@link Getter} (might be invalid)
	 */
	public Object extract() {
		if (context != null && context.get() != null) {
			context.get().sweep();
		}
		return data;
	}

	public void importData() {
		data = dataWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	public void processData() {
		// collecting data already done in importData()
	}

	public void setPersistent(final boolean persistent) {
		this.persistent = persistent;
	}

	public void startExecution(final TransformationContext context) {
		// initialize inputs
		dataWrapper = context.getInputWrapper(IN_DATA);
		this.context = new WeakReference<TransformationContext>(context);
	}

	public void stopExecution() {
		// if persistent: let data be not null; it can be collected in design mode
		if (!persistent) {
			data = null;
		}
		dataWrapper = null;
		context = null;
	}
}
