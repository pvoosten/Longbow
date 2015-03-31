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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.*;

import longbow.LongbowException;
import longbow.Transformation;
import longbow.TransformationContext;
import longbow.Workflow;
import longbow.core.DefaultWorkflow;
import longbow.metadata.MedalMetadata;
import longbow.swing.controller.DisconnectIfExists;
import longbow.swing.controller.DoLayout;
import longbow.swing.controller.GetContextsAndConnections;
import longbow.swing.controller.GetSelectedNodes;
import longbow.swing.controller.RemoveDanglingDataAdapters;
import longbow.swing.controller.RemoveIfContained;
import longbow.swing.jgraph.LongbowCellViewFactory;
import longbow.swing.jgraph.TransformationAdapter;
import longbow.transformations.Setter;

import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

/**
 * A visual {@link JGraph} representation of a {@link Workflow}.
 * 
 * @author Philip van Oosten
 * 
 */
public class GraphPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(GraphPanel.class);

	private static final String ACTION_DELETE_SELECTED_CONTEXTS = "deleteSelectedContexts";

	private static Map<JGraph, GraphPanel> parentGraphMap = new WeakHashMap<JGraph, GraphPanel>();

	/**
	 * A {@link JGraph} that represents the content of a {@link Workflow}.
	 */
	JGraph jgraph;

	/**
	 * The {@link Workflow} that is visually represented.
	 */
	Workflow workflow;

	final Map<String, TransformationContext> markers;

	final Map<String, TransformationContext> sweepers;

	private final Setter jgraphSetter;

	private final Setter workflowSetter;

	private final Setter layoutSetter;

	private final Workflow controller;

	/**
	 * Creates a new instance of GraafPaneel
	 */
	public GraphPanel() {
		setLayout(new BorderLayout());
		markers = new HashMap<String, TransformationContext>();
		sweepers = new HashMap<String, TransformationContext>();
		jgraphSetter = new Setter(MedalMetadata.acceptAll());
		workflowSetter = new Setter(MedalMetadata.acceptAll());
		layoutSetter = new Setter(MedalMetadata.acceptAll());
		controller = createController();
	}

	public boolean addTransformation(final Transformation transformation, final Point2D location) {
		if (workflow != null) {
			final TransformationContext context = workflow.add(transformation);
			add(context, location);
		}
		return false;
	}

	public void execute() {
		workflow.execute();
	}

	public void invokeAction(final String actionCommand) {
		final Action action = getActionMap().get(actionCommand);
		if (action != null && action.isEnabled()) {
			final ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand);
			final Runnable runnable = new Runnable() {

				public void run() {
					// double-check if the action is enabled before performing it.
					if (action.isEnabled()) {
						action.actionPerformed(event);
					}
				}
			};
			if (SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(runnable);
			} else {
				try {
					SwingUtilities.invokeAndWait(runnable);
				} catch (final InterruptedException e) {
					final String msg = "Interrupted while waiting for action to finish";
					logger.error(msg, e);
					throw new LongbowException(msg, e);
				} catch (final InvocationTargetException e) {
					final String msg = "Error while executing a swing command";
					logger.error(msg, e);
					throw new LongbowException(msg, e);
				}
			}
		}
	}

	public void setLayout(final WorkflowLayout layout) {
		layoutSetter.inject(layout);
		tryExecuteController();
	}

	/**
	 * Sets the {@link Workflow} that is visually represented.
	 * 
	 * @param wf
	 *            the workflow to set
	 */
	public void setWorkflow(final Workflow wf) {
		if (workflow == wf) {
			return;
		}
		disableDragAndDrop();
		if (wf != null && workflowSetter.inject(wf)) {
			final GraphModel model = new DefaultGraphModel();
			final JGraph newJgraph = new JGraph();
			newJgraph.getGraphLayoutCache().setFactory(new LongbowCellViewFactory());
			newJgraph.setModel(model);
			setJGraph(newJgraph);
			enableDragAndDrop(wf, newJgraph);
			workflow = wf;
		} else {
			workflow = null;
		}
		tryExecuteController();
	}

	private void add(final TransformationContext context, final Point2D location) {
		assert workflow != null;
		if (jgraph != null) {
			final TransformationAdapter adapter = new TransformationAdapter(context);
			final AttributeMap atts = adapter.getAttributes();
			final Rectangle2D bounds = GraphConstants.getBounds(atts);
			final Rectangle2D newBounds = new Rectangle2D.Double(location.getX() - bounds.getWidth() / 2, location.getY() - bounds.getHeight() / 2, bounds.getWidth(), bounds.getHeight());
			GraphConstants.setBounds(atts, newBounds);
			jgraph.getGraphLayoutCache().insert(adapter);
		}
	}

	private Workflow createController() {
		final Workflow controller = new DefaultWorkflow();
		final TransformationContext ctxGetSelectedNodes = controller.add(new GetSelectedNodes());
		final TransformationContext ctxGetContextsAndConnections = controller.add(new GetContextsAndConnections());
		final TransformationContext ctxDisconnectIfExists = controller.add(new DisconnectIfExists());
		final TransformationContext ctxRemoveIfContained = controller.add(new RemoveIfContained());
		final TransformationContext ctxRemoveDangling = controller.add(new RemoveDanglingDataAdapters());
		final TransformationContext ctxDoLayout = controller.add(new DoLayout());

		// jgraph constants
		final TransformationContext ctxJGraphSetter = controller.add(jgraphSetter);
		controller.connect(ctxJGraphSetter, Setter.OUT_DATA, ctxGetSelectedNodes, GetSelectedNodes.IN_JGRAPH);
		controller.connect(ctxJGraphSetter, Setter.OUT_DATA, ctxRemoveDangling, RemoveDanglingDataAdapters.IN_JGRAPH);
		controller.connect(ctxJGraphSetter, Setter.OUT_DATA, ctxRemoveIfContained, RemoveIfContained.IN_JGRAPH);
		controller.connect(ctxJGraphSetter, Setter.OUT_DATA, ctxDisconnectIfExists, DisconnectIfExists.IN_JGRAPH);
		controller.connect(ctxJGraphSetter, Setter.OUT_DATA, ctxDoLayout, DoLayout.IN_JGRAPH);

		// workflow constants
		final TransformationContext ctxWorkflowSetter = controller.add(workflowSetter);
		controller.connect(ctxWorkflowSetter, Setter.OUT_DATA, ctxDisconnectIfExists, DisconnectIfExists.IN_WORKFLOW);
		controller.connect(ctxWorkflowSetter, Setter.OUT_DATA, ctxRemoveIfContained, RemoveIfContained.IN_WORKFLOW);

		// workflow layout constant
		final TransformationContext ctxLayoutSetter = controller.add(layoutSetter);
		controller.connect(ctxLayoutSetter, Setter.OUT_DATA, ctxDoLayout, DoLayout.IN_LAYOUT);

		// data connections
		controller.connect(ctxGetSelectedNodes, GetSelectedNodes.OUT_SELECTED_NODES, ctxGetContextsAndConnections, GetContextsAndConnections.IN_SELECTED_NODES);
		controller.connect(ctxGetContextsAndConnections, GetContextsAndConnections.OUT_CONTEXTS, ctxRemoveIfContained, RemoveIfContained.IN_CONTEXTS);
		controller.connect(ctxGetContextsAndConnections, GetContextsAndConnections.OUT_CONNECTIONS, ctxDisconnectIfExists, DisconnectIfExists.IN_CONNECTIONS);

		// priority connections
		controller.connect(ctxDisconnectIfExists, ctxRemoveDangling);
		controller.connect(ctxRemoveIfContained, ctxDisconnectIfExists);
		controller.connect(ctxRemoveIfContained, ctxRemoveDangling);
		controller.connect(ctxRemoveDangling, ctxDoLayout);

		// set markers and sweepers for actions
		markers.put(ACTION_DELETE_SELECTED_CONTEXTS, ctxGetSelectedNodes);
		sweepers.put(ACTION_DELETE_SELECTED_CONTEXTS, ctxDoLayout);

		// change to run mode.
		assert controller.isExecutable() : "Controller is not executable";
		controller.execute();
		return controller;
	}

	private void disableDragAndDrop() {
		// workflow must not be null, because if it would be null, 
		// this method should not be called.
		if (workflow != null && jgraph != null) {
			jgraph.setDropEnabled(false);
			jgraph.setDragEnabled(false);
			jgraph.setDropTarget(null);
			setTransferHandler(null);
		}
	}

	private void enableDragAndDrop(final Workflow workflow, final JGraph jgraph) {
		if (workflow != null && jgraph != null) {
			final TransferHandler transferHandler = GraafTransferHandler.getInstance();
			setTransferHandler(transferHandler);
			jgraph.setTransferHandler(GraafTransferHandler.delegateFrom(this));
			jgraph.setDropEnabled(true);
			jgraph.setDragEnabled(false);
		}
	}

	/**
	 * Set the general properties of the jgraph
	 */
	private void layoutSettings(final JGraph jgraph) {
		jgraph.setAntiAliased(true);

		jgraph.setMinimumMove(1);
		jgraph.setTolerance(3);

		jgraph.setBackground(Color.lightGray);
		jgraph.setHighlightColor(Color.WHITE);

		// vaste eigenschappen
		jgraph.setHandleSize(7);
		jgraph.setSizeable(false);

		jgraph.setConnectable(true);
		jgraph.setCloneable(false); // CTRL + slepen kloont knopen NIET
		jgraph.setDisconnectOnMove(false);
	}

	/**
	 * Fill the action map of this panel.
	 */
	private void putActions() {
		logger.info("put actions");
		final ActionMap actionMap = getActionMap();
		final Action action = new GraphPanelAction(ACTION_DELETE_SELECTED_CONTEXTS);
		actionMap.put(ACTION_DELETE_SELECTED_CONTEXTS, action);
		final InputMap inputMap = getInputMap();
		final ResourceBundle bundle = ResourceBundle.getBundle(getClass().getPackage().getName().replace('.', File.separatorChar) + File.separatorChar + "GraphPanel_InputMap");
		inputMap.put(KeyStroke.getKeyStroke(bundle.getString(ACTION_DELETE_SELECTED_CONTEXTS)), ACTION_DELETE_SELECTED_CONTEXTS);
		jgraph.setActionMap(actionMap);

	}

	private Point2D randomLocation() {
		final Dimension size = jgraph.getSize();
		final double x = Math.random() * size.getWidth();
		final double y = Math.random() * size.getHeight();
		return new Point2D.Double(x, y);
	}

	/**
	 * Sets the {@link JGraph} that is used to visually represent the
	 * {@link Workflow}.
	 * 
	 * @param jgraph
	 */
	private void setJGraph(final JGraph jgraph) {
		if (this.jgraph == jgraph) {
			return;
		}
		disableDragAndDrop();
		removeAll();
		if (jgraph != null) {
			enableDragAndDrop(workflow, jgraph);
			layoutSettings(jgraph);
			// graaf weergeven op dit paneel
			// volledig paneel innemen
			jgraph.setBounds(0, 0, getWidth(), getHeight());
			jgraph.getInputMap().setParent(getInputMap());
			this.add(new JScrollPane(jgraph), BorderLayout.CENTER);
			parentGraphMap.remove(this.jgraph);
		}
		this.jgraph = jgraph;
		parentGraphMap.put(jgraph, this);
		putActions();
	}

	// TODO: create a transformation to synchronize model and view
	private void synchronizeModelAndView() {
		logger.info("Synchronize model and view");
		if (jgraph != null) {
			final GraphModel model = jgraph.getModel();
			final GraphLayoutCache layout = jgraph.getGraphLayoutCache();
			final Object[] cells = DefaultGraphModel.getAll(model);
			if (workflow == null) {
				layout.remove(cells);
			} else {
				final Set<TransformationContext> toAdd = new HashSet<TransformationContext>(workflow);
				final List<GraphCell> toRemove = new LinkedList<GraphCell>();
				for (final Object o : cells) {
					if (DefaultGraphModel.isVertex(model, o)) {
						final Object c = model.getValue(o);
						if (c != null && c instanceof TransformationContext) {
							final TransformationContext context = (TransformationContext) c;
							if (toAdd.contains(context)) {
								toAdd.remove(context);
							} else {
								toRemove.add((GraphCell) o);
							}
						}
					}
				}
				logger.info("To add: " + toAdd);
				for (final TransformationContext c : toAdd) {
					add(c, randomLocation());
				}
				logger.info("to remove: " + toRemove);
				layout.remove(toRemove.toArray());
			}
		}
	}

	private void tryExecuteController() {
		if (controller.isExecutable()) {
			controller.execute();
		}
	}

	/**
	 * Superclass for actions that can be performed on a {@link GraphPanel}.
	 */
	static class GraphPanelAction extends AbstractAction {

		private static final long serialVersionUID = -1387812622510863205L;

		public GraphPanelAction(final String actionCommand) {
			putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		}

		public void actionPerformed(final ActionEvent e) {
			final Object source = e.getSource();
			final GraphPanel panel;
			if (source instanceof GraphPanel) {
				panel = (GraphPanel) source;
			} else if (source instanceof JGraph) {
				JComponent comp = (JComponent) source;
				comp = (JComponent) comp.getParent(); // comp is now the JViewPort of the JScrollPane containing the JGraph
				comp = (JComponent) comp.getParent(); // comp is now the JScrollPane containing the JGraph
				comp = (JComponent) comp.getParent(); // comp is now the GraphPanel containing the JGraph
				panel = (GraphPanel) comp;
			} else {
				return;
			}
			assert panel != null : "Can't find graph panel";
			assert panel.markers != null : "No markers map for panel";
			assert panel.sweepers != null : "No sweepers map for panel";
			assert panel.markers.get(e.getActionCommand()) != null : "There is no marker for action command \"" + e.getActionCommand() + "\"";
			assert panel.sweepers.get(e.getActionCommand()) != null : "There is no sweeper for action command \"" + e.getActionCommand() + "\"";
			doAction(panel.markers.get(e.getActionCommand()), panel.sweepers.get(e.getActionCommand()));
		}

		private void doAction(final TransformationContext marker, final TransformationContext sweeper) {
			marker.mark();
			sweeper.sweep();
		}

	}

}
