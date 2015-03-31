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


import static longbow.Mode.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import longbow.*;

/**
 * A default implementation for practically all designer actions.
 * 
 * @author Philip van Oosten
 * 
 */
public abstract class AbstractWorkflow implements Workflow {

	/**
	 * The factory used to create new objects for this {@link Workflow}
	 */
	protected final LongbowFactory factory;

	/**
	 * The mode this workflow finds itself in currently.
	 */
	protected Mode mode;

	/**
	 * Listeners that listen to changes of all contexts in this {@link Workflow}
	 */
	protected final WeakHashMap<ContextListener, Object> contextListeners;

	/**
	 * The transformations and their partial ordering
	 */
	protected final PartialOrder<TransformationContext> transformations;

	/**
	 * 
	 * @param factory
	 *            The factory that creates components for this {@link Workflow}.
	 *            {@code null} is allowed to ease development of decorators.
	 */
	protected AbstractWorkflow(final LongbowFactory factory) {
		this.factory = factory;
		contextListeners = new WeakHashMap<ContextListener, Object>();
		transformations = new PartialOrderImpl<TransformationContext>();
	}

	public TransformationContext add(final Transformation toAdd) {
		final TransformationContext context = factory.createTransformationContext(this);
		transformations.add(context);
		context.setTransformation(toAdd);
		return context;
	}

	public boolean add(final TransformationContext context) {
		return transformations.add(context);
	}

	/**
	 * This optional operation is not supported in this implementation
	 * 
	 * @param c
	 * @return nothing, throws {@link UnsupportedOperationException}
	 */
	public boolean addAll(final Collection<? extends TransformationContext> c) {
		throw new UnsupportedOperationException();
	}

	public void addContextListener(final ContextListener listener) {
		if (listener != null) {
			contextListeners.put(listener, null);
		}
	}

	public void addContextListener(final ContextListener listener, final TransformationContext transformation) {
		if (transformations.contains(transformation)) {
			transformation.addContextListener(listener);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void clear() {
		transformations.clear();
	}

	public boolean connect(final TransformationContext fromContext, final String outputid, final TransformationContext toContext, final String inputid) {
		if (fromContext == toContext || !contains(fromContext) || !contains(toContext)) {
			return false;
		}

		final Output fromOutput = fromContext.getOutput(outputid);
		final Input toInput = toContext.getInput(inputid);

		if (fromOutput != null && toInput != null && !toInput.isConnected() && toInput.getMetadata().acceptsMetadata(fromOutput.getMetadata()) && transformations.connect(fromContext, toContext)) {
			linkData(fromOutput, toInput);
			return true;
		}
		return false;
	}

	public boolean connect(final TransformationContext from, final TransformationContext to) {
		if (mode != DESIGN || !contains(from) || !contains(to)) {
			return false;
		}
		return transformations.connect(from, to);
	}

	public boolean contains(final Object object) {
		return transformations.contains(object);
	}

	public boolean containsAll(final Collection<?> collection) {
		return transformations.containsAll(collection);
	}

	public boolean disconnect(final TransformationContext from, final String outputid, final TransformationContext to, final String inputid) {
		// must be in design mode
		// contexts must be part of this
		if (mode != DESIGN || !contains(from) || !contains(to)) {
			return false;
		}

		// check if output and input exist and are connected to the same data node
		final Output output = from.getOutput(outputid);
		final Input input = to.getInput(inputid);
		if (output.isConnected() && output.getConnection() == input.getConnection()) {
			input.setConnection(null);
			final DataNode datanode = output.getConnection();
			if (datanode.connectedInputs() == 0) {
				output.setConnection(null);
			}
			// maybe disconnect transformations in data structure
			final boolean disconnected = transformations.disconnect(from, to);
			assert disconnected;
			// fire events
			if (!output.isConnected()) {
				from.fireContextChanged();
			}
			to.fireContextChanged();
			return disconnected;
		}
		return false;
	}

	public boolean disconnect(final TransformationContext from, final TransformationContext to) {
		if (mode != Mode.DESIGN) {
			return false;
		}
		return transformations.disconnect(from, to);
	}

	public final void fireAddToContext(final ContextEvent event) {
		for (final ContextListener listener : contextListeners.keySet()) {
			if (!event.getSource().hasListener(listener)) {
				listener.addToContext(event);
			}
		}
	}

	public final void fireContextChanged(final ContextEvent event) {
		for (final ContextListener listener : contextListeners.keySet()) {
			if (!event.getSource().hasListener(listener)) {
				listener.contextChange(event);
			}
		}
	}

	public final void fireRemoved(final ContextEvent event) {
		for (final ContextListener listener : contextListeners.keySet()) {
			if (!event.getSource().hasListener(listener)) {
				listener.contextRemoved(event);
			}
		}
	}

	public boolean follows(final TransformationContext first, final TransformationContext last) {
		return transformations.follows(first, last);
	}

	public List<TransformationContext> getFollowers(final TransformationContext key) {
		return transformations.getFollowers(key);
	}

	public Mode getMode() {
		return mode;
	}

	public List<TransformationContext> getPrecursors(final TransformationContext key) {
		return transformations.getPrecursors(key);
	}

	public Collection<TransformationContext> getTransformations() {
		return Collections.unmodifiableCollection(transformations);
	}

	public boolean isEmpty() {
		return transformations.isEmpty();
	}

	public Iterator<TransformationContext> iterator() {
		return transformations.iterator();
	}

	public void linkBack(final TransformationContext from, final String outputid, final TransformationContext to, final String inputid) {
		// TODO: assert that backlinks become data links if forward links disappear   
		// there must already exist a connection from from to to
		if (transformations.follows(to, from)) {
			final Output output = from.getOutput(outputid);
			final Input input = to.getInput(inputid);
			linkData(output, input);
		}
	}

	public boolean remove(final Object o) {
		// transformation contexts are removed with an overloaded method.
		return false;
	}

	public boolean remove(final TransformationContext toRemove) {
		if (mode != Mode.DESIGN) {
			return false;
		}
		final boolean removed = transformations.remove(toRemove);
		if (removed) {
			toRemove.fireRemoved();
		}
		return removed;
	}

	/**
	 * This optional operation is not supported for this implementation.
	 * 
	 * @param c
	 * 
	 * @return nothing, throws {@link UnsupportedOperationException}
	 */
	public boolean removeAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public void removeContextListener(final ContextListener listener) {
		contextListeners.remove(listener);
	}

	public void removeContextListener(final ContextListener listener, final TransformationContext transformation) {
		transformation.removeContextListener(listener);
	}

	/**
	 * This optional operation is not supported for this implementation.
	 * 
	 * @param c
	 * @return nothing, throws {@link UnsupportedOperationException}
	 */
	public boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return transformations.size();
	}

	public Object[] toArray() {
		return transformations.toArray();
	}

	public <T> T[] toArray(final T[] a) {
		return transformations.toArray(a);
	}

	private void linkData(final Output fromOutput, final Input toInput) {
		final boolean outputChanged = !fromOutput.isConnected();

		final DataNode datanode;
		if (!outputChanged) {
			datanode = fromOutput.getConnection();
			assert datanode.isOutputConnected();
		} else {
			datanode = factory.createDataNode(fromOutput.getMetadata());
			fromOutput.setConnection(datanode);
		}
		toInput.setConnection(datanode);

		if (outputChanged) {
			fromOutput.getTransformationContext().fireContextChanged();
		}
		toInput.getTransformationContext().fireContextChanged();
	}
}
