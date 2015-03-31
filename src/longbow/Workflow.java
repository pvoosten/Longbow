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


import java.util.Collection;

/**
 * A workflow is what designers work with. It contains transformations that can
 * be interconnected, so that they form a directed acyclic graph. This means
 * that transformations can point to other transformations, but no
 * transformation can point to itself, directly or indirectly.
 * 
 * Everything that matters from the outside, like adding, removing, connecting
 * and disconnecting transformations, can be monitored. Methods that can change
 * something in the workflow normally don't return whether it worked or not.
 * That is to encourage embedding workflows in a Model-View-Controller pattern,
 * wherein the workflow is the model. Another workflow might be the controller.
 * 
 * @author Philip van Oosten
 * 
 */
public interface Workflow extends PartialOrder<TransformationContext> {

	/**
	 * Attempts to add a {@link Transformation} and wrap it in a
	 * {@link TransformationContext}.
	 * 
	 * Adding transformations is only possible in {@link longbow.Mode#DESIGN}
	 * mode.
	 * 
	 * @param toAdd
	 *            The transformation to add to the workflow
	 * @return The context that wraps the added transformation, or null if the
	 *         transformation could not be added to the workflow.
	 */
	TransformationContext add(Transformation toAdd);

	/**
	 * Adds a {@link ContextListener} to monitor the changes of all
	 * {@link TransformationContext}s in this {@link Workflow}.
	 * 
	 * @param listener
	 */
	void addContextListener(ContextListener listener);

	/**
	 * Adds a {@link ContextListener} to monitor a specific
	 * {@link TransformationContext} in this {@link Workflow}.
	 * 
	 * @param listener
	 * @param transformation
	 *            the {@link TransformationContext} to monitor
	 */
	void addContextListener(ContextListener listener, TransformationContext transformation);

	/**
	 * Attempts to connect two transformations.
	 * 
	 * To observe changes in the workflow, a {@link ContextListener} can be
	 * registered to either or both of the {@link TransformationContext}s.
	 * 
	 * Connecting transformations is only possible in
	 * {@link longbow.Mode#DESIGN} mode.
	 * 
	 * @param from
	 *            the {@link TransformationContext} to connect from
	 * @param outputid
	 *            the identification of the {@link Output} of {@code from},
	 *            where the new connection will depart from
	 * @param to
	 *            the {@link TransformationContext} to connect to
	 * @param inputid
	 *            the identification of the {@link Input} of {@code to} where
	 *            the connection will end
	 * @return whether a connection has been established
	 */
	boolean connect(TransformationContext from, String outputid, TransformationContext to, String inputid);

	/**
	 * Disconnects a connection from an {@link Output} of a
	 * {@link TransformationContext} to an {@link Input} of another
	 * {@link TransformationContext}.
	 * 
	 * @param from
	 *            The context to disconnect an {@link Output} of
	 * @param outputid
	 *            The id of the {@link Output} to disconnect
	 * @param to
	 *            The context to disconnect an {@link Input} of.
	 * @param inputid
	 *            The id of the {@link Input} to disconnect
	 * @return true if a connection was broken
	 */
	boolean disconnect(TransformationContext from, String outputid, TransformationContext to, String inputid);

	/**
	 * Attempts to execute this {@link Workflow}. If successful, the execution
	 * mode will be changed to {@link longbow.Mode#RUN}. The {@link Workflow}
	 * will continue running until it is explicitly stopped.
	 * 
	 * Starting to execute the {@link Workflow} is only possible in
	 * {@link longbow.Mode#DESIGN} mode.
	 * 
	 * @return whether the {@link Workflow} has started executing.
	 */
	boolean execute();

	/**
	 * Notify the context listeners that listen to changes of all
	 * {@link TransformationContext}s in this {@link Workflow} that a
	 * {@link Transformation} got a {@link TransformationContext}.
	 * 
	 * @param event
	 */
	void fireAddToContext(ContextEvent event);

	/**
	 * Notify the context listeners that listen to changes of all
	 * {@link TransformationContext}s in this {@link Workflow} that a
	 * {@link TransformationContext} has changed.
	 * 
	 * @param event
	 */
	void fireContextChanged(ContextEvent event);

	/**
	 * Notify the context listeners that listen to changes of all
	 * {@link TransformationContext}s in this {@link Workflow} that a
	 * {@link TransformationContext} has been removed.
	 * 
	 * @param event
	 */
	void fireRemoved(ContextEvent event);

	/**
	 * @return The current {@link longbow.Mode} of the {@link Workflow}. Note
	 *         that the {@link longbow.Mode} can change immediately after this
	 *         method returns a value, so you should not rely on its result for
	 *         further actions.
	 */
	Mode getMode();

	/**
	 * @return an unmodifiable collection that contains all the
	 *         {@link TransformationContext}s in this workflow.
	 */
	Collection<TransformationContext> getTransformations();

	/**
	 * @return Whether this {@link Workflow} can be executed. Returns
	 *         {@code false} when in {@link longbow.Mode#RUN} mode.
	 */
	boolean isExecutable();

	void linkBack(TransformationContext from, String outputid, TransformationContext to, String inputid);

	/**
	 * Removes a {@link TransformationContext} from this {@link Workflow}.
	 * 
	 * This action can only be executed in {@link Mode#DESIGN} mode.
	 * 
	 * @param toRemove
	 * @return whether the context was removed successfully
	 */
	boolean remove(TransformationContext toRemove);

	/**
	 * Unregisters the {@link ContextListener} from every
	 * {@link TransformationContext} in this {@link Workflow} it listens to. If,
	 * however the {@link ContextListener} is used internally, the internal uses
	 * will be preserved.
	 * 
	 * The listener must have been previously added by the
	 * {@link #addContextListener(ContextListener) corresponding method}
	 * 
	 * @param listener
	 *            the {@link ContextListener} to remove, added by
	 *            {@link #addContextListener(ContextListener)}
	 */
	void removeContextListener(ContextListener listener);

	/**
	 * Unregisters the {@link longbow.ContextListener} from the selected
	 * {@link TransformationContext} in this {@link Workflow}. If, however the
	 * {@link ContextListener} is used internally, the internal uses will be
	 * preserved.
	 * 
	 * @param listener
	 *            the {@link longbow.ContextListener} to remove
	 * @param transformation
	 *            the {@link TransformationContext} from which to remove the
	 *            {@link ContextListener}
	 */
	void removeContextListener(ContextListener listener, TransformationContext transformation);

	/**
	 * Forces this {@link Workflow} to stop its execution, if it is running.
	 * 
	 * Terminating the {@link Workflow} is only possible in
	 * {@link longbow.Mode#RUN} mode.
	 */
	void terminate();
}
