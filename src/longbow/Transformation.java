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
 * This interface defines a number of hooks that will be executed by the
 * {@link TransformationContext} this is a part of. Make sure that either
 * <ol>
 * <li>either this {@link Transformation} is added to only one
 * {@link TransformationContext}, or</li>
 * <li>adding this {@link Transformation} to more than one
 * {@link TransformationContext} in one or more {@link Workflow}s will have
 * absolutely no side effects, or</li>
 * <li>the side effect will with absolute certainty cause the other
 * {@link TransformationContext}s containing this {@link Transformation} to be
 * marked</li>
 * </ol>
 * 
 * @author Philip van Oosten
 * 
 */
public interface Transformation extends ContextListener {

	/**
	 * Make sure the result of the transformation is available for other
	 * transformations. Executed every time after this {@link Transformation} is
	 * performed.
	 */
	void exportData();

	/**
	 * Fetch data required to execute the transformation, every time befor this
	 * {@link Transformation} is performed.
	 */
	void importData();

	/**
	 * A {@link Transformation} can be a wrapper around just about anything, so
	 * its ability to execute can depend on external factors. This method
	 * returns whether the {@link Transformation} can be executed. There is no
	 * need to check if necessary connections are created in the context,
	 * because the context checks that itself.
	 * 
	 * @return whether this is executable.
	 */
	boolean isExecutable();

	/**
	 * The reason of existence of this transformation, where the actual work
	 * happens.
	 */
	void processData();

	/**
	 * Will be executed before execution of the {@link Workflow}. Can be used
	 * to acquire resources needed to perform this {@link Transformation}.
	 * 
	 * @param context
	 *            The context of this transformation
	 */
	void startExecution(TransformationContext context);

	/**
	 * Will be executed after execution of the {@link Workflow}. Can be used to
	 * release resources used by the this {@link Transformation}.
	 */
	void stopExecution();

}
