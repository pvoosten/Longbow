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

package longbow.swing;


import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import longbow.Transformation;

import org.apache.log4j.Logger;

/**
 * TransferHandler, die ervan uitgaat dat enkel TransformatieKnopen naar de
 * JComponent met dit als TransferHandler kunnen gesleept worden. Dit mag enkel
 * als TransferHandler van een JGraph instantie gebruikt worden.
 * 
 * @author Philip van Oosten
 * 
 */
public final class GraafTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(GraafTransferHandler.class);

	private static TransferHandler instance;

	/**
	 * Creates a new instance of {@link GraafTransferHandler}
	 * 
	 */
	private GraafTransferHandler() {
	}

	@Override
	public boolean canImport(final JComponent component, final DataFlavor[] flavors) {
		return component instanceof GraphPanel;
	}

	@Override
	public boolean importData(final JComponent component, final Transferable transferable) {
		try {
			final GraphPanel graaf = (GraphPanel) component;
			if (graaf.isEnabled()) {
				final DataFlavor[] flavors = transferable.getTransferDataFlavors();
				if (flavors.length == 1) {
					final DataFlavor flavor = flavors[0];

					final Point2D location = graaf.getMousePosition();
					if (location != null) {

						final Object object = transferable.getTransferData(flavor);
						if (object instanceof Transformation) {
							logger.info("Importing transformation from drag and drop");
							final Transformation transformation = (Transformation) object;
							return graaf.addTransformation(transformation, location);
						}
					}
				}
			}
		} catch (final UnsupportedFlavorException ex) {
			final String msg = "Error in drop";
			logger.error(msg, ex);
		} catch (final IOException ex) {
			final String msg = "Error in drop";
			logger.error(msg, ex);
		} catch (final RuntimeException ex) {
			final String msg = "Error in drop";
			logger.error(msg, ex);
		}
		return false;
	}

	@Override
	protected Object clone() {
		return getInstance();
	}

	static TransferHandler delegateFrom(final JComponent component) {
		// create delegate TransferHandler
		return new DelegatedTransferHandler(component);
	}

	static TransferHandler getInstance() {
		if (instance == null) {
			synchronized (GraafTransferHandler.class) {
				if (instance == null) {
					instance = new GraafTransferHandler();
				}
			}
		}
		return instance;
	}

	static class DelegatedTransferHandler extends TransferHandler {

		private static final long serialVersionUID = -6025458454059716264L;

		private final WeakReference<JComponent> component;

		/**
		 * 
		 * @param component
		 *            The component to delegate all methods to.
		 */
		public DelegatedTransferHandler(final JComponent component) {
			this.component = new WeakReference<JComponent>(component);
		}

		@Override
		public boolean canImport(final JComponent comp, final DataFlavor[] transferFlavors) {
			return getInstance().canImport(component.get(), transferFlavors);
		}

		@Override
		public void exportAsDrag(final JComponent comp, final InputEvent e, final int action) {
			getInstance().exportAsDrag(component.get(), e, action);
		}

		@Override
		public void exportToClipboard(final JComponent comp, final Clipboard clip, final int action) throws IllegalStateException {
			getInstance().exportToClipboard(component.get(), clip, action);
		}

		@Override
		public int getSourceActions(final JComponent c) {
			return getInstance().getSourceActions(component.get());
		}

		@Override
		public Icon getVisualRepresentation(final Transferable t) {
			return getInstance().getVisualRepresentation(t);
		}

		@Override
		public boolean importData(final JComponent comp, final Transferable t) {
			return getInstance().importData(component.get(), t);
		}
	}
}
