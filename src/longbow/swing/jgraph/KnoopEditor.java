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

package longbow.swing.jgraph;


import java.awt.Component;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.event.CellEditorListener;

import longbow.DataNode;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.GraphCellEditor;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class KnoopEditor implements GraphCellEditor {

	/**
	 * Implementatie van GraphCellEditor
	 */
	private final DefaultGraphCellEditor editorImpl;

	/**
	 * De DataNode die ge-editeerd wordt
	 */
	private DataNode DataNode;

	/** Creates a new instance of KnoopEditor */
	public KnoopEditor() {
		editorImpl = new DefaultGraphCellEditor();
		editorImpl.setBorderSelectionColor(DataRenderer.getConstVoorgrond());
	}

	public void addCellEditorListener(final CellEditorListener listener) {
		editorImpl.addCellEditorListener(listener);
	}

	public void cancelCellEditing() {
		editorImpl.cancelCellEditing();
	}

	public Object getCellEditorValue() {
		return DataNode;
	}

	public Component getGraphCellEditorComponent(final JGraph graph, final Object value, final boolean isSelected) {
		// zorg ervoor dat voldaan is aan gebruikte implementatie
		editorImpl.getGraphCellEditorComponent(graph, value, isSelected);
		// maak nu zelf een andere component
		DataNode = (DataNode) ((DataAdapter) value).getUserObject();
		final Component component = new JButton("A button");
		return component;
	}

	public boolean isCellEditable(final EventObject event) {
		return true;
	}

	public void removeCellEditorListener(final CellEditorListener listener) {
		editorImpl.removeCellEditorListener(listener);
	}

	public boolean shouldSelectCell(final EventObject event) {
		return false;
	}

	public boolean stopCellEditing() {
		return editorImpl.stopCellEditing();
	}

}
