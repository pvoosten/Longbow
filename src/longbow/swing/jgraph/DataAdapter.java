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

package longbow.swing.jgraph;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import longbow.DataNode;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;

/**
 * An adapter that represents a {@link DataNode} as a {@link DefaultGraphCell}
 * 
 * @author Philip van Oosten
 * 
 */
public class DataAdapter extends DefaultGraphCell {

	private static final long serialVersionUID = 1L;

	protected boolean mark = false;

	private final DefaultPort poort;

	/**
	 * Creates a new instance of DataAdapter
	 * 
	 * @param data
	 */
	public DataAdapter(final DataNode data) {
		super(data);

		// poort toevoegen
		poort = new DefaultPort();
		add(poort);
		assert poort.getParent() == this;
		// breedte en hoogte instellen
		GraphConstants.setBounds(getAttributes(), new Rectangle2D.Double(0, 0, 35, 35));
	}

	public Port getPoort() {
		return poort;
	}

	public void setLocatie(final Point2D nieuweLocatie) {
		final AttributeMap atts = getAttributes();
		final double nieuwX = nieuweLocatie.getX();
		final double nieuwY = nieuweLocatie.getY();
		final double hoogte = GraphConstants.getBounds(atts).getHeight();
		final double breedte = GraphConstants.getBounds(atts).getWidth();
		GraphConstants.setBounds(atts, new Rectangle2D.Double(nieuwX, nieuwY, breedte, hoogte));
	}

	public void setMark(final boolean mark) {
		this.mark = mark;
	}

	@Override
	public void setUserObject(final Object object) {
		throw new UnsupportedOperationException("User object mag niet veranderd worden.");
	}
}
