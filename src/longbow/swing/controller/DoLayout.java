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
import longbow.swing.WorkflowLayout;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphModel;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DoLayout implements Transformation {

	public static final String IN_LAYOUT = "layout";

	public static final String IN_JGRAPH = "jgraph";

	private static final Metadata layoutMetadata = MedalMetadata.parse("accept all");

	private static final Metadata jgraphMetadata = MedalMetadata.parse("accept all");

	private DataWrapper layoutWrapper;

	private WorkflowLayout layout;

	private DataWrapper jgraphWrapper;

	private JGraph jgraph;

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addInput(IN_LAYOUT, layoutMetadata);
		context.addInput(IN_JGRAPH, jgraphMetadata);
	}

	public void contextChange(final ContextEvent event) {
	}

	public void contextRemoved(final ContextEvent event) {
	}

	public void exportData() {
	}

	public void importData() {
		layout = (WorkflowLayout) layoutWrapper.getData();
		jgraph = (JGraph) jgraphWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	public void processData() {
		if (jgraph != null) {
			final GraphModel model = new DefaultGraphModel();
			jgraph.setModel(model);
			layout.doLayout(model);
			jgraph.getGraphLayoutCache().reload();
		}
	}

	public void startExecution(final TransformationContext context) {
		// initialize inputs
		layoutWrapper = context.getInputWrapper(IN_LAYOUT);
		jgraphWrapper = context.getInputWrapper(IN_JGRAPH);

	}

	public void stopExecution() {
		layout = null;
		layoutWrapper = null;
		jgraph = null;
		jgraphWrapper = null;
	}

}
