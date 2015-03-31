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
import longbow.swing.jgraph.TransformationAdapter;

import org.jgraph.JGraph;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class RemoveIfContained implements Transformation {

	public static final String IN_CONTEXTS = "contexts";

	public static final String IN_WORKFLOW = "workflow";

	public static final String IN_JGRAPH = "jgraph";

	private static final Metadata contextsMetadata = MedalMetadata.parse("accept all");

	private static final Metadata workflowMetadata = MedalMetadata.parse("accept all");

	private static final Metadata jgraphMetadata = MedalMetadata.parse("accept all");

	private DataWrapper contextsWrapper;

	private DataWrapper jgraphWrapper;

	private TransformationAdapter[] contexts;

	private JGraph jgraph;

	private DataWrapper workflowWrapper;

	private Workflow workflow;

	public void addToContext(final ContextEvent event) {
		final TransformationContext context = event.getSource();
		context.addInput(IN_CONTEXTS, contextsMetadata);
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
		contexts = (TransformationAdapter[]) contextsWrapper.getData();
		workflow = (Workflow) workflowWrapper.getData();
		jgraph = (JGraph) jgraphWrapper.getData();
	}

	public boolean isExecutable() {
		return true;
	}

	public void processData() {
		final List<TransformationAdapter> list = new LinkedList<TransformationAdapter>();
		for (int i = 0; i < contexts.length; i++) {
			if (contexts[i] != null && workflow.remove(contexts[i].getUserObject())) {
				list.add(contexts[i]);
			}
		}
		if (!list.isEmpty()) {
			jgraph.getGraphLayoutCache().remove(list.toArray());
		}
	}

	public void startExecution(final TransformationContext context) {
		// initialize inputs
		contextsWrapper = context.getInputWrapper(IN_CONTEXTS);
		workflowWrapper = context.getInputWrapper(IN_WORKFLOW);

	}

	public void stopExecution() {
		contexts = null;
		contextsWrapper = null;
		workflow = null;
		workflowWrapper = null;
	}

}
