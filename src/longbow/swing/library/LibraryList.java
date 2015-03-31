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


import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import longbow.LongbowException;
import longbow.Transformation;

/**
 * A {@link JList} that graphically represents the library.
 * 
 * @author Philip van Oosten
 * 
 */
public class LibraryList extends JList {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance
	 */
	public LibraryList() {
		super(new LibraryListModel());
		// layout
		setBorder(BorderFactory.createLoweredBevelBorder());
		// enable DnD
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final TransferHandler transferHandler = new TransferHandler("transformation");
		setTransferHandler(transferHandler);
		setDragEnabled(true);

		addTransformations();

	}

	@SuppressWarnings("unchecked")
	public Transformation getTransformation() {
		try {
			return ((Class<? extends Transformation>) getModel().getElementAt(getSelectedIndex())).newInstance();
		} catch (final InstantiationException e) {
			throw new LongbowException(e);
		} catch (final IllegalAccessException e) {
			throw new LongbowException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void addTransformations() {
		final LibraryListModel model = (LibraryListModel) getModel();
		final ResourceBundle bundle = ResourceBundle.getBundle("longbow.swing.library.transformations");
		final List<String> nietGevonden = new LinkedList<String>();
		for (final Enumeration<String> keys = bundle.getKeys(); keys.hasMoreElements();) {
			final String key = keys.nextElement();
			if (bundle.getString(key).equals("true")) {
				try {
					final Class<?> tfie = Class.forName(key);
					if (Transformation.class.isAssignableFrom(tfie)) {
						model.add((Class<? extends Transformation>) tfie);
					}
				} catch (final ClassNotFoundException ex) {
					nietGevonden.add(key);
				}
			} else {
				assert bundle.getString(key).equals("false") : "Fout in configuratie bibliotheek: enkel \"true\" en \"false\" toegestaan als waarde in bibliotheek.properties";
			}
		}
	}
}
