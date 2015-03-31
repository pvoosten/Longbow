/*
 * Copyright 2008 Philip van Oosten (Mentoring Systems BVBA)
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

package longbow.swing.controller;


import java.util.LinkedList;
import java.util.List;

import longbow.ContextEvent;
import longbow.DataWrapper;
import longbow.Metadata;
import longbow.Transformation;
import longbow.TransformationContext;
import longbow.Workflow;
import longbow.metadata.MedalMetadata;
import longbow.swing.jgraph.DataAdapter;
import longbow.swing.jgraph.TransformationAdapter;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphModel;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DisconnectIfExists implements Transformation {

	public static final String IN_CONNECTIONS = "connections";

	public static final String IN_WORKFLOW = "workflow";

	public static final String IN_JGRAPH = "jgraph";

	private static final Metadata connectionsMetadata = MedalMetadata.parse("accept all");

	private static final Metadata workflowMetadata = MedalMetadata.parse("accept all");

	private static final Metadata jgraphMetadata = MedalMetadata.parse("accept all");

	private DataWrapper connectionsWrapper;

	private DataWrapper jgraphWrapper;

	private Edge[] connections;

	private JGraph jgraph;

	private DataWrapper workflowWrapper;

	private Workflow workflow;

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addInput(IN_CONNECTIONS, connectionsMetadata);
		context.addInput(IN_WORKFLOW, workflowMetadata);
		context.addInput(IN_JGRAPH, jgraphMetadata);
	}

	public void contextChange(final ContextEvent event) {
	}

	public void contextRemoved(final ContextEvent event) {
	}

	public void exportData() {
	}

	public void importData() {
		connections = (Edge[]) connectionsWrapper.getData();
		workflow = (Workflow) workflowWrapper.getData();
		jgraph = (JGraph) jgraphWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	public void processData() {
		final List<GraphCell> toRemove = new LinkedList<GraphCell>();
		final GraphModel model = jgraph.getModel();
		for (int i = 0; i < connections.length; i++) {
			final Edge connection = connections[i];
			final Object o1 = model.getParent(model.getSource(connection));
			final Object o2 = model.getParent(model.getTarget(connection));
			if (o1 == null || o2 == null) {
				// remove from view
				toRemove.add(connection);
			} else if (o1 instanceof TransformationAdapter && o2 instanceof TransformationAdapter) {
				// direct link (no data link) between two contexts
				final TransformationAdapter fromAdapter = (TransformationAdapter) DefaultGraphModel.getSourceVertex(model, connection);
				final TransformationContext from = (TransformationContext) fromAdapter.getUserObject();
				final TransformationAdapter toAdapter = (TransformationAdapter) DefaultGraphModel.getTargetVertex(model, connection);
				final TransformationContext to = (TransformationContext) toAdapter.getUserObject();
				workflow.disconnect(from, to);
				toRemove.add(connection);
			} else if (o1 instanceof TransformationAdapter && o2 instanceof DataAdapter) {
				// Connection between an output and a data node.
				final TransformationAdapter fromAdapter = (TransformationAdapter) DefaultGraphModel.getSourceVertex(model, connection);
				final TransformationContext from = (TransformationContext) model.getValue(fromAdapter);
				final String outputid = (String) model.getValue(connection);
				final DataAdapter dataAdapter = (DataAdapter) o2;
				final Object[] outgoingEdges = DefaultGraphModel.getOutgoingEdges(model, dataAdapter);
				for (final Object edge : outgoingEdges) {
					final TransformationAdapter toAdapter = (TransformationAdapter) DefaultGraphModel.getTargetVertex(model, edge);
					final TransformationContext to = (TransformationContext) model.getValue(toAdapter);
					final String inputid = (String) model.getValue(edge);
					if (workflow.disconnect(from, outputid, to, inputid)) {
						toRemove.add((GraphCell) edge);
					}
				}
				if (toRemove.size() == outgoingEdges.length) {
					toRemove.add(dataAdapter);
					toRemove.add(connection);
				} else {
					toRemove.clear();
				}
			} else if (o1 instanceof DataAdapter && o2 instanceof TransformationAdapter) {
				// connection between a data node and a context
				final TransformationAdapter toAdapter = (TransformationAdapter) DefaultGraphModel.getSourceVertex(model, connection);
				final TransformationContext to = (TransformationContext) model.getValue(toAdapter);
				final String inputid = (String) model.getValue(connection);
				final DataAdapter dataAdapter = (DataAdapter) o1;

				final Object[] incomingEdges = DefaultGraphModel.getIncomingEdges(model, dataAdapter);
				for (final Object edge : incomingEdges) {
					final TransformationAdapter fromAdapter = (TransformationAdapter) DefaultGraphModel.getSourceVertex(model, edge);
					final TransformationContext from = (TransformationContext) model.getValue(fromAdapter);
					final String outputid = (String) model.getValue(edge);
					if (workflow.disconnect(from, outputid, to, inputid)) {
						toRemove.add((GraphCell) edge);
					}
				}
				if (toRemove.size() == incomingEdges.length) {
					toRemove.add(dataAdapter);
					toRemove.add(connection);
				}
			} else {
				assert false : "Invalid connection in jgraph";
			}
		}
		jgraph.getGraphLayoutCache().remove(toRemove.toArray());
	}

	public void startExecution(final TransformationContext context) {
		// initialize inputs
		connectionsWrapper = context.getInputWrapper(IN_CONNECTIONS);
		workflowWrapper = context.getInputWrapper(IN_WORKFLOW);

	}

	public void stopExecution() {
		connections = null;
		connectionsWrapper = null;
		workflow = null;
		workflowWrapper = null;
	}

}
