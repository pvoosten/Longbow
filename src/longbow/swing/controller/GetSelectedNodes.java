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


import longbow.ContextEvent;
import longbow.DataWrapper;
import longbow.Metadata;
import longbow.Transformation;
import longbow.TransformationContext;
import longbow.metadata.MedalMetadata;

import org.jgraph.JGraph;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class GetSelectedNodes implements Transformation {

	public static final String IN_JGRAPH = "jgraph";

	public static final String OUT_SELECTED_NODES = "selectedNodes";

	private static final Metadata jgraphMetadata = MedalMetadata.parse("accept all");

	private static final Metadata selectedNodesMetadata = MedalMetadata.parse("accept all");

	private DataWrapper jgraphWrapper;

	private JGraph jgraph;

	private DataWrapper selectedNodesWrapper;

	private Object[] selectedNodes;

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addInput(IN_JGRAPH, jgraphMetadata);
		context.addOutput(OUT_SELECTED_NODES, selectedNodesMetadata);
	}

	public void contextChange(final ContextEvent event) {
	}

	public void contextRemoved(final ContextEvent event) {
	}

	public void exportData() {
		selectedNodesWrapper.setData(selectedNodes);
	}

	public void importData() {
		jgraph = (JGraph) jgraphWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	public void processData() {
		selectedNodes = jgraph.getSelectionCells();
	}

	public void startExecution(final TransformationContext context) {
		// initialize outputs
		selectedNodesWrapper = context.getOutputWrapper(OUT_SELECTED_NODES);
		// initialize inputs
		jgraphWrapper = context.getInputWrapper(IN_JGRAPH);

	}

	public void stopExecution() {
		jgraph = null;
		jgraphWrapper = null;
		selectedNodes = null;
		selectedNodesWrapper = null;
	}

}
