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


import static org.jgraph.graph.GraphConstants.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import longbow.TransformationContext;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/**
 * Represents a {@link TransformationContext} as a cell for display with JGraph.
 * 
 * @author Philip van Oosten
 * 
 */
public class TransformationAdapter extends DefaultGraphCell {

	private static final long serialVersionUID = 1L;

	private static int offset = 1;

	private final DefaultPort port;

	public TransformationAdapter(final TransformationContext tfie) {
		this(tfie, new Point2D.Double(offset * 10, offset * 10));
		offset++;
	}

	public TransformationAdapter(final TransformationContext context, final Point2D location) {
		super(context);
		final AttributeMap map = getAttributes();
		final double height = 60.0;
		final double width = 130.0;
		setBounds(map, new Rectangle2D.Double(location.getX() - width / 2.0, location.getY() - height / 2.0, width, height));
		setResize(map, false);
		setEditable(map, false);
		setOpaque(map, true);
		port = new DefaultPort();
		add(port);
		assert GraphConstants.getOffset(port.getAttributes()) == null;
	}

	/**
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void setUserObject(final Object o) {
		throw new UnsupportedOperationException("User object mag niet veranderd worden.");
	}
}
