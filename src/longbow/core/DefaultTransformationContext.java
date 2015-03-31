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


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import longbow.*;
import longbow.util.CompositeIterable;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DefaultTransformationContext implements TransformationContext {

	/**
	 * Listeners for changes of this context
	 */
	private final Map<ContextListener, Object> contextListeners;

	private final Map<String, Input> inputs;

	private final Map<String, Output> outputs;

	private volatile Transformation transformation;

	private final WeakReference<Workflow> workflow;

	private Runner runner;

	private final LongbowFactory factory;

	public DefaultTransformationContext(final Workflow workflow, final LongbowFactory factory) {
		this.factory = factory;
		contextListeners = new WeakHashMap<ContextListener, Object>();
		inputs = new HashMap<String, Input>();
		outputs = new HashMap<String, Output>();
		this.workflow = new WeakReference<Workflow>(workflow);
	}

	public void addContextListener(final ContextListener listener) {
		if (listener != null) {
			contextListeners.put(listener, null);
		}
	}

	public void addInput(final String inputid, final Metadata metadata) {
		if (!designMode()) {
			return;
		}
		if (inputid != null && metadata != null && !inputs.containsKey(inputid)) {
			inputs.put(inputid, factory.createInput(this, metadata));
			fireContextChanged();
		}

	}

	public void addOutput(final String outputid, final Metadata metadata) {
		if (!designMode()) {
			return;
		}
		if (outputid != null && metadata != null && !outputs.containsKey(outputid)) {
			outputs.put(outputid, factory.createOutput(this, metadata));
			fireContextChanged();
		}
	}

	public void executeTransformation() {
		if (!runMode()) {
			return;
		}
		synchronized (transformation) {
			transformation.importData();
			transformation.processData();
			transformation.exportData();
		}
	}

	public void fireContextChanged() {
		final ContextEvent event = new ContextEvent(this);
		transformation.contextChange(event);
		for (final ContextListener listener : contextListeners.keySet()) {
			if (listener != transformation) {
				listener.contextChange(event);
			}
		}
		getWorkflow().fireContextChanged(event);
	}

	public void fireRemoved() {
		final ContextEvent event = new ContextEvent(this);
		transformation.contextRemoved(event);
		for (final ContextListener listener : contextListeners.keySet()) {
			if (listener != transformation) {
				listener.contextRemoved(event);
			}
		}
		getWorkflow().fireRemoved(event);
	}

	public Input getInput(final String inputid) {
		if (!designMode()) {
			return null;
		}
		return inputs.get(inputid);
	}

	public DataWrapper getInputWrapper(final String inputid) {
		if (runMode() || designMode()) {
			return null;
		}
		return inputs.get(inputid).getConnection().getDataWrapper();
	}

	public Output getOutput(final String outputid) {
		if (!designMode()) {
			return null;
		}
		return outputs.get(outputid);
	}

	public DataWrapper getOutputWrapper(final String outputid) {
		if (runMode() || designMode()) {
			return null;
		}
		final Output output = outputs.get(outputid);
		if (!output.isConnected() && !output.isOptional()) {
			throw new IllegalStateException("Context should not be executable now");
		}
		return outputs.get(outputid).getConnection().getDataWrapper();
	}

	public Workflow getWorkflow() {
		return workflow.get();
	}

	public boolean hasInput(final String inputid) {
		return inputs.containsKey(inputid);
	}

	public boolean hasListener(final ContextListener listener) {
		return listener != transformation && contextListeners.containsKey(listener);
	}

	public boolean hasOutput(final String outputid) {
		return outputs.containsKey(outputid);
	}

	@SuppressWarnings("unchecked")
	public boolean isExecutable() {
		// the contained transformation must be executable
		boolean executable = transformation.isExecutable();
		// only optional ports can be unconnected
		if (executable) {
			final Iterable<Port> ports = new CompositeIterable<Port>(inputs.values(), outputs.values());
			for (final Port port : ports) {
				if (!port.isOptional() && !port.isConnected()) {
					executable = false;
					break;
				}
			}
		}
		return executable;
	}

	public void mark() {
		if (!runMode()) {
			return;
		}
		assert runner != null;
		runner.mark(this);
	}

	public void removeContextListener(final ContextListener listener) {
		contextListeners.remove(listener);
	}

	public void removeInput(final String inputid) {
		if (!designMode()) {
			return;
		}
		inputs.remove(inputid);
	}

	public void removeOutput(final String outputid) {
		if (!designMode()) {
			return;
		}
		outputs.remove(outputid);
	}

	public void setRunner(final Runner runner) {
		if (runMode() || designMode()) {
			return;
		}
		this.runner = runner;
	}

	public void setTransformation(final Transformation transformation) {
		if (!designMode()) {
			return;
		}
		inputs.clear();
		outputs.clear();
		this.transformation = transformation;
		fireAddToContext();
	}

	public void startExecution() {
		if (runMode() || designMode()) {
			return;
		}
		transformation.startExecution(this);
	}

	public void stopExecution() {
		if (runMode() || designMode()) {
			return;
		}
		transformation.stopExecution();
	}

	public void sweep() {
		if (!runMode()) {
			return;
		}
		runner.sweep(this);
	}

	@Override
	public String toString() {
		return "Context of a " + transformation.getClass().getCanonicalName();
	}

	private boolean designMode() {
		final Workflow wf = workflow.get();
		return wf != null && wf.getMode() == Mode.DESIGN;
	}

	private void fireAddToContext() {
		final ContextEvent event = new ContextEvent(this);
		transformation.addToContext(event);
		for (final ContextListener listener : contextListeners.keySet()) {
			if (listener != transformation) {
				listener.addToContext(event);
			}
		}
		getWorkflow().fireAddToContext(event);
	}

	private boolean runMode() {
		final Workflow wf = workflow.get();
		return wf != null && wf.getMode() == Mode.RUN;
	}

}
