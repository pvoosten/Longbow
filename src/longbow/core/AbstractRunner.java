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


import java.util.LinkedList;
import java.util.List;

import longbow.Runner;
import longbow.TransformationContext;
import longbow.Workflow;

/**
 * 
 * @author Philip van Oosten
 * 
 */
abstract public class AbstractRunner implements Runner {

	protected final Workflow workflow;

	protected volatile boolean running;

	public AbstractRunner(final Workflow workflow) {
		this.workflow = workflow;
	}

	public final boolean start() {
		if (running) {
			throw new IllegalStateException("Runner already started.");
		}
		running = doStart();
		if (!running) {
			return false;
		}
		final List<TransformationContext> startedContexts = new LinkedList<TransformationContext>();
		int i = 0;
		for (final TransformationContext context : workflow) {
			context.setRunner(this);
			context.startExecution();
			startedContexts.add(context);
			if (!doStart(context, i++)) {
				running = false;
				break;
			}
		}
		if (!running) {
			for (final TransformationContext context : startedContexts) {
				context.stopExecution();
				context.setRunner(null);
			}
		}
		return running;
	}

	public final void stop() {
		if (!running) {
			throw new IllegalStateException("Runner has already stopped or is not running");
		}
		running = false;
		doStop();
		for (final TransformationContext context : workflow) {
			context.stopExecution();
			context.setRunner(null);
			doStop(context);
		}
	}

	abstract protected boolean doStart();

	abstract protected boolean doStart(TransformationContext context, int index);

	abstract protected void doStop();

	abstract protected void doStop(TransformationContext context);

}
