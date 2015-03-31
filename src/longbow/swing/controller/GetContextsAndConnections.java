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
import longbow.metadata.MedalMetadata;
import longbow.swing.jgraph.TransformationAdapter;

import org.jgraph.graph.Edge;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class GetContextsAndConnections implements Transformation {

	public static final String IN_SELECTED_NODES = "selectedNodes";

	public static final String OUT_CONNECTIONS = "connections";

	public static final String OUT_CONTEXTS = "contexts";

	private static final Metadata selectedNodesMetadata = MedalMetadata.parse("accept all");

	private static final Metadata connectionsMetadata = MedalMetadata.parse("accept all");

	private static final Metadata contextsMetadata = MedalMetadata.parse("accept all");

	private DataWrapper selectedNodesWrapper;

	private Object[] selectedNodes;

	private DataWrapper connectionsWrapper;

	private Edge[] connections;

	private DataWrapper contextsWrapper;

	private TransformationAdapter[] contexts;

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addInput(IN_SELECTED_NODES, selectedNodesMetadata);
		context.addOutput(OUT_CONNECTIONS, connectionsMetadata);
		context.addOutput(OUT_CONTEXTS, contextsMetadata);
	}

	public void contextChange(final ContextEvent event) {
	}

	public void contextRemoved(final ContextEvent event) {
	}

	public void exportData() {
		connectionsWrapper.setData(connections);
		contextsWrapper.setData(contexts);
	}

	public void importData() {
		selectedNodes = (Object[]) selectedNodesWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	public void processData() {
		final List<Edge> connectionsList = new LinkedList<Edge>();
		final List<TransformationAdapter> vertexList = new LinkedList<TransformationAdapter>();
		for (final Object o : selectedNodes) {
			if (o instanceof TransformationAdapter) {
				vertexList.add((TransformationAdapter) o);
			} else if (o instanceof Edge) {
				connectionsList.add((Edge) o);
			} else {
				assert o instanceof longbow.swing.jgraph.DataAdapter;
			}
		}
		connections = connectionsList.toArray(new Edge[connectionsList.size()]);
		contexts = vertexList.toArray(new TransformationAdapter[vertexList.size()]);
	}

	public void startExecution(final TransformationContext context) {
		// initialize outputs
		connectionsWrapper = context.getOutputWrapper(OUT_CONNECTIONS);
		contextsWrapper = context.getOutputWrapper(OUT_CONTEXTS);
		// initialize inputs
		selectedNodesWrapper = context.getInputWrapper(IN_SELECTED_NODES);

	}

	public void stopExecution() {
		selectedNodes = null;
		selectedNodesWrapper = null;
		connections = null;
		connectionsWrapper = null;
		contexts = null;
		contextsWrapper = null;
	}

}
