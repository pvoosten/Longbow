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
 * A {@link Port} is a means of communication between
 * {@link TransformationContext}s in the same workflow. Subinterfaces decide
 * how this communication takes place. It is impossible to enforce that
 * {@link Port}s are the only possible way for transformations to exchange
 * data. If, however, it becomes obscure for designer how and when data is
 * transferred from one {@link Transformation} to another, it is a good idea to
 * provide ports in the {@link Transformation}'s context. Being verbose in such
 * a manner enables designers to understand what happens in a {@link Workflow}
 * in the blink of an eye.
 * 
 * A {@link Port} is a part of one and only one {@link TransformationContext}.
 * 
 * This interface is provided as a service to {@link TransformationContext}s,
 * but {@link Port} are in itself unaware of the {@link Mode} its parents
 * {@link Workflow} finds itself in. Like {@link DataNode}s, {@link Port} are
 * generally dumb objects that can be used by {@link TransformationContext}s.
 * It is therefore important that {@link Port} are only accessible in DESIGN
 * mode.
 * 
 * @see Input
 * @see Output
 * 
 * @author Philip van Oosten
 * 
 */
public interface Port {

	/**
	 * 
	 * @return the {@link DataNode} connected to this port, or {@code null} if
	 *         this {@link Port} is not connected.
	 */
	DataNode getConnection();

	/**
	 * @return the {@link Metadata} associated with this {@link Port}.
	 */
	Metadata getMetadata();

	/**
	 * @return the {@link TransformationContext} this {@link Port} is attached
	 *         to.
	 */
	TransformationContext getTransformationContext();

	/**
	 * @return whether this {@link Port} is connected.
	 */
	boolean isConnected();

	/**
	 * An optional {@link Port} is one that does not make its
	 * {@link TransformationContext} unexecutable if it is not connected. Hence,
	 * in order to be executable, all non-optional {@link Port}s of a
	 * {@link TransformationContext} must be connected.
	 * 
	 * To ensure that a {@link Port} is not connected, it should be removed from
	 * its {@link TransformationContext}.
	 * 
	 * By default, a {@link Port} is not optional, to ease implementation of
	 * {@link Transformation}s. If a {@link Transformation} creates an optional
	 * port in its {@link TransformationContext context}, the
	 * {@link Transformation} must take into account whether the {@link Port} is
	 * connected or not. Else, the {@link Port} will either be connected, or the
	 * {@link Workflow} will not be able to execute. Hence, it can then be
	 * assumed by the programmer of the {@link Transformation} that the
	 * {@link Port} in question is connected.
	 * 
	 * @return whether this {@link Port} needs to be connected in order for its
	 *         {@link TransformationContext} to be executable.
	 * @see TransformationContext#isExecutable()
	 */
	boolean isOptional();

	/**
	 * Connects or disconnects this {@link Port} with a {@link DataNode}
	 * 
	 * @param datanode
	 *            The {@link DataNode} to connect to, or null to disconnect.
	 */
	void setConnection(DataNode datanode);

	/**
	 * Set the port optional or compulsory.
	 * 
	 * @param optional
	 *            {@code true}Êif the {@link Port} must be optional,
	 *            {@code false} otherwise.
	 */
	void setOptional(boolean optional);

}
