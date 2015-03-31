/*
 * Copyright 2007-2008 Philip van Oosten (Mentoring Systems BVBA)
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
import longbow.LongbowFactory;
import longbow.Mode;
import longbow.Runner;
import longbow.TransformationContext;
import longbow.Workflow;

/**
 * A basic implementation of the {@link Workflow} interface. This implementation
 * is not thread safe.
 * 
 * @author Philip van Oosten
 * 
 */
public class DefaultWorkflow extends AbstractWorkflow {

	/**
	 * Reference to the {@link Runner} of this {@link Workflow}, when in
	 * {@link Mode#RUN} mode.
	 */
	private Runner runner;

	public DefaultWorkflow() {
		this(new DefaultLongbowFactory());
	}

	public DefaultWorkflow(final LongbowFactory factory) {
		super(factory);
		mode = DESIGN;
	}

	public boolean execute() {
		// mode must be DESIGN
		if (mode != DESIGN || !isExecutable()) {
			return false;
		}
		mode = START_RUN;
		runner = factory.createRunner(this);
		if (runner.start()) {
			mode = RUN;
			return true;
		}
		return false;
	}

	public boolean isExecutable() {
		boolean executable = !transformations.isEmpty();
		if (executable) {
			for (final TransformationContext context : transformations) {
				if (!context.isExecutable()) {
					executable = false;
					break;
				}
			}
		}
		return executable;
	}

	public void terminate() {
		mode = Mode.END_RUN;
		runner.stop();
		mode = DESIGN;
	}

}
