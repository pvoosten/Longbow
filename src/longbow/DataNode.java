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

package longbow;


/**
 * A {@link DataNode} wraps a {@link DataWrapper} and controls what data can be
 * put in the {@link DataWrapper} at design time. At runtime, methods of
 * {@link DataNode}s must not be called. {@link TransformationContext}s and
 * their {@link Port}s are responsible for calling {@link DataNode}'s methods,
 * and must also control access to {@link DataNode}s.
 * 
 * No other objects than {@link Port}s ({@link Input}s or {@link Output}s)
 * can have a reference to a {@link DataNode} instance. No object can have more
 * than one reference to a {@link DataNode}. When all connections to a
 * {@link DataNode} are disconnected, the {@link DataNode} becomes unreachable,
 * and will be garbage collected at some time in the future.
 * 
 * A {@link DataNode} itself is unaware of design time or runtime.
 * 
 * <h3>{@link #equals(Object)}</h3>
 * For any {@link DataNode} {@code datanode1},
 * {@link #equals(Object) datanode1.equals(datanode2)} should return
 * {@code true} if and only if {@code datanode1==datanode2}
 * 
 * <h3>{@link #toString()}</h3>
 * The {@link #toString()} method should make clear that this {@link Object} is
 * a {@link DataNode}.
 * 
 * <h3>Constructor</h3>
 * There should be a constructor with ( {@link Metadata},
 * {@link LongbowFactory} ) arguments. The {@link Metadata} argument controls
 * what data is allowed in the {@link DataWrapper} of this {@link DataNode}.
 * The {@link LongbowFactory} is responsible for creating the
 * {@link DataWrapper}.
 * 
 * @author Philip van Oosten
 * 
 */
public interface DataNode {

	/**
	 * 
	 * @return the number of {@link Input}s that are connected to this
	 *         {@link DataNode}
	 */
	int connectedInputs();

	/**
	 * {@link Transformation}s must be able to get a reference to the
	 * {@link DataWrapper}s in their neighbourhood to import or export data
	 * objects. They can achieve such a reference using this method.
	 * 
	 * A {@link DataWrapper} is created when it doesn't already exist. Lazy
	 * initialization of data wrappers optimizes design time, especially when
	 * {@link DataNode}s are created that are removed afterwards, without
	 * executing the {@link Workflow}.
	 * 
	 * Transformations need their {@link TransformationContext} to call this
	 * method (using {@link TransformationContext#getInputWrapper(String)} or
	 * {@link TransformationContext#getOutputWrapper(String)}), because they
	 * should not own a reference to a {@link DataNode} itself.
	 * 
	 * @return the {@link DataWrapper}
	 */
	DataWrapper getDataWrapper();

	/**
	 * Every time an {@link Input} connects to this {@link DataNode}, this
	 * {@link DataNode} must be notified of this event by calling this method.
	 * 
	 * This method increments the number of {@link Input}s connected to this
	 * {@link DataNode}
	 * 
	 * @return the number of {@link Input}s of {@link TransformationContext}s
	 *         that are connected to this {@link DataNode} after adding the
	 *         connection.
	 */
	int inputConnected();

	/**
	 * Every time an {@link Input} disconnects from this {@link DataNode}, this
	 * {@link DataNode} must be notified thereof by calling this method.
	 * 
	 * Decrements the number of {@link Input}s connected to this
	 * {@link DataNode}.
	 * 
	 * @return the number of {@link Input}s of {@link TransformationContext}s
	 *         that are still connected to this {@link DataNode} after removing
	 *         the connection.
	 */
	int inputDisconnected();

	/**
	 * 
	 * @return {@code true} if an output of a {@link TransformationContext} is
	 *         connected to this {@link DataNode}. {@code false} otherwise.
	 */
	boolean isOutputConnected();

	/**
	 * Let this {@link DataNode} know when an {@link Output} is connected to or
	 * disconnected from it.
	 * 
	 * @param connect
	 *            {@code true} when connecting with an {@link Output} of a
	 *            {@link TransformationContext}, {@code false} when
	 *            disconnecting from an {@link Output} of a
	 *            {@link TransformationContext}.
	 */
	void setOutputConnected(boolean connect);

}
