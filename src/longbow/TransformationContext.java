/*
 * Copyright 2007 Philip van Oosten (Mentoring Systems BVBA)
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

package longbow;


/**
 * A {@link TransformationContext} is a transformation as it is viewed from the
 * side of the {@link Workflow}. The {@link Workflow} requires a uniform view
 * on transformations and therefore it needs {@link TransformationContext}s.
 * 
 * @author Philip van Oosten
 * 
 */
public interface TransformationContext {

	/**
	 * Register a {@link ContextListener} that will be informed about changes of
	 * this {@link TransformationContext}.
	 * 
	 * @param listener
	 *            the {@link ContextListener} to register
	 */
	void addContextListener(ContextListener listener);

	/**
	 * Attempt to add an {@link Input} to this {@link TransformationContext}.
	 * 
	 * @param inputid
	 *            the identification of the {@link Input} that will be added.
	 *            Two or more {@link Input}s with the same identification can't
	 *            coexist in the same {@link TransformationContext}.
	 * @param metadata
	 *            the {@link Metadata} of the {@link Input} that will be added
	 */
	void addInput(String inputid, Metadata metadata);

	/**
	 * Attempt to add an {@link Output} to this {@link TransformationContext}.
	 * 
	 * @param outputid
	 *            the identification of the {@link Output} that will be added;
	 *            two or more{@link Output}s with the same name can not
	 *            coexist in the same {@link TransformationContext}.
	 * @param metadata
	 */
	void addOutput(String outputid, Metadata metadata);

	/**
	 * Execute the transformation
	 */
	void executeTransformation();

	/**
	 * Notify listeners that this context has changed.
	 */
	void fireContextChanged();

	/**
	 * Notify listeners of removal of this {@link TransformationContext} from a
	 * {@link Workflow}
	 */
	void fireRemoved();

	/**
	 * 
	 * @param inputid
	 *            The id of the {@link Input} to get.
	 * @return the requested {@link Input}, or {@code null} if there is no such
	 *         input
	 */
	Input getInput(String inputid);

	/**
	 * 
	 * @param inputid
	 *            The identification of the input
	 * @return The {@link DataWrapper} contained in the {@link DataNode}
	 *         connected to the input
	 */
	DataWrapper getInputWrapper(String inputid);

	/**
	 * 
	 * @param outputid
	 *            The id of the {@link Output} to get.
	 * @return the requested {@link Output}, or {@code null} if there is no
	 *         such output
	 */
	Output getOutput(String outputid);

	DataWrapper getOutputWrapper(String outputid);

	/**
	 * 
	 * @return The {@link Workflow} that contains this
	 *         {@link TransformationContext}, or {@code null} if this context
	 *         is a poor orphan.
	 */
	Workflow getWorkflow();

	/**
	 * @param inputid
	 *            The name of an input
	 * @return whether an input with name {@code inputid} exists.
	 */
	boolean hasInput(String inputid);

	/**
	 * @param listener
	 * @return if the listeners observes this {@link TransformationContext}.
	 */
	boolean hasListener(ContextListener listener);

	/**
	 * 
	 * @param outputid
	 *            The name of an output
	 * @return whether an output with name {@code outputid} exists.
	 */
	boolean hasOutput(String outputid);

	/**
	 * @return whether this transformation is executable. Will return
	 *         {@code false} while the containing workflow is in
	 *         {@link Mode#RUN} mode.
	 */
	boolean isExecutable();

	/**
	 * Allows external components to mark a transformation.
	 */
	void mark();

	/**
	 * Removes a {@link ContextListener} from this, if the listener is not
	 * required for this to work properly in the containing {@link Workflow}.
	 * 
	 * @param listener
	 *            the {@link ContextListener} to remove
	 */
	void removeContextListener(ContextListener listener);

	/**
	 * Removes the {@link Input} identified by {@code inputid}. As a result,
	 * the {@link Input} will be disconnected (if it was connected).
	 * 
	 * This method can only be used in {@link longbow.Mode#DESIGN} mode.
	 * 
	 * @param inputid
	 *            the identification of the {@link Input} to remove.
	 */
	void removeInput(String inputid);

	/**
	 * Removes the {@link Output} identified by {@code outputid}. As a result,
	 * the {@link Output} may be disconnected.
	 * 
	 * This method can only be used in {@link longbow.Mode#DESIGN} mode.
	 * 
	 * @param outputid
	 *            the identification of the {@link Output} to remove.
	 */
	void removeOutput(String outputid);

	/**
	 * Set the runner that will be used to transform marks and sweeps into
	 * execution.
	 * 
	 * @param runner
	 */
	void setRunner(Runner runner);

	/**
	 * Associates a {@link Transformation} with this
	 * {@link TransformationContext}.
	 * 
	 * <ul>
	 * <li>If the transformation is the same as the already associated one,
	 * nothing happens.</li>
	 * <li>If null is passed, this {@link TransformationContext} will be
	 * removed from the {@link Workflow} it is contained in, otherwise,
	 * synchronously proceed with the following steps.</li>
	 * <li>If current {@link Transformation} is not {@code null}, deregister
	 * it as {@link ContextListener} and register the new one</li>
	 * <li>Set the newly added transformation as associated transformation
	 * internally, but store old ports in temporary variables</li>
	 * <li>Wait for the callback initializing the {@link Transformation} in the
	 * {@link TransformationContext} to finish</li>
	 * <li>Transfer all previous connections of the
	 * {@link TransformationContext} to {@link Port}s of the same kind and with
	 * the same identification as {@link Port}s that are added to the context,
	 * if the corresponding {@link Metadata} allows it</li>
	 * </ul>
	 * 
	 * @param transformation
	 *            the {@link Transformation} to associate with this
	 *            {@link TransformationContext}
	 */
	void setTransformation(Transformation transformation);

	/**
	 * Prepare this context for execution.
	 */
	void startExecution();

	/**
	 * Release resources held directly or indirectly by this context.
	 */
	void stopExecution();

	/**
	 * Allows external components to sweep a transformation
	 */
	void sweep();

}
