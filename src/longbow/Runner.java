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
 * A {@link Runner} is used to execute a {@link Workflow}. Deployers should
 * choose which {@link Runner} should be used for which {@link Workflow}.
 * 
 * Must have a constructor with a {@link Workflow} parameter.
 * 
 * @author Philip van Oosten
 * 
 */
public interface Runner {

	/**
	 * Mark a {@link TransformationContext}. The order in which marks and
	 * sweeps are performed in this {@link Runner} can be preserved. Wait if
	 * necessary until this {@link Runner} is completely set up.
	 * 
	 * @param context
	 */
	void mark(TransformationContext context);

	/**
	 * Prepare all {@link TransformationContext}s to run and start accepting
	 * marks and sweeps from them.
	 * 
	 * @return whether the runner started successfully.
	 */
	boolean start();

	/**
	 * Stop accepting marks and sweeps from {@link TransformationContext}s and
	 * let them clean up their potential mess.
	 * 
	 * It is not necessarily possible to reset a {@link Runner} after it is
	 * stopped.
	 */
	void stop();

	/**
	 * Sweep a {@link TransformationContext}. The order in which marks an
	 * sweeps are performed on this {@link Runner} can be preserved. Wait if
	 * necessary until
	 * 
	 * @param context
	 */
	void sweep(TransformationContext context);

}
