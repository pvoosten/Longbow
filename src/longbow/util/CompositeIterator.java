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

package longbow.util;


import java.util.Iterator;

/**
 * Glue for iterators. Note that this implementation is not fail-fast, so it
 * doesn't fit 100 percent in the Collections framework.
 * 
 * @param <T>
 *            The type of object that is iterated over
 * 
 * @author Philip van Oosten
 * 
 */
public class CompositeIterator<T> implements Iterator<T> {

	private final Iterator<? extends T>[] iterators;

	private int index;

	public CompositeIterator(final Iterator<? extends T>... iterators) {
		this.iterators = iterators;
	}

	public boolean hasNext() {
		ensureIndex();
		return index < iterators.length;
	}

	public T next() {
		ensureIndex();
		return iterators[index].next();
	}

	/**
	 * Supported if supported by the currently underlying iterator.
	 */
	public void remove() {
		ensureIndex();
		iterators[index].remove();
	}

	private void ensureIndex() {
		while (index < iterators.length && !iterators[index].hasNext()) {
			index++;
		}
	}

}
