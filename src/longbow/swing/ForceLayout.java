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
import gnu.trove.TIntArrayList;
import gnu.trove.TObjectIntHashMap;

import java.awt.geom.Rectangle2D;

import longbow.TransformationContext;

import org.apache.log4j.Logger;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class ForceLayout implements WorkflowLayout {

	private static final double damping = 0.2;

	private static final Logger logger = Logger.getLogger(ForceLayout.class);

	private static final double timestep = 0.1;

	public void doLayout(final GraphModel jgraphModel) {
		logger.info("ForceLayout.doLayout()");

		final Object[] cells = getAll(jgraphModel);
		final TIntArrayList list = new TIntArrayList();
		final TObjectIntHashMap<Object> indices = new TObjectIntHashMap<Object>();
		int index = 0;
		for (int i = 0; i < cells.length; i++) {
			if (isVertex(jgraphModel, cells[i])) {
				list.add(i);
				indices.put(cells[i], index++);
			}
		}
		final int[] vertices = list.toNativeArray();
		final NodeType[] types = new NodeType[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			if (cells[vertices[i]] instanceof TransformationContext) {
				types[i] = NodeType.TRANSFORMATION;
			} else {
				types[i] = NodeType.DATANODE;
			}
		}

		//		set up initial node velocities to (0,0)

		// x coordinates of the node velocities
		final double[] vx = new double[vertices.length];
		// y coordinates of the node velocities
		final double[] vy = new double[vertices.length];
		// total_kinetic_energy := 0 // running sum of total kinetic energy over all particles
		double totalKineticEnergy = 1.0;

		//		 set up initial node positions randomly // make sure no 2 nodes are in exactly the same position

		// x coordinates of the nodes
		final double[] x = new double[vertices.length];
		// y coordinates of the nodes
		final double[] y = new double[vertices.length];
		// mass of the nodes
		final double[] mass = new double[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			final GraphCell c = (GraphCell) cells[vertices[i]];
			final Rectangle2D bound = GraphConstants.getBounds(c.getAttributes());
			assert bound != null : "All nodes must be given an initial location and size before layout";
			x[i] = bound.getCenterX() + 20 * Math.random() - 10;
			y[i] = bound.getCenterY() + 20 * Math.random() - 10;
			mass[i] = bound.getHeight() * bound.getWidth();
		}

		// x coordinates of the forces on the nodes
		final double fx[] = new double[vertices.length];
		// y coordinates of the forces on the nodes
		final double fy[] = new double[vertices.length];

		if (vertices.length > 1) {
			while (totalKineticEnergy > 1.000001) {
				//		 loop
				//		     for each node
				for (int i = 0; i < vertices.length; i++) {
					totalKineticEnergy -= mass[i] * (vx[i] * vx[i] + vy[i] * vy[i]);
					// net-force := (0, 0) // running sum of total force on this particular node
					fx[i] = 0.0;
					fy[i] = 0.0;
					// for each other node
					for (int j = 0; j < vertices.length; j++) {
						if (j != i) {
							//		             net-force := net-force + Coulomb_repulsion( this_node, other_node )
							fx[i] += coulombRepulsion(types, x, i, j);
							fy[i] += coulombRepulsion(types, y, i, j);
						}
					} // next node

					// for each spring connected to this node
					//		             net-force := net-force + Hooke_attraction( this_node, spring )
					//		         next spring
					for (final Object o : getIncomingEdges(jgraphModel, cells[i])) {
						final int vertex = indices.get(getSourceVertex(jgraphModel, o));
						hookeAttraction(types, fx, fy, x, y, i, vertex);
					}
					for (final Object o : getOutgoingEdges(jgraphModel, cells[i])) {
						final int vertex = indices.get(getTargetVertex(jgraphModel, o));
						hookeAttraction(types, fx, fy, x, y, i, vertex);
					}
					//		         
					//		         // without damping, it moves forever
					//		         this_node.velocity := (this_node.velocity + timestep * net-force) * damping
					vx[i] = (vx[i] + timestep * fx[i]) * damping;
					vy[i] = (vy[i] + timestep * fy[i]) * damping;
					//		         this_node.position := this_node.position + timestep * this_node.velocity
					x[i] += timestep * vx[i];
					y[i] += timestep * vy[i];
					//		         total_kinetic_energy := total_kinetic_energy + this_node.mass * (this_node.speed)^2
					totalKineticEnergy += mass[i] * (vx[i] * vx[i] + vy[i] * vy[i]);
					logger.info("kinetic energy: " + totalKineticEnergy);
				} // next node
			} //		 until total_kinetic_energy is less than some small number  //the simulation has stopped moving
		}

		// adjust cell positions
		for (int i = 0; i < vertices.length; i++) {
			final GraphCell cell = (GraphCell) cells[vertices[i]];
			final AttributeMap atts = cell.getAttributes();
			Rectangle2D bounds = GraphConstants.getBounds(atts);
			final double w = bounds.getWidth();
			final double h = bounds.getHeight();
			bounds = new Rectangle2D.Double(x[i] - w / 2, y[i] - h / 2, w, h);
			GraphConstants.setBounds(atts, bounds);
		}
	}

	private double coulombRepulsion(final NodeType[] types, final double[] x, final int i, final int j) {
		double repulsion = 0.0;
		if (x[i] != x[j]) {
			// between transformations: 5
			// between a transformation and a data node: 7 
			// between data nodes: 10
			switch (types[i]) {
			case TRANSFORMATION:
				switch (types[j]) {
				case TRANSFORMATION:
					repulsion = 5;
				case DATANODE:
					repulsion = 7;
				}
			case DATANODE:
				switch (types[j]) {
				case TRANSFORMATION:
					repulsion = 7;
				case DATANODE:
					repulsion = 10;
				}
			}
		}
		logger.info("Coulomb repulsion: " + repulsion);
		final double factor = x[i] > x[j] ? 1.0 : -1.0;
		return factor * repulsion * (x[i] - x[j]) * (x[i] - x[j]);
	}

	private void hookeAttraction(final NodeType[] types, final double[] fx, final double[] fy, final double[] x, final double[] y, final int i, final int j) {
		final double k = 1.1;
		final double d0;
		if (types[i] == NodeType.TRANSFORMATION) {
			if (types[j] == NodeType.TRANSFORMATION) {
				d0 = 100;
			} else if (types[j] == NodeType.DATANODE) {
				d0 = 40;
			} else {
				throw new IllegalArgumentException("Illegal NodeType");
			}
		} else if (types[i] == NodeType.DATANODE) {
			if (types[j] == NodeType.TRANSFORMATION) {
				d0 = 40;
			} else if (types[j] == NodeType.DATANODE) {
				d0 = 60;
			} else {
				throw new IllegalArgumentException("Illegal NodeType");
			}
		} else {
			throw new IllegalArgumentException("Illegal NodeType");
		}
		final double dx = x[i] - x[j];
		final double dy = y[i] - y[j];
		final double d = Math.sqrt(dx * dx + dy * dy);
		final double dd = k * (d - d0) / d;
		fx[i] -= dd * dx;
		fy[i] -= dd * dy;
	}

	enum NodeType {
		TRANSFORMATION, DATANODE;
	}
}
