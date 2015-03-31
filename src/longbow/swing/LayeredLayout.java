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


import static org.jgraph.graph.DefaultGraphModel.*;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import gnu.trove.TObjectIntHashMap;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

/**
 * Positions vertices in a layered dependency graph in a jgraph model.
 * 
 * @author Philip van Oosten
 * 
 */
public class LayeredLayout implements WorkflowLayout {

	private static final Logger logger = Logger.getLogger(LayeredLayout.class);

	public LayeredLayout() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see longbow.swing.WorkflowLayout#doLayout(org.jgraph.graph.GraphModel)
	 */
	public void doLayout(final GraphModel jgraphModel) {
		logger.info("place in layers");
		final Object[] cells = getAll(jgraphModel);
		logger.info(cells.length + " cells total");
		final LinkedList<GraphCell> list = new LinkedList<GraphCell>();
		for (final Object o : cells) {
			if (isVertex(jgraphModel, o)) {
				list.add((GraphCell) o);
			}
		}
		final GraphCell[] vertices = new GraphCell[list.size()];
		logger.info(vertices.length + " vertices");
		final int[] indegrees = new int[vertices.length];
		final int[] outdegrees = new int[vertices.length];
		final TObjectIntHashMap<GraphCell> vertexIndices = new TObjectIntHashMap<GraphCell>(vertices.length);
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = list.remove();
			vertexIndices.put(vertices[i], i);
			indegrees[i] = getIncomingEdges(jgraphModel, vertices[i]).length;
			outdegrees[i] = getOutgoingEdges(jgraphModel, vertices[i]).length;
		}
		logger.info("indegrees: " + Arrays.toString(indegrees));
		logger.info("outdegrees: " + Arrays.toString(outdegrees));

		int numToPlace = vertices.length;
		int prevnumtoplace = vertices.length + 1;
		final LinkedList<LinkedList<GraphCell>> positions = new LinkedList<LinkedList<GraphCell>>();
		final TIntHashSet set = new TIntHashSet();
		while (numToPlace > 0) {
			while (numToPlace < prevnumtoplace) {
				final LinkedList<GraphCell> currentRow = new LinkedList<GraphCell>();
				positions.add(currentRow);
				assert set.isEmpty();
				prevnumtoplace = numToPlace;
				// select vertices to remove
				for (int i = 0; i < indegrees.length; i++) {
					if (indegrees[i] == 0) {
						set.add(i);
					}
				}
				numToPlace -= set.size();
				// effectively remove vertices
				for (final TIntIterator it = set.iterator(); it.hasNext();) {
					final int i = it.next();
					indegrees[i] = -1;
					final GraphCell cell = vertices[i];
					currentRow.add(cell);
					final Object[] outgoingEdges = getOutgoingEdges(jgraphModel, cell);
					for (final Object edge : outgoingEdges) {
						final GraphCell next = (GraphCell) getTargetVertex(jgraphModel, edge);
						final int nextIndex = vertexIndices.get(next);
						indegrees[nextIndex]--;
					}
					it.remove();
				}
			}
		}

		int row = 1;
		for (final LinkedList<GraphCell> l : positions) {
			int col = 1;
			for (final GraphCell cell : l) {
				placeCell(cell, row, col);
				col++;
			}
			row++;
		}
	}

	private void placeCell(final GraphCell cell, final int row, final int col) {
		final AttributeMap atts = cell.getAttributes();
		Rectangle2D bounds = GraphConstants.getBounds(atts);
		final double w = bounds.getWidth();
		final double h = bounds.getHeight();
		final double x = 180 * col + 40 * row - w / 2 - 120;
		final double y = 120 * row - h / 2 - 60;
		bounds = new Rectangle2D.Double(x, y, w, h);
		GraphConstants.setBounds(atts, bounds);
	}

}
