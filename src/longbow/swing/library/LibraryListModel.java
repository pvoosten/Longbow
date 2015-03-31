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

package longbow.swing.library;


import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import longbow.Transformation;

/**
 * Contains the {@link Transformation}s of the library.
 * 
 * @author Philip van Oosten
 * 
 */
public class LibraryListModel extends AbstractListModel {

	private static final long serialVersionUID = 1L;

	/**
	 * A list containing the classes of transformations in the library.
	 */
	private final List<Class<? extends Transformation>> transformationClasses;

	/**
	 * Creates a new instance of this class.
	 */
	public LibraryListModel() {
		transformationClasses = new ArrayList<Class<? extends Transformation>>();
	}

	/**
	 * Adds a {@link Transformation} class to the library.
	 * 
	 * @param transformation
	 *            the class of the {@link Transformation} to add to the library.
	 */
	public void add(final Class<? extends Transformation> transformation) {
		final int position = transformationClasses.size();
		transformationClasses.add(transformation);
		fireIntervalAdded(this, position, position);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getElementAt(final int index) {
		return transformationClasses.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getSize() {
		return transformationClasses.size();
	}

}
