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

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class TransformatieView extends VertexView {

	protected static final CellViewRenderer transformationRenderer = new TransformationRenderer();

	private static final long serialVersionUID = 1L;

	public TransformatieView(final TransformationAdapter adapter) {
		super(adapter);
	}

	@Override
	public Point2D getPerimeterPoint(final EdgeView edge, final Point2D source, final Point2D point) {
		return ((TransformationRenderer) transformationRenderer).getPerimeterPoint(this, source, point);
	}

	/**
	 * Returns a renderer for the class.
	 */
	@Override
	public CellViewRenderer getRenderer() {
		return transformationRenderer;
	}
}
