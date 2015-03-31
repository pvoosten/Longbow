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


import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.VertexView;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DataView extends VertexView {

	protected static final CellViewRenderer cellRenderer = new DataRenderer();

	private static final long serialVersionUID = 1L;

	public DataView(final DataAdapter adapter) {
		super(adapter);
	}

	/**
	 * Een DataView heeft een vaste grootte, wat het makkelijker maakt voor de
	 * gebruiker om in een oogopslag te zien welke knopen allemaal dataknopen
	 * zijn.
	 * 
	 * @return {@code null}, want een DataView heeft een vaste grootte.
	 */
	@Override
	public CellHandle getHandle(final GraphContext context) {
		return null;
	}

	@Override
	public CellViewRenderer getRenderer() {
		return cellRenderer;
	}

	// 1 CellEditor voor alle dataviews
	static {
		cellEditor = new KnoopEditor();
	}

}
