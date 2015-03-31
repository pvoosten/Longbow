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


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.VertexRenderer;

/**
 * 
 * @author Philip van Oosten
 * 
 */
public class DataRenderer extends VertexRenderer {

	private static final long serialVersionUID = 1L;

	private static Color dataVoorgrond = new Color(127, 50, 0);

	private static Color dataAchtergrond = new Color(255, 100, 0);

	private static Color constVoorgrond = new Color(127, 50, 0);

	private static Color constAchtergrond = new Color(150, 0, 75);

	private boolean constant;

	private boolean mark;

	/** Creates a new instance of DataRenderer */
	public DataRenderer() {
		// default constructor
	}

	@Override
	public void paint(final Graphics graphics) {
		// controleer of DataNode constant is
		final Color voor = constant ? constVoorgrond : dataVoorgrond;
		final Color achter;
		if (mark) {
			achter = Color.WHITE;
		} else {
			achter = constant ? constAchtergrond : dataAchtergrond;
		}
		// teken achtergrond
		graphics.setColor(achter);
		graphics.fillRect(2, 2, getWidth() - 4, getHeight() - 4);
		// teken voorgrond
		graphics.setColor(voor);
		final Graphics2D g2D = (Graphics2D) graphics;
		g2D.setStroke(new BasicStroke(3f));
		graphics.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
		paintSelectionBorder(graphics);
	}

	@Override
	protected void paintSelectionBorder(final Graphics graphics) {
		if (selected) {
			final Graphics2D g2D = (Graphics2D) graphics;
			g2D.setColor(highlightColor);
			final float[] dashpattern = { 5f };
			g2D.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5f, dashpattern, 0f));
			g2D.drawRect(2, 2, getHeight() - 4, getWidth() - 4);
		}
	}

	public static Color getConstAchtergrond() {
		return constAchtergrond;
	}

	public static Color getConstVoorgrond() {
		return constVoorgrond;
	}

	public static Color getDataAchtergrond() {
		return dataAchtergrond;
	}

	public static Color getDataVoorgrond() {
		return dataVoorgrond;
	}

	public static void setConstAchtergrond(final Color kleur) {
		constAchtergrond = kleur;
	}

	public static void setConstVoorgrond(final Color kleur) {
		constVoorgrond = kleur;
	}

	public static void setDataAchtergrond(final Color kleur) {
		dataAchtergrond = kleur;
	}

	public static void setDataVoorgrond(final Color kleur) {
		dataVoorgrond = kleur;
	}
}
