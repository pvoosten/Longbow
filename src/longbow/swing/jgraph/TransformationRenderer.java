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


import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * Panel to draw a transformation
 * 
 * @author Philip van Oosten
 * 
 */
public class TransformationRenderer extends VertexRenderer {

	private static final long serialVersionUID = 1L;

	private static Color voorgrond = new Color(100, 100, 100);

	private static Color achtergrond = new Color(50, 50, 50);

	private static Font font = Font.getFont("Times new Roman");

	public TransformationRenderer() {
		// default constructor
	}

	@Override
	public Point2D getPerimeterPoint(final VertexView view, final Point2D source, final Point2D point) {
		final double spacing = 2.0;
		final Rectangle2D bounds = view.getBounds();
		final double x = bounds.getX() - spacing;
		final double y = bounds.getY() - spacing;
		final double width = bounds.getWidth() + 2 * spacing;
		final double height = bounds.getHeight() + 2 * spacing;
		final double xCenter = x + width / 2;
		final double yCenter = y + height / 2;
		final double pointX = point.getX() - xCenter;
		final double pointY = point.getY() - yCenter;

		final double EPSILON = 0.001;

		final double alpha = pointY / pointX;

		assert width > height : "height > width in ellipse";

		final double r2 = width / 2;
		final double r1 = height / 2;

		double perimeterX = Math.sqrt(r2 * r2 * r1 * r1 / (r1 * r1 + r2 * r2 * alpha * alpha)); // zie tekening in documentatie voor uitleg
		double perimeterY = perimeterX < EPSILON ? (pointY < 0 ? -r1 : r1) : alpha * perimeterX; // we delen niet graag door (bijna) nul
		if (pointX < 0) {
			perimeterX = -perimeterX;
			perimeterY = -perimeterY;
		}
		perimeterX += xCenter;
		perimeterY += yCenter;
		return new Point2D.Double(perimeterX, perimeterY);
	}

	@Override
	public void paint(final Graphics graphics) {
		if (preview) {
			graphics.setColor(achtergrond);
			graphics.fillOval(0, 0, getWidth(), getHeight());
		} else {
			setIcon(new ImageIcon());
			setOpaque(false);
			if (hasFocus) {
				graphics.setColor(achtergrond);
				graphics.fillOval(0, 0, getWidth(), getHeight());
			} else {
				final Graphics2D g2d = (Graphics2D) graphics;
				g2d.setPaint(new GradientPaint(0, 0, achtergrond, getWidth() * 1.2f, getHeight() * 1.2f, voorgrond, true));
				g2d.fillOval(0, 0, getWidth(), getHeight());
			}
			graphics.setColor(voorgrond);
			graphics.setFont(font);
			graphics.drawString(getText(), 7, 30);
		}
		//super.paint (g);
		paintSelectionBorder(graphics);
	}

	@Override
	protected void paintSelectionBorder(final Graphics graphics) {
		final Graphics2D g2D = (Graphics2D) graphics;
		final Stroke previousStroke = g2D.getStroke();
		g2D.setStroke(GraphConstants.SELECTION_STROKE);
		if (childrenSelected || selected) {
			if (childrenSelected) {
				graphics.setColor(gridColor);
			} else if (hasFocus && selected) {
				graphics.setColor(lockedHandleColor);
			} else if (selected) {
				graphics.setColor(highlightColor);
			}
			final Dimension dim = getSize();
			graphics.drawOval(0, 0, dim.width - 1, dim.height - 1);
		}
		g2D.setStroke(previousStroke);
	}

	public static Color getAchtergrond() {
		return achtergrond;
	}

	public static Color getVoorgrond() {
		return voorgrond;
	}

	public static void setAchtergrond(final Color kleur) {
		achtergrond = kleur;
	}

	public static void setVoorgrond(final Color kleur) {
		voorgrond = kleur;
	}
}
