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


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import longbow.Transformation;

/**
 * Drag and drop support for the library; a {@link Transferable} implementation
 * that can be used to drag {@link Transformation}s from the library and drop
 * them elsewhere.
 * 
 * @author Philip van Oosten
 * 
 */
public class LibraryTransferable implements Transferable {

	private final Transformation transformation;

	/**
	 * Creates a new {@link LibraryTransferable}.
	 * 
	 * @param transformation
	 *            the {@link Transformation} to drag.
	 */
	public LibraryTransferable(final Transformation transformation) {
		this.transformation = transformation;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getTransferData(final DataFlavor flavor) {
		return transformation;
	}

	/**
	 * {@inheritDoc}
	 */
	public DataFlavor[] getTransferDataFlavors() {
		final DataFlavor[] flavors = new DataFlavor[1];
		try {
			flavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
		} catch (final ClassNotFoundException ex) {
			throw new BibliotheekException(ex);
		}
		return flavors;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		return flavor.getMimeType().equals(DataFlavor.javaJVMLocalObjectMimeType);
	}

}
