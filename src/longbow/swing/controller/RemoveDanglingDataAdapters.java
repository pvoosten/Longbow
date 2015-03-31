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

import longbow.ContextEvent;
import longbow.DataWrapper;
import longbow.Metadata;
import longbow.Transformation;
import longbow.TransformationContext;
import longbow.metadata.MedalMetadata;
import longbow.swing.jgraph.DataAdapter;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphModel;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class RemoveDanglingDataAdapters implements Transformation {

	public static final String IN_JGRAPH = "jgraph";

	private static final Metadata jgraphMetadata = MedalMetadata.parse("accept all");

	private DataWrapper jgraphWrapper;

	private JGraph jgraph;

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addInput(IN_JGRAPH, jgraphMetadata);
	}

	public void contextChange(final ContextEvent event) {
	}

	public void contextRemoved(final ContextEvent event) {
	}

	public void exportData() {
	}

	public void importData() {
		jgraph = (JGraph) jgraphWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	public void processData() {
		final GraphModel model = jgraph.getModel();
		final Object[] allCells = DefaultGraphModel.getAll(model);
		final LinkedList<DataAdapter> toRemove = new LinkedList<DataAdapter>();
		for (final Object o : allCells) {
			if (o instanceof DataAdapter && DefaultGraphModel.getOutgoingEdges(model, o).length == 0 && DefaultGraphModel.getIncomingEdges(model, o).length == 0) {
				toRemove.add((DataAdapter) o);
			}
		}
		jgraph.getGraphLayoutCache().remove(toRemove.toArray());
	}

	public void startExecution(final TransformationContext context) {
		// initialize inputs
		jgraphWrapper = context.getInputWrapper(IN_JGRAPH);

	}

	public void stopExecution() {
		jgraph = null;
		jgraphWrapper = null;
	}

}
