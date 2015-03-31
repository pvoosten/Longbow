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

import java.util.HashMap;
import java.util.Map;

import longbow.LongbowException;
import longbow.TransformationContext;
import longbow.Workflow;

import org.apache.log4j.Logger;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DefaultRunner extends AbstractRunner {

	private static final Logger logger = Logger.getLogger(DefaultRunner.class);

	Map<TransformationContext, MarkAndSweepSupport> markAndSweeps;

	public DefaultRunner(final Workflow workflow) {
		super(workflow);
	}

	public void mark(final TransformationContext tcontext) {
		if (!running) {
			return;
		}
		final MarkAndSweepSupport marksweep = markAndSweeps.get(tcontext);
		if (workflow.getMode() == RUN) {
			marksweep.fireMarking();
			for (final TransformationContext context : workflow.getFollowers(tcontext)) {
				context.mark();
			}

			marksweep.fireMarked();
		}
	}

	public void sweep(final TransformationContext tcontext) {
		if (!running) {
			return;
		}
		// MarkAndSweepSupport.lastEvent is declared volatile for this purpose
		final MarkAndSweepSupport marksweep = markAndSweeps.get(tcontext);
		if (workflow.getMode() == RUN && !marksweep.isValid()) {
			synchronized (marksweep) {
				if (!marksweep.isValidating() && !marksweep.isValid()) {
					marksweep.fireSweeping();
					// prepare a soft landing in case all hell breaks loose
					try {
						for (final TransformationContext context : workflow.getPrecursors(tcontext)) {
							context.sweep();
						}

						tcontext.executeTransformation();

						if (!marksweep.isValid()) {
							marksweep.fireSwept();
						}
					} catch (final LongbowException e) {
						// easy now, we don't want this runner to become inconsistent.
						// Make sure everybody agrees about validity
						//						marksweep.fireSweepInterrupted();
						// TODO: Observer pattern for exception handlers
						// Don't throw anything that might not get caught.
						final String msg = "Error while sweeping";
						logger.error(msg, e);
					}
				}
			}
		}
	}

	@Override
	protected boolean doStart() {
		markAndSweeps = new HashMap<TransformationContext, MarkAndSweepSupport>();
		return true;
	}

	@Override
	protected boolean doStart(final TransformationContext context, final int index) {
		markAndSweeps.put(context, new MarkAndSweepSupport(context));
		return true;
	}

	@Override
	protected void doStop() {
		markAndSweeps.clear();
		markAndSweeps = null;
	}

	@Override
	protected void doStop(final TransformationContext context) {
	}
}
