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
import gnu.trove.TObjectIntIterator;

import java.util.*;

import longbow.PartialOrder;
import longbow.TransformationContext;
import longbow.Workflow;

/**
 * This class defines and remembers a partial ordering of its elements. The
 * elements can be of any object type. The partial ordering of the elements is
 * defined only by an object of this class, and doesn't take external factors
 * for ordering into account.
 * 
 * This class is primarily used to create a partial ordering for
 * {@link TransformationContext}s in a {@link Workflow}.
 * 
 * This class is not thread safe. Simultaneous calls on methods of the same
 * instance can corrupt the partial ordering.
 * 
 * @param <T>
 *            The type of elements that are partially ordered.
 * 
 * @author Philip van Oosten
 * 
 */
public class PartialOrderImpl<T> implements PartialOrder<T> {

	int newIndex;

	final Map<T, Element> elements;

	private final List<T> orderedList;

	private final Comparator<T> comparator;

	public PartialOrderImpl() {
		elements = new HashMap<T, Element>();
		orderedList = new LinkedList<T>();
		comparator = new Comparator<T>() {

			public int compare(final T former, final T latter) {
				return elements.get(former).index - elements.get(latter).index;
			}
		};
	}

	public boolean add(final T o) {
		if (elements.containsKey(o)) {
			return false;
		}
		elements.put(o, new Element(o));
		orderedList.add(o);
		return true;
	}

	public boolean addAll(final Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		orderedList.clear();
		elements.clear();
		newIndex = 0;
	}

	/**
	 * Connects two elements. The former element will be a precursor of the
	 * latter element
	 * 
	 * @param former
	 *            connection start
	 * @param latter
	 *            connection end
	 * @return true if a new connection has been established. False otherwise.
	 */
	public boolean connect(final T former, final T latter) {
		final Element first = elements.get(former);
		final Element last = elements.get(latter);
		if (first == null || last == null || follows(last, first)) {
			return false;
		}
		// update neighbour lists
		first.addFollower(last);
		last.addPrecursor(first);
		// update indexes
		final int lastIndex = last.index;
		if (first.index > lastIndex) {
			// switch indexes between last and precursor of last with
			// greatest index until the heap condition is fulfilled everywhere
			final LinkedList<Element> path = new LinkedList<Element>();
			// collect precursor with greatest index
			// add it to path
			Element precursor = greatestIndexPrecursor(last);
			while (precursor != null && precursor.index > lastIndex) {
				path.addLast(precursor);
				precursor = greatestIndexPrecursor(precursor);
			}
			Element pathElement = last;
			while (!path.isEmpty()) {
				pathElement.index = path.peek().index;
				pathElement = path.poll();
			}
			pathElement.index = lastIndex;
		}
		return true;
	}

	public boolean contains(final Object o) {
		return elements.keySet().contains(o);
	}

	public boolean containsAll(final Collection<?> c) {
		return elements.keySet().containsAll(c);
	}

	public boolean disconnect(final T former, final T latter) {
		final Element first = elements.get(former);
		final Element last = elements.get(latter);
		final boolean disconnected = first != null && last != null && first.followers.contains(last);
		if (disconnected) {
			first.removeFollower(last);
			last.removePrecursor(first);
		}
		return disconnected;
	}

	public boolean follows(final T first, final T last) {
		return contains(first) && contains(last) && follows(elements.get(first), elements.get(last));
	}

	@SuppressWarnings("unchecked")
	public List<T> getFollowers(final T key) {
		final LinkedList<T> f = new LinkedList<T>();
		for (final Object followerElement : elements.get(key).followers.keys()) {
			f.add(((Element) followerElement).value);
		}
		return f;
	}

	@SuppressWarnings("unchecked")
	public List<T> getPrecursors(final T key) {
		final LinkedList<T> f = new LinkedList<T>();
		for (final Object followerElement : elements.get(key).precursors.keys()) {
			f.add(((Element) followerElement).value);
		}
		return f;
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public Iterator<T> iterator() {
		sort();
		return orderedList.iterator();
	}

	public boolean remove(final Object o) {
		if (!elements.containsKey(o)) {
			return false;
		}
		final Element element = elements.get(o);
		assert element != null;
		// disconnect element
		final TObjectIntIterator<Element> preIterator = element.precursors.iterator();
		while (preIterator.hasNext()) {
			preIterator.advance();
			final Element pre = preIterator.key();
			pre.removeFollower(element);
		}
		final TObjectIntIterator<Element> postIterator = element.followers.iterator();
		while (postIterator.hasNext()) {
			postIterator.advance();
			final Element post = postIterator.key();
			post.removePrecursor(element);
		}
		orderedList.remove(element.value);
		elements.remove(element.value);
		return true;
	}

	public boolean removeAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return elements.size();
	}

	public Object[] toArray() {
		return orderedList.toArray();
	}

	public <E> E[] toArray(final E[] a) {
		return orderedList.toArray(a);
	}

	/**
	 * 
	 * @param first
	 *            starting point of a possible indirect connection
	 * @param last
	 *            endpoint of a possible indirect connection
	 * @return if there exist a direct or indirect connection from first to
	 *         last, or if first and last are the same
	 */
	private boolean follows(Element first, Element last) {
		// first==last ==> true (should not connect)
		// search for a path from first to last.
		// path found ==> true
		// path not found ==> false

		if (first.outdegree == 0 || last.indegree == 0 || first.index > last.index) {
			// there can be no path from first to last because:
			// 1. first has no followers, or
			// 2. last has no precursors, or
			// 3. there can be no path from first to last,
			//    because first is higher in partial order.
			return false;
		}

		// make first and last come as close as possible to each other,
		// replacing them by their only follower or precursor
		while (first.outdegree == 1 && first.index < last.index) {
			final TObjectIntIterator<Element> it = first.followers.iterator();
			it.advance();
			first = it.key();
		}
		while (last.indegree == 1 && first.index < last.index) {
			final TObjectIntIterator<Element> it = last.precursors.iterator();
			it.advance();
			last = it.key();
		}

		if (first == last || first.followers.contains(last)) {
			// there is definately a path from first to last,
			// or they are the same.
			return true;
		}

		final int firstIndex = first.index;
		if (firstIndex > last.index) {
			// there can be no path from first to last,
			// because there can be no connection from 
			// an element with a higher index
			// to an element with a lower index.
			return false;
		}

		boolean path = false;
		if (firstIndex < last.index) {
			// there is still a chance that there exists a path
			//   from first to last, but that is unsure yet.

			// it might be just a wild guess, but indegrees might
			// generally be averagely smaller than outdegrees 
			// in human-made DAGs. Human-made DAGs might be a little
			// more tree-like than random DAGs.
			// Therefore searching for a path backwards would be faster
			// than searching for them in the right way.
			final TObjectIntIterator<Element> firstIterator = last.precursors.iterator();
			while (firstIterator.hasNext() && !path) {
				firstIterator.advance();
				final Element lastPrecursor = firstIterator.key();

				// we already know that lastPrecursor can not be
				// a direct precursor of last, so we immediately
				// continue with the precursors of lastPrecursor,
				// provided that their index is greater than firstIndex.

				if (firstIndex < lastPrecursor.index) {
					final TObjectIntIterator<Element> secondIterator = lastPrecursor.precursors.iterator();
					while (secondIterator.hasNext() && !path) {
						secondIterator.advance();
						final Element secondPrecursor = secondIterator.key();
						if (secondPrecursor.indegree != 0 && firstIndex < secondPrecursor.index && follows(first, secondPrecursor)) {
							path = true;
						}
					}
				}
			}
		}
		return path;
	}

	private Element greatestIndexPrecursor(final Element element) {
		Element greatest = null;
		if (element.indegree != 0) {
			final TObjectIntIterator<Element> it = element.precursors.iterator();
			if (it.hasNext()) {
				it.advance();
				greatest = it.key();
			}
			while (it.hasNext()) {
				it.advance();
				final Element currentElement = it.key();
				if (currentElement.index > greatest.index) {
					greatest = currentElement;
				}
			}
		}
		return greatest;
	}

	private void sort() {
		Collections.sort(orderedList, comparator);
	}

	private class Element {

		/**
		 * index of this element in ordered list of elements
		 */
		int index;

		int indegree;

		final T value;

		final TObjectIntHashMap<Element> precursors;

		final TObjectIntHashMap<Element> followers;

		int outdegree;

		public Element(final T value) {
			this.value = value;
			indegree = 0;
			outdegree = 0;
			followers = new TObjectIntHashMap<Element>();
			precursors = new TObjectIntHashMap<Element>();
			index = newIndex++;
		}

		@Override
		public String toString() {
			return "(indegree=" + indegree + "; outdegree=" + outdegree + "; index=" + index + ")";
		}

		void addFollower(final Element follower) {
			if (!followers.contains(follower)) {
				outdegree++;
			}
			followers.adjustOrPutValue(follower, 1, 1);
		}

		void addPrecursor(final Element precursor) {
			if (!precursors.contains(precursor)) {
				indegree++;
			}
			precursors.adjustOrPutValue(precursor, 1, 1);
		}

		boolean removeFollower(final Element follower) {
			if (followers.get(follower) == 1) {
				followers.remove(follower);
				outdegree--;
				return true;
			}
			return followers.adjustValue(follower, -1);
		}

		boolean removePrecursor(final Element precursor) {
			if (precursors.get(precursor) == 1) {
				precursors.remove(precursor);
				indegree--;
				return true;
			}
			return precursors.adjustValue(precursor, -1);
		}
	}

}
