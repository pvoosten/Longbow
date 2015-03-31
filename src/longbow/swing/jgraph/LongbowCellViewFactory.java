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

import java.awt.Color;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class LongbowCellViewFactory extends DefaultCellViewFactory {

	private static final long serialVersionUID = 1L;

	private static final Color edgeBackground = Color.green;

	private static final Color edgeGradient = Color.yellow;

	private static final Color edgeLineColor = Color.green;

	public LongbowCellViewFactory() {
	}

	@Override
	protected EdgeView createEdgeView(final Object cell) {
		final EdgeView view = super.createEdgeView(cell);
		final AttributeMap atts = view.getAllAttributes();
		setBackground(atts, edgeBackground);
		setBeginFill(atts, true);
		setBeginSize(atts, 10);
		setEndFill(atts, false);
		setEndSize(atts, 20);
		setBendable(atts, true);
		setGradientColor(atts, edgeGradient);
		setLabelAlongEdge(atts, true);
		setLineColor(atts, edgeLineColor);
		setLineEnd(atts, ARROW_SIMPLE);
		setLineStyle(atts, STYLE_SPLINE);
		setLineWidth(atts, 4.0F);
		return view;
	}

	@Override
	protected VertexView createVertexView(final Object cell) {
		VertexView view = null;
		if (cell instanceof TransformationAdapter) {
			view = createTransformationView((TransformationAdapter) cell);
		} else if (cell instanceof DataAdapter) {
			view = maakDataView((DataAdapter) cell);
		}
		return view;
	}

	private TransformatieView createTransformationView(final TransformationAdapter cel) {
		return new TransformatieView(cel);
	}

	private DataView maakDataView(final DataAdapter cel) {
		return new DataView(cel);
	}

}
