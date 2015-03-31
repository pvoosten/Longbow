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


import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import longbow.Workflow;
import longbow.core.DefaultWorkflow;
import longbow.swing.library.LibraryList;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class Main {

	public static void main(final String[] args) {
		final Workflow workflow = new DefaultWorkflow();

		final GraphPanel jgraphPanel = new GraphPanel();
		jgraphPanel.setWorkflow(workflow);

		final JComponent library = new JScrollPane(new LibraryList());

		final JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, library, jgraphPanel);
		pane.setOneTouchExpandable(true);

		final JFrame frame = new JFrame("Longbow Designer View");
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.add(pane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		pane.setDividerLocation(150);
	}

}
