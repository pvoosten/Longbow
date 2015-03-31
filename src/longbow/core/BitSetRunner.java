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


import gnu.trove.TObjectIntHashMap;

import java.util.BitSet;

import longbow.TransformationContext;
import longbow.Workflow;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class BitSetRunner extends AbstractRunner {

	private BitSet[] followers;

	private BitSet[] precursors;

	private BitSet invalid;

	private TObjectIntHashMap<TransformationContext> indices;

	private TransformationContext[] contexts;

	public BitSetRunner(final Workflow workflow) {
		super(workflow);
	}

	public void mark(final TransformationContext context) {
		if (!running) {
			return;
		}
		final int index = indices.get(context);
		synchronized (invalid) {
			invalid.and(followers[index]);
		}
	}

	public void sweep(final TransformationContext context) {
		if (!running) {
			return;
		}
		final int index = indices.get(context);
		synchronized (invalid) {
			final BitSet set = (BitSet) invalid.clone();
			set.and(followers[index]);
			for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
				contexts[i].executeTransformation();
			}
			invalid.andNot(followers[index]);
		}

	}

	@Override
	protected boolean doStart() {
		final int size = workflow.size();
		followers = new BitSet[size];
		precursors = new BitSet[size];
		invalid = new BitSet(size);
		invalid.set(0, size);
		indices = new TObjectIntHashMap<TransformationContext>(size);
		contexts = new TransformationContext[size];
		return true;
	}

	@Override
	protected boolean doStart(final TransformationContext context, final int index) {
		contexts[index] = context;
		indices.put(context, index);
		final int size = workflow.size();
		precursors[index] = new BitSet(size);
		followers[index] = new BitSet(size);

		// this method is executed in partial order,
		// so the index of precursors is already known.

		// Also, each context is follower of its precursors
		// and there are no other followers than those
		// that have precursors.

		for (final TransformationContext precursor : workflow.getPrecursors(context)) {
			final int preIndex = indices.get(precursor);
			precursors[index].set(preIndex);
			followers[preIndex].set(index);
		}

		// Transitive closures
		if (index == size - 1) {
			for (int i = 0; i < size; i++) {
				final BitSet p = precursors[i];
				for (int j = p.nextSetBit(0); j >= 0; j = p.nextSetBit(j + 1)) {
					// First create transitive closure for precursors of j
					// There is nothing to do for that (go figure)!
					p.and(precursors[j]);
				}
			}
			for (int i = size - 1; i >= 0; i--) {
				final BitSet f = followers[i];
				for (int j = f.nextSetBit(0); j >= 0; j = f.nextSetBit(j + 1)) {
					f.and(followers[j]);
				}
			}
		}

		return true;
	}

	@Override
	protected void doStop() {
		followers = null;
		precursors = null;
		invalid = null;
		indices = null;
		contexts = null;
	}

	@Override
	protected void doStop(final TransformationContext context) {
	}

}
