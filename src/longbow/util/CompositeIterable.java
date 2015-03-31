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
 * Composite pattern for {@link Iterable}s
 * 
 * @param <T>
 * 
 * @author Philip van Oosten
 * 
 */
public class CompositeIterable<T> implements Iterable<T> {

	private final Iterable<? extends T>[] iterables;

	public CompositeIterable(final Iterable<? extends T>... iterables) {
		this.iterables = iterables;
	}

	public Iterator<T> iterator() {
		final Iterator<? extends T>[] iterators = newIterators();
		for (int i = 0; i < iterators.length; i++) {
			iterators[i] = iterables[i].iterator();
		}
		return new CompositeIterator<T>(iterators);
	}

	@SuppressWarnings("unchecked")
	private Iterator<T>[] newIterators() {
		return new Iterator[iterables.length];
	}
}
